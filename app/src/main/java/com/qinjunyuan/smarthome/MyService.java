package com.qinjunyuan.smarthome;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.util.ByteAndHexConverter;
import com.qinjunyuan.smarthome.util.ModBus;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;


public class MyService extends Service {
    private final MyBinder binder = new MyBinder();
    private final SocketAddress address = new InetSocketAddress("255.255.255.255", 1500);
    private HandlerThread workerThread;
    private Handler workerHandler;
    private ModbusMaster master;
    private List<Parameter> readParameter;
    private AbsView writeParameter;
    private boolean writeFlag = true;
    private int currentPageId;
    private static final int WRITE = -250;
    private static final int READ_AFTER_WRITE = -1;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("233", "onCreate: ");
        createMaster();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("233", "Service onDestroy: ");
        if (master != null) {
            master.destroy();
            master = null;
        }
        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
            workerHandler = null;
        }
        if (workerThread != null) {
            workerThread.quit();
            workerThread = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("233", "onBind: ");
        return binder;
    }

    private void createMaster() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket();
                    socket.setSoTimeout(5000);
                    byte[] command;
                    DatagramPacket receive = new DatagramPacket(new byte[130], 130);
                    command = ByteAndHexConverter.hexStringToBytes("FF010102");
                    DatagramPacket sendPacket = new DatagramPacket(command, command.length, address);
                    socket.send(sendPacket);

                    socket.receive(receive);
                    byte[] data = receive.getData();
                    String mac = ByteAndHexConverter.bytesToHexString(new byte[]{data[9], data[10], data[11], data[12], data[13], data[14]});
                    StringBuilder builder = new StringBuilder("");
                    builder.append("FF1303");
                    builder.append(mac);
                    builder.append("61646D696E0061646D696E00");//用户名与密码
                    builder.append(ByteAndHexConverter.makeChecksum(builder.substring(2)));
                    command = ByteAndHexConverter.hexStringToBytes(builder.toString());
                    DatagramPacket send = new DatagramPacket(command, command.length, address);
                    socket.send(send);

                    socket.receive(receive);
                    byte[] bytes = receive.getData();
                    String s = ByteAndHexConverter.bytesToHexString(new byte[]{bytes[80], bytes[79]});
                    int port = Integer.parseInt(s, 16);
                    IpParameters parameters = new IpParameters();
                    parameters.setHost(receive.getAddress().getHostAddress());
                    parameters.setPort(port);
                    parameters.setEncapsulated(true);
                    master = ModBus.init(parameters);
                    if (master.isInitialized()) {
                        openThread();
                    }
                } catch (Exception e) {

                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                    Intent intent = new Intent();
                    intent.setAction(master != null && master.isInitialized() ? Util.ACTION_BROADCAST_OK : Util.ACTION_BROADCAST_CANCELED);
                    sendBroadcast(intent);
                }
            }
        }).start();
    }

    private void openThread() {
        if (workerThread == null || !workerThread.isAlive()) {
            workerThread = new HandlerThread("openThread");
            workerThread.start();
            Log.d("233", "开启执行轮询工作的线程");
            workerHandler = new Handler(workerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    Log.d("233", "handleMessage: ");
                    if (master != null && master.isInitialized()) {
                        if (msg.what == WRITE) {
                            if (writeFlag && writeParameter != null) {
                                Log.d("233", "write ");
                                writeFlag = false;
                                try {
                                    writeParameter.write(master);
                                    sendEmptyMessageDelayed(READ_AFTER_WRITE, 1500);
                                } catch (ModbusTransportException e) {
                                    writeFlag = true;
                                }
                            }
                        } else if (readParameter != null) {
                            Log.d("233", "read ");
                            for (int i = 0; i < readParameter.size(); i++) {
                                Parameter parameter = readParameter.get(i);
                                try {
                                    parameter.read(master);
                                } catch (ModbusTransportException e) {
                                    Log.d("233", "读取失败 ");
                                }
                            }
                            Intent intent = new Intent(Util.ACTION_BROADCAST_UPDATE);
                            sendBroadcast(intent);
                            if (msg.what != READ_AFTER_WRITE) {
                                if (msg.what == currentPageId) {
                                    sendEmptyMessageDelayed(msg.what, 8000);
                                } else {
                                    Log.d("233", "停止了轮询操作 ");
                                }
                            } else {
                                writeFlag = true;
                            }
                        }
                    }
                }
            };
        }
    }

    public class MyBinder extends Binder {
        public void startRead(List<Parameter> parameters, int pageId) {
            if (workerHandler != null) {
                readParameter = parameters;
                currentPageId = pageId;
                workerHandler.sendEmptyMessage(pageId);
            }
        }

        public void write(AbsView view) {
            if (workerHandler != null && writeFlag) {
                writeParameter = view;
                workerHandler.sendEmptyMessage(WRITE);
            }
        }

        public void stopRead(int pageId) {
            if (workerHandler != null) {
                workerHandler.removeMessages(pageId);
            }
        }
    }
}

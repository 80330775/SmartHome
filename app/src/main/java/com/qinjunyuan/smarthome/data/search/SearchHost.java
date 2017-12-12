package com.qinjunyuan.smarthome.data.search;

import com.qinjunyuan.smarthome.util.ByteAndHexConverter;
import com.qinjunyuan.smarthome.util.ModBus;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class SearchHost {
    private DatagramSocket socket;
    private SocketAddress address = new InetSocketAddress("255.255.255.255", 1500);
    private ModbusMaster master;

    public void getMaster(final Callback callback) {
        if (master == null || !master.isInitialized()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatagramPacket receive = new DatagramPacket(new byte[130], 130);
                    byte[] command;
                    try {
                        socket = new DatagramSocket();
                        socket.setSoTimeout(5000);
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
                            callback.onResponse(master);
                        }
                    } catch (IOException | ModbusInitException e) {
                        callback.onFailure(e);
                    } finally {
                        if (socket != null) {
                            socket.close();
                        }
                    }
                }
            }).start();
        } else {
            callback.onResponse(master);
        }
    }

    public boolean isNeedCreateMaster() {
        return master == null || !master.isInitialized();
    }

    public void onDestroy() {
        if (master != null) {
            master.destroy();
        }
    }

    public interface Callback {
        void onResponse(ModbusMaster master);

        void onFailure(Exception e);
    }
}

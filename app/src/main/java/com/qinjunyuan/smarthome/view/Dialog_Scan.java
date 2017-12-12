package com.qinjunyuan.smarthome.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qinjunyuan.smarthome.R;
import com.qinjunyuan.smarthome.application.MyApplication;
import com.qinjunyuan.smarthome.data.search.SearchHost;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.NodeScanListener;
import com.serotonin.modbus4j.sero.util.ProgressiveTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;


public class Dialog_Scan extends DialogFragment implements NodeScanListener {
    private TextView textView;
    private AVLoadingIndicatorView loadingView;
    private boolean isFirstLoad = true;
    private ProgressiveTask task;
    private StringBuilder builder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_scan, container, false);
        textView = (TextView) view.findViewById(R.id.progress);
        loadingView = (AVLoadingIndicatorView) view.findViewById(R.id.loadingView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            loadingView.show();
            if (getView() != null) {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if (task != null) {
                                task.cancel();
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
            ((MyApplication) getActivity().getApplication()).getAppComponent().getSearchHost().getMaster(new SearchHost.Callback() {
                @Override
                public void onResponse(ModbusMaster master) {
                    task = master.scanForSlaveNodes(Dialog_Scan.this);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "没找到ModBusMaster", Toast.LENGTH_LONG).show();
                    dismiss();
                }
            });
        }
    }

    @Override
    public void nodeFound(int nodeNumber) {
        Log.d("233", "nodeFound: " + nodeNumber);
    }

    @Override
    public void progressUpdate(float progress) {
        int i = (int) (progress * 100);
        if (builder != null) {
            builder.replace(0, builder.length() - 1, String.valueOf(i));
        } else {
            builder = new StringBuilder();
            builder.append(i);
            builder.append("%");
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public void taskCancelled() {
        Log.d("233", "taskCancelled: ");
        dismiss();
    }

    @Override
    public void taskCompleted() {
        Log.d("233", "taskCompleted: ");
        dismiss();
    }

    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<Dialog_Scan> reference;

        private MyHandler(Dialog_Scan dialog) {
            reference = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            Dialog_Scan dialog = reference.get();
            if (dialog != null) {
                dialog.textView.setText(dialog.builder);
            }
        }
    }
}

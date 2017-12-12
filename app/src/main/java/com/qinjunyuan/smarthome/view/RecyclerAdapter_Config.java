package com.qinjunyuan.smarthome.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinjunyuan.smarthome.R;

import java.util.List;


public class RecyclerAdapter_Config extends RecyclerView.Adapter<RecyclerAdapter_Config.ViewHolder> {
    private final String[] options = {ConfigUtil.FOUR_TWO, ConfigUtil.THREE_TWO, ConfigUtil.THREE_ONE, ConfigUtil.TWO_ONE};
    private Context context;
    private RecyclerView recyclerView;
    private int lastPosition = -1;

    public RecyclerAdapter_Config(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.welcome_option, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!holder.option.hasOnClickListeners()) {
            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildViewHolder(v).getAdapterPosition();
                    if (position != lastPosition) {
                        notifyItemChanged(position, true);
                        if (lastPosition != -1) {
                            notifyItemChanged(lastPosition, false);
                        }
                        lastPosition = position;
                        listener.setButtonEnabled();
                    }
                }
            });
            holder.option.setText(options[position]);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            boolean isChecked = (boolean) payloads.get(0);
            if (isChecked) {
                holder.option.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_300));
            } else {
                holder.option.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_50));
            }
        }
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    public String getHouseType() {
        return options[lastPosition];
    }

    private OnStartActivityListener listener;

    public interface OnStartActivityListener {
        void setButtonEnabled();
    }

    public void setListener(OnStartActivityListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView option;

        private ViewHolder(View itemView) {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.option);
        }
    }
}

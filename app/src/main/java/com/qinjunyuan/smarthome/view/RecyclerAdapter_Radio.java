package com.qinjunyuan.smarthome.view;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.qinjunyuan.smarthome.R;
import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Button;
import com.qinjunyuan.smarthome.feature.modbus.Title;
import com.qinjunyuan.smarthome.feature.modbus.Picker;
import com.qinjunyuan.smarthome.feature.modbus.Text;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.List;


public class RecyclerAdapter_Radio extends RecyclerView.Adapter<RecyclerAdapter_Radio.ViewHolder_Radio> {
    private List<AbsView> viewList;
    private Uri uri;
    private Context context;
    private RecyclerView recyclerView;
    private int mMinWidth;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_TITLE = 2;
    private static final int TYPE_TEXT = 3;
    private static final int TYPE_PICKER = 4;
    private static final int TYPE_BUTTON_IMAGE = 5;
    private static final int TYPE_BUTTON_TEXT = 6;

    public RecyclerAdapter_Radio(List<AbsView> viewList) {
        this.viewList = viewList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        AbsView view = viewList.get(position);
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (view instanceof Title) {
            return TYPE_TITLE;
        }
        if (view instanceof Text) {
            return TYPE_TEXT;
        }
        if (view instanceof Picker) {
            return TYPE_PICKER;
        }
        if (view instanceof Button.Image) {
            return TYPE_BUTTON_IMAGE;
        }
        if (view instanceof Button.Text) {
            return TYPE_BUTTON_TEXT;
        }
        return -1;
    }

    @Override
    public ViewHolder_Radio onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (mMinWidth == 0) {
            mMinWidth = ((int) Math.floor(parent.getMeasuredWidth() / 4)) - 20;
            Log.d("233", "mMinWidth = " + mMinWidth);
        }
        View view = null;
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_view_image, parent, false);
                break;
            case TYPE_TITLE:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_view_header, parent, false);
                break;
            case TYPE_TEXT:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_view_button_text, parent, false);
                break;
            case TYPE_PICKER:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_view_picker, parent, false);
                if (view instanceof NumberPicker) {
                    NumberPicker picker = (NumberPicker) view;
                    picker.setOnScrollListener(onScrollListener);
                }
                break;
            case TYPE_BUTTON_IMAGE:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_view_button_icon, parent, false);
                view.setOnClickListener(onClickListener);
                break;
            case TYPE_BUTTON_TEXT:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_view_button_text, parent, false);
                view.setOnClickListener(onClickListener);
                break;
        }
        if (viewType == TYPE_BUTTON_IMAGE || viewType == TYPE_BUTTON_TEXT || viewType == TYPE_TEXT) {
            if (view.getLayoutParams() instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams lp = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
                lp.setFlexGrow(1);
                if (mMinWidth != 0) {
                    lp.setMinWidth(mMinWidth);
                }
            }
        }
        return new ViewHolder_Radio(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder_Radio holder, int position) {

    }

    @Override
    public void onBindViewHolder(ViewHolder_Radio holder, int position, List<Object> payloads) {
        if (uri != null && position == 0 && holder.view instanceof ImageView) {
            ImageView imageView = (ImageView) holder.view;
            Glide.with(context).load(uri).into(imageView);
            Log.d("233", "更新图片");
            return;
        }
        AbsView absView = viewList.get(position);
        absView.update(holder.view, context);
    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildLayoutPosition(v);
            AbsView absView = viewList.get(position);
            listener.write(absView);
        }
    };

    private final NumberPicker.OnScrollListener onScrollListener = new NumberPicker.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPicker view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                int position = recyclerView.getChildLayoutPosition(view);
                Picker picker = (Picker) viewList.get(position);
                picker.setWriteValue(view.getValue());
                listener.write(picker);
            }
        }
    };

    static class ViewHolder_Radio extends RecyclerView.ViewHolder {
        private View view;

        ViewHolder_Radio(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    private OnWriteDataListener listener;

    public interface OnWriteDataListener {
        void write(AbsView absView);
    }

    public void setListener(OnWriteDataListener listener) {
        this.listener = listener;
    }
}

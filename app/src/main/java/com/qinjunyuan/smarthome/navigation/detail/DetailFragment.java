package com.qinjunyuan.smarthome.navigation.detail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.qinjunyuan.smarthome.R;
import com.qinjunyuan.smarthome.application.MyApplication;
import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.util.BaseFragment;
import com.qinjunyuan.smarthome.util.SQLite;
import com.qinjunyuan.smarthome.view.RecyclerAdapter_Radio;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;

import javax.inject.Inject;


public class DetailFragment extends BaseFragment implements DetailContract.View, RecyclerAdapter_Radio.OnWriteDataListener {
    private int pageId;
    private Uri uri;

    private RecyclerView.RecycledViewPool pool;
    private RecyclerView recyclerView;
    private FlexboxLayoutManager manager;
    private RecyclerAdapter_Radio adapter;

    @Inject
    DetailContract.Presenter presenter;

    public static DetailFragment newInstance(int roomId, String image) {
        Bundle args = new Bundle();
        args.putInt(SQLite.PAGE_ID, roomId);
        args.putString(SQLite.IMAGE, image);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pageId = bundle.getInt(SQLite.PAGE_ID);
            final String image = bundle.getString(SQLite.IMAGE);
            if (!TextUtils.isEmpty(image)) {
                uri = Uri.parse(image);
            }
        }
        DaggerDetailComponent
                .builder()
                .appComponent(((MyApplication) getActivity().getApplication()).getAppComponent())
                .detailPresenterModule(new DetailPresenterModule(this, pageId))
                .build()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        manager = new FlexboxLayoutManager(getContext(), FlexDirection.ROW, FlexWrap.WRAP);
        manager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        if (pool != null) {
            recyclerView.setRecycledViewPool(pool);
        }
        adapter = presenter.initAdapter();
        adapter.setUri(uri);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isViewCreate && getUserVisibleHint()) {
            lazyLoading();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        manager = null;
        adapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
        int color = ContextCompat.getColor(getContext(), R.color.grey_300);
        recyclerView.setBackgroundColor(color);
        if (isVisible()) {
            onVisible();
        }
    }

    @Override
    public void lazyLoading() {
        super.lazyLoading();
        presenter.start();
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (getActivity() instanceof OnSetUserVisibleHintListener) {
            OnSetUserVisibleHintListener listener = (OnSetUserVisibleHintListener) getActivity();
            listener.startRead(presenter.getParameter(), pageId);
        }
    }

    @Override
    public void onInvisible() {
        super.onInvisible();
        if (getActivity() instanceof OnSetUserVisibleHintListener) {
            OnSetUserVisibleHintListener listener = (OnSetUserVisibleHintListener) getActivity();
            listener.stopRead(pageId);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (isVisible()) {
            int firstPosition = manager.findFirstVisibleItemPosition();
            int lastPosition = manager.findLastVisibleItemPosition();
            int count = lastPosition - firstPosition + 1;
            Log.d("233", "notifyItemRangeChanged " + firstPosition + ", " + lastPosition + ", " + count);
            adapter.notifyItemRangeChanged(firstPosition, count, "");
        }
    }

    @Override
    public void write(AbsView absView) {
        if (getActivity() instanceof OnSetUserVisibleHintListener) {
            OnSetUserVisibleHintListener listener = (OnSetUserVisibleHintListener) getActivity();
            listener.write(absView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pageId && resultCode == Activity.RESULT_OK) {
            List<Uri> list = Matisse.obtainResult(data);
            if (list != null && !list.isEmpty()) {
                Uri uri = list.get(0);
                presenter.savePic(uri);
                adapter.setUri(uri);
                adapter.notifyItemChanged(0, "");
            }
        }
    }

    public void openMatisseActivity() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            open();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            open();
        } else {
            Toast.makeText(getContext(), "申请权限失败，无法启动图片选择器", Toast.LENGTH_SHORT).show();
        }
    }

    private void open() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Dracula)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.qinjunyuan.smarthome.fileprovider"))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(pageId);
    }

    public void setPool(RecyclerView.RecycledViewPool pool) {
        this.pool = pool;
    }
}

package com.sun.music61.screen.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.home.adapter.CustomSliderAdapter;
import com.sun.music61.screen.home.adapter.TrackAdapter;
import com.sun.music61.util.RepositoryInstance;
import com.sun.music61.util.helpers.ImageLoadingServiceHelpers;
import com.sun.music61.util.listener.ItemRecyclerOnClickListener;
import dmax.dialog.SpotsDialog;
import java.util.List;
import java.util.Objects;
import ss.com.bannerslider.Slider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.music61.util.CommonUtils.Genres;

public class AllSongsFragment extends Fragment implements AllSongsContract.View,
        ItemRecyclerOnClickListener {

    private static final String OFFSET_DEFAULT = "0";

    private AllSongsContract.Presenter mPresenter;
    private AlertDialog mDialogWaiting;
    private Slider mSlider;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerMusics;
    private TrackAdapter mMusicAdapter;

    public static AllSongsFragment newInstance() {
        return new AllSongsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_songs_fragment, container, false);
        initView(rootView);
        mPresenter = new AllSongPresenter(
                RepositoryInstance.getInstanceTrackRepository(Objects.requireNonNull(getContext())),
                this);
        onListenerEvent();
        return rootView;
    }

    private void initView(View rootView) {
        mDialogWaiting = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage(R.string.text_waiting)
                .setCancelable(false)
                .build();
        mSlider = rootView.findViewById(R.id.slider);
        Slider.init(new ImageLoadingServiceHelpers());
        mRecyclerMusics = rootView.findViewById(R.id.recyclerMusic);
        mMusicAdapter = new TrackAdapter(R.layout.item_track_square);
        mMusicAdapter.setOnItemClickListener(this);
        mRecyclerMusics.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerMusics.setAdapter(mMusicAdapter);
        mRefreshLayout = rootView.findViewById(R.id.swipeLayout);
        mRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
    }

    private void onListenerEvent() {
        mRefreshLayout.post(this::loadData);
        mRefreshLayout.setOnRefreshListener(() -> {
            loadData();
            mRefreshLayout.setRefreshing(false);
        });
    }

    private void loadData() {
        mDialogWaiting.show();
        mPresenter.loadAllBanners();
        mPresenter.loadAllTracks(Genres.ALL_MUSIC, OFFSET_DEFAULT);
    }

    @Override
    public void setPresenter(AllSongsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onGetBannersSuccess(List<Track> banners) {
        mSlider.setVisibility(View.VISIBLE);
        mSlider.setAdapter(new CustomSliderAdapter(banners));
    }

    @Override
    public void onDataBannersNotAvailable() {
        mSlider.setVisibility(View.GONE);
    }

    @Override
    public void onGetTracksSuccess(List<Track> tracks) {
        mRecyclerMusics.setVisibility(View.VISIBLE);
        mMusicAdapter.updateData(tracks);
        mDialogWaiting.dismiss();
    }

    @Override
    public void onDataTracksNotAvailable() {
        mRecyclerMusics.setVisibility(View.GONE);
        mDialogWaiting.dismiss();
    }

    @Override
    public void showErrors(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecyclerItemClick(Object object, int position) {
    }
}
package com.azweb.scheellarsen.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.azweb.scheellarsen.MainActivity;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.adapters.VideoAdapter;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.models.Video;
import com.azweb.scheellarsen.session.Session;

import java.util.ArrayList;

/**
 * Created by Thaind on 11/5/2015.
 */
public class TabVideoFragment extends BaseContainerFragment{

    private ArrayList<Video> listVideo;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private MainActivity mActivity;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_video_layout, null);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler);
        listVideo = new ArrayList<>();

        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            setAdapter();
            mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Video.getInstance().getVideos(getContext(), handler);
        } else {
            listVideo = savedInstanceState.getParcelableArrayList("list_video");
            setAdapter();
        }
        mAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = (String) v.getTag();
                mActivity.openVideo(videoId);

            }
        });
    }

    private DataTransferHandler handler = new DataTransferHandler() {
        @Override
        public void onDataTransferResultHandler(Response response) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            if (response.getObject() != null) {
                listVideo.addAll((ArrayList<Video>) response.getObject());
                mAdapter.notifyDataSetChanged();
            }
            if (listVideo.size() == 0) {
                mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(String messageError) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
        }
    };

    private void setAdapter() {
        mAdapter = new VideoAdapter(getActivity(), listVideo);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list_video", listVideo);
    }



    public static final class VideoFragment extends YouTubePlayerFragment
            implements OnInitializedListener {


        private YouTubePlayer player;
        private String videoId;
        private static VideoFragment videoFragment;

        public static synchronized VideoFragment getInstance() {
            if (videoFragment == null) {
                videoFragment = new VideoFragment();
            }
            return videoFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            initialize(Session.YOUTUBE_KEY, this);
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
            videoFragment = null;
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.loadVideo(videoId);
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            if (!restored && videoId != null) {
                player.loadVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }


    }
}

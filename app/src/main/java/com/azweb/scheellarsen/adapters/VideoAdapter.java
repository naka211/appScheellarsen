package com.azweb.scheellarsen.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.models.Video;
import com.azweb.scheellarsen.session.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thaind on 12/14/2015.
 */
public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Video> listVideo;
    private Context mContext;
    private ThumbnailListener thumbnailListener;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private View.OnClickListener mOnClickListener;

    public VideoAdapter(Context context, ArrayList<Video> listVideo) {
        this.mContext = context;
        this.listVideo = listVideo;
        thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_tab_video_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Video video = listVideo.get(position);
        ViewHolder v = (ViewHolder) holder;
        v.title.setText(video.getTitle().toUpperCase());
        v.content.setText(Html.fromHtml(video.getContent()));
        v.youTubeView.setOnClickListener(mOnClickListener);
        YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(v.youTubeView);
        if (loader == null) {
            // 2) The view is already created, and is currently being initialized. We store the
            //    current videoId in the tag.
            v.youTubeView.setTag(video.getVideoId());
        } else {
            // 3) The view is already created and already initialized. Simply set the right videoId
            //    on the loader.
            //v.youTubeView.setImageResource(R.drawable.loading_thumbnail);
            loader.setVideo(video.getVideoId());
        }
    }

    @Override
    public int getItemCount() {
        return listVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private ImageView image;
        private ProgressBar progressBar;
        private YouTubeThumbnailView youTubeView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.video_title);
            this.content = (TextView) itemView.findViewById(R.id.video_content);
            this.image = (ImageView) itemView.findViewById(R.id.playvideo);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            this.youTubeView = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
            thumbnailListener = new ThumbnailListener(this);
            this.youTubeView.initialize(Session.YOUTUBE_KEY, thumbnailListener);
        }
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.mOnClickListener = onItemClickListener;
    }

    private class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener, YouTubePlayer.OnInitializedListener {
        private ViewHolder mHolder;

        public ThumbnailListener(ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);

        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
            mHolder.progressBar.setVisibility(View.GONE);
            mHolder.image.setVisibility(View.VISIBLE);
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, ErrorReason errorReason) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }
    }

}


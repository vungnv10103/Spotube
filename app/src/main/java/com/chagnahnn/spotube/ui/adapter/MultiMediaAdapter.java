package com.chagnahnn.spotube.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chagnahnn.spotube.R;
import com.chagnahnn.spotube.ui.model.MultiMedia;
import com.chagnahnn.spotube.ui.perform.MultiMediaCallback;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class MultiMediaAdapter extends RecyclerView.Adapter<MultiMediaAdapter.MultiMediaViewHolder> {

    private final Context context;
    private List<MultiMedia> mMultiMediaList;
    private final int layoutResId;
    private final MultiMediaCallback multiMediaCallback;

    public MultiMediaAdapter(Context context, int layoutResId, MultiMediaCallback multiMediaCallback) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.multiMediaCallback = multiMediaCallback;
        setHasStableIds(true);
    }

    public void setItemList(List<MultiMedia> newListMultiMedia) {
        if (mMultiMediaList == null) {
            mMultiMediaList = newListMultiMedia;
            notifyItemRangeInserted(0, newListMultiMedia.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mMultiMediaList.size();
                }

                @Override
                public int getNewListSize() {
                    return newListMultiMedia.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mMultiMediaList.get(oldItemPosition).getId()
                            .equals(newListMultiMedia.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mMultiMediaList.get(oldItemPosition).equals(newListMultiMedia.get(newItemPosition));
                }
            });
            mMultiMediaList.clear();
            mMultiMediaList.addAll(newListMultiMedia);
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public MultiMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, parent, false);
        return new MultiMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiMediaViewHolder holder, int position) {
        onBindViewHolder(holder, position, new ArrayList<>());
    }

    @Override
    public void onBindViewHolder(@NonNull MultiMediaViewHolder holder, int position, @NonNull List<Object> payloads) {
        // super.onBindViewHolder(holder, position, payloads);

        if (payloads.isEmpty()) {
            MultiMedia multiMedia = mMultiMediaList.get(position);

            loadImage(holder.imgMultiMedia, multiMedia.getArtworkUrl());
            holder.tvTitle.setText(multiMedia.getTitle());
            holder.tvDesc.setText(multiMedia.getCompose());

            holder.itemView.setOnClickListener(view -> multiMediaCallback.onClick(mMultiMediaList, multiMedia, position));
            holder.itemView.setOnLongClickListener(view -> {
                multiMediaCallback.onLongClick(mMultiMediaList, multiMedia, position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mMultiMediaList != null) {
            return mMultiMediaList.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return mMultiMediaList.get(position).getId().hashCode();
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.artwork)
                .transition(DrawableTransitionOptions.withCrossFade(350))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .timeout(5000)
                .into(imageView);
    }

    public static class MultiMediaViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView imgMultiMedia;
        private final TextView tvTitle, tvDesc;

        public MultiMediaViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMultiMedia = itemView.findViewById(R.id.artwork_media);
            tvTitle = itemView.findViewById(R.id.tv_title_media);
            tvDesc = itemView.findViewById(R.id.tv_desc_media);
        }

    }
}

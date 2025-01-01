package com.chagnahnn.spotube.ui.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chagnahnn.spotube.MainActivity;
import com.chagnahnn.spotube.R;
import com.chagnahnn.spotube.ui.model.Comment;
import com.chagnahnn.spotube.util.FormatUtils;
import com.chagnahnn.spotube.util.ToastUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context context;
    private List<Comment> mCommentList;
    private final SpannableString spannableStringTerm;

    public CommentAdapter(Context context, SpannableString spannableString) {
        this.context = context;
        this.spannableStringTerm = spannableString;
        setHasStableIds(true);
    }

    public void setItemList(final List<Comment> newListComment) {
        if (mCommentList == null) {
            newListComment.sort((o1, o2) -> Boolean.compare(o2.isPinned(), o1.isPinned()));
            mCommentList = newListComment;
            notifyItemRangeInserted(0, newListComment.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCommentList.size();
                }

                @Override
                public int getNewListSize() {
                    return newListComment.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mCommentList.get(oldItemPosition).getId()
                            .equals(newListComment.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Comment multiMediaNew = newListComment.get(newItemPosition);
                    Comment multiMediaOld = mCommentList.get(oldItemPosition);
                    return multiMediaOld.equals(multiMediaNew);
                }
            });
            newListComment.sort((o1, o2) -> Boolean.compare(o2.isPinned(), o1.isPinned()));
            mCommentList = newListComment;
            result.dispatchUpdatesTo(this);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return Long.MIN_VALUE;
        }
        return mCommentList.get(position - 1).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        if (mCommentList != null) {
            return mCommentList.size() + 1;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_top, parent, false);
            return new ViewHolderTerm(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderData) {
            ViewHolderData commentViewHolder = (ViewHolderData) holder;
            Comment comment = mCommentList.get(position - 1);

            loadImage(commentViewHolder.imgUserCmt, "1");

            if (comment.isPinned()) {
                commentViewHolder.tvPin.setVisibility(View.VISIBLE);
                commentViewHolder.tvPin.setText(String.format(context.getString(R.string.pinned), "anonymous"));
            } else {
                commentViewHolder.tvPin.setVisibility(View.GONE);
            }

            commentViewHolder.tvUsername.setText(context.getString(R.string.username_cmt, "anonymous"));
            StringBuilder statusStringBuilder = new StringBuilder();
//            statusStringBuilder.append(" • ").append(FormatUtils.compareTime(comment.created_at));
            statusStringBuilder.append(" • ").append(FormatUtils.compareTime(context, comment.getCreatedAt(), false));
            if (comment.isEdit()) {
                statusStringBuilder.append("(").append(context.getString(R.string.edited)).append(")");
//            statusStringBuilder.append("●").append(comment.updated_at);
            }
            commentViewHolder.tvStatus.setText(statusStringBuilder.toString());

            commentViewHolder.tvContent.setText(comment.getContent());
            if (comment.getLikeCount() > 0) {
                commentViewHolder.btnLike.setText(FormatUtils.formatCount(context, comment.getLikeCount()));
            }
            if (comment.getDislikeCount() > 0) {
                commentViewHolder.btnDislike.setText(FormatUtils.formatCount(context, comment.getDislikeCount()));
            }
            if (comment.isParentLike()) {
                commentViewHolder.userParentLikeContainer.setVisibility(View.VISIBLE);
                loadImage(commentViewHolder.imgUserLikeCmt, "");
            } else {
                commentViewHolder.userParentLikeContainer.setVisibility(View.INVISIBLE);
            }
            if (comment.getReplyCount() > 0) {
                commentViewHolder.btnDetailReply.setVisibility(View.VISIBLE);
                commentViewHolder.btnDetailReply.setText(context.getString(R.string.replies, comment.getReplyCount()));
            } else {
                commentViewHolder.btnDetailReply.setVisibility(View.GONE);
            }


            commentViewHolder.itemContainer.setOnClickListener(view -> goToReplyCmt(false));
            commentViewHolder.tvContent.setOnLongClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", comment.getContent());
                clipboard.setPrimaryClip(clip);
                ToastUtils.show(context, context.getString(R.string.copied_to_clipboard));
                return true;
            });
            commentViewHolder.btnReply.setOnClickListener(view -> goToReplyCmt(true));
            commentViewHolder.btnDetailReply.setOnClickListener(view -> goToReplyCmt(false));
            commentViewHolder.btnMore.setOnClickListener(view -> {
            });
        } else {
            ViewHolderTerm viewHolderTerm = (ViewHolderTerm) holder;

            //viewHolderTerm.tvTerm.setHighlightColor(ThemeUtils.getColor(context, R.color.blue_100));
            viewHolderTerm.tvTerm.setText(spannableStringTerm);
            viewHolderTerm.tvTerm.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void goToReplyCmt(boolean isFocusEditText) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.showReplyCmt(isFocusEditText);
    }

    private void loadImage(ImageView imageView, String imgUrl) {
        Glide.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_account_circle_24dp)
                .transition(DrawableTransitionOptions.withCrossFade(350))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .timeout(5000)
                .into(imageView);
    }

    public static class ViewHolderTerm extends RecyclerView.ViewHolder {
        private final TextView tvTerm;

        public ViewHolderTerm(@NonNull View itemView) {
            super(itemView);

            tvTerm = itemView.findViewById(R.id.tv_term);
        }
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {
        private final ConstraintLayout itemContainer, userParentLikeContainer;
        private final ShapeableImageView imgUserCmt;
        private final MaterialButton btnMore, btnLike, btnDislike, btnReply, btnDetailReply;
        private final TextView tvContent;
        private final TextView tvPin, tvUsername, tvStatus;
        private final ImageView imgUserLikeCmt;

        public ViewHolderData(@NonNull View itemView) {
            super(itemView);

            itemContainer = itemView.findViewById(R.id.item_cmt_container);
            userParentLikeContainer = itemView.findViewById(R.id.user_parent_like_cmt_container);
            imgUserCmt = itemView.findViewById(R.id.img_user_cmt);
            tvPin = itemView.findViewById(R.id.tv_pin);
            tvUsername = itemView.findViewById(R.id.tv_username_cmt);
            tvContent = itemView.findViewById(R.id.tv_content_cmt);
            btnLike = itemView.findViewById(R.id.btn_like_cmt);
            btnDislike = itemView.findViewById(R.id.btn_dislike_cmt);
            btnReply = itemView.findViewById(R.id.btn_reply_cmt);
            btnDetailReply = itemView.findViewById(R.id.btn_detail_reply);
            btnMore = itemView.findViewById(R.id.btn_more_cmt);
            tvStatus = itemView.findViewById(R.id.tv_status_cmt);
            imgUserLikeCmt = itemView.findViewById(R.id.img_user_like_cmt);
        }
    }

}

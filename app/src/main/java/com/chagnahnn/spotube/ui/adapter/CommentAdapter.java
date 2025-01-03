package com.chagnahnn.spotube.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.chagnahnn.spotube.MainActivity;
import com.chagnahnn.spotube.R;
import com.chagnahnn.spotube.ui.model.Comment;
import com.chagnahnn.spotube.util.FormatUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MAX_LINES_COMMENT = 4;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context context;
    private final boolean isReplyCmtList;
    private List<Comment> mCommentList;
    private final SpannableString spannableStringTerm;

    public CommentAdapter(Context context, boolean isReplyCmtList, SpannableString spannableString) {
        this.context = context;
        this.isReplyCmtList = isReplyCmtList;
        this.spannableStringTerm = spannableString;
        setHasStableIds(true);
    }

    private void sortByLikeCount(@NonNull final List<Comment> newListComment) {
        newListComment.sort(Comparator
                .comparing(Comment::isPinned, Comparator.reverseOrder())
                .thenComparing(Comment::getLikeCount, Comparator.reverseOrder())
                .thenComparing(Comment::getDislikeCount, Comparator.reverseOrder())
        );
    }


    @SuppressWarnings("unused")
    private void sortByCreatedTime(@NonNull final List<Comment> newListComment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newListComment.sort(Comparator
                    .comparing(Comment::isPinned, Comparator.reverseOrder())
                    .thenComparing(comment -> Instant.parse(comment.getCreatedAt()), Comparator.reverseOrder())
            );
        } else {
            newListComment.sort(
                    Comparator.comparing(Comment::isPinned, Comparator.reverseOrder())
                            .thenComparing(c -> FormatUtils.parseISO8601Date(c.getCreatedAt()), Comparator.reverseOrder())
            );
        }
    }

    public void setItemList(final List<Comment> newListComment) {
        if (mCommentList == null) {
            sortByLikeCount(newListComment);
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
            sortByLikeCount(newListComment);
            mCommentList = newListComment;
            result.dispatchUpdatesTo(this);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isReplyCmtList) {
            return TYPE_ITEM;
        }
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        if (isReplyCmtList) {
            return mCommentList.get(position).getId().hashCode();
        }
        if (getItemViewType(position) == TYPE_HEADER) {
            return Long.MIN_VALUE;
        }
        return mCommentList.get(position - 1).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        if (mCommentList != null) {
            if (isReplyCmtList) {
                return mCommentList.size();
            }
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
        if (isReplyCmtList) {
            ViewHolderData commentViewHolder = (ViewHolderData) holder;
            Comment comment = mCommentList.get(position);
            commentViewHolder.bind(context, comment, position);
        } else {
            if (holder instanceof ViewHolderData) {
                ViewHolderData commentViewHolder = (ViewHolderData) holder;
                Comment comment = mCommentList.get(position - 1);
                commentViewHolder.bind(context, comment, position);
            } else {
                ViewHolderTerm viewHolderTerm = (ViewHolderTerm) holder;
                //viewHolderTerm.tvTerm.setHighlightColor(ThemeUtils.getColor(context, R.color.blue_100));
                viewHolderTerm.tvTerm.setText(spannableStringTerm);
                viewHolderTerm.tvTerm.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    public static class ViewHolderTerm extends RecyclerView.ViewHolder {
        private final TextView tvTerm;

        public ViewHolderTerm(@NonNull View itemView) {
            super(itemView);

            tvTerm = itemView.findViewById(R.id.tv_term);
        }
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        //        private final ConstraintLayout userParentLikeContainer;
        private final ShapeableImageView imgUserCmt, imgUserReplyCmt;
        private final MaterialButton btnMore, btnLike, btnDislike, btnReply;
        private final TextView tvContent;
        private final TextView tvPin, tvUsername, tvLikeCount, tvDislikeCount, tvReplyCount;
        private final ImageView imgUserLikeCmt, imgHeart;
        private final MaterialCardView topCommentContainer, bottomCommentContainer;

        public ViewHolderData(@NonNull View itemView) {
            super(itemView);

            imgUserCmt = itemView.findViewById(R.id.img_user_cmt);
            imgUserReplyCmt = itemView.findViewById(R.id.img_user_reply_cmt);
            tvPin = itemView.findViewById(R.id.tv_pin_cmt);
            tvUsername = itemView.findViewById(R.id.tv_username_cmt);
            tvContent = itemView.findViewById(R.id.tv_content_cmt);
            btnLike = itemView.findViewById(R.id.btn_like_cmt);
            btnDislike = itemView.findViewById(R.id.btn_dislike_cmt);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count_cmt);
            tvDislikeCount = itemView.findViewById(R.id.tv_dislike_count_cmt);
            btnReply = itemView.findViewById(R.id.btn_reply_cmt);
            tvReplyCount = itemView.findViewById(R.id.tv_reply_count_cmt);
            btnMore = itemView.findViewById(R.id.btn_more_cmt);
            imgUserLikeCmt = itemView.findViewById(R.id.img_user_like_cmt);
            imgHeart = itemView.findViewById(R.id.img_heart);
            topCommentContainer = itemView.findViewById(R.id.top_comment_container);
            bottomCommentContainer = itemView.findViewById(R.id.bottom_comment_container);
        }

        private void bind(Context context, @NonNull Comment comment, int position) {
//            loadImage(commentViewHolder.imgUserCmt, "1");
            if (!isReplyCmtList) {
                if (comment.isPinned()) {
                    tvPin.setVisibility(View.VISIBLE);
                    tvPin.setText(String.format(context.getString(R.string.pinned), "anonymousanonymousanonymousanonymousanonymous"));
                } else {
                    tvPin.setVisibility(View.GONE);
                }
            }

            StringBuilder statusStringBuilder = new StringBuilder();
            statusStringBuilder.append(context.getString(R.string.username_cmt, "anonymousanonymousanonymousanonymousanonymous"));
            statusStringBuilder.append(" • ").append(FormatUtils.compareTime(context, comment.getCreatedAt(), false));
            if (!comment.getUpdatedAt().isEmpty()) {
                statusStringBuilder.append("(").append(context.getString(R.string.edited)).append(")");
                statusStringBuilder.append(" • ").append(FormatUtils.compareTime(context, comment.getUpdatedAt(), false));
            }
            tvUsername.setText(statusStringBuilder.toString());

            String content = comment.getContent();
            if (comment.isExpanded()) {
                tvContent.setText(content);
                tvContent.setMaxLines(Integer.MAX_VALUE);
            } else {
                truncateText(content, tvContent, tvContent.getContext(), () -> {
                    comment.setExpanded(true);
                    notifyItemChanged(position, "text");
                });
                tvContent.setMaxLines(4);
            }

            if (comment.getLikeCount() > 0) {
                tvLikeCount.setText(FormatUtils.formatCount(context, comment.getLikeCount()));
            }
            if (comment.isShowDisLikeCount && comment.getDislikeCount() > 0) {
                tvDislikeCount.setText(FormatUtils.formatCount(context, comment.getDislikeCount()));
            }
            if (comment.isParentLike()) {
                imgUserLikeCmt.setVisibility(View.VISIBLE);
                imgHeart.setVisibility(View.VISIBLE);
//                loadImage(imgUserLikeCmt, "");
            } else {
                imgUserLikeCmt.setVisibility(View.INVISIBLE);
                imgHeart.setVisibility(View.INVISIBLE);
            }
            if (!isReplyCmtList) {
                if (comment.getReplyCount() > 0) {
                    bottomCommentContainer.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder();
                    if (comment.isParentCmt()) {
                        imgUserReplyCmt.setVisibility(View.VISIBLE);
                        sb.append(" • ");
                    } else {
                        imgUserReplyCmt.setVisibility(View.GONE);
                    }
                    sb.append(context.getString(R.string.replies, comment.getReplyCount()));
                    tvReplyCount.setVisibility(View.VISIBLE);
                    tvReplyCount.setText(sb.toString());
                } else {
                    bottomCommentContainer.setVisibility(View.GONE);
                    imgUserReplyCmt.setVisibility(View.GONE);
                    tvReplyCount.setVisibility(View.GONE);
                }
            }

            topCommentContainer.setOnClickListener(view -> goToReplyCmt(context, comment, false));
            btnLike.setOnClickListener(view -> {
            });
            btnDislike.setOnClickListener(view -> {
            });
            btnReply.setOnClickListener(view -> goToReplyCmt(context, comment, true));
            bottomCommentContainer.setOnClickListener(view -> goToReplyCmt(context, comment, false));
            btnMore.setOnClickListener(view -> {
            });
        }

        private void goToReplyCmt(Context context, Comment comment, boolean isFocusEditText) {
            if (!isReplyCmtList) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.showReplyCmt(comment, isFocusEditText);
            }
        }

        private void loadImage(Context context, ImageView imageView, String imgUrl) {
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_account_circle_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(350))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .timeout(5000)
                    .into(imageView);
        }

        private void truncateText(String fullText, @NonNull TextView textView, @NonNull Context context, Runnable onViewMoreClick) {
            String viewMoreText = context.getString(R.string.content_description_more);
            String ellipsis = "... " + viewMoreText;

            textView.setText(fullText);
            textView.setMaxLines(MAX_LINES_COMMENT);

            textView.post(() -> {
                if (textView.getLayout() != null && textView.getLayout().getLineCount() > MAX_LINES_COMMENT) {
                    int lineEndIndex = textView.getLayout().getLineEnd(MAX_LINES_COMMENT - 1);

                    String truncated = fullText.substring(0, lineEndIndex).trim();

                    while (textView.getPaint().measureText(truncated + ellipsis) > textView.getWidth()) {
                        truncated = truncated.substring(0, truncated.length() - 1).trim();
                    }

                    SpannableString spannableString = getSpannableString(onViewMoreClick, truncated, ellipsis);

                    textView.setText(spannableString);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    textView.setText(fullText);
                }
            });
        }

        @NonNull
        private SpannableString getSpannableString(Runnable onViewMoreClick, @NonNull String truncated, String ellipsis) {
            SpannableString spannableString = new SpannableString(truncated + ellipsis);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (onViewMoreClick != null) {
                        onViewMoreClick.run();
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.GRAY);
                    ds.setUnderlineText(false);
                }
            };

            int start = truncated.length() + 4;
            spannableString.setSpan(clickableSpan, start, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }
}

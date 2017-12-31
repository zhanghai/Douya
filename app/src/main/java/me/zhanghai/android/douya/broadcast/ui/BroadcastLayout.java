/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.DeleteBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.LikeBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.BroadcastAttachment;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.ui.CardIconButton;
import me.zhanghai.android.douya.ui.DividerItemDecoration;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.ui.ImageLayout;
import me.zhanghai.android.douya.ui.OnHorizontalScrollListener;
import me.zhanghai.android.douya.ui.SizedImageItem;
import me.zhanghai.android.douya.ui.TimeTextView;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * A LinearLayout that can display a broadcast.
 *
 * <p>Note that this layout tries to avoid the glitch if the same broadcast is bound again by
 * leaving attachment and text unchanged (since they cannot change once a broadcast is created).</p>
 */
public class BroadcastLayout extends LinearLayout {

    @BindView(R.id.author_time_action_layout)
    ViewGroup mAuthorTimeActionLayout;
    @BindView(R.id.avatar)
    ImageView mAvatarImage;
    @BindView(R.id.name)
    TextView mNameText;
    @BindView(R.id.time)
    TimeTextView mTimeText;
    @BindView(R.id.action)
    TextView mActionText;
    @BindView(R.id.text)
    TextView mTextText;
    @BindView(R.id.rebroadcasted_attachment_images_layout)
    ViewGroup mRebroadcastedAttachmentImagesLayout;
    @BindView(R.id.rebroadcasted_layout)
    ViewGroup mRebroadcastedLayout;
    @BindView(R.id.rebroadcasted_name)
    TextView mRebroadcastedNameText;
    @BindView(R.id.rebroadcasted_action)
    TextView mRebroadcastedActionText;
    @BindView(R.id.rebroadcasted_text)
    TextView mRebroadcastedTextText;
    @BindView(R.id.rebroadcasted_broadcast_deleted)
    TextView mRebroadcastedBroadcastDeletedText;
    @BindView(R.id.attachment)
    RelativeLayout mAttachmentLayout;
    @BindView(R.id.attachment_image)
    ImageView mAttachmentImage;
    @BindView(R.id.attachment_title)
    TextView mAttachmentTitleText;
    @BindView(R.id.attachment_description)
    TextView mAttachmentDescriptionText;
    @BindView(R.id.single_image)
    ImageLayout mSingleImageLayout;
    @BindView(R.id.image_list_layout)
    FrameLayout mImageListLayout;
    @BindView(R.id.image_list_description_layout)
    FrameLayout mImageListDescriptionLayout;
    @BindView(R.id.image_list_description)
    TextView mImageListDescriptionText;
    @BindView(R.id.image_list)
    RecyclerView mImageList;
    @BindView(R.id.rebroadcasted_attachment_images_space)
    Space mRebroadcastedAttachmentImagesSpace;
    @BindView(R.id.actions)
    ViewGroup mActionsLayout;
    @BindView(R.id.like)
    CardIconButton mLikeButton;
    @BindView(R.id.comment)
    CardIconButton mCommentButton;
    @BindView(R.id.rebroadcast)
    CardIconButton mRebroadcastButton;

    private Listener mListener;

    private Long mBoundBroadcastId;
    private Boolean mBoundBroadcastHadParentBroadcast;
    private Boolean mBoundBroadcastRebroadcastedBroadcastWasDeleted;

    private HorizontalImageAdapter mImageListAdapter;

    public BroadcastLayout(Context context) {
        super(context);

        init();
    }

    public BroadcastLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public BroadcastLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BroadcastLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        setOrientation(VERTICAL);

        ViewUtils.inflateInto(R.layout.broadcast_layout, this);
        ButterKnife.bind(this);

        ViewCompat.setBackground(mImageListDescriptionLayout, DrawableUtils.makeScrimDrawable());
        mImageList.setHasFixedSize(true);
        mImageList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mImageList.addItemDecoration(new DividerItemDecoration(DividerItemDecoration.HORIZONTAL,
                R.drawable.transparent_divider_vertical_2dp, getContext()));
        mImageListAdapter = new HorizontalImageAdapter();
        mImageList.setAdapter(mImageListAdapter);
        mImageList.addOnScrollListener(new OnHorizontalScrollListener() {
            private boolean mShowingDescription = true;
            @Override
            public void onScrolledLeft() {
                if (!mShowingDescription) {
                    mShowingDescription = true;
                    ViewUtils.fadeIn(mImageListDescriptionLayout);
                }
            }
            @Override
            public void onScrolledRight() {
                if (mShowingDescription) {
                    mShowingDescription = false;
                    ViewUtils.fadeOut(mImageListDescriptionLayout);
                }
            }
        });

        ViewUtils.setTextViewLinkClickable(mTextText);
        ViewUtils.setTextViewLinkClickable(mRebroadcastedTextText);

        CheatSheetUtils.setup(mLikeButton);
        CheatSheetUtils.setup(mCommentButton);
        CheatSheetUtils.setup(mRebroadcastButton);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void bind(Broadcast broadcast, Broadcast rebroadcastedBroadcast,
                      boolean isSimpleRebroadcastByOneself, boolean isUnrebroadcasting) {

        Context context = getContext();

        // TODO: Integrate API V2.
//        if (broadcast.isInterest) {
//            mAvatarImage.setImageDrawable(ContextCompat.getDrawable(context,
//                    R.drawable.recommendation_avatar_icon_40dp));
//            mAvatarImage.setOnClickListener(view -> UriHandler.open(DoubanUtils.getInterestTypeUrl(
//                    broadcast.interestType), context));
//        } else {
        ImageUtils.loadAvatar(mAvatarImage, broadcast.author.avatar);
        mAvatarImage.setOnClickListener(view -> context.startActivity(ProfileActivity.makeIntent(
                broadcast.author, context)));
        //mNameText.setText(broadcast.getAuthorName());
        mNameText.setText(broadcast.author.name);
        mTimeText.setDoubanTime(broadcast.createdAt);
        mActionText.setText(broadcast.action);

        boolean isRebind = ObjectsCompat.equals(mBoundBroadcastId, broadcast.id);
        // HACK: Attachment and text should not change on rebind.
        boolean hasParentBroadcast = broadcast.parentBroadcast != null;
        if (!(isRebind && ObjectsCompat.equals(mBoundBroadcastHadParentBroadcast,
                hasParentBroadcast))) {
            mBoundBroadcastHadParentBroadcast = hasParentBroadcast;
            mTextText.setText(broadcast.getTextWithEntities(mTextText.getContext()));
        }
        boolean hasRebroadcastedBroadcast = rebroadcastedBroadcast != null;
        if (!(isRebind && (!hasRebroadcastedBroadcast || ObjectsCompat.equals(
                mBoundBroadcastRebroadcastedBroadcastWasDeleted,
                rebroadcastedBroadcast.isDeleted)))) {
            if (hasRebroadcastedBroadcast) {
                mBoundBroadcastRebroadcastedBroadcastWasDeleted = rebroadcastedBroadcast.isDeleted;
            }
            bindRebroadcastedAttachmentImages(broadcast, rebroadcastedBroadcast);
        } else if (hasRebroadcastedBroadcast) {
            // In case the broadcast has changed (e.g. likeCount).
            setRebroadcastedAttachmentImagesLayoutOnClickListener(rebroadcastedBroadcast);
        }

        mLikeButton.setText(broadcast.getLikeCountString());
        LikeBroadcastManager likeBroadcastManager = LikeBroadcastManager.getInstance();
        if (likeBroadcastManager.isWriting(broadcast.id)) {
            mLikeButton.setActivated(likeBroadcastManager.isWritingLike(broadcast.id));
            mLikeButton.setEnabled(false);
        } else {
            mLikeButton.setActivated(broadcast.isLiked);
            mLikeButton.setEnabled(true);
        }
        mLikeButton.setOnClickListener(view -> {
            if (mListener == null) {
                return;
            }
            mListener.onLikeClicked();
        });
        mRebroadcastButton.setText(broadcast.getRebroadcastCountString());
        if (isSimpleRebroadcastByOneself) {
            mRebroadcastButton.setActivated(!isUnrebroadcasting);
            mRebroadcastButton.setEnabled(!isUnrebroadcasting);
        } else {
            boolean isWritingQuickRebroadcast = RebroadcastBroadcastManager.getInstance()
                    .isWritingQuickRebroadcast(broadcast.id);
            mRebroadcastButton.setActivated(isWritingQuickRebroadcast);
            mRebroadcastButton.setEnabled(!isWritingQuickRebroadcast);
        }
        mRebroadcastButton.setOnClickListener(view -> {
            if (mListener == null) {
                return;
            }
            mListener.onRebroadcastClicked(false);
        });
        mRebroadcastButton.setOnLongClickListener(view -> {
            if (mListener == null) {
                return false;
            }
            mListener.onRebroadcastClicked(true);
            return true;
        });
        mCommentButton.setText(broadcast.getCommentCountString());
        mCommentButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onCommentClicked();
            }
        });

        mBoundBroadcastId = broadcast.id;
    }

    private void bindRebroadcastedAttachmentImages(Broadcast broadcast,
                                                   Broadcast rebroadcastedBroadcast) {
        boolean hasRebroadcastedBroadcast = rebroadcastedBroadcast != null;
        ViewUtils.setVisibleOrGone(mRebroadcastedLayout, hasRebroadcastedBroadcast);
        if (hasRebroadcastedBroadcast) {
            ViewUtils.setVisibleOrGone(mRebroadcastedBroadcastDeletedText,
                    rebroadcastedBroadcast.isDeleted);
            if (rebroadcastedBroadcast.isDeleted) {
                mRebroadcastedAttachmentImagesLayout.setOnClickListener(null);
                mRebroadcastedNameText.setText(null);
                mRebroadcastedActionText.setText(null);
                mRebroadcastedTextText.setText(null);
            } else {
                setRebroadcastedAttachmentImagesLayoutOnClickListener(rebroadcastedBroadcast);
                mRebroadcastedNameText.setText(rebroadcastedBroadcast.author.name);
                mRebroadcastedActionText.setText(rebroadcastedBroadcast.action);
                mRebroadcastedTextText.setText(rebroadcastedBroadcast.getTextWithEntities(
                        mRebroadcastedTextText.getContext()));
            }
        } else {
            mRebroadcastedAttachmentImagesLayout.setOnClickListener(null);
        }

        Broadcast contentBroadcast = hasRebroadcastedBroadcast ? rebroadcastedBroadcast
                : broadcast;
        BroadcastAttachment attachment = contentBroadcast.attachment;
        List<? extends SizedImageItem> images = contentBroadcast.attachment != null
                && contentBroadcast.attachment.imageBlock != null ?
                contentBroadcast.attachment.imageBlock.images : contentBroadcast.images;

        if (attachment != null) {
            mAttachmentLayout.setVisibility(VISIBLE);
            mAttachmentTitleText.setText(attachment.title);
            mAttachmentDescriptionText.setText(attachment.text);
            if (attachment.image != null && images.isEmpty()) {
                mAttachmentImage.setVisibility(VISIBLE);
                ImageUtils.loadImage(mAttachmentImage, attachment.image);
            } else {
                mAttachmentImage.setVisibility(GONE);
            }
            String attachmentUrl = attachment.url;
            if (!TextUtils.isEmpty(attachmentUrl)) {
                mAttachmentLayout.setOnClickListener(view -> UriHandler.open(attachmentUrl,
                        view.getContext()));
            } else {
                mAttachmentLayout.setOnClickListener(null);
            }
        } else {
            mAttachmentLayout.setVisibility(GONE);
        }

        int numImages = images.size();
        if (numImages == 1) {
            SizedImageItem image = images.get(0);
            mSingleImageLayout.setVisibility(VISIBLE);
            mSingleImageLayout.loadImage(image);
            mSingleImageLayout.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(GalleryActivity.makeIntent(image, context));
            });
        } else {
            mSingleImageLayout.setVisibility(GONE);
        }
        if (numImages > 1) {
            mImageListLayout.setVisibility(VISIBLE);
            mImageListDescriptionText.setText(mImageListDescriptionText.getContext().getString(
                    R.string.broadcast_image_list_count_format, numImages));
            mImageListAdapter.replace(images);
            mImageListAdapter.setOnItemClickListener((parent, itemView, item, position) -> {
                Context context = itemView.getContext();
                context.startActivity(GalleryActivity.makeIntent(images, position, context));
            });
        } else {
            mImageListLayout.setVisibility(GONE);
        }

        boolean rebroadecastedAttachmentImagesVisible = hasRebroadcastedBroadcast
                || attachment != null || !images.isEmpty();
        ViewUtils.setVisibleOrGone(mRebroadcastedAttachmentImagesLayout,
                rebroadecastedAttachmentImagesVisible);
        ViewUtils.setVisibleOrGone(mRebroadcastedAttachmentImagesSpace,
                rebroadecastedAttachmentImagesVisible);
    }

    private void setRebroadcastedAttachmentImagesLayoutOnClickListener(
            Broadcast rebroadcastedBroadcast) {
        mRebroadcastedAttachmentImagesLayout.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(BroadcastActivity.makeIntent(rebroadcastedBroadcast,
                    context));
        });
    }

    public void bind(Broadcast broadcast) {
        if (broadcast.isSimpleRebroadcast()) {
            boolean isSimpleRebroadcastByOneself = broadcast.isSimpleRebroadcastByOneself();
            boolean isUnrebroadcasting = isSimpleRebroadcastByOneself &&
                    DeleteBroadcastManager.getInstance().isWriting(broadcast.id);
            if (broadcast.parentBroadcast != null) {
                bind(broadcast.parentBroadcast, broadcast.rebroadcastedBroadcast,
                        isSimpleRebroadcastByOneself, isUnrebroadcasting);
            } else {
                bind(broadcast.rebroadcastedBroadcast,
                        broadcast.rebroadcastedBroadcast.rebroadcastedBroadcast,
                        isSimpleRebroadcastByOneself, isUnrebroadcasting);
            }
        } else {
            bind(broadcast, broadcast.rebroadcastedBroadcast, false, false);
        }
    }

    public void unbind() {
        mAvatarImage.setImageDrawable(null);
        mAttachmentImage.setImageDrawable(null);
        mSingleImageLayout.releaseImage();
        mImageListAdapter.clear();
        mBoundBroadcastId = null;
    }

    public void bindForRebroadcast(Broadcast broadcast) {
        ViewUtils.setVisibleOrGone(mAuthorTimeActionLayout, false);
        if (broadcast.isSimpleRebroadcast()) {
            mTextText.setText(broadcast.parentBroadcast != null ?
                    broadcast.parentBroadcast.getTextWithEntitiesAsParent(mTextText.getContext())
                    : null);
        } else {
            mTextText.setText(broadcast.rebroadcastedBroadcast != null ?
                    broadcast.getTextWithEntitiesAsParent(mTextText.getContext()) : null);
        }
        bindRebroadcastedAttachmentImages(null, broadcast.rebroadcastedBroadcast != null ?
                broadcast.rebroadcastedBroadcast : broadcast);
        ViewUtils.setVisibleOrGone(mRebroadcastedAttachmentImagesSpace, false);
        ViewUtils.setVisibleOrGone(mActionsLayout, false);
    }

    public interface Listener {
        void onLikeClicked();
        void onRebroadcastClicked(boolean isLongClick);
        void onCommentClicked();
    }
}

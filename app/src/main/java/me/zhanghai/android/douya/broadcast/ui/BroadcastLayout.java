/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
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
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.ui.CardIconButton;
import me.zhanghai.android.douya.ui.DividerItemDecoration;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.ui.ImageLayout;
import me.zhanghai.android.douya.ui.OnHorizontalScrollListener;
import me.zhanghai.android.douya.ui.SizedImageItem;
import me.zhanghai.android.douya.ui.TimeTextView;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.TooltipUtils;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * A LinearLayout that can display a broadcast.
 *
 * <p>Note that this layout tries to avoid the glitch if the same broadcast is bound again by
 * leaving attachment and text unchanged (since they cannot change once a broadcast is created).</p>
 */
public class BroadcastLayout extends LinearLayout {

    @BindView(R.id.broadcastlayout_author_time_action_layout)
    ViewGroup mAuthorTimeActionLayout;
    @BindView(R.id.broadcastlayout_avatar)
    ImageView mAvatarImage;
    @BindView(R.id.broadcastlayout_name)
    TextView mNameText;
    @BindView(R.id.broadcastlayout_time)
    TimeTextView mTimeText;
    @BindView(R.id.broadcastlayout_time_action_space)
    View mTimeActionSpace;
    @BindView(R.id.broadcastlayout_action)
    TextView mActionText;
    @BindView(R.id.broadcastlayout_text)
    TextView mTextText;
    @BindView(R.id.broadcastlayout_rebroadcasted_attachment_images_layout)
    ViewGroup mRebroadcastedAttachmentImagesLayout;
    @BindView(R.id.broadcastlayout_rebroadcasted_layout)
    ViewGroup mRebroadcastedLayout;
    @BindView(R.id.broadcastlayout_rebroadcasted_name)
    TextView mRebroadcastedNameText;
    @BindView(R.id.broadcastlayout_rebroadcasted_action)
    TextView mRebroadcastedActionText;
    @BindView(R.id.broadcastlayout_rebroadcasted_text)
    TextView mRebroadcastedTextText;
    @BindView(R.id.broadcastlayout_rebroadcasted_broadcast_deleted)
    TextView mRebroadcastedBroadcastDeletedText;
    @BindView(R.id.broadcastlayout_attachment)
    RelativeLayout mAttachmentLayout;
    @BindView(R.id.broadcastlayout_attachment_image)
    ImageView mAttachmentImage;
    @BindView(R.id.broadcastlayout_attachment_title)
    TextView mAttachmentTitleText;
    @BindView(R.id.broadcastlayout_attachment_description)
    TextView mAttachmentDescriptionText;
    @BindView(R.id.broadcastlayout_single_image)
    ImageLayout mSingleImageLayout;
    @BindView(R.id.broadcastlayout_image_list_layout)
    FrameLayout mImageListLayout;
    @BindView(R.id.broadcastlayout_image_list_description_layout)
    FrameLayout mImageListDescriptionLayout;
    @BindView(R.id.broadcastlayout_image_list_description)
    TextView mImageListDescriptionText;
    @BindView(R.id.broadcastlayout_image_list)
    RecyclerView mImageList;
    @BindView(R.id.broadcastlayout_rebroadcasted_attachment_images_space)
    Space mRebroadcastedAttachmentImagesSpace;
    @BindView(R.id.broadcastlayout_actions)
    ViewGroup mActionsLayout;
    @BindView(R.id.broadcastlayout_like)
    CardIconButton mLikeButton;
    @BindView(R.id.broadcastlayout_comment)
    CardIconButton mCommentButton;
    @BindView(R.id.broadcastlayout_rebroadcast)
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

        TooltipUtils.setup(mLikeButton);
        TooltipUtils.setup(mCommentButton);
        // Handled by the OnLongClickListener set in bind().
        //TooltipUtils.setup(mRebroadcastButton);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void bind(Broadcast broadcast, Broadcast rebroadcastedBroadcast,
                      boolean isSimpleRebroadcastByOneself, boolean isUnrebroadcasting) {

        Context context = getContext();

        ImageUtils.loadAvatar(mAvatarImage, broadcast.author.avatar);
        mAvatarImage.setOnClickListener(view -> context.startActivity(ProfileActivity.makeIntent(
                broadcast.author, context)));
        mNameText.setText(broadcast.author.name);
        boolean hasTime = !TextUtils.isEmpty(broadcast.createTime);
        ViewUtils.setVisibleOrGone(mTimeText, hasTime);
        if (hasTime) {
            mTimeText.setDoubanTime(broadcast.createTime);
        }
        ViewUtils.setVisibleOrGone(mTimeActionSpace, hasTime);
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
        TooltipUtils.setup(mRebroadcastButton);
        View.OnLongClickListener rebroadcastTooltipListener =
                mRebroadcastButton.getOnLongClickListener();
        mRebroadcastButton.setOnLongClickListener(view -> {
            if (mListener == null || !Settings.LONG_CLICK_TO_QUICK_REBROADCAST.getValue()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    return rebroadcastTooltipListener.onLongClick(view);
                } else {
                    return true;
                }
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
            setRebroadcastedAttachmentImagesLayoutOnClickListener(rebroadcastedBroadcast);
            ViewUtils.setVisibleOrGone(mRebroadcastedBroadcastDeletedText,
                    rebroadcastedBroadcast.isDeleted);
            if (rebroadcastedBroadcast.isDeleted) {
                mRebroadcastedNameText.setText(null);
                mRebroadcastedActionText.setText(null);
                mRebroadcastedTextText.setText(null);
            } else {
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
                && contentBroadcast.attachment.imageList != null ?
                contentBroadcast.attachment.imageList.images : contentBroadcast.images;

        if (attachment != null) {
            mAttachmentLayout.setVisibility(VISIBLE);
            mAttachmentTitleText.setText(attachment.title);
            CharSequence attachmentDescription = attachment.getTextWithEntities();
            if (TextUtils.isEmpty(attachmentDescription) && images.isEmpty()) {
                attachmentDescription = attachment.url;
            }
            mAttachmentDescriptionText.setText(attachmentDescription);
            boolean hasAttachmentImage = attachment.image != null && images.isEmpty();
            ViewUtils.setVisibleOrGone(mAttachmentImage, hasAttachmentImage);
            if (hasAttachmentImage) {
                ImageUtils.loadImage(mAttachmentImage, attachment.image);
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

        boolean hasSingleImage = images.size() == 1;
        ViewUtils.setVisibleOrGone(mSingleImageLayout, hasSingleImage);
        if (hasSingleImage) {
            SizedImageItem image = images.get(0);
            mSingleImageLayout.loadImage(image);
            mSingleImageLayout.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(GalleryActivity.makeIntent(image, context));
            });
        }
        boolean hasImageList = images.size() > 1;
        ViewUtils.setVisibleOrGone(mImageListLayout, hasImageList);
        if (hasImageList) {
            mImageListDescriptionText.setText(mImageListDescriptionText.getContext().getString(
                    R.string.broadcast_image_list_count_format, images.size()));
            mImageListAdapter.replace(images);
            mImageListAdapter.setOnItemClickListener((parent, itemView, item, position) -> {
                Context context = itemView.getContext();
                context.startActivity(GalleryActivity.makeImageListIntent(images, position,
                        context));
            });
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
        if (rebroadcastedBroadcast.isDeleted) {
            mRebroadcastedAttachmentImagesLayout.setOnClickListener(null);
        } else {
            mRebroadcastedAttachmentImagesLayout.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(BroadcastActivity.makeIntent(rebroadcastedBroadcast,
                        context));
            });
        }
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

    public void setTextSelectable() {
        ViewUtils.setTextViewLinkClickableAndTextSelectable(mTextText);
    }

    public interface Listener {
        void onLikeClicked();
        void onRebroadcastClicked(boolean isLongClick);
        void onCommentClicked();
    }
}

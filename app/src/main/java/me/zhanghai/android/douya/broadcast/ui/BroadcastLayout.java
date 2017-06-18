/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.LikeBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.apiv2.Attachment;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.Image;
import me.zhanghai.android.douya.network.api.info.apiv2.Photo;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.ui.CardIconButton;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.ui.ImageLayout;
import me.zhanghai.android.douya.ui.OnHorizontalScrollListener;
import me.zhanghai.android.douya.ui.TimeActionTextView;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.DoubanUtils;
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

    @BindView(R.id.avatar)
    ImageView mAvatarImage;
    @BindView(R.id.name)
    TextView mNameText;
    @BindView(R.id.time_action)
    TimeActionTextView mTimeActionText;
    @BindView(R.id.text_space)
    Space mTextSpace;
    @BindView(R.id.text)
    TextView mTextText;
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
    @BindView(R.id.like)
    CardIconButton mLikeButton;
    @BindView(R.id.comment)
    CardIconButton mCommentButton;
    @BindView(R.id.rebroadcast)
    CardIconButton mRebroadcastButton;

    private Listener mListener;

    private Long mBoundBroadcastId;

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

        CheatSheetUtils.setup(mLikeButton);
        CheatSheetUtils.setup(mCommentButton);
        CheatSheetUtils.setup(mRebroadcastButton);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bindBroadcast(final Broadcast broadcast) {

        final Context context = getContext();

        if (broadcast.isInterest) {
            mAvatarImage.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.recommendation_avatar_icon_40dp));
            mAvatarImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    UriHandler.open(DoubanUtils.getInterestTypeUrl(broadcast.interestType),
                            context);
                }
            });
        } else {
            ImageUtils.loadAvatar(mAvatarImage, broadcast.author.avatar);
            mAvatarImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(ProfileActivity.makeIntent(broadcast.author, context));
                }
            });
        }
        mNameText.setText(broadcast.getAuthorName());
        mTimeActionText.setDoubanTimeAndAction(broadcast.createdAt, broadcast.action);

        boolean isRebind = mBoundBroadcastId != null && mBoundBroadcastId == broadcast.id;
        // HACK: Attachment and text should not change on rebind.
        if (!isRebind) {

            mTextText.setText(broadcast.getTextWithEntities(context));

            Attachment attachment = broadcast.attachment;
            if (attachment != null) {
                mAttachmentLayout.setVisibility(VISIBLE);
                mAttachmentTitleText.setText(attachment.title);
                mAttachmentDescriptionText.setText(attachment.description);
                if (!TextUtils.isEmpty(attachment.image)) {
                    mAttachmentImage.setVisibility(VISIBLE);
                    ImageUtils.loadImage(mAttachmentImage, attachment.image);
                } else {
                    mAttachmentImage.setVisibility(GONE);
                }
                final String attachmentUrl = attachment.href;
                if (!TextUtils.isEmpty(attachmentUrl)) {
                    mAttachmentLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UriHandler.open(attachmentUrl, context);
                        }
                    });
                } else {
                    mAttachmentLayout.setOnClickListener(null);
                }
            } else {
                mAttachmentLayout.setVisibility(GONE);
            }

            final ArrayList<Image> images = broadcast.images.size() > 0 ? broadcast.images
                    : Photo.toImageList(broadcast.photos);
            int numImages = images.size();
            if (numImages == 1) {
                final Image image = images.get(0);
                mSingleImageLayout.setVisibility(VISIBLE);
                mSingleImageLayout.loadImage(image);
                mSingleImageLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(GalleryActivity.makeIntent(image, context));
                    }
                });
            } else {
                mSingleImageLayout.setVisibility(GONE);
            }
            if (numImages > 1) {
                mImageListLayout.setVisibility(VISIBLE);
                mImageListDescriptionText.setText(context.getString(
                        R.string.broadcast_image_list_count_format, numImages));
                mImageListAdapter.replace(images);
                mImageListAdapter.setOnImageClickListener(
                        new HorizontalImageAdapter.OnImageClickListener() {
                            @Override
                            public void onImageClick(int position) {
                                context.startActivity(GalleryActivity.makeImageListIntent(images,
                                        position, context));
                            }
                        });
            } else {
                mImageListLayout.setVisibility(GONE);
            }

            boolean textSpaceVisible = (attachment != null || numImages > 0)
                    && !TextUtils.isEmpty(broadcast.text);
            ViewUtils.setVisibleOrGone(mTextSpace, textSpaceVisible);
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
        mLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onLikeClicked();
                }
            }
        });
        RebroadcastBroadcastManager rebroadcastBroadcastManager =
                RebroadcastBroadcastManager.getInstance();
        if (rebroadcastBroadcastManager.isWriting(broadcast.id)) {
            mRebroadcastButton.setActivated(rebroadcastBroadcastManager.isWritingRebroadcast(
                    broadcast.id));
            mRebroadcastButton.setEnabled(false);
        } else {
            mRebroadcastButton.setActivated(broadcast.isRebroadcasted());
            mRebroadcastButton.setEnabled(true);
        }
        mRebroadcastButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onRebroadcastClicked();
                }
            }
        });
        mCommentButton.setText(broadcast.getCommentCountString());
        mCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCommentClicked();
                }
            }
        });

        mBoundBroadcastId = broadcast.id;
    }

    public void releaseBroadcast() {
        mAvatarImage.setImageDrawable(null);
        mAttachmentImage.setImageDrawable(null);
        mSingleImageLayout.releaseImage();
        mImageListAdapter.clear();
        mBoundBroadcastId = null;
    }

    public interface Listener {
        void onLikeClicked();
        void onRebroadcastClicked();
        void onCommentClicked();
    }
}

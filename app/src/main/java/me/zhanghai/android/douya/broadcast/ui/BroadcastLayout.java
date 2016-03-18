/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.Attachment;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.Image;
import me.zhanghai.android.douya.network.api.info.Photo;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.ui.CardIconButton;
import me.zhanghai.android.douya.ui.GalleryActivity;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.ui.ImageLayout;
import me.zhanghai.android.douya.ui.OnHorizontalScrollListener;
import me.zhanghai.android.douya.ui.RatioHeightRecyclerView;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.ui.TimeActionTextView;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewCompat;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * A LinearLayout that can display a broadcast.
 *
 * <p>Note that this layout tries to avoid the glitch if the same broadcast is bound again by
 * leaving attachment and text unchanged (since they cannot change once a broadcast is created).</p>
 */
public class BroadcastLayout extends LinearLayout {

    @Bind(R.id.avatar)
    public ImageView mAvatarImage;
    @Bind(R.id.name)
    public TextView mNameText;
    @Bind(R.id.time_action)
    public TimeActionTextView mTimeActionText;
    @Bind(R.id.attachment)
    public RelativeLayout mAttachmentLayout;
    @Bind(R.id.attachment_image)
    public ImageView mAttachmentImage;
    @Bind(R.id.attachment_title)
    public TextView mAttachmentTitleText;
    @Bind(R.id.attachment_description)
    public TextView mAttachmentDescriptionText;
    @Bind(R.id.single_image)
    public ImageLayout mSingleImageLayout;
    @Bind(R.id.image_list_layout)
    public FrameLayout mImageListLayout;
    @Bind(R.id.image_list_description_layout)
    public FrameLayout mImageListDescriptionLayout;
    @Bind(R.id.image_list_description)
    public TextView mImageListDescriptionText;
    @Bind(R.id.image_list)
    public RatioHeightRecyclerView mImageList;
    @Bind(R.id.text_space)
    public Space mTextSpace;
    @Bind(R.id.text)
    public TextView mTextText;
    @Bind(R.id.like)
    public CardIconButton mLikeButton;
    @Bind(R.id.comment)
    public CardIconButton mCommentButton;
    @Bind(R.id.rebroadcast)
    public CardIconButton mRebroadcastButton;

    private HorizontalImageAdapter mImageListAdapter;

    private Long mBoundBroadcastId;

    public BroadcastLayout(Context context) {
        super(context);

        init(getContext(), null, 0, 0);
    }

    public BroadcastLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(getContext(), attrs, 0, 0);
    }

    public BroadcastLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(getContext(), attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BroadcastLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(getContext(), attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        setOrientation(VERTICAL);

        inflate(context, R.layout.broadcast_layout, this);
        ButterKnife.bind(this);

        ViewCompat.setBackground(mImageListDescriptionLayout, DrawableUtils.makeScrimDrawable());
        mImageList.setHasFixedSize(true);
        mImageList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false));
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

    public void bindBroadcast(final Broadcast broadcast) {

        final Context context = getContext();

        ImageUtils.loadAvatar(mAvatarImage, broadcast.author.avatar, context);
        mAvatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(ProfileActivity.makeIntent(broadcast.author, context));
            }
        });
        mNameText.setText(broadcast.author.name);
        mTimeActionText.setDoubanTimeAndAction(broadcast.createdAt, broadcast.action);

        boolean isRebind = mBoundBroadcastId != null && mBoundBroadcastId == broadcast.id;
        // HACK: Attachment and text should not change on rebind.
        if (!isRebind) {

            Attachment attachment = broadcast.attachment;
            if (attachment != null) {
                mAttachmentLayout.setVisibility(View.VISIBLE);
                mAttachmentTitleText.setText(attachment.title);
                mAttachmentDescriptionText.setText(attachment.description);
                if (!TextUtils.isEmpty(attachment.image)) {
                    mAttachmentImage.setVisibility(View.VISIBLE);
                    ImageUtils.loadImage(mAttachmentImage, attachment.image, context);
                } else {
                    mAttachmentImage.setVisibility(View.GONE);
                }
                final String attachmentUrl = attachment.href;
                if (!TextUtils.isEmpty(attachmentUrl)) {
                    mAttachmentLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UriHandler.open(attachmentUrl, context);
                        }
                    });
                }
            } else {
                mAttachmentLayout.setVisibility(View.GONE);
            }

            final ArrayList<Image> images = broadcast.images.size() > 0 ? broadcast.images
                    : Photo.toImageList(broadcast.photos);
            int numImages = images.size();
            if (numImages == 1) {
                mSingleImageLayout.setVisibility(View.VISIBLE);
                mSingleImageLayout.loadImage(images.get(0));
                mSingleImageLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(GalleryActivity.makeIntent(images, 0, context));
                    }
                });
            } else {
                mSingleImageLayout.setVisibility(View.GONE);
            }
            if (numImages > 1) {
                mImageListLayout.setVisibility(View.VISIBLE);
                mImageListDescriptionText.setText(context.getString(
                        R.string.broadcast_image_list_count_format, numImages));
                mImageListAdapter.replace(images);
                mImageListAdapter.setOnImageClickListener(
                        new HorizontalImageAdapter.OnImageClickListener() {
                            @Override
                            public void onImageClick(int position) {
                                context.startActivity(GalleryActivity.makeIntent(images, position,
                                        context));
                            }
                        });
            } else {
                mImageListLayout.setVisibility(View.GONE);
            }

            boolean textSpaceVisible = (attachment != null || numImages > 0)
                    && !TextUtils.isEmpty(broadcast.text);
            ViewUtils.setVisibleOrGone(mTextSpace, textSpaceVisible);
            mTextText.setText(broadcast.getTextWithEntities(context));
        }

        mLikeButton.setText(broadcast.getLikeCountString());
        mLikeButton.setActivated(broadcast.liked);
        mLikeButton.setEnabled(true);
        mRebroadcastButton.setActivated(broadcast.isRebroadcasted());
        mRebroadcastButton.setEnabled(true);
        mCommentButton.setText(broadcast.getCommentCountString());

        mBoundBroadcastId = broadcast.id;
    }

    public void releaseBroadcast() {
        mAvatarImage.setImageDrawable(null);
        mAttachmentImage.setImageDrawable(null);
        mSingleImageLayout.releaseImage();
        mImageListAdapter.clear();
        mBoundBroadcastId = null;
    }
}

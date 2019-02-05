/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.SendBroadcastManager;
import me.zhanghai.android.douya.eventbus.BroadcastSendErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastSentEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.ui.ConfirmDiscardContentDialogFragment;
import me.zhanghai.android.douya.ui.CounterTextView;
import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.util.AppUtils;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FileUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.IntentUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.TooltipUtils;
import me.zhanghai.android.douya.util.ViewUtils;
import me.zhanghai.android.effortlesspermissions.AfterPermissionDenied;
import me.zhanghai.android.effortlesspermissions.EffortlessPermissions;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class SendBroadcastFragment extends Fragment
        implements ConfirmRemoveAllImagesDialogFragment.Listener, EditLinkDialogFragment.Listener,
        ConfirmRemoveLinkDialogFragment.Listener, ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = SendBroadcastFragment.class.getName() + '.';

    private static final String EXTRA_TEXT = KEY_PREFIX + "text";
    private static final String EXTRA_IMAGE_URIS = KEY_PREFIX + "image_uris";
    private static final String EXTRA_LINK_INFO = KEY_PREFIX + "link_info";

    private static final String STATE_IMAGE_URIS = KEY_PREFIX + "image_uris";
    private static final String STATE_LINK_INFO = KEY_PREFIX + "link_info";
    private static final String STATE_CHANGED = KEY_PREFIX + "changed";
    private static final String STATE_CAPTURE_IMAGE_OUTPUT_FILE = KEY_PREFIX +
            "capture_image_output_file";
    private static final String STATE_WRITER_ID = KEY_PREFIX + "writer_id";

    private static final int REQUEST_CODE_CAPTURE_IMAGE_PERMISSION = 1;
    private static final String[] PERMISSIONS_CAPTURE_IMAGE;
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSIONS_CAPTURE_IMAGE = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            // Never used.
            PERMISSIONS_CAPTURE_IMAGE = null;
        }
    }

    private static final int REQUEST_CODE_PICK_OR_CAPTURE_IMAGE = 2;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text)
    EditText mTextEdit;
    @BindView(R.id.attachment_layout)
    SendBroadcastAttachmentLayout mAttachmentLayout;
    @BindView(R.id.add_image)
    ImageButton mAddImageButton;
    @BindView(R.id.add_more_image)
    ImageButton mAddMoreImageButton;
    @BindView(R.id.remove_all_images)
    ImageButton mRemoveAllImagesButton;
    @BindView(R.id.add_link)
    ImageButton mAddLinkButton;
    @BindView(R.id.edit_link)
    ImageButton mEditLinkButton;
    @BindView(R.id.remove_link)
    ImageButton mRemoveLinkButton;
    @BindView(R.id.add_mention)
    ImageButton mAddMentionButton;
    @BindView(R.id.add_topic)
    ImageButton mAddTopicButton;
    @BindView(R.id.counter)
    CounterTextView mCounterText;

    private MenuItem mSendMenuItem;

    private String mExtraText;

    private ArrayList<Uri> mImageUris;
    private LinkInfo mLinkInfo;

    private boolean mChanged;

    private File mCaptureImageOutputFile;

    private long mWriterId;

    private boolean mSent;

    public static SendBroadcastFragment newInstance(String text, ArrayList<Uri> imageUris,
                                                    LinkInfo linkInfo) {
        //noinspection deprecation
        SendBroadcastFragment fragment = new SendBroadcastFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putString(EXTRA_TEXT, text)
                .putParcelableArrayList(EXTRA_IMAGE_URIS, imageUris)
                .putParcelable(EXTRA_LINK_INFO, linkInfo);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, ArrayList, LinkInfo)} instead.
     */
    public SendBroadcastFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mExtraText = arguments.getString(EXTRA_TEXT);

        if (savedInstanceState == null) {
            mImageUris = arguments.getParcelableArrayList(EXTRA_IMAGE_URIS);
            mLinkInfo = arguments.getParcelable(EXTRA_LINK_INFO);
        } else {
            mImageUris = savedInstanceState.getParcelableArrayList(STATE_IMAGE_URIS);
            mLinkInfo = savedInstanceState.getParcelable(STATE_LINK_INFO);
            mChanged = savedInstanceState.getBoolean(STATE_CHANGED);
            mCaptureImageOutputFile = (File) savedInstanceState.getSerializable(
                    STATE_CAPTURE_IMAGE_OUTPUT_FILE);
            mWriterId = savedInstanceState.getLong(STATE_WRITER_ID);
        }
        if (mImageUris == null) {
            mImageUris = new ArrayList<>();
        }

        setHasOptionsMenu(true);

        EventBusUtils.register(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(STATE_IMAGE_URIS, mImageUris);
        outState.putParcelable(STATE_LINK_INFO, mLinkInfo);
        outState.putBoolean(STATE_CHANGED, mChanged);
        outState.putSerializable(STATE_CAPTURE_IMAGE_OUTPUT_FILE, mCaptureImageOutputFile);
        outState.putLong(STATE_WRITER_ID, mWriterId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_send_broadcast_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            mTextEdit.setText(mExtraText);
        }
        mTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mChanged = true;
            }
        });
        mAttachmentLayout.setOnRemoveImageListener(this::removeImage);
        bindAttachmentLayout();
        TooltipUtils.setup(mAddImageButton);
        mAddImageButton.setOnClickListener(view -> pickOrCaptureImage());
        TooltipUtils.setup(mAddMoreImageButton);
        mAddMoreImageButton.setOnClickListener(view -> pickOrCaptureImage());
        TooltipUtils.setup(mRemoveAllImagesButton);
        mRemoveAllImagesButton.setOnClickListener(view -> onRemoveAllImages());
        TooltipUtils.setup(mAddLinkButton);
        mAddLinkButton.setOnClickListener(view -> editLink());
        TooltipUtils.setup(mEditLinkButton);
        mEditLinkButton.setOnClickListener(view -> editLink());
        TooltipUtils.setup(mRemoveLinkButton);
        mRemoveLinkButton.setOnClickListener(view -> onRemoveLink());
        updateBottomBar();
        TooltipUtils.setup(mAddMentionButton);
        mAddMentionButton.setOnClickListener(view -> addMention());
        TooltipUtils.setup(mAddTopicButton);
        mAddTopicButton.setOnClickListener(view -> addTopic());
        mCounterText.setEditText(mTextEdit, Broadcast.MAX_TEXT_LENGTH);

        updateSendStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.broadcast_send_broadcast, menu);
        mSendMenuItem = menu.findItem(R.id.action_send);
        updateSendStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onFinish();
                return true;
            case R.id.action_send:
                onSend();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EffortlessPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
                this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_OR_CAPTURE_IMAGE:
                onImagePickedOrCaptured(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CAPTURE_IMAGE_PERMISSION)
    private void pickOrCaptureImage() {
        if (mImageUris.size() >= Broadcast.MAX_IMAGES_SIZE) {
            ToastUtils.show(R.string.broadcast_send_add_image_too_many, getActivity());
            return;
        }
        if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_CAPTURE_IMAGE)) {
            pickOrCaptureImageWithPermission();
        } else if (EffortlessPermissions.somePermissionPermanentlyDenied(this,
                PERMISSIONS_CAPTURE_IMAGE)) {
            ToastUtils.show(
                    R.string.broadcast_send_capture_image_permission_permanently_denied_message,
                    getActivity());
            pickImage();
        } else  {
            EffortlessPermissions.requestPermissions(this,
                    R.string.broadcast_send_capture_image_permission_request_message,
                    REQUEST_CODE_CAPTURE_IMAGE_PERMISSION, PERMISSIONS_CAPTURE_IMAGE);
        }
    }

    private void pickOrCaptureImageWithPermission() {
        mCaptureImageOutputFile = FileUtils.makeCaptureImageOutputFile();
        startActivityForResult(IntentUtils.makePickOrCaptureImageWithChooser(true,
                mCaptureImageOutputFile, getActivity()), REQUEST_CODE_PICK_OR_CAPTURE_IMAGE);
    }

    @AfterPermissionDenied(REQUEST_CODE_CAPTURE_IMAGE_PERMISSION)
    private void onCaptureImagePermissionDenied() {
        ToastUtils.show(R.string.broadcast_send_capture_image_permission_denied, getActivity());
        pickImage();
    }

    private void pickImage() {
        AppUtils.startActivityForResultWithChooser(IntentUtils.makePickImage(true),
                REQUEST_CODE_PICK_OR_CAPTURE_IMAGE, this);
    }

    private List<Uri> parsePickOrCaptureImageResult(Intent data) {
        if (data != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int itemCount = clipData.getItemCount();
                    if (itemCount > 0) {
                        List<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < itemCount; ++i) {
                            uris.add(clipData.getItemAt(i).getUri());
                        }
                        return uris;
                    }
                }
            }
            Uri uri = data.getData();
            if (uri != null) {
                return Collections.singletonList(uri);
            }
        }
        if (mCaptureImageOutputFile != null) {
            getActivity().sendBroadcast(IntentUtils.makeMediaScan(mCaptureImageOutputFile));
            return Collections.singletonList(Uri.fromFile(mCaptureImageOutputFile));
        }
        return Collections.emptyList();
    }

    private void onImagePickedOrCaptured(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        List<Uri> uris = parsePickOrCaptureImageResult(data);
        mCaptureImageOutputFile = null;
        int maxUrisSize = Broadcast.MAX_IMAGES_SIZE - mImageUris.size();
        if (uris.size() > maxUrisSize) {
            ToastUtils.show(R.string.broadcast_send_add_image_too_many, getActivity());
            if (maxUrisSize <= 0) {
                return;
            }
            uris = uris.subList(0, maxUrisSize);
        }
        boolean appendingImages = !mImageUris.isEmpty();
        mImageUris.addAll(uris);
        bindAttachmentLayout(appendingImages);
        updateBottomBar();
        mChanged = true;
    }

    private void removeImage(int position) {
        mImageUris.remove(position);
        boolean removedImageAtEnd = position == mImageUris.size();
        bindAttachmentLayout(removedImageAtEnd);
        updateBottomBar();
        mChanged = true;
    }

    private void onRemoveAllImages() {
        ConfirmRemoveAllImagesDialogFragment.show(this);
    }

    @Override
    public void removeAllImages() {
        mImageUris.clear();
        bindAttachmentLayout();
        updateBottomBar();
        mChanged = true;
    }

    private void editLink() {
        EditLinkDialogFragment.show(mLinkInfo, this);
    }

    @Override
    public void setLink(LinkInfo linkInfo) {
        mLinkInfo = linkInfo;
        bindAttachmentLayout();
        updateBottomBar();
        mChanged = true;
    }

    private void onRemoveLink() {
        ConfirmRemoveLinkDialogFragment.show(this);
    }

    @Override
    public void removeLink() {
        setLink(null);
    }

    private void bindAttachmentLayout(boolean scrollImageListToEnd) {
        mAttachmentLayout.bind(mLinkInfo, mImageUris, scrollImageListToEnd);
    }

    private void bindAttachmentLayout() {
        bindAttachmentLayout(false);
    }

    private void updateBottomBar() {
        boolean isImagesEmpty = mImageUris.isEmpty();
        boolean isLinkEmpty = mLinkInfo == null;
        boolean isEmpty = isImagesEmpty && isLinkEmpty;
        boolean hasImage = !isImagesEmpty;
        boolean hasLink = !isLinkEmpty;
        ViewUtils.setVisibleOrGone(mAddImageButton, isEmpty);
        ViewUtils.setVisibleOrGone(mAddMoreImageButton, hasImage);
        ViewUtils.setVisibleOrGone(mRemoveAllImagesButton, hasImage);
        ViewUtils.setVisibleOrGone(mAddLinkButton, isEmpty);
        ViewUtils.setVisibleOrGone(mEditLinkButton, hasLink);
        ViewUtils.setVisibleOrGone(mRemoveLinkButton, hasLink);
    }

    private void addMention() {
        DoubanUtils.addMentionString(mTextEdit);
    }

    private void addTopic() {
        DoubanUtils.addTopicString(mTextEdit);
    }

    private void onSend() {
        String text = mTextEdit.getText().toString();
        if (TextUtils.isEmpty(text) && mImageUris.isEmpty() && mLinkInfo == null) {
            ToastUtils.show(R.string.broadcast_send_error_empty, getActivity());
            return;
        }
        if (text.length() > Broadcast.MAX_TEXT_LENGTH) {
            ToastUtils.show(R.string.broadcast_send_error_text_too_long, getActivity());
            return;
        }
        send(text, mImageUris, mLinkInfo);
    }

    private void send(String text, List<Uri> imageUris, LinkInfo linkInfo) {
        String linkTitle = linkInfo != null ? linkInfo.title : null;
        String linkUrl = linkInfo != null ? linkInfo.url : null;
        mWriterId = SendBroadcastManager.getInstance().write(text, imageUris, linkTitle, linkUrl,
                getActivity());
        if (!imageUris.isEmpty()) {
            // If there's any image, we'll upload them and send broadcast in background.
            finish();
            return;
        }
        updateSendStatus();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastSent(BroadcastSentEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.writerId == mWriterId) {
            mSent = true;
            mWriterId = 0;
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastSendError(BroadcastSendErrorEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.writerId == mWriterId) {
            mWriterId = 0;
            updateSendStatus();
        }
    }

    private void updateSendStatus() {
        if (mSent) {
            return;
        }
        SendBroadcastManager manager = SendBroadcastManager.getInstance();
        boolean sending = manager.isWriting(mWriterId);
        getActivity().setTitle(sending ? R.string.broadcast_send_title_sending
                : R.string.broadcast_send_title);
        boolean enabled = !sending;
        if (mSendMenuItem != null) {
            mSendMenuItem.setEnabled(enabled);
        }
        mTextEdit.setEnabled(enabled);
        if (sending) {
            mTextEdit.setText(manager.getText(mWriterId));
        }
        mAddImageButton.setEnabled(enabled);
        mAddMoreImageButton.setEnabled(enabled);
        mRemoveAllImagesButton.setEnabled(enabled);
        mAddLinkButton.setEnabled(enabled);
        mEditLinkButton.setEnabled(enabled);
        mRemoveLinkButton.setEnabled(enabled);
        mAddMentionButton.setEnabled(enabled);
        mAddTopicButton.setEnabled(enabled);
    }

    public void onFinish() {
        boolean isEmpty = TextUtils.isEmpty(mTextEdit.getText()) && mImageUris.isEmpty()
                && mLinkInfo == null;
        if (mChanged && !isEmpty) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            finish();
        }
    }

    @Override
    public void discardContent() {
        finish();
    }

    private void finish() {
        FragmentFinishable.finish(getActivity());
    }

    public static class LinkInfo implements Parcelable {

        public String url;
        public String title;
        public String description;
        public String imageUrl;

        public LinkInfo(String url, String title, String description, String imageUrl) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Empty url: " + url);
            }
            this.url = url;
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public LinkInfo(String url) {
            this(url, null, null, null);
        }


        public static final Creator<LinkInfo> CREATOR = new Creator<LinkInfo>() {
            @Override
            public LinkInfo createFromParcel(Parcel source) {
                return new LinkInfo(source);
            }
            @Override
            public LinkInfo[] newArray(int size) {
                return new LinkInfo[size];
            }
        };
        protected LinkInfo(Parcel in) {
            url = in.readString();
            title = in.readString();
            description = in.readString();
            imageUrl = in.readString();
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
            dest.writeString(title);
            dest.writeString(description);
            dest.writeString(imageUrl);
        }
    }
}

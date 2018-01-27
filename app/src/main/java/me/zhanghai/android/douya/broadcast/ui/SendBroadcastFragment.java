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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import me.zhanghai.android.douya.ui.ConfirmDiscardContentDialogFragment;
import me.zhanghai.android.douya.util.AppUtils;
import me.zhanghai.android.douya.util.FileUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.IntentUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;
import me.zhanghai.android.effortlesspermissions.AfterPermissionDenied;
import me.zhanghai.android.effortlesspermissions.EffortlessPermissions;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class SendBroadcastFragment extends Fragment
        implements ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = SendBroadcastFragment.class.getName() + '.';

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

    private MenuItem mSendMenuItem;

    private CharSequence mText;
    private Uri mStream;

    private long mWriterId;

    private File mCaptureImageOutputFile;

    private boolean mSent;

    public static SendBroadcastFragment newInstance(CharSequence text, Uri stream) {
        //noinspection deprecation
        SendBroadcastFragment fragment = new SendBroadcastFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putCharSequence(Intent.EXTRA_TEXT, text);
        arguments.putParcelable(Intent.EXTRA_STREAM, stream);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(CharSequence, Uri)} instead.
     */
    public SendBroadcastFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mText = arguments.getCharSequence(Intent.EXTRA_TEXT);
        mStream = arguments.getParcelable(Intent.EXTRA_STREAM);

        if (savedInstanceState != null) {
            mWriterId = savedInstanceState.getLong(STATE_WRITER_ID);
        }

        setHasOptionsMenu(true);

        EventBusUtils.register(this);
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
            mTextEdit.setText(mText);
        }
        // TODO
        mAttachmentLayout.bind(null, null);
        mAddImageButton.setOnClickListener(view -> pickOrCaptureImage());
        updateSendStatus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(STATE_WRITER_ID, mWriterId);
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
                REQUEST_CODE_PICK_OR_CAPTURE_IMAGE, getActivity());
    }

    private void onImagePickedOrCaptured(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        List<Uri> uris = parsePickOrCaptureImageResult(data);
        mCaptureImageOutputFile = null;
        mAttachmentLayout.bind(null, uris);
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

    private void onSend() {
        String text = mTextEdit.getText().toString();
        send(text);
    }

    private void send(String text) {
        mWriterId = SendBroadcastManager.getInstance().write(text, null, null, null, getActivity());
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
            getActivity().finish();
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
        mTextEdit.setEnabled(enabled);
        if (mSendMenuItem != null) {
            mSendMenuItem.setEnabled(enabled);
        }
        if (sending) {
            mTextEdit.setText(manager.getText(mWriterId));
        }
    }

    public void onFinish() {
        if (mTextEdit.getText().length() > 0) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void discardContent() {
        getActivity().finish();
    }
}

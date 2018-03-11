package org.bitbrothers.shoppi.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

public class AlertDialogFragment extends AppCompatDialogFragment {

    public interface OnButtonClickListener {
        void onButtonClick(AlertDialogFragment fragment, int whichButton);
    }

    public interface OnSingleItemSelectedListener {
        void onSingleItemSelected(Bundle custom, int itemPosition);
    }

    public static class Builder {

        private final Context context;
        private boolean cancelable;
        private String title;
        private String message;
        private int itemsArrayResId;
        private String negativeButtonText;
        private String positiveButtonText;
        private Bundle customBundle;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int stringResId) {
            this.title = context.getString(stringResId);
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int stringResId) {
            this.message = context.getString(stringResId);
            return this;
        }

        public Builder setItems(int stringArrayResId) {
            this.itemsArrayResId = stringArrayResId;
            return this;
        }

        public Builder setPositiveButton(String text) {
            this.positiveButtonText = text;
            return this;
        }

        public Builder setPositiveButton(int stringResId) {
            this.positiveButtonText = context.getString(stringResId);
            return this;
        }

        public Builder setNegativeButton(String text) {
            this.negativeButtonText = text;
            return this;
        }

        public Builder setNegativeButton(int stringResId) {
            this.negativeButtonText = context.getString(stringResId);
            return this;
        }

        public Builder setCustom(Bundle bundle) {
            this.customBundle = bundle;
            return this;
        }

        public AlertDialogFragment create() {
            Bundle args = new Bundle();
            args.putBoolean(KEY_CANCELABLE, cancelable);
            args.putString(KEY_TITLE, title);
            args.putInt(KEY_ITEMS, itemsArrayResId);
            args.putBundle(KEY_CUSTOM, customBundle);
            args.putString(KEY_MESSAGE, message);
            args.putString(KEY_POSITIVE_BUTTON_TEXT, positiveButtonText);
            args.putString(KEY_NEGATIVE_BUTTON_TEXT, negativeButtonText);

            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    private static final String KEY_CANCELABLE = "cancelable";
    private static final String KEY_CUSTOM = "custom";
    private static final String KEY_ITEMS = "array";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String KEY_NEGATIVE_BUTTON_TEXT = "negative_button_text";

    private OnSingleItemSelectedListener onSingleItemSelectedListener;
    private OnButtonClickListener onButtonClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(getArguments().getBoolean(KEY_CANCELABLE, false));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString(KEY_TITLE);
        if (title != null) {
            builder.setTitle(title);
        }

        String message = getArguments().getString(KEY_MESSAGE);
        if (message != null) {
            builder.setMessage(message);
        }

        String positiveButtonText = getArguments().getString(KEY_POSITIVE_BUTTON_TEXT);
        if (positiveButtonText != null) {
            builder.setPositiveButton(positiveButtonText, null);
        }

        String negativeButtonText = getArguments().getString(KEY_NEGATIVE_BUTTON_TEXT);
        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, null);
        }

        int itemsArrayResId = getArguments().getInt(KEY_ITEMS, 0);
        if (itemsArrayResId != 0) {
            builder.setItems(itemsArrayResId, (dialog, which) -> {
                this.onSingleItemSelectedListener.onSingleItemSelected(getArguments().getBundle(KEY_CUSTOM), which);
            });
        }

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (getArguments().getString(KEY_POSITIVE_BUTTON_TEXT) != null) {
            d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                onButtonClickListener.onButtonClick(this, AlertDialog.BUTTON_POSITIVE);
            });
        }
        if (getArguments().getString(KEY_NEGATIVE_BUTTON_TEXT) != null) {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                onButtonClickListener.onButtonClick(this, AlertDialog.BUTTON_NEGATIVE);
            });
        }
    }

    public Bundle getCustomBundle() {
        return getArguments().getBundle(KEY_CUSTOM);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public void setOnSingleItemSelectedListener(OnSingleItemSelectedListener onSingleItemSelectedListener) {
        this.onSingleItemSelectedListener = onSingleItemSelectedListener;
    }
}

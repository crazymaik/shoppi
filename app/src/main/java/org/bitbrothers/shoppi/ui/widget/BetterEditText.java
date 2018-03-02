package org.bitbrothers.shoppi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class BetterEditText extends android.support.v7.widget.AppCompatEditText {

    public interface OnImeBackListener {
        void onImeBack(BetterEditText editText);
    }

    private OnImeBackListener onImeBackListener;

    public BetterEditText(Context context) {
        super(context);
    }

    public BetterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BetterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onImeBackListener != null) {
                onImeBackListener.onImeBack(this);
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * Sets the {@link OnImeBackListener} to be called when the 'back' action is detected
     * and the keyboard is about to be dismissed.
     */
    public void setOnImeBackListener(OnImeBackListener onImeBackListener) {
        this.onImeBackListener = onImeBackListener;
    }
}

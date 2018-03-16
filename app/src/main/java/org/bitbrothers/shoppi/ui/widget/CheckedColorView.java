package org.bitbrothers.shoppi.ui.widget;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ui.view.Dimensions;

public class CheckedColorView extends AppCompatImageView {

    private GradientDrawable colorDrawable;
    private int strokeWidth;
    private int color;
    private boolean isFilled;

    public CheckedColorView(Context context) {
        super(context);
        init();
    }

    public CheckedColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckedColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        apply();
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
        apply();
    }

    private void init() {
        colorDrawable = (GradientDrawable) getContext().getDrawable(R.drawable.category_colorfield);
        strokeWidth = Dimensions.dpToPixels(getContext(), 2);
        color = 0xff000000;
        setImageDrawable(this.colorDrawable);
        apply();
    }

    private void apply() {
        colorDrawable.setStroke(strokeWidth, color);
        colorDrawable.setColor(isFilled ? color : 0);
    }
}

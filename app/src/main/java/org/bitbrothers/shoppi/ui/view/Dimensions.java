package org.bitbrothers.shoppi.ui.view;


import android.content.Context;
import android.util.TypedValue;

public final class Dimensions {

    public static int dpToPixels(Context context, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }
}

package com.tweener.floatingactiongroup.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

/**
 * @author Vivien Mahe
 */

public final class DrawableUtils {

    private static final String TAG = DrawableUtils.class.getSimpleName();

    private DrawableUtils() {
    }

    /**
     * Applies a color to a given {@link Drawable}.
     *
     * @param context
     * @param drawable
     * @param resId
     */
    public static void applyColor(final Context context, final Drawable drawable, final int resId) {
        if (context == null || drawable == null) {
            return;
        }

        int color;

        try {
            color = ResourcesCompat.getColor(context.getResources(), resId, null);
        } catch (final Resources.NotFoundException ex) {
            color = resId;
        }

        try {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } catch (final Resources.NotFoundException ex) {
            Log.e(TAG, "applyColor: ", ex);
        }
    }
}

package com.tweener.floatingactiongroup.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.tweener.floatingactiongroup.R;

/**
 * Allows the font file to be loaded from an asset. Include the extension in the file name.
 */
public class CustomTextView extends AppCompatTextView {
    public CustomTextView(final Context context) {
        super(context);
    }

    public CustomTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        final String fontFileName = ta.getString(R.styleable.CustomTextView_fontFileName);
        ta.recycle();

        if (!isInEditMode()) {
            setFont(context, fontFileName);
        }
    }

    public final void setFont(final Context context, final String fontFileName) {
        if (!TextUtils.isEmpty(fontFileName)) {
            setTypeface(Typefaces.createFromAsset(context.getAssets(), fontFileName));
        }
    }
}

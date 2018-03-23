package com.tweener.floatingactiongroup;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.tweener.floatingactiongroup.util.CustomTextView;
import com.tweener.floatingactiongroup.util.DrawableUtils;

/**
 * @author Vivien Mahe
 */
public class FloatingActionLabelledButton extends LinearLayout implements View.OnClickListener {

    private static final String TAG = FloatingActionLabelledButton.class.getSimpleName();

    private static int PADDING_VERT;
    private static int PADDING_HORZ;

    private String label;
    private int srcResId;
    private int srcColorTint;
    private ColorStateList backgroundNormal;
    private int backgroundPressed;
    private String fontFileName;
    private FloatingActionGroup.OnFloatingActionGroupListener fabClickListener;
    private boolean isExpanded;

    public FloatingActionLabelledButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        PADDING_VERT = (int) context.getResources().getDimension(R.dimen.favg_padding_vert);
        PADDING_HORZ = (int) context.getResources().getDimension(R.dimen.falb_padding_horz);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionLabelledButton);
        label = ta.getString(R.styleable.FloatingActionLabelledButton_label);
        srcResId = ta.getResourceId(R.styleable.FloatingActionLabelledButton_src, 0);
        srcColorTint = ta.getColor(R.styleable.FloatingActionLabelledButton_srcTint, 0);
        backgroundNormal = ta.getColorStateList(R.styleable.FloatingActionLabelledButton_backgroundNormal);
        backgroundPressed = ta.getColor(R.styleable.FloatingActionLabelledButton_backgroundPressed, 0);
        fontFileName = ta.getString(R.styleable.FloatingActionLabelledButton_fontFileName);
        ta.recycle();

        init(context);
    }

    private void init(final Context context) {
        View.inflate(context, R.layout.floating_action_labelled_button, this);

        final CustomTextView labelTextView = findViewById(R.id.falb_label);
        labelTextView.setText(label);
        labelTextView.setFont(context, fontFileName);

        final FloatingActionButton fab = findViewById(R.id.falb_fab);
        fab.setImageResource(srcResId);
        fab.setBackgroundTintList(backgroundNormal);
        fab.setRippleColor(backgroundPressed);
        fab.setOnClickListener(this);
        labelTextView.setOnClickListener(this);
        setOnClickListener(this);

        DrawableUtils.applyColor(getContext(), fab.getDrawable(), srcColorTint);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Add padding to this button
        setMeasuredDimension(getMeasuredWidth() + 2 * PADDING_HORZ, getMeasuredHeight() + 2 * PADDING_VERT);
    }

    @Override
    public void onClick(@NonNull final View view) {
        // Simply forwards this view click to another listener
        if (fabClickListener != null) {
            fabClickListener.onFloatingActionGroupChildClicked(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return !isExpanded;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return false;
    }

    public void setFabClickListener(final FloatingActionGroup.OnFloatingActionGroupListener fabClickListener) {
        this.fabClickListener = fabClickListener;
    }

    public void setExpanded(final boolean expanded) {
        isExpanded = expanded;
    }
}
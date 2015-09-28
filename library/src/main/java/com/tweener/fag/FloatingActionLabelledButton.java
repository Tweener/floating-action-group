package com.tweener.fag;

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
import android.widget.TextView;

/**
 * Created by vivienmahe on 25/09/15.
 */
public class FloatingActionLabelledButton extends LinearLayout implements View.OnClickListener {

    private static int PADDING_BOTTOM;
    private static int PADDING_RIGHT;

    private String label;
    private int srcResId;
    private ColorStateList backgroundNormal;
    private int backgroundPressed;
    private OnClickListener fabClickListener;

    public FloatingActionLabelledButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        PADDING_BOTTOM = (int) context.getResources().getDimension(R.dimen.fag_padding);
        PADDING_RIGHT = (int) context.getResources().getDimension(R.dimen.falb_padding_right);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionLabelledButton);
        label = ta.getString(R.styleable.FloatingActionLabelledButton_label);
        srcResId = ta.getResourceId(R.styleable.FloatingActionLabelledButton_src, 0);
        backgroundNormal = ta.getColorStateList(R.styleable.FloatingActionLabelledButton_backgroundNormal);
        backgroundPressed = ta.getColor(R.styleable.FloatingActionLabelledButton_backgroundPressed, 0);
        ta.recycle();

        init(context);
    }

    private void init(final Context context) {
        View.inflate(context, R.layout.floating_action_labelled_button, this);

        final TextView labelTextView = ((TextView) findViewById(R.id.falb_label));
        labelTextView.setText(label);
        labelTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                // Disable touch on this textview so any view below this textview won't receive a touch event
                return true;
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.falb_fab);
        fab.setImageResource(srcResId);
        fab.setBackgroundTintList(backgroundNormal);
        fab.setRippleColor(backgroundPressed);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Add padding to this button
        setMeasuredDimension(getMeasuredWidth() + PADDING_RIGHT, getMeasuredHeight() + PADDING_BOTTOM);
    }

    @Override
    public void onClick(@NonNull final View view) {
        if (fabClickListener != null) {
            // simply forwards the FAB button click to another listener - replacing the button with this class a what was clicked.
            fabClickListener.onClick(this);
        }
    }

    public void setFabClickListener(OnClickListener fabClickListener) {
        this.fabClickListener = fabClickListener;
    }
}

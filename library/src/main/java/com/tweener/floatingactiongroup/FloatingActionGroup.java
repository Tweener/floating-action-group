package com.tweener.floatingactiongroup;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author Vivien Mahe
 */
public abstract class FloatingActionGroup extends FrameLayout {

    public interface OnFloatingActionGroupListener {
        void onFloatingActionGroupExpanded();

        void onFloatingActionGroupCollapsed();

        void onFloatingActionGroupChildClicked(View view);
    }

    /**
     * Fired when this group has been expanded. Subclasses must implement their own related logic.
     *
     * @param animate animate expansion
     */
    protected abstract void onExpanded(final boolean animate);

    /**
     * Fired when this group has been collapsed. Subclasses must implement their own related logic.
     *
     * @param animate animate collaspe
     */
    protected abstract void onCollapsed(final boolean animate);

    private int srcCollapsedResId;
    private int srcExpandedResId;
    private ColorStateList backgroundNormal;
    private int backgroundPressed;
    private ColorStateList backgroundExpanded;
    private boolean isExpanded;

    private FloatingActionButton mainFab;

    protected OnFloatingActionGroupListener listener;

    public FloatingActionGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionGroup);
        srcCollapsedResId = ta.getResourceId(R.styleable.FloatingActionGroup_srcCollapsed, 0);
        srcExpandedResId = ta.getResourceId(R.styleable.FloatingActionGroup_srcExpanded, 0);
        backgroundNormal = ta.getColorStateList(R.styleable.FloatingActionGroup_backgroundNormal);
        backgroundPressed = ta.getColor(R.styleable.FloatingActionGroup_backgroundPressed, 0);
        backgroundExpanded = ta.getColorStateList(R.styleable.FloatingActionGroup_backgroundExpanded);
        isExpanded = ta.getBoolean(R.styleable.FloatingActionGroup_groupExpanded, false);
        ta.recycle();
    }

    /**
     * Call this method to start configuring the {@link FloatingActionGroup}. If this method is not called, nothing will happen.
     */
    protected void configure(final Context context) {
        configureMainButton(context);
    }

    private void configureMainButton(final Context context) {
        final View view = View.inflate(context, R.layout.floating_action_group, this);

        mainFab = (FloatingActionButton) view.findViewById(R.id.fag_main_fab);
        mainFab.setBackgroundTintList(backgroundNormal);
        mainFab.setRippleColor(backgroundPressed);

        updateMainFabImage();

        mainFab.setOnClickListener(view1 -> onMainFabButtonClicked());
    }

    @Override
    protected boolean isChildrenDrawingOrderEnabled() {
        return true;
    }

    @Override
    protected int getChildDrawingOrder(final int childCount, final int index) {
        return childCount - (index + 1);
    }

    /**
     * Fired when this group has been expanded or collapsed. Subclasses can implement their own logic to animate the main FAB<br>
     * Return {@code true} to use custom animation, or {@code false} for the main FAB to be updated with the attributes 'srcExpanded' and
     * 'srcCollapsed'.
     */
    protected boolean onAnimateMainFab(final boolean isExpanded) {
        return false;
    }

    protected FloatingActionButton getMainFAB() {
        return mainFab;
    }

    public ColorStateList getBackgroundNormal() {
        return backgroundNormal;
    }

    public ColorStateList getBackgroundExpanded() {
        return backgroundExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public boolean isCollapsed() {
        return !isExpanded;
    }

    private void onMainFabButtonClicked() {
        toggle(true);
    }

    public void toggle(final boolean animate) {
        if (listener != null) {
            if (isExpanded) {
                listener.onFloatingActionGroupCollapsed();
            } else {
                listener.onFloatingActionGroupExpanded();
            }
        }

        if (isExpanded) {
            onCollapsed(animate);
        } else {
            onExpanded(animate);
        }

        isExpanded = !isExpanded;

        if (!onAnimateMainFab(isExpanded)) {
            updateMainFabImage();
        }
    }

    @Override
    public float getScaleX() {
        return isExpanded() ? super.getScaleX() : mainFab.getScaleX();
    }

    @Override
    public void setScaleX(final float scaleX) {
        if (isExpanded()) {
            super.setScaleX(scaleX);
        } else {
            mainFab.setScaleX(scaleX);
        }
    }

    @Override
    public void setScaleY(final float scaleY) {
        if (isExpanded()) {
            super.setScaleY(scaleY);
        } else {
            mainFab.setScaleY(scaleY);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        mainFab.setEnabled(enabled);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enabled);
        }
    }

    public void setOnFloatingActionGroupListener(final OnFloatingActionGroupListener listener) {
        this.listener = listener;
    }

    private void updateMainFabImage() {
        mainFab.setImageResource(isExpanded ? srcExpandedResId : srcCollapsedResId);
    }
}


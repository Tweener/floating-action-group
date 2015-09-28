package com.tweener.fag;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by vivienmahe on 25/09/15.
 */
public class FloatingActionGroup extends ViewGroup {

    public interface OnFloatingActionGroupListener {
        void onFloatingActionGroupExpanded();

        void onFloatingActionGroupCollapsed();
    }

    private static final int ANIMATION_DURATION = 300;

    private static int MARGIN_BETWEEN_FABS;
    private static int PADDING;

    private int srcCollapsedResId;
    private int srcExpandedResId;
    private ColorStateList backgroundNormal;
    private int backgroundPressed;
    private boolean isExpanded;

    private FloatingActionButton mainFab;

    private OnFloatingActionGroupListener listener;

    public FloatingActionGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        MARGIN_BETWEEN_FABS = (int) context.getResources().getDimension(R.dimen.fag_margin_between_fab);
        PADDING = (int) context.getResources().getDimension(R.dimen.fag_padding);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionGroup);
        srcCollapsedResId = ta.getResourceId(R.styleable.FloatingActionGroup_srcCollapsed, 0);
        srcExpandedResId = ta.getResourceId(R.styleable.FloatingActionGroup_srcExpanded, 0);
        backgroundNormal = ta.getColorStateList(R.styleable.FloatingActionGroup_backgroundNormal);
        backgroundPressed = ta.getColor(R.styleable.FloatingActionGroup_backgroundPressed, 0);
        isExpanded = ta.getBoolean(R.styleable.FloatingActionGroup_expanded, false);
        ta.recycle();

        configureMainButton(context);
    }

    private void configureMainButton(final Context context) {
        final View view = View.inflate(context, R.layout.floating_action_group, this);

        mainFab = (FloatingActionButton) view.findViewById(R.id.fag_main_fab);
        mainFab.setBackgroundTintList(backgroundNormal);
        mainFab.setRippleColor(backgroundPressed);

        updateMainFabImage();

        mainFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                onMainFabButtonClicked();
            }
        });
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final int childWidth = child.getMeasuredWidth();
            if (childWidth > width) {
                width = childWidth;
            }

            height += child.getMeasuredHeight();
        }

        // Add padding
        width += 2 * PADDING;
        height += 2 * PADDING + MARGIN_BETWEEN_FABS;

        setMeasuredDimension(width, height);
    }

    @Override
    protected boolean isChildrenDrawingOrderEnabled() {
        return true;
    }

    @Override
    protected int getChildDrawingOrder(final int childCount, final int i) {
        return childCount - (i + 1);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        final int collapsedMainFabY = b - t - mainFab.getMeasuredHeight() - PADDING;
        int height = PADDING;

        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == mainFab) {
                continue;
            }

            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            final int childX = getMeasuredWidth() - child.getMeasuredWidth() - PADDING;
            final int deltaY = height + childHeight;
            child.layout(childX, height, childX + childWidth, deltaY);
            child.setAlpha(isExpanded ? 1 : 0);

            if (!isExpanded) {
                child.setTranslationY(collapsedMainFabY - height);
            }

            height += childHeight + (i <= 1 ? MARGIN_BETWEEN_FABS : 0);
        }

        // Places the main button as last
        final int mainWidth = mainFab.getMeasuredWidth();
        final int mainHeight = mainFab.getMeasuredHeight();
        final int mainX = getMeasuredWidth() - mainFab.getMeasuredWidth() - PADDING;
        mainFab.layout(mainX, height, mainX + mainWidth, height + mainHeight);

        // Forces the main button to be above all the others changing its z order
//        bringChildToFront(mainFab);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private void onMainFabButtonClicked() {
        if (listener != null) {
            if (isExpanded) {
                listener.onFloatingActionGroupCollapsed();
            } else {
                listener.onFloatingActionGroupExpanded();
            }
        }

        toggle();
    }

    public void toggle() {
        if (isExpanded) {
            collapse();
        } else {
            expand();
        }

        isExpanded = !isExpanded;

        updateMainFabImage();
    }

    /**
     * Expands this {@link FloatingActionGroup}. Each child will move vertically to its original position above the main FAB.
     */
    private void expand() {
        final AccelerateDecelerateInterpolator accDecInterpolator = new AccelerateDecelerateInterpolator();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == mainFab) {
                continue;
            }

            final ValueAnimator animator = ValueAnimator.ofFloat(mainFab.getTop() - child.getTop(), 0);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(accDecInterpolator);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                    child.setAlpha(animation.getAnimatedFraction());
                    child.setTranslationY((Float) animation.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    /**
     * Each child will translate vertically, from their actual position to behind the main FAB.
     */
    private void collapse() {
        final AccelerateDecelerateInterpolator accDecInterpolator = new AccelerateDecelerateInterpolator();
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == mainFab) {
                continue;
            }

            final float deltaY = mainFab.getTop() - child.getTop();
            final float fromYDelta = isExpanded ? 0 : -deltaY;
            final float toYDelta = isExpanded ? deltaY : 0;

            final ValueAnimator animator = ValueAnimator.ofFloat(fromYDelta, toYDelta);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(accDecInterpolator);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                    child.setAlpha(1 - animation.getAnimatedFraction());
                    child.setTranslationY((Float) animation.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    public void setOnFloatingActionGroupListener(final OnFloatingActionGroupListener listener) {
        this.listener = listener;
    }

    private void updateMainFabImage() {
        mainFab.setImageResource(isExpanded ? srcExpandedResId : srcCollapsedResId);
    }
}

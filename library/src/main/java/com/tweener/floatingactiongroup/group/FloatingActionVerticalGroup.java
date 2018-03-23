package com.tweener.floatingactiongroup.group;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.tweener.floatingactiongroup.FloatingActionGroup;
import com.tweener.floatingactiongroup.FloatingActionLabelledButton;
import com.tweener.floatingactiongroup.R;

/**
 * Custom implementation of {@link FloatingActionGroup} to display FABs vertically.
 *
 * @author Vivien Mahe
 */
public class FloatingActionVerticalGroup extends FloatingActionGroup {

    private static final String TAG = FloatingActionVerticalGroup.class.getSimpleName();

    private static int MARGIN_BETWEEN_FABS;
    private static int PADDING_VERT;
    private static int PADDING_RIGHT;
    private static final boolean MAIN_FAB_SHOULD_ROTATE_DEFAULT = false;
    private static final int MAIN_FAB_ROTATION_ANGLE_DEFAULT = 45;
    private static final boolean MAIN_FAB_SHOULD_SCALE_DEFAULT = false;
    private static final float MAIN_FAB_SCALE_VALUE_DEFAULT = 0.8f;
    private static final int ANIMATION_DURATION = 300;

    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private final boolean mainFabShouldRotate;
    private final int mainFabRotationAngle;
    private final boolean mainFabShouldScale;
    private final float mainFabScaleValue;

    public FloatingActionVerticalGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        MARGIN_BETWEEN_FABS = (int) context.getResources().getDimension(R.dimen.favg_margin_between_fab);
        PADDING_VERT = (int) context.getResources().getDimension(R.dimen.favg_padding_vert);
        PADDING_RIGHT = (int) context.getResources().getDimension(R.dimen.falb_padding_horz);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionGroup);
        mainFabShouldRotate = ta.getBoolean(R.styleable.FloatingActionGroup_mainFabShouldRotate, MAIN_FAB_SHOULD_ROTATE_DEFAULT);
        mainFabRotationAngle = ta.getInteger(R.styleable.FloatingActionGroup_mainFabRotateAngle, MAIN_FAB_ROTATION_ANGLE_DEFAULT);
        mainFabShouldScale = ta.getBoolean(R.styleable.FloatingActionGroup_mainFabShouldScale, MAIN_FAB_SHOULD_SCALE_DEFAULT);
        mainFabScaleValue = ta.getFloat(R.styleable.FloatingActionGroup_mainFabScaleValue, MAIN_FAB_SCALE_VALUE_DEFAULT);
        ta.recycle();

        configure(context);
    }

    @Override
    protected boolean onAnimateMainFab(final boolean isExpanded) {
        // Rotate main FAB 45 degrees clockwise when group expanded.
        final int fromAngle = isExpanded ? 0 : mainFabRotationAngle;
        final int toAngle = isExpanded ? mainFabRotationAngle : 0;

        // Scale main FAB
        final float fromScale = isExpanded ? 1.0f : mainFabScaleValue;
        final float toScale = isExpanded ? mainFabScaleValue : 1.0f;

        // Change color main FAB
        final int startColor;
        final int endColor;
        if (getBackgroundExpanded() != null) {
            startColor = isExpanded ? getBackgroundNormal().getDefaultColor() : getBackgroundExpanded().getDefaultColor();
            endColor = isExpanded ? getBackgroundExpanded().getDefaultColor() : getBackgroundNormal().getDefaultColor();
        } else {
            startColor = endColor = 0;
        }

        final FloatingActionButton mainFAB = getMainFAB();

        final ValueAnimator mainFABAnimator = ValueAnimator.ofFloat(0f, 1f);
        mainFABAnimator.addUpdateListener(animation -> {
            final float animatedValue = (float) animation.getAnimatedValue();
            final float scale = fromScale + ((toScale - fromScale) * animatedValue);
            final float rotation = fromAngle + ((toAngle - fromAngle) * animatedValue);

            if (mainFabShouldRotate) {
                mainFAB.setRotation(rotation);
            }

            if (mainFabShouldScale) {
                mainFAB.setScaleX(scale);
                mainFAB.setScaleY(scale);
            }

            if (getBackgroundExpanded() != null) {
                final int backgroundColor = (int) argbEvaluator.evaluate(animatedValue, startColor, endColor);
                mainFAB.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{backgroundColor}));
            }
        });
        mainFABAnimator.setDuration(ANIMATION_DURATION);
        mainFABAnimator.start();

        return true;
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

            width = Math.max(child.getMeasuredWidth(), width);
            height += child.getMeasuredHeight() + MARGIN_BETWEEN_FABS;
        }

        // Add padding
        height += 2 * PADDING_VERT;
        width += PADDING_RIGHT;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        final int collapsedMainFabY = b - t - getMainFAB().getMeasuredHeight() - PADDING_VERT;
        int height = 0;

        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == getMainFAB()) {
                continue;
            }

            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            final int deltaY = height + childHeight;
            child.layout(getMeasuredWidth() - childWidth, height, getMeasuredWidth(), deltaY);
            child.setAlpha(isExpanded() ? 1 : 0);

            if (child instanceof FloatingActionLabelledButton) {
                ((FloatingActionLabelledButton) child).setFabClickListener(listener);
            }

            if (isCollapsed()) {
                child.setTranslationY(collapsedMainFabY - height);
            }

            height += childHeight + MARGIN_BETWEEN_FABS;
        }

        // Places the main button as last
        final int mainX = getMeasuredWidth() - PADDING_RIGHT;
        final int mainY = height + PADDING_VERT;
        getMainFAB().layout(mainX - getMainFAB().getMeasuredWidth(), mainY, mainX, mainY + getMainFAB().getMeasuredHeight());
    }

    /**
     * Expands this {@link FloatingActionVerticalGroup}. Each child will move vertically to its original position above the main FAB.
     *
     * @param animate
     */
    @Override
    protected void onExpanded(final boolean animate) {
        final AccelerateDecelerateInterpolator accDecInterpolator = new AccelerateDecelerateInterpolator();

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == getMainFAB()) {
                continue;
            }

            if (child instanceof FloatingActionLabelledButton) {
                ((FloatingActionLabelledButton) child).setExpanded(true);
            }

            final ValueAnimator animator = ValueAnimator.ofFloat(getMainFAB().getTop() - child.getTop(), 0);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(accDecInterpolator);
            animator.addUpdateListener(animation -> {
                child.setAlpha(animation.getAnimatedFraction());
                child.setTranslationY((Float) animation.getAnimatedValue());
            });
            animator.start();
        }
    }

    /**
     * Each child will translate vertically, from their actual position to behind the main FAB.
     *
     * @param animate
     */
    @Override
    protected void onCollapsed(final boolean animate) {
        final AccelerateDecelerateInterpolator accDecInterpolator = new AccelerateDecelerateInterpolator();

        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == getMainFAB()) {
                continue;
            }

            if (child instanceof FloatingActionLabelledButton) {
                ((FloatingActionLabelledButton) child).setExpanded(false);
            }

            final float deltaY = getMainFAB().getTop() - child.getTop();
            final float fromYDelta = isExpanded() ? 0 : -deltaY;
            final float toYDelta = isExpanded() ? deltaY : 0;

            final ValueAnimator animator = ValueAnimator.ofFloat(fromYDelta, toYDelta);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(accDecInterpolator);
            animator.addUpdateListener(animation -> {
                child.setAlpha(1 - animation.getAnimatedFraction());
                child.setTranslationY((Float) animation.getAnimatedValue());
            });
            animator.start();
        }
    }
}

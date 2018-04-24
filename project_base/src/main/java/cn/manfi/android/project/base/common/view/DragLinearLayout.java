package cn.manfi.android.project.base.common.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import cn.manfi.android.project.base.R;

/**
 * A LinearLayout that supports children Views that can be dragged and swapped around.
 * See {@link #addDragView(View, View)},
 * {@link #addDragView(View, View, int)},
 * {@link #setViewDraggable(View, View)}, and
 * {@link #removeDragView(View)}.
 * <p/>
 * Currently, no error-checking is done on standard {@link #addView(View)} and
 * {@link #removeView(View)} calls, so avoid using these with children previously
 * declared as draggable to prevent memory leaks and/or subtle bugs. Pull requests welcome!
 */
public class DragLinearLayout extends LinearLayout {

    private static final String LOG_TAG = DragLinearLayout.class.getSimpleName();
    private static final long NOMINAL_SWITCH_DURATION = 150;
    private static final long MIN_SWITCH_DURATION = NOMINAL_SWITCH_DURATION;
    private static final long MAX_SWITCH_DURATION = NOMINAL_SWITCH_DURATION * 2;
    private static final float NOMINAL_DISTANCE = 20;
    private final float nominalDistanceScaled;

    /**
     * Use with {@link }
     */
    public interface OnViewSwapListener {

        /**
         * Invoked right before the two items are swapped due to a drag event.
         * After the swap, the firstView will be in the secondPosition, and vice versa.
         * <p/>
         * No guarantee is made as to which of the two has a lesser/greater position.
         */
        void onSwap(View firstView, int firstPosition, View secondView, int secondPosition);
    }

    private OnViewSwapListener swapListener;

    private LayoutTransition layoutTransition;

    /**
     * Mapping from child index to drag-related info container.
     * Presence of mapping implies the child can be dragged, and is considered for swaps with the
     * currently dragged item.
     */
    private final SparseArray<DraggableChild> draggableChildren;

    /**
     * The currently dragged item, if {@link DragItem#detecting}.
     */
    private final DragItem draggedItem;
    private final int slop;

    private static final int INVALID_POINTER_ID = -1;
    private int downY = -1;
    private int activePointerId = INVALID_POINTER_ID;

    /**
     * See {@link #setContainerScrollView(NestedScrollView)}.
     */
    private NestedScrollView containerScrollView;
    private int scrollSensitiveAreaHeight;
    private static final int DEFAULT_SCROLL_SENSITIVE_AREA_HEIGHT_DP = 48;
    private static final int MAX_DRAG_SCROLL_SPEED = 16;

    public DragLinearLayout(Context context) {
        this(context, null);
    }

    public DragLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);

        draggableChildren = new SparseArray<>();

        draggedItem = new DragItem();

        ViewConfiguration vc = ViewConfiguration.get(context);
        slop = vc.getScaledTouchSlop();

        final Resources resources = getResources();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DragLinearLayout, 0, 0);
        try {
            scrollSensitiveAreaHeight = a.getDimensionPixelSize(R.styleable.DragLinearLayout_scrollSensitiveHeight,
                    (int) (DEFAULT_SCROLL_SENSITIVE_AREA_HEIGHT_DP * resources.getDisplayMetrics().density + 0.5f));
        } finally {
            a.recycle();
        }

        nominalDistanceScaled = (int) (NOMINAL_DISTANCE * resources.getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void setOrientation(int orientation) {
        // enforce VERTICAL orientation; remove if HORIZONTAL support is ever added
        if (LinearLayout.HORIZONTAL == orientation) {
            throw new IllegalArgumentException("DragLinearLayout must be VERTICAL.");
        }
        super.setOrientation(orientation);
    }

    /**
     * Makes the child a candidate for dragging. Must be an existing child of this layout.
     */
    public void setViewDraggable(View child, View dragHandle) {
        if (null == child || null == dragHandle) {
            throw new IllegalArgumentException(
                    "Draggable children and their drag handles must not be null.");
        }

        if (this == child.getParent()) {
            dragHandle.setOnTouchListener(new DragHandleOnTouchListener(child));
            draggableChildren.put(indexOfChild(child), new DraggableChild());
        } else {
            Log.e(LOG_TAG, child + " is not a child, cannot make draggable.");
        }
    }

    /**
     * Calls {@link #addView(View)} followed by {@link #setViewDraggable(View, View)}.
     */
    public void addDragView(View child, View dragHandle) {
        addView(child);
        setViewDraggable(child, dragHandle);
    }

    /**
     * Calls {@link #addView(View, int)} followed by
     * {@link #setViewDraggable(View, View)} and correctly updates the
     * drag-ability state of all existing views.
     */
    public void addDragView(View child, View dragHandle, int index) {
        addView(child, index);

        // update drag-able children mappings
        final int numMappings = draggableChildren.size();
        for (int i = numMappings - 1; i >= 0; i--) {
            final int key = draggableChildren.keyAt(i);
            if (key >= index) {
                draggableChildren.put(key + 1, draggableChildren.get(key));
            }
        }

        setViewDraggable(child, dragHandle);
    }

    /**
     * Calls {@link #removeView(View)} and correctly updates the drag-ability state of
     * all remaining views.
     */
    public void removeDragView(View child) {
        if (this == child.getParent()) {
            final int index = indexOfChild(child);
            removeView(child);

            // update drag-able children mappings
            final int mappings = draggableChildren.size();
            for (int i = 0; i < mappings; i++) {
                final int key = draggableChildren.keyAt(i);
                if (key >= index) {
                    DraggableChild next = draggableChildren.get(key + 1);
                    if (null == next) {
                        draggableChildren.delete(key);
                    } else {
                        draggableChildren.put(key, next);
                    }
                }
            }
        }
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        draggableChildren.clear();
    }

    /**
     * If this layout is within a {@link NestedScrollView}, register it here so that it
     * can be scrolled during item drags.
     */
    public void setContainerScrollView(NestedScrollView scrollView) {
        this.containerScrollView = scrollView;
    }

    /**
     * Sets the height from upper / lower edge at which a container {@link NestedScrollView},
     * if one is registered via {@link #setContainerScrollView(NestedScrollView)},
     * is scrolled.
     */
    public void setScrollSensitiveHeight(int height) {
        this.scrollSensitiveAreaHeight = height;
    }

    public int getScrollSensitiveHeight() {
        return scrollSensitiveAreaHeight;
    }

    /**
     * See {@link OnViewSwapListener}.
     */
    public void setOnViewSwapListener(OnViewSwapListener swapListener) {
        this.swapListener = swapListener;
    }

    /**
     * A linear relationship b/w distance and duration, bounded.
     */
    private long getTranslateAnimationDuration(float distance) {
        return Math.min(MAX_SWITCH_DURATION, Math.max(MIN_SWITCH_DURATION,
                (long) (NOMINAL_SWITCH_DURATION * Math.abs(distance) / nominalDistanceScaled)));
    }

    private void startDrag() {
        // remove layout transition, it conflicts with drag animation
        // we will restore it after drag animation end, see onDragStop()
        layoutTransition = getLayoutTransition();
        if (layoutTransition != null) {
            setLayoutTransition(null);
        }

        draggedItem.onDragStart();
        requestDisallowInterceptTouchEvent(true);
    }

    /**
     * Updates the dragged item with the given total offset from its starting position.
     * Evaluates and executes draggable view swaps.
     */
    private void onDrag(final int offset) {
        draggedItem.setTotalOffset(offset);
        invalidate();

        int currentTop = draggedItem.startTop + draggedItem.totalDragOffset;

        handleContainerScroll(currentTop);

        int belowPosition = nextDraggablePosition(draggedItem.position);
        int abovePosition = previousDraggablePosition(draggedItem.position);

        View belowView = getChildAt(belowPosition);
        View aboveView = getChildAt(abovePosition);

        final boolean isBelow = (belowView != null) &&
                (currentTop + draggedItem.height > belowView.getTop() + belowView.getHeight() / 2);
        final boolean isAbove = (aboveView != null) &&
                (currentTop < aboveView.getTop() + aboveView.getHeight() / 2);

        if (isBelow || isAbove) {
            final View switchView = isBelow ? belowView : aboveView;

            // swap elements
            final int originalPosition = draggedItem.position;
            final int switchPosition = isBelow ? belowPosition : abovePosition;

            draggableChildren.get(switchPosition).cancelExistingAnimation();
            final float switchViewStartY = switchView.getY();

            if (null != swapListener) {
                swapListener.onSwap(draggedItem.view, draggedItem.position, switchView, switchPosition);
            }

            if (isBelow) {
                removeViewAt(originalPosition);
                removeViewAt(switchPosition - 1);

                addView(belowView, originalPosition);
                addView(draggedItem.view, switchPosition);
            } else {
                removeViewAt(switchPosition);
                removeViewAt(originalPosition - 1);

                addView(draggedItem.view, switchPosition);
                addView(aboveView, originalPosition);
            }
            draggedItem.position = switchPosition;

            final ViewTreeObserver switchViewObserver = switchView.getViewTreeObserver();
            switchViewObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    switchViewObserver.removeOnPreDrawListener(this);

                    final ObjectAnimator switchAnimator = ObjectAnimator.ofFloat(switchView, "y",
                            switchViewStartY, switchView.getTop())
                            .setDuration(getTranslateAnimationDuration(switchView.getTop() - switchViewStartY));
                    switchAnimator.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            draggableChildren.get(originalPosition).swapAnimation = switchAnimator;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            draggableChildren.get(originalPosition).swapAnimation = null;
                        }
                    });
                    switchAnimator.start();

                    return true;
                }
            });

            final ViewTreeObserver observer = draggedItem.view.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    draggedItem.updateTargetTop();

                    // TODO test if still necessary..
                    // because draggedItem#view#getTop() is only up-to-date NOW
                    // (and not right after the #addView() swaps above)
                    // we may need to update an ongoing settle animation
                    if (draggedItem.settling()) {
                        Log.d(LOG_TAG, "Updating settle animation");
                        draggedItem.settleAnimation.removeAllListeners();
                        draggedItem.settleAnimation.cancel();
                        onDragStop();
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Animates the dragged item to its final resting position.
     */
    private void onDragStop() {
        draggedItem.settleAnimation = ValueAnimator.ofFloat(draggedItem.totalDragOffset,
                draggedItem.totalDragOffset - draggedItem.targetTopOffset)
                .setDuration(getTranslateAnimationDuration(draggedItem.targetTopOffset));
        draggedItem.settleAnimation.addUpdateListener(animation -> {
            if (!draggedItem.detecting) return; // already stopped

            draggedItem.setTotalOffset(((Float) animation.getAnimatedValue()).intValue());

            invalidate();
        });
        draggedItem.settleAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                draggedItem.onDragStop();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!draggedItem.detecting) {
                    return; // already stopped
                }

                draggedItem.settleAnimation = null;
                draggedItem.stopDetecting();

                // restore layout transition
                if (layoutTransition != null && getLayoutTransition() == null) {
                    setLayoutTransition(layoutTransition);
                }
            }
        });
        draggedItem.settleAnimation.start();
    }

    /**
     * By Ken Perlin. See <a href="http://en.wikipedia.org/wiki/Smoothstep">Smoothstep - Wikipedia</a>.
     */
    private static float smootherStep(float edge1, float edge2, float val) {
        val = Math.max(0, Math.min((val - edge1) / (edge2 - edge1), 1));
        return val * val * val * (val * (val * 6 - 15) + 10);
    }

    private int previousDraggablePosition(int position) {
        int startIndex = draggableChildren.indexOfKey(position);
        if (startIndex < 1 || startIndex > draggableChildren.size()) return -1;
        return draggableChildren.keyAt(startIndex - 1);
    }

    private int nextDraggablePosition(int position) {
        int startIndex = draggableChildren.indexOfKey(position);
        if (startIndex < -1 || startIndex > draggableChildren.size() - 2) return -1;
        return draggableChildren.keyAt(startIndex + 1);
    }

    private Runnable dragUpdater;

    private void handleContainerScroll(final int currentTop) {
        if (null != containerScrollView) {
            final int startScrollY = containerScrollView.getScrollY();
            final int absTop = getTop() - startScrollY + currentTop;
            final int height = containerScrollView.getHeight();

            final int delta;

            if (absTop < scrollSensitiveAreaHeight) {
                delta = (int) (-MAX_DRAG_SCROLL_SPEED * smootherStep(scrollSensitiveAreaHeight, 0, absTop));
            } else if (absTop > height - scrollSensitiveAreaHeight) {
                delta = (int) (MAX_DRAG_SCROLL_SPEED * smootherStep(height - scrollSensitiveAreaHeight, height, absTop));
            } else {
                delta = 0;
            }

            containerScrollView.removeCallbacks(dragUpdater);
            // NestedScrollView需要执行fling，再调用smothScrollBy才能正常滚动
            containerScrollView.fling(delta);
            containerScrollView.smoothScrollBy(0, delta);
            dragUpdater = () -> {
                if (draggedItem.dragging && startScrollY != containerScrollView.getScrollY()) {
                    onDrag(draggedItem.totalDragOffset + delta);
                }
            };
            containerScrollView.post(dragUpdater);
        }
    }

    /**
     * Initiates a new {@link #draggedItem} unless the current one is still
     * {@link DragItem#detecting}.
     */
    private void startDetectingDrag(View child) {
        if (draggedItem.detecting)
            return; // existing drag in process, only one at a time is allowed

        final int position = indexOfChild(child);

        // complete any existing animations, both for the newly selected child and the previous dragged one
        draggableChildren.get(position).endExistingAnimation();

        draggedItem.startDetectingOnPossibleDrag(child, position);
        if (containerScrollView != null) {
            containerScrollView.requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        if (draggedItem.detecting && (draggedItem.dragging || draggedItem.settling())) {
            canvas.save();
            canvas.translate(0, draggedItem.totalDragOffset);
            draggedItem.viewDrawable.draw(canvas);
            draggedItem.viewDrawable.setAlpha((int) (255 * 0.6));

            canvas.restore();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (draggedItem.detecting) return false; // an existing item is (likely) settling
                downY = (int) MotionEventCompat.getY(event, 0);
//                activePointerId = MotionEventCompat.getPointerId(event, 0);
                activePointerId = event.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!draggedItem.detecting) return false;
                if (INVALID_POINTER_ID == activePointerId) break;
                final int pointerIndex = event.findPointerIndex(activePointerId);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final float dy = y - downY;
                if (Math.abs(dy) > slop) {
                    startDrag();
                    return true;
                }
                return false;
            }
            case MotionEvent.ACTION_POINTER_UP: {
//                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerIndex = event.getActionIndex();
//                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId != activePointerId)
                    break; // if active pointer, fall through and cancel!
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                onTouchEnd();

                if (draggedItem.detecting) draggedItem.stopDetecting();
                break;
            }
        }

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!draggedItem.detecting || draggedItem.settling()) return false;
                startDrag();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!draggedItem.dragging) break;
                if (INVALID_POINTER_ID == activePointerId) break;

                int pointerIndex = event.findPointerIndex(activePointerId);
//                int lastEventY = (int) MotionEventCompat.getY(event, pointerIndex);
                int lastEventY = (int) event.getY(pointerIndex);
                int deltaY = lastEventY - downY;

                onDrag(deltaY);
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP: {
//                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerIndex = event.getActionIndex();
//                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId != activePointerId)
                    break; // if active pointer, fall through and cancel!
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                onTouchEnd();

                if (draggedItem.dragging) {
                    onDragStop();
                } else if (draggedItem.detecting) {
                    draggedItem.stopDetecting();
                }
                return true;
            }
        }
        return false;
    }

    private void onTouchEnd() {
        downY = -1;
        activePointerId = INVALID_POINTER_ID;
    }

    /**
     * @return a bitmap showing a screenshot of the view passed in.
     */
    private static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private BitmapDrawable getDragDrawable(View view) {
        int top = view.getTop();
        int left = view.getLeft();

        Bitmap bitmap = getBitmapFromView(view);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

        drawable.setBounds(new Rect(left, top, left + view.getWidth(), top + view.getHeight()));

        return drawable;
    }

    private class DraggableChild {

        /**
         * If non-null, a reference to an on-going position animation.
         */
        private ValueAnimator swapAnimation;

        public void endExistingAnimation() {
            if (null != swapAnimation) swapAnimation.end();
        }

        public void cancelExistingAnimation() {
            if (null != swapAnimation) swapAnimation.cancel();
        }
    }

    /**
     * Holds state information about the currently dragged item.
     * <p/>
     * Rough lifecycle:
     * <li>#startDetectingOnPossibleDrag - #detecting == true</li>
     * <li>     if drag is recognised, #onDragStart - #dragging == true</li>
     * <li>     if drag ends, #onDragStop - #dragging == false, #settling == true</li>
     * <li>if gesture ends without drag, or settling finishes, #stopDetecting - #detecting == false</li>
     */
    private class DragItem {

        private View view;
        private int startVisibility;
        private BitmapDrawable viewDrawable;
        private int position;
        private int startTop;
        private int height;
        private int totalDragOffset;
        private int targetTopOffset;
        private ValueAnimator settleAnimation;

        private boolean detecting;
        private boolean dragging;

        public DragItem() {
            stopDetecting();
        }

        public void startDetectingOnPossibleDrag(final View view, final int position) {
            this.view = view;
            this.startVisibility = view.getVisibility();
            this.viewDrawable = getDragDrawable(view);
            this.position = position;
            this.startTop = view.getTop();
            this.height = view.getHeight();
            this.totalDragOffset = 0;
            this.targetTopOffset = 0;
            this.settleAnimation = null;

            this.detecting = true;
        }

        public void onDragStart() {
            view.setVisibility(View.INVISIBLE);
            this.dragging = true;
        }

        public void setTotalOffset(int offset) {
            totalDragOffset = offset;
            updateTargetTop();
        }

        public void updateTargetTop() {
            targetTopOffset = startTop - view.getTop() + totalDragOffset;
        }

        public void onDragStop() {
            this.dragging = false;
        }

        public boolean settling() {
            return null != settleAnimation;
        }

        public void stopDetecting() {
            this.detecting = false;
            if (null != view) view.setVisibility(startVisibility);
            view = null;
            startVisibility = -1;
            viewDrawable = null;
            position = -1;
            startTop = -1;
            height = -1;
            totalDragOffset = 0;
            targetTopOffset = 0;
            if (null != settleAnimation) settleAnimation.end();
            settleAnimation = null;
        }
    }

    private class DragHandleOnTouchListener implements OnTouchListener {

        private final View view;

        public DragHandleOnTouchListener(final View view) {
            this.view = view;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == MotionEventCompat.getActionMasked(event)) {
                startDetectingDrag(view);
            }
            return false;
        }
    }
}
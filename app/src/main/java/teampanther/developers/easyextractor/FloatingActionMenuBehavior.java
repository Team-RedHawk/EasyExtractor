package teampanther.developers.easyextractor;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private int accumulator = 0;
    private int threshold = 0;

    public FloatingActionMenuBehavior() {
        super();
    }

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        threshold = child.getHeight() / 2;
        return true;
    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if ((accumulator * dyConsumed) < 0) { //scroll direction change
            accumulator = 0;
        }
        accumulator += dyConsumed;

        if (accumulator > threshold && !child.isHidden()) {
            child.hide(true);
        } else if (accumulator < -threshold && child.isHidden()) {
            child.show(true);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
        accumulator = 0;
    }

}


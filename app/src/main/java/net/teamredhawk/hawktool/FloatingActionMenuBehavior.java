package net.teamredhawk.hawktool;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    public FloatingActionMenuBehavior() {
        super();
    }

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        return true;
    }


    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
                               final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && !child.isHidden()) {
            child.hide(true);
        } else if (dyConsumed <0 && child.isHidden()) {
            child.show(true);
        }
    }

}


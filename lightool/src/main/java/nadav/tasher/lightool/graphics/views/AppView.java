package nadav.tasher.lightool.graphics.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class AppView extends FrameLayout {
    FrameLayout content;
    LinearLayout scrolly;
    DragNavigation dragNavigation;
    int currentColor = Color.WHITE;

    public AppView(Context context, Drawable icon, int dragColor) {
        super(context);
        dragNavigation = new DragNavigation(context, icon, dragColor);
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content = new FrameLayout(context);
        scrolly = new LinearLayout(context);
        scrolly.setOrientation(LinearLayout.VERTICAL);
        scrolly.setGravity(Gravity.CENTER);
        scrollView.addView(scrolly);
        scrolly.addView(new View(context), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dragNavigation.spacerSize()));
        scrolly.addView(content);
        addView(scrollView);
        addView(dragNavigation);
    }

    public void setOnState(DragNavigation.OnStateChangedListener onState){
        dragNavigation.setOnStateChangedListener(onState);
    }

    public void setOnIconClick(OnClickListener onIconClick){
        dragNavigation.setOnIconClick(onIconClick);
    }

    public void overlaySelf(Window w) {
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(dragNavigation.calculateOverlayedColor(currentColor));
        w.setNavigationBarColor(currentColor);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        currentColor = color;
    }
}

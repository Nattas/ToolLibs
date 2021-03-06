package nadav.tasher.lightool.graphics.views.appview.navigation.corner;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class CornerView extends FrameLayout {

    private LinearLayout topLeft, topRight, bottomLeft, bottomRight;

    public CornerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Init Views
        topLeft = new LinearLayout(getContext());
        topRight = new LinearLayout(getContext());
        bottomLeft = new LinearLayout(getContext());
        bottomRight = new LinearLayout(getContext());
        // Set LayoutDirection
        topLeft.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        topRight.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        bottomLeft.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        bottomRight.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        // Set Orientation
        topLeft.setOrientation(LinearLayout.VERTICAL);
        topRight.setOrientation(LinearLayout.VERTICAL);
        bottomLeft.setOrientation(LinearLayout.VERTICAL);
        bottomRight.setOrientation(LinearLayout.VERTICAL);
        // Set Gravity
        topLeft.setGravity(Gravity.TOP | Gravity.START);
        topRight.setGravity(Gravity.TOP | Gravity.END);
        bottomLeft.setGravity(Gravity.BOTTOM | Gravity.START);
        bottomRight.setGravity(Gravity.BOTTOM | Gravity.END);
        // Set Size
        LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        topLeft.setLayoutParams(parameters);
        topRight.setLayoutParams(parameters);
        bottomLeft.setLayoutParams(parameters);
        bottomRight.setLayoutParams(parameters);
        // Set Padding
        // Add To Layout
        addView(topLeft);
        addView(topRight);
        addView(bottomLeft);
        addView(bottomRight);
    }

    public void setTopLeft(Corner s) {
        if (s == null) {
            topLeft.removeAllViews();
        } else {
            s.setLocation(Corner.TOP_LEFT);
            topLeft.removeAllViews();
            topLeft.addView(s);
        }
    }

    public void setTopRight(Corner s) {
        if (s == null) {
            topRight.removeAllViews();
        } else {
            s.setLocation(Corner.TOP_RIGHT);
            topRight.removeAllViews();
            topRight.addView(s);
        }
    }

    public void setBottomLeft(Corner s) {
        if (s == null) {
            bottomLeft.removeAllViews();
        } else {
            s.setLocation(Corner.BOTTOM_LEFT);
            bottomLeft.removeAllViews();
            bottomLeft.addView(s);
        }
    }

    public void setBottomRight(Corner s) {
        if (s == null) {
            bottomRight.removeAllViews();
        } else {
            s.setLocation(Corner.BOTTOM_RIGHT);
            bottomRight.removeAllViews();
            bottomRight.addView(s);
        }
    }
}

package com.mspsfe.hocapp;

import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyItemDragListener implements View.OnDragListener {
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:

                return  true;

            case DragEvent.ACTION_DRAG_LOCATION:

                return  true;

            case DragEvent.ACTION_DROP:
                ViewGroup parent = (ViewGroup) view.getParent();
                View localState = (View) dragEvent.getLocalState();
                if (parent.getId() == R.id.mainLayout) {
                    LinearLayout newLayout = makeNewLayout(view);
                    parent.addView(newLayout);
                    changeLayout(view, newLayout);
                    changeLayout(localState, newLayout);
                }else {
                    ViewGroup localParent = (ViewGroup) localState.getParent();
                    int index = parent.indexOfChild(view);
                    //if (parent == localParent && index > parent.indexOfChild(localParent)) index--;
                    changeLayout(localState, (LinearLayout) parent, index + 1);
                }
                return  true;

            case DragEvent.ACTION_DRAG_EXITED:

                return  true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;
        }
        return false;
    }

    private LinearLayout makeNewLayout(View view) {
        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setX(view.getX());
        layout.setY(view.getY());
        layout.setTag("Layout");
        return layout;
    }

    private void changeLayout(View view, LinearLayout newLayout) {
        ((ViewGroup)view.getParent()).removeView(view);
        view.setX(0);
        view.setY(0);
        view.setVisibility(View.VISIBLE);
        newLayout.addView(view);
    }

    private void changeLayout(View view, LinearLayout newLayout, int index) {
        ((ViewGroup)view.getParent()).removeView(view);
        view.setX(0);
        view.setY(0);
        view.setVisibility(View.VISIBLE);
        newLayout.addView(view, index);
    }
}

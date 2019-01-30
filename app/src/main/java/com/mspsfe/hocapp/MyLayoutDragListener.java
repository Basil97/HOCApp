package com.mspsfe.hocapp;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MyLayoutDragListener implements View.OnDragListener {

    private float lastX;
    private float lastY;

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        final int action = dragEvent.getAction();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                lastX = dragEvent.getX();
                lastY = dragEvent.getY();
                return true;

            case DragEvent.ACTION_DROP:
                View localState = (View) dragEvent.getLocalState();
                if (view != localState.getParent()) {
                    changeLayout(localState, (RelativeLayout) view);
                }
                if (lastX < localState.getWidth()/2) lastX = localState.getWidth()/2;
                localState.setX(lastX - localState.getWidth()/2);
                if (lastY < localState.getHeight()/2) lastY = localState.getHeight()/2 + 5;
                localState.setY(lastY - localState.getHeight()/2);
                localState.setVisibility(View.VISIBLE);
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                if (lastY < 10) {
                    View v = (View) dragEvent.getLocalState();
                    v.setX(lastX - v.getWidth()/2);
                    v.setY(10);
                    v.setVisibility(View.VISIBLE);
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;
        }
        return false;
    }

    private void changeLayout(View view, RelativeLayout parent) {
        ((ViewGroup)view.getParent()).removeView(view);
        view.setX(0);
        view.setY(0);
        view.setVisibility(View.VISIBLE);
        parent.addView(view);
    }
}

package com.mspsfe.hocapp;

import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
                if (localState.getTag() == "Layout") {
                    if (parent.getId() == R.id.mainLayout) {
                        localState.setX(view.getX());
                        localState.setY(view.getY());
                        changeLayout(view, (LinearLayout) localState, 0);
                        localState.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(view.getContext(), "Drop Item by Item Better", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }else {
                    if (parent.getId() == R.id.mainLayout) {
                        LinearLayout newLayout = makeNewLayout(view);
                        parent.addView(newLayout);
                        changeLayout(view, newLayout);
                        changeLayout(localState, newLayout);
                    } else {
                        ViewGroup localParent = (ViewGroup) localState.getParent();
                        int index = parent.indexOfChild(view);
                        if (localParent == parent && index > parent.indexOfChild(localParent)) {
                            changeLayout(localState, (LinearLayout) parent, index);
                            localState.setVisibility(View.VISIBLE);
                        } else {
                            changeLayout(localState, (LinearLayout) parent, index + 1);
                            localState.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return  true;

            case DragEvent.ACTION_DRAG_EXITED:

                return  true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;
        }
        return false;
    }

    public static LinearLayout makeNewLayout(View view) {
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setX(view.getX());
        layout.setY(view.getY());
        layout.setDividerPadding(5);
        layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        layout.setTag("Layout");
        layout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        ((View)dragEvent.getLocalState()).setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        layout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View view, View view1) {

            }

            @Override
            public void onChildViewRemoved(View view, View child) {
                if (layout.getChildCount() == 1) {
                    View lastView = layout.getChildAt(0);
                    lastView.setX(layout.getX());
                    lastView.setY(layout.getY());
                    ViewGroup root = (ViewGroup) layout.getParent();
                    root.removeView(layout);
                    layout.removeView(lastView);
                    root.addView(lastView);
                }
            }
        });
        return layout;
    }

    public static void changeLayout(View view, LinearLayout newLayout) {
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

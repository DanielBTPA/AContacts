package br.alphap.acontacts.util;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onScrollUp();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onScrollDown();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
            onScrolled(scrolledDistance += dy);
        }
    }

    public abstract void onScrollUp();

    public abstract void onScrollDown();

    public abstract void onScrolled(int distance);
}
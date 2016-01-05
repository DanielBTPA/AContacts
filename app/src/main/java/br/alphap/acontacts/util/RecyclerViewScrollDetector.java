package br.alphap.acontacts.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener {
    private static final int THRESHOLD = 5;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > THRESHOLD) {
            onScrollDown();
        } else if (dy < -THRESHOLD) {
            onScrollUp();
        }
    }

    public abstract void onScrollUp();

    public abstract void onScrollDown();
}
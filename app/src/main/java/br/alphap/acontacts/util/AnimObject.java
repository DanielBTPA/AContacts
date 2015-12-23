package br.alphap.acontacts.util;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import br.alphap.acontacts.R;

/**
 * Created by danielbt on 22/12/15.
 */
public class AnimObject {

    private AnimObject() {
    }

    private RecyclerViewScrollDetector detector;

    public void show() {
        detector.onScrollDown();

    }

    public void hide() {
        detector.onScrollUp();
    }

    public static AnimObject animationFab(final Context context, RecyclerView recyclerView, final FloatingActionButton fab) {
        AnimObject animObject = new AnimObject();

        animObject.detector = new RecyclerViewScrollDetector() {
            @Override
            public void onScrollUp() {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_show_fab);
                fab.startAnimation(animation);
            }

            @Override
            public void onScrollDown() {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_hide_fab);
                fab.startAnimation(animation);
            }

            @Override
            public void onScrolled(int distance) {

            }
        };


        recyclerView.addOnScrollListener(animObject.detector);

        return animObject;
    }

}

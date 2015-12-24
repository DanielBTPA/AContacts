package br.alphap.acontacts.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import br.alphap.acontacts.R;

/**
 * Created by danielbt on 22/12/15.
 */
public class AnimObject {

    public static final int[] RECYCLER_ANIM_FAB_TRANSLATE = {R.anim.anim_translate_show_fab, R.anim.anim_translate_hide_fab};
    public static final int[] RECYCLER_ANIM_TOOLBAR_TRANSLATE = {R.anim.anim_translate_show_toolbar, R.anim.anim_translate_hide_toolbar};

    private View viewToAnimation;
    private int[] resources;
    private boolean isHide;

    private AnimObject() {
    }

    public void show() {
        viewShow();
    }

    public void hide() {
        viewHide();
    }

    public static AnimObject animateOnRecyclerView(final RecyclerView recyclerView, final View viewToAnimation, final int[] resources) {
        final AnimObject animObject = new AnimObject();
        animObject.viewToAnimation = viewToAnimation;
        animObject.resources = resources;

        recyclerView.addOnScrollListener(new RecyclerViewScrollDetector() {
            @Override
            public void onScrollUp() {
                animObject.viewHide();
            }

            @Override
            public void onScrollDown() {
                animObject.viewShow();
            }
        });

        return animObject;
    }

    public void reset() {
        Animation animation = viewToAnimation.getAnimation();

        if (animation.isInitialized() && isHide) {
            viewToAnimation.clearAnimation();
            viewHide();
        }
    }

    private void viewShow() {
        Animation animation = AnimationUtils.loadAnimation(viewToAnimation.getContext(), resources[0]);
        viewToAnimation.startAnimation(animation);
    }

    private void viewHide() {
        isHide = true;

        Animation animation = AnimationUtils.loadAnimation(viewToAnimation.getContext(), resources[1]);
        viewToAnimation.startAnimation(animation);
    }
}

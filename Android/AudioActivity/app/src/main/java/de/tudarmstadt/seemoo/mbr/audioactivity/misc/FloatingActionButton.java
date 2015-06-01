/*
 * Copyright (c) 2015, Markus Brandt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.tudarmstadt.seemoo.mbr.audioactivity.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import de.tudarmstadt.seemoo.mbr.audioactivity.R;

public class FloatingActionButton extends ImageButton implements Animation.AnimationListener {
    private final Animation hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_hide);
    private final Animation showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_show);
    private final Animation rotInAnim = AnimationUtils.loadAnimation(getContext(),
            R.anim.fab_rotin);
    private final Animation rotOutAnim = AnimationUtils.loadAnimation(getContext(),
                                                                      R.anim.fab_rotout);

    public FloatingActionButton(Context context) {
        super(context);
        init();
    }

    public void setVisible() {
        setVisibility(VISIBLE);
        setClickable(true);
    }

    public void setInvisible() {
        setVisibility(GONE);
        setClickable(false);
    }

    private void init() {
        showAnim.setAnimationListener(this);
        hideAnim.setAnimationListener(this);
        rotInAnim.setAnimationListener(this);
        rotOutAnim.setAnimationListener(this);

        if (isVisible()) {
            setClickable(true);
        } else {
            setClickable(false);
        }
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("WeakerAccess")
    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (animation.equals(showAnim) || animation.equals(rotInAnim)) {
            setClickable(true);
        } else {
            setClickable(false);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(hideAnim) || animation.equals(rotOutAnim)) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void hideFor(Object... objects) {
        boolean success = false;
        for (Object o : objects) {
            if (o.getClass() == FloatingActionButton.class) {
                ((FloatingActionButton)o).rotIn();
                success = true;
            }
        }

        if (success) {
            rotOut();
        }
    }

    private void rotIn() {
        setVisibility(VISIBLE);
        startAnimation(rotInAnim);
    }

    private void rotOut() {
        startAnimation(rotOutAnim);
    }

    public void hide() {
        startAnimation(hideAnim);
    }

    public void show() {
        setVisibility(VISIBLE);
        startAnimation(showAnim);
        setAlpha(1.0f);
    }

    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }
}

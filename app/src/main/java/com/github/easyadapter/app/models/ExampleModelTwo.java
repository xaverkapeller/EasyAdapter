package com.github.easyadapter.app.models;

import android.animation.Animator;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.github.easyadapter.EasyAdapter;
import com.github.easyadapter.annotations.BindToView;
import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.annotations.InjectView;
import com.github.easyadapter.annotations.Layout;
import com.github.easyadapter.annotations.OnBind;
import com.github.easyadapter.annotations.OnCheckedChanged;
import com.github.easyadapter.api.ViewModel;
import com.github.easyadapter.app.R;
import com.github.easyadapter.app.utils.AnimationUtils;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/02/15
 */
@Layout(R.layout.list_item_two)
public class ExampleModelTwo implements ViewModel {

    private final int textResId;

    private boolean checkedState = false;
    private boolean animateExpand = false;
    private boolean animateCollapse = false;

    public ExampleModelTwo(int textResId) {
        this.textResId = textResId;
    }

    @OnBind
    public void bind(@InjectView(R.id.frameLayout) final FrameLayout frameLayout) {
        if (animateExpand) {
            animateExpand = false;
            frameLayout.setVisibility(View.VISIBLE);
            AnimationUtils.animate(frameLayout).height(200).setDuration(300).start();
        } else if (animateCollapse) {
            animateCollapse = false;
            frameLayout.setVisibility(View.VISIBLE);
            Animator animator = AnimationUtils.animate(frameLayout).height(0).setDuration(300);
            animator.addListener(new AnimationUtils.AnimatorEndListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    frameLayout.setVisibility(View.GONE);
                }
            });
            animator.start();
        } else {
            if (checkedState) {
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.getLayoutParams().height = 200;
            } else {
                frameLayout.setVisibility(View.GONE);
                frameLayout.getLayoutParams().height = 0;
            }
        }
    }

    @BindToView(R.id.checkbox)
    public int getCheckBoxText() {
        return this.textResId;
    }

    @BindToView(R.id.checkbox)
    public boolean checkedState() {
        return checkedState;
    }

    @OnCheckedChanged(R.id.checkbox)
    public void onCheckedChanged(@Inject EasyAdapter<ViewModel> adapter, @InjectView(R.id.checkbox) CheckBox checkBox) {
        checkedState = checkBox.isChecked();
        int index = adapter.models().indexOf(this);
        if (checkedState) {
            animateExpand = true;
            animateCollapse = false;
        } else {
            animateExpand = false;
            animateCollapse = true;
        }
        adapter.notifyItemChanged(index);
    }
}

package com.github.easyadapter.annotations;

import com.github.easyadapter.api.ViewModel;

/**
 * Created by kapeller on 14/04/15.
 */
public @interface Listener {
    public Class<? extends ViewModel> value();
}

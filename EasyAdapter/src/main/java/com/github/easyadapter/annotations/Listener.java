package com.github.easyadapter.annotations;

import com.github.easyadapter.api.ViewModel;

/**
 * Defines that Objects which implement or extend the annotated class need to be called in response
 * some action of the supplied view model.
 */
public @interface Listener {
    public Class<? extends ViewModel> value();
}

package com.github.easyadapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/05/15
 */
class InjectorImpl implements EasyAdapter.Injector {

    private final List<Object> mInjected = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> cls) {
        for (Object object : mInjected) {
            if (cls.isAssignableFrom(object.getClass())) {
                return (T) object;
            }
        }
        return null;
    }

    public void add(Object object) {
        if (object != null) {
            mInjected.add(object);
        }
    }
}

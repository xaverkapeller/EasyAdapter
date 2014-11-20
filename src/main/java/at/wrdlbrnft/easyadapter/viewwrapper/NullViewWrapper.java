package at.wrdlbrnft.easyadapter.viewwrapper;

import android.view.View;

/**
 * Created by Xaver on 16/11/14.
 */
class NullViewWrapper implements ViewWrapper {

    @Override
    public boolean bind(Property property, Class<?> valueClass, Object value) {
        return false;
    }

    @Override
    public View getView() {
        return null;
    }
}

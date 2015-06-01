package com.github.easyadapter.processor.builder.viewholder;

import com.github.easyadapter.codewriter.elements.Type;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/06/15
 */
class ViewHolderInfoImpl implements ViewHolderInfo {

    private final Type mViewHolderType;
    private final List<Type> mConstructorParameterList;

    public ViewHolderInfoImpl(Type viewHolderType, List<Type> constructorParameterList) {
        mViewHolderType = viewHolderType;
        mConstructorParameterList = constructorParameterList;
    }

    @Override
    public List<Type> getConstructorParameters() {
        return mConstructorParameterList;
    }

    @Override
    public Type getViewHolderType() {
        return mViewHolderType;
    }
}

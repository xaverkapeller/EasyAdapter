package com.github.easyadapter.processor.builder.viewholder;

import com.github.easyadapter.codewriter.elements.Type;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/06/15
 */
public interface ViewHolderInfo {
    public List<Type> getConstructorParameters();
    public Type getViewHolderType();
}

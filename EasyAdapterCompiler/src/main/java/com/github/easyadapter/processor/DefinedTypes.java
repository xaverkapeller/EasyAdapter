package com.github.easyadapter.processor;

import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.impl.Types;

import java.util.EnumSet;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
public class DefinedTypes {
    public static final Type ABS_VIEW_HOLDER = Types.create("com.github.easyadapter.impl", "AbsViewHolder", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT), Types.Android.RECYCLERVIEW_VIEWHOLDER);
    public static final Type ABS_VIEW_HOLDER_FACTORY = Types.create("com.github.easyadapter.impl", "AbsViewHolderFactory", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT), Types.OBJECT);
    public static final Type INJECTOR = Types.create("com.github.easyadapter.EasyAdapter", "Injector", EnumSet.of(Modifier.PUBLIC), Types.OBJECT);
    public static final Type VIEW_MODEL = Types.create("com.github.easyadapter.api", "ViewModel", EnumSet.of(Modifier.PUBLIC), Types.OBJECT);
    public static final Type FORMATER = Types.create("com.github.easyadapter.api", "Formater", EnumSet.of(Modifier.PUBLIC), null);
}

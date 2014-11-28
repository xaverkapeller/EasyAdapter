package at.wrdlbrnft.easyadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import at.wrdlbrnft.easyadapter.annotations.BindToView;
import at.wrdlbrnft.easyadapter.annotations.Inject;
import at.wrdlbrnft.easyadapter.annotations.InjectView;
import at.wrdlbrnft.easyadapter.annotations.OnBind;
import at.wrdlbrnft.easyadapter.annotations.OnCheckedChanged;
import at.wrdlbrnft.easyadapter.annotations.OnClick;
import at.wrdlbrnft.easyadapter.annotations.OnUnbind;
import at.wrdlbrnft.easyadapter.exceptions.AnnotationMismatchException;
import at.wrdlbrnft.easyadapter.helper.ReflectionHelper;
import at.wrdlbrnft.easyadapter.model.ViewModel;
import at.wrdlbrnft.easyadapter.enums.Property;
import at.wrdlbrnft.easyadapter.viewwrapper.ViewWrapper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 16/11/14
 */
class AnnotationViewHolder<T extends ViewModel> extends RecyclerView.ViewHolder {

    private final SparseArray<ViewWrapper> mViewWrappers = new SparseArray<ViewWrapper>();
    private final Class<T> mModelClass;
    private final Context mContext;
    private final EasyAdapter<T> mAdapter;
    private final Object[] mInjectedObjects;
    private final List<Action<T>> mActions = new ArrayList<Action<T>>();

    private T mCurrentModel;

    public AnnotationViewHolder(Context context, EasyAdapter<T> adapter, Object[] injectedObjects, View itemView, Class<T> modelClass) {
        super(itemView);

        mContext = context;
        mAdapter = adapter;
        mInjectedObjects = injectedObjects;
        mModelClass = modelClass;

        initialize();
    }

    private void initialize() {

        final List<Field> fields = ReflectionHelper.getAllFields(mModelClass);
        for(Field field : fields) {

            if(field.isAnnotationPresent(BindToView.class)) {
                final BindToView annotation = field.getAnnotation(BindToView.class);
                final int[] ids = annotation.id();
                final Property property = annotation.property();

                for(int viewId : ids) {
                    final ViewWrapper wrapper = getViewWrapper(viewId);
                    final Action<T> action = new BindActionImpl<>(field, property, wrapper);
                    mActions.add(action);
                }
                continue;
            }

            if(field.isAnnotationPresent(InjectView.class)) {
                final InjectView annotation = field.getAnnotation(InjectView.class);
                final int viewId = annotation.id();

                final ViewWrapper wrapper = getViewWrapper(viewId);
                final View view = wrapper.getView();
                final Action<T> action = new InjectActionImpl<>(field, view);
                mActions.add(action);
                continue;
            }

            if(field.isAnnotationPresent(Inject.class)) {
                final Class<?> classToInject = field.getType();

                final Object objectToInject = getObjectToInject(classToInject);
                final Action<T> action = new InjectActionImpl<>(field, objectToInject);
                mActions.add(action);
                continue;
            }
        }

        final Method[] methods = mModelClass.getMethods();
        for(Method method : methods) {

            if(method.isAnnotationPresent(OnBind.class)) {
                final Object[] parameters = getArgumentsForMethod(method);

                final Action<T> action = new OnBindActionImpl<>(method, parameters);
                mActions.add(action);
                continue;
            }

            if(method.isAnnotationPresent(OnUnbind.class)) {
                final Object[] parameters = getArgumentsForMethod(method);

                final Action<T> action = new OnUnbindActionImpl<>(method, parameters);
                mActions.add(action);
                continue;
            }

            if(method.isAnnotationPresent(OnClick.class)) {
                final OnClick annotation = method.getAnnotation(OnClick.class);
                final int viewId = annotation.id();
                final ViewWrapper wrapper = getViewWrapper(viewId);
                final View view = wrapper.getView();

                final Object[] parameters = getArgumentsForMethod(method);

                final Action<T> action = new OnClickActionImpl<>(view, method, parameters);
                mActions.add(action);
                continue;
            }

            if(method.isAnnotationPresent(OnCheckedChanged.class)) {
                final OnCheckedChanged annotation = method.getAnnotation(OnCheckedChanged.class);
                final int viewId = annotation.id();
                final ViewWrapper wrapper = getViewWrapper(viewId);
                final View view = wrapper.getView();

                if(!(view instanceof CheckBox)) {
                     throw new AnnotationMismatchException("A CheckBox referenced by @OnCheckedChanged could not be found or is not a CheckBox");
                }

                final CheckBox checkBox = (CheckBox) view;
                final Object[] parameters = getArgumentsForMethod(method);

                final Action<T> action = new OnCheckedChangeActionImpl<>(checkBox, method, parameters);
                mActions.add(action);
                continue;
            }
        }
    }

    private Object[] getArgumentsForMethod(Method method) {

        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Annotation[][] annotationsArray = method.getParameterAnnotations();
        final Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0, length = parameterTypes.length; i < length; i++) {
            final Class<?> cls = parameterTypes[i];
            final Annotation[] annotations = annotationsArray[i];
            Object objectToInject = null;

            for(Annotation annotation : annotations) {
                if(annotation instanceof InjectView) {
                    final InjectView injectViewAnnotation = (InjectView) annotation;
                    final int viewId = injectViewAnnotation.id();
                    final ViewWrapper wrapper = getViewWrapper(viewId);
                    objectToInject = wrapper.getView();
                    break;
                }
            }

            if(objectToInject == null) {
                objectToInject = getObjectToInject(cls);
            }

            parameters[i] = objectToInject;
        }

        return parameters;
    }

    private Object getObjectToInject(Class<?> classToInject) {
        if(classToInject == Context.class) {
            return mContext;
        }

        if(classToInject == EasyAdapter.class) {
            return mAdapter;
        }

        for (Object object : mInjectedObjects) {
            if (object == null) {
                continue;
            }

            final Class<?> cls = object.getClass();
            if (cls.isAssignableFrom(classToInject)) {
                return object;
            }
        }

        return null;
    }

    public void bind(T model) {

        if(mCurrentModel != null) {
            for (Action<T> action : mActions) {
                action.unbind(mCurrentModel);
            }
        }

        mCurrentModel = model;

        if (model == null) {
            return;
        }

        if(mModelClass != model.getClass()) {
            throw new IllegalStateException("ViewHolder has encountered an unexpected model class");
        }

        for(Action<T> action : mActions) {
            action.bind(model);
        }
    }

    protected ViewWrapper getViewWrapper(int id) {
        final ViewWrapper wrapper = mViewWrappers.get(id, null);
        if(wrapper != null) {
            return wrapper;
        }

        final View newView = itemView.findViewById(id);
        final ViewWrapper newWrapper = ViewWrapper.Factory.wrap(mContext, newView);
        mViewWrappers.put(id, newWrapper);

        return newWrapper;
    }

    private interface Action<T> {
        public void bind(T model);
        public void unbind(T model);
    }

    private static class BindActionImpl<T> implements Action<T> {

        protected final Field mField;
        protected final Class<?> mFieldType;
        protected final Property mProperty;
        protected final ViewWrapper mWrapper;

        private BindActionImpl(Field field, Property property, ViewWrapper wrapper) {
            mField = field;
            mFieldType = field.getType();
            mProperty = property;
            mWrapper = wrapper;
        }

        @Override
        public void bind(T model) {
            final Object value = ReflectionHelper.getValueOf(mField, model);

            mWrapper.bind(mProperty, mField, value);
        }

        @Override
        public void unbind(T model) {

        }
    }

    private static class InjectActionImpl<T> implements Action<T> {

        private final Field mField;
        private final Object mItem;

        private InjectActionImpl(Field field, Object item) {
            mField = field;
            mItem = item;
        }

        @Override
        public void bind(T model) {
            ReflectionHelper.setValueOf(mField, model, mItem);
        }

        @Override
        public void unbind(T model) {

        }
    }

    private static class OnBindActionImpl<T> implements Action<T> {

        private final Method mMethod;
        private final Object[] mArguments;

        private OnBindActionImpl(Method method, Object[] arguments) {
            mMethod = method;
            mArguments = arguments;
        }

        @Override
        public void bind(T model) {
            ReflectionHelper.invoke(mMethod, model, mArguments);
        }

        @Override
        public void unbind(T model) {

        }
    }

    private static class OnUnbindActionImpl<T> implements Action<T> {

        private final Method mMethod;
        private final Object[] mArguments;

        private OnUnbindActionImpl(Method method, Object[] arguments) {
            mMethod = method;
            mArguments = arguments;
        }

        @Override
        public void bind(T model) {

        }

        @Override
        public void unbind(T model) {
            ReflectionHelper.invoke(mMethod, model, mArguments);
        }
    }

    private static class OnClickActionImpl<T> implements Action<T> {

        private final View mView;
        private final Method mMethod;
        private final Object[] mArguments;

        private OnClickActionImpl(View view, Method method, Object[] arguments) {
            mView = view;
            mMethod = method;
            mArguments = arguments;
        }

        @Override
        public void bind(T model) {
            mView.setOnClickListener(new MethodClickListener(model, mMethod, mArguments));
        }

        @Override
        public void unbind(T model) {
            mView.setOnClickListener(null);
        }

        private final class MethodClickListener implements View.OnClickListener {

            private final Object mModel;
            private final Method mMethod;
            private final Object[] mArguments;

            private MethodClickListener(Object model, Method method, Object[] arguments) {
                mModel = model;
                mMethod = method;
                mArguments = arguments;
            }

            @Override
            public void onClick(View v) {
                ReflectionHelper.invoke(mMethod, mModel, mArguments);
            }
        }
    }

    private static class OnCheckedChangeActionImpl<T> implements Action<T> {

        private final CheckBox mCheckBox;
        private final Method mMethod;
        private final Object[] mArguments;

        private OnCheckedChangeActionImpl(CheckBox checkBox, Method method, Object[] arguments) {
            mCheckBox = checkBox;
            mMethod = method;
            mArguments = arguments;
        }

        @Override
        public void bind(T model) {
            mCheckBox.setOnCheckedChangeListener(new MethodCheckedChangedListener(model, mMethod, mArguments));
        }

        @Override
        public void unbind(T model) {
            mCheckBox.setOnCheckedChangeListener(null);
        }

        private final class MethodCheckedChangedListener implements CheckBox.OnCheckedChangeListener {

            private final Object mModel;
            private final Method mMethod;
            private final Object[] mArguments;

            private MethodCheckedChangedListener(Object model, Method method, Object[] arguments) {
                mModel = model;
                mMethod = method;
                mArguments = arguments;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReflectionHelper.invoke(mMethod, mModel, mArguments);
            }
        }
    }
}

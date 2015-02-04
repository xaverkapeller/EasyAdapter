package com.github.easyadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.easyadapter.api.ViewModel;
import com.github.easyadapter.impl.AbsViewHolder;
import com.github.easyadapter.impl.AbsViewHolderFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 26/01/15
 */
public class EasyAdapter<T extends ViewModel> extends RecyclerView.Adapter<AbsViewHolder<T>> {

    public interface Injector {
        public <T> T get(Class<T> cls);
    }

    private final Map<Integer, AbsViewHolderFactory<T>> mFactoryMap = new HashMap<>();
    private final InjectorImpl mInjector = new InjectorImpl();
    private final LayoutInflater mInflater;
    private List<T> mViewModels;

    public EasyAdapter(Context context, List<T> viewModels, Object... injectedObjects) {
        mInflater = LayoutInflater.from(context);
        mViewModels = viewModels;

        mInjector.add(context);
        mInjector.add(this);
        for (Object injected : injectedObjects) {
            mInjector.add(injected);
        }
    }

    @Override
    public AbsViewHolder<T> onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        final AbsViewHolderFactory<T> factory = mFactoryMap.get(itemType);
        return factory.newInstance(viewGroup);
    }

    @Override
    public void onBindViewHolder(AbsViewHolder<T> viewHolder, int position) {
        final T item = mViewModels.get(position);
        viewHolder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        final T item = mViewModels.get(position);
        final Class<?> itemClass = item.getClass();
        final int itemType = itemClass.hashCode();

        if (!mFactoryMap.containsKey(itemType)) {
            try {
                final String factoryName = itemClass.getName() + "$$ViewHolderFactory";
                final Class<? extends AbsViewHolderFactory<? extends T>> cls = (Class<? extends AbsViewHolderFactory<? extends T>>) Class.forName(factoryName);
                final AbsViewHolderFactory<T> factory = (AbsViewHolderFactory<T>) cls.getConstructor(LayoutInflater.class, Injector.class).newInstance(mInflater, mInjector);
                mFactoryMap.put(itemType, factory);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("ViewHolderFactory for " + item + " could not be found! Have you setup the Annotation Processor correctly?");
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        return itemType;
    }

    @Override
    public int getItemCount() {
        if(mViewModels != null) {
            return mViewModels.size();
        }
        return 0;
    }

    public void setModels(List<T> models) {
        mViewModels = models;
    }

    public List<T> models() {
        return mViewModels;
    }

    private static class InjectorImpl implements Injector {

        private final List<Object> mInjected = new ArrayList<>();

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
}

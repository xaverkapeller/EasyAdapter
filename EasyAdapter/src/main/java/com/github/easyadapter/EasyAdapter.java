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
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 26/01/15
 */
public class EasyAdapter<T extends ViewModel> extends RecyclerView.Adapter<AbsViewHolder<T>> {

    public interface Injector {
        public <T> T get(Class<T> cls);
    }

    private final List<RecyclerView> mAttachedRecyclerViews = new ArrayList<>(10);
    private final SparseArray<AbsViewHolderFactory<T>> mFactoryMap = new SparseArray<>();
    private final InjectorImpl mInjector = new InjectorImpl();
    private final LayoutInflater mInflater;
    private List<T> mViewModels;

    /**
     * Creates a new EasyAdapter instance.
     *
     * <p>
     *     <b><span style="color: red">Deprecated!</span> Will be removed in future versions.</b><br />
     *     Use {@link #EasyAdapter(android.content.Context, java.util.List)} instead
     *     and use {@link #inject(Object...)} to inject dependencies.
     * </p>
     *
     * @param context The {@link Context} which will be used by the {@link com.github.easyadapter.EasyAdapter} and all {@link com.github.easyadapter.impl.AbsViewHolder}.
     * @param models The initial {@link java.util.List} of view models. May be an empty list, but must not be null.
     * @param injectedObjects Will be supplied to the underlying {@link com.github.easyadapter.impl.AbsViewHolder} instances to satisfy their dependencies.
     */
    @Deprecated
    public EasyAdapter(Context context, List<T> models, Object... injectedObjects) {
        this(context, models);

        for (Object object : injectedObjects) {
            mInjector.add(object);
        }
    }

    /**
     * Creates a new EasyAdapter instance.
     *
     * @param context The {@link Context} which will be used by the {@link com.github.easyadapter.EasyAdapter} and all {@link com.github.easyadapter.impl.AbsViewHolder}.
     * @param models The initial {@link java.util.List} of view models. May be an empty list, but must not be null.
     */
    public EasyAdapter(Context context, List<T> models) {
        mInflater = LayoutInflater.from(context);
        mViewModels = models;

        mInjector.add(context);
        mInjector.add(this);
    }

    /**
     * Provides the supplied objects to the underlying {@link com.github.easyadapter.impl.AbsViewHolder} instances to satisfy their dependencies.
     * <p>
     *     <b><span style="color: red">WARNING: You can only inject Objects before setting the
     *     {@link com.github.easyadapter.EasyAdapter} to a {@link android.support.v7.widget.RecyclerView}!</span></b>
     * </p>
     *
     * @param objects The Objects to inject
     */
    public void inject(Object... objects) {
        if (mAttachedRecyclerViews.size() == 0) {
            for (Object object : objects) {
                mInjector.add(object);
            }
        } else {
            throw new IllegalStateException("This EasyAdapter is already attached to a RecyclerView! You can only inject Objects before setting the EasyAdapter to a RecyclerView!");
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

        if (mFactoryMap.get(itemType) == null) {
            try {
                final String factoryName = itemClass.getName() + "$$ViewHolderFactory";
                final Class<? extends AbsViewHolderFactory<? extends T>> cls = (Class<? extends AbsViewHolderFactory<? extends T>>) Class.forName(factoryName);
                final AbsViewHolderFactory<T> factory = (AbsViewHolderFactory<T>) cls.getConstructor(LayoutInflater.class, Injector.class).newInstance(mInflater, mInjector);
                mFactoryMap.put(itemType, factory);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("ViewHolderFactory for " + item + " could not be found! Have you setup the Annotation Processor correctly?");
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        return itemType;
    }

    @Override
    public void onViewAttachedToWindow(AbsViewHolder<T> holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttach();
    }

    @Override
    public void onViewDetachedFromWindow(AbsViewHolder<T> holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetach();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mAttachedRecyclerViews.add(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mAttachedRecyclerViews.remove(recyclerView);
    }

    @Override
    public int getItemCount() {
        if (mViewModels != null) {
            return mViewModels.size();
        }
        return 0;
    }

    /**
     * Sets the {@link java.util.List} of view models the {@link com.github.easyadapter.EasyAdapter}
     * is working with.
     * <p>
     *     <b>You need to use one of the notify methods for the changes to take effect!</b>
     * </p>
     *
     * @param models The new {@link java.util.List} of view models.
     */
    public void setModels(List<T> models) {
        mViewModels = models;
    }

    /**
     * Exposes the <b>unfiltered</b> {@link java.util.List} of view models used by the {@link com.github.easyadapter.EasyAdapter}.
     * @return The {@link java.util.List} of <b>unfiltered</b> view models.
     */
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

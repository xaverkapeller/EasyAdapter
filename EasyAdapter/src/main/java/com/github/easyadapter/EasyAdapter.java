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

    private static final String VIEW_HOLDER_FACTORY_POSTFIX = "$$ViewHolderFactory";

    public interface Injector {
        public <T> T get(Class<T> cls);
    }

    private final List<RecyclerView> mAttachedRecyclerViews = new ArrayList<>();
    private final SparseArray<AbsViewHolderFactory<T>> mFactoryMap = new SparseArray<>();
    private final InjectorImpl mInjector = new InjectorImpl();
    private final LayoutInflater mInflater;
    private List<T> mViewModels;

    /**
     * Creates a new EasyAdapter instance.
     *
     * @param context The {@link Context} which will be used by the {@link com.github.easyadapter.EasyAdapter} and all {@link com.github.easyadapter.impl.AbsViewHolder}.
     * @param models  The initial {@link java.util.List} of items. May be an empty list, but must not be null.
     */
    public EasyAdapter(Context context, List<T> models) {
        mInflater = LayoutInflater.from(context);
        mViewModels = new ArrayList<>(models);

        mInjector.add(context);
        mInjector.add(this);
    }

    /**
     * Provides the supplied objects to the underlying {@link com.github.easyadapter.impl.AbsViewHolder} instances to satisfy their dependencies.
     * <p>
     * <b><span style="color: #DA5252">WARNING: You can only inject Objects before setting the
     * {@link com.github.easyadapter.EasyAdapter} to a {@link android.support.v7.widget.RecyclerView}!</span></b>
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
            final AbsViewHolderFactory<T> factory = createViewModelFactory(itemClass);
            mFactoryMap.put(itemType, factory);
        }

        return itemType;
    }

    private AbsViewHolderFactory<T> createViewModelFactory(Class<?> itemClass) {
        try {
            return tryCreateViewModelFactory(itemClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("ViewHolderFactory could not be found! Have you setup the Annotation Processor correctly?");
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate ViewModelFactory! Have you setup the Annotation Processor correctly? If yes then rebuild your project!", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Required constructor could not be found! Have you setup the Annotation Processor correctly? If yes then rebuild your project!", e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could nto create an instance of the ViewModelFactory! Have you setup the Annotation Processor correctly? If yes then rebuild your project!", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Malformed constructor! Have you setup the Annotation Processor correctly? If yes then rebuild your project!", e);
        }
    }

    private AbsViewHolderFactory<T> tryCreateViewModelFactory(Class<?> itemClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Class<? extends AbsViewHolderFactory<T>> viewModelFactoryClass = getViewModelFactoryClass(itemClass);
        return viewModelFactoryClass
                .getConstructor(LayoutInflater.class, Injector.class)
                .newInstance(mInflater, mInjector);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends AbsViewHolderFactory<T>> getViewModelFactoryClass(Class<?> itemClass) throws ClassNotFoundException {
        final String factoryName = getViewModelFactoryClassName(itemClass);
        return (Class<? extends AbsViewHolderFactory<T>>) Class.forName(factoryName);
    }

    private String getViewModelFactoryClassName(Class<?> itemClass) {
        return itemClass.getName() + VIEW_HOLDER_FACTORY_POSTFIX;
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
        return mViewModels.size();
    }

    /**
     * Animates all items currently in the {@link RecyclerView} to match the supplied {@link List}.
     * <ul>
     *     <li>Removes all items which need to be removed</li>
     *     <li>Adds all new items which need to be added</li>
     *     <li>Moves all items which appear in different order in the supplied {@link List}</li>
     * </ul>
     *
     * The item animation defined in the {@link android.support.v7.widget.RecyclerView.ItemAnimator}
     * are automatically triggered.
     *
     * @param models The new {@link List} which should be displayed in the {@link RecyclerView}.
     */
    public void animateTo(List<T> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<T> newModels) {
        for (int i = mViewModels.size() - 1; i >= 0; i--) {
            final T model = mViewModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final T model = newModels.get(i);
            if (!mViewModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final T model = newModels.get(toPosition);
            final int fromPosition = mViewModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    /**
     * Removes an item from the {@link EasyAdapter} at the supplied position with an animation.
     *
     * @param position The position of the item which should be removed.
     * @return The {@link T} that was removed.
     */
    public T removeItem(int position) {
        final T model = mViewModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    /**
     * Adds the supplied item to the {@link EasyAdapter} at the supplied position with an animation.
     *
     * @param position The position where the item should be inserted.
     * @param model    The model which should be inserted.
     */
    public void addItem(int position, T model) {
        mViewModels.add(position, model);
        notifyItemInserted(position);
    }

    /**
     * Moves an item from one position to another with an animation.
     *
     * @param fromPosition The position of the item which should be moved.
     * @param toPosition   The target position to which the item will be moved.
     */
    public void moveItem(int fromPosition, int toPosition) {
        final T model = mViewModels.remove(fromPosition);
        mViewModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Sets the {@link java.util.List} of view models the {@link com.github.easyadapter.EasyAdapter}
     * is working with.
     * <p>
     * <b>You need to call the appropriate notify methods for the changes to take effect!</b>
     * </p>
     *
     * @param models The new {@link java.util.List} of items.
     */
    public void setModels(List<T> models) {
        mViewModels = new ArrayList<>(models);
    }

    /**
     * Exposes the {@link java.util.List} of item used by the {@link com.github.easyadapter.EasyAdapter}.
     *
     * @return The {@link java.util.List} of items.
     */
    public List<T> models() {
        return mViewModels;
    }
}

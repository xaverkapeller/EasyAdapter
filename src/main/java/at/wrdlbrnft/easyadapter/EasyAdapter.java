package at.wrdlbrnft.easyadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import at.wrdlbrnft.easyadapter.annotations.Layout;
import at.wrdlbrnft.easyadapter.exceptions.RequiredAnnotationMissingException;
import at.wrdlbrnft.easyadapter.helper.ReflectionHelper;
import at.wrdlbrnft.easyadapter.model.ViewModel;

/**
 * Created with Android Studio
 * User: Xaver Kapeller
 * Date: 14/11/14
 */
public class EasyAdapter<T extends ViewModel> extends RecyclerView.Adapter<AnnotationViewHolder<T>> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final Object[] mInjectedObjects;
    private final SparseArray<Class<T>> mClassArray = new SparseArray<Class<T>>();

    private List<T> mViewModels = null;

    public EasyAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mInjectedObjects = new Object[0];
    }

    public EasyAdapter(Context context, List<T> models) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mViewModels = models;
        mInjectedObjects = new Object[0];
    }

    public EasyAdapter(Context context, List<T> models, Object... inject) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mViewModels = models;
        mInjectedObjects = inject;
    }

    @Override
    public AnnotationViewHolder<T> onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Class<T> cls = mClassArray.get(viewType, null);

        if (!cls.isAnnotationPresent(Layout.class)) {
            throw new RequiredAnnotationMissingException("The @Layout annotation is missing from " + cls);
        }

        final Layout annotation = cls.getAnnotation(Layout.class);
        final int layoutId = annotation.id();
        final View itemView = mInflater.inflate(layoutId, viewGroup, false);

        return new AnnotationViewHolder<T>(mContext, this, mInjectedObjects, itemView, cls);
    }

    @Override
    public void onBindViewHolder(AnnotationViewHolder<T> viewHolder, int position) {
        final T model = mViewModels.get(position);

        viewHolder.bind(model);
    }

    @Override
    public int getItemViewType(int position) {
        final T model = mViewModels.get(position);
        final Class<T> cls = ReflectionHelper.getClassOf(model);

        final int hashCode = cls.hashCode();
        mClassArray.put(hashCode, cls);

        return hashCode;
    }

    @Override
    public int getItemCount() {
        if (mViewModels == null) {
            return 0;
        }

        return mViewModels.size();
    }

    public void setModels(List<T> models) {
        mViewModels = models;
    }

    public List<T> models() {
        return mViewModels;
    }
}

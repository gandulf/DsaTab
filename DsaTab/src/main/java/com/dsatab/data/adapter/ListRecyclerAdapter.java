package com.dsatab.data.adapter;

import android.animation.AnimatorInflater;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Filterable;

import com.dsatab.R;
import com.dsatab.util.Debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ListRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends BaseRecyclerAdapter<VH,T> implements Filterable  {

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter. The content of this list is referred
     * to as "the array" in the documentation.
     */
    protected List<T> mObjects;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation performed on the array should be
     * synchronized on this lock. This lock is also used by the filter (see {@link #getFilter()} to make a synchronized
     * copy of the original array of data.
     */
    final Object mLock = new Object();

    protected List<T> mOriginalValues;

    private ListRecyclerFilter<T> mFilter;

    public ListRecyclerAdapter(Collection<T> objects) {
        this.mObjects = new ArrayList<T>(objects);
    }

    protected View inflate(LayoutInflater inflater, ViewGroup parent, int layoutId, boolean markable) {
        View convertView;
        if (markable) {
            View child = inflater.inflate(layoutId, parent, false);

            View check = child.findViewById(android.R.id.checkbox);
            if (check instanceof Checkable) {
                child.setOnClickListener(null);
                child.setClickable(false);
                convertView = child;
            } else {
                ViewGroup container = (ViewGroup) inflater.inflate(R.layout.item_checkable, parent, false);
                child.setId(R.id.list_item_stub);
                child.setBackgroundColor(parent.getContext().getResources().getColor(android.R.color.transparent));
                child.setDuplicateParentStateEnabled(true);
                container.addView(child, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    child.setStateListAnimator(AnimatorInflater.loadStateListAnimator(parent.getContext(), R.animator.selection_animator));
                }
                convertView = container;
            }
        } else {
            convertView = inflater.inflate(layoutId, parent, false);
        }
        return convertView;
    }

    public void addAll(Collection<? extends T> objects) {
        if (objects == null || objects.isEmpty())
            return;

        int position = mObjects.size();
        if (mOriginalValues != null && mOriginalValues != mObjects) {

            synchronized (mLock) {
                int addedItems = 0;
                mOriginalValues.addAll(objects);

                // add object to current filtered objects too if it passes the filter
                for (T object : objects) {
                    if (getFilter().filter(object)) {
                        mObjects.add(object);
                        addedItems++;
                    }
                }
                notifyItemRangeInserted(position, addedItems);
            }

        } else {
            mObjects.addAll(objects);
            notifyItemRangeInserted(position, objects.size());
        }

    }

    public void set(T object, int position) {
        if (mOriginalValues != null && mOriginalValues != mObjects) {

            synchronized (mLock) {
                T oldObject =mOriginalValues.get(position);
                mOriginalValues.set(position, object);

                // add object to current filtered objects too if it passes the filter
                if (mOriginalValues != mObjects && getFilter().filter(object)) {
                    int pos = mObjects.indexOf(oldObject);
                    mObjects.set(pos, object);
                    notifyItemChanged(pos);
                }
            }

        } else {
            mObjects.set(position, object);
            notifyItemChanged(position);
        }
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        if (mOriginalValues != null && mOriginalValues != mObjects) {

            synchronized (mLock) {
                mOriginalValues.add(object);

                // add object to current filtered objects too if it passes the filter
                if (mOriginalValues != mObjects && getFilter().filter(object)) {
                    int position = mObjects.size();
                    mObjects.add(object);
                    notifyItemInserted(position);
                }
            }

        } else {
            int position = mObjects.size();
            mObjects.add(object);
            notifyItemInserted(position);
        }
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        if (mOriginalValues != null && mOriginalValues != mObjects) {
            synchronized (mLock) {
                mOriginalValues.add(index, object);

                // add object to current filtered objects too if it passes the
                // filter, index has to be ignored since it's different,
                // TODO find better solution for filter
                if (getFilter().filter(object)) {
                    int position = mObjects.size();
                    mObjects.add(object);
                    notifyItemInserted(position);
                }

            }
        } else {
            mObjects.add(index, object);
            notifyItemInserted(index);
        }
    }

    /**
     * Removes the element at the specified position in the list
     */
    public T remove(int position) {
        T result = null;
        if (mOriginalValues != null && mOriginalValues != mObjects) {
            synchronized (mLock) {
                result = mOriginalValues.remove(position);
                position = mObjects.indexOf(result);
                if (position >= 0) {
                    mObjects.remove(position);
                    notifyItemRemoved(position);
                }
            }
        } else {
            result = mObjects.remove(position);
            notifyItemRemoved(position);
        }

        if (getEventListener() != null) {
            getEventListener().onItemRemoved(this, position);
        }

        return result;
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public boolean remove(T object) {
        boolean result=false;
        if (mOriginalValues != null && mOriginalValues != mObjects) {
            synchronized (mLock) {
                result = mOriginalValues.remove(object);
                int position = mObjects.indexOf(result);
                if (position >= 0) {
                    mObjects.remove(position);
                    notifyItemRemoved(position);
                }
            }
        } else {
            int position = mObjects.indexOf(object);
            if (position >= 0) {
                mObjects.remove(position);
                notifyItemRemoved(position);
                result = true;
            }
        }

        return result;
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        int size = mObjects.size();
        if (mOriginalValues != null && mOriginalValues != mObjects) {
            synchronized (mLock) {
                mOriginalValues.clear();
                mObjects.clear();
            }
        } else {
            mObjects.clear();
        }

        notifyItemRangeRemoved(0,size);
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mObjects, comparator);

        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {

        if (fromPosition == toPosition) {
            return;
        }

        if (mOriginalValues != null && mOriginalValues != mObjects) {
            synchronized (mLock) {

                T temp = mObjects.get(fromPosition);

                int pos1 = mOriginalValues.indexOf(temp);
                int pos2 = mOriginalValues.indexOf(mObjects.get(toPosition));

                mObjects.remove(fromPosition);
                mObjects.add(toPosition, temp);

                if (pos1 >= 0 && pos2 >= 0) {
                    mOriginalValues.remove(pos1);
                    mOriginalValues.add(pos2,temp);
                }
                notifyItemMoved(fromPosition, toPosition);
            }
        } else {
            final T item = mObjects.remove(fromPosition);
            mObjects.add(toPosition, item);

            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void swapItems(int positionOne, int positionTwo) {
        Debug.v("swap " + positionOne + ", " + positionTwo);

        if (mOriginalValues != null && mOriginalValues != mObjects) {
            synchronized (mLock) {
                T temp = mObjects.get(positionOne);

                int pos1 = mOriginalValues.indexOf(temp);
                int pos2 = mOriginalValues.indexOf(mObjects.get(positionTwo));

                mObjects.set(positionOne, mObjects.get(positionTwo));
                mObjects.set(positionTwo, temp);

                if (pos1 >= 0 && pos2 >= 0) {
                    mOriginalValues.set(pos1, mOriginalValues.get(pos2));
                    mOriginalValues.set(pos2, temp);
                }

                notifyItemMoved(positionOne,positionTwo);
                notifyItemMoved(positionTwo,positionOne);
            }
        } else {
            T temp = mObjects.get(positionOne);
            mObjects.set(positionOne, mObjects.get(positionTwo));
            mObjects.set(positionTwo, temp);

            notifyItemMoved(positionOne, positionTwo);
            notifyItemMoved(positionTwo,positionOne);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public T getItem(int position) {
        if (position >= 0 && position < mObjects.size())
            return mObjects.get(position);
        else
            return null;
    }

    public List<T> getItems() {
        if (mOriginalValues != null)
            return mOriginalValues;
        else
            return mObjects;
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int indexOf(T item) {
        return mObjects.indexOf(item);
    }

    public int lastIndexOf(Class<? extends T> listableClazz) {
        for (int i = mObjects.size() -1; i>=0;i--) {
            T listable = mObjects.get(i);
            if (listable!=null && listableClazz.isAssignableFrom(listable.getClass())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListRecyclerFilter<T> getFilter() {
        if (mFilter == null) {
            mFilter = new ListRecyclerFilter<T>(this);
        }
        return mFilter;
    }

    public void refilter() {
        if (getFilter().isFilterSet()) {
            getFilter().filter(getFilter().constraint);
        }
    }

}

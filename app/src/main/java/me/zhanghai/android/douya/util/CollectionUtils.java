/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <E> List<E> union(List<E> list1, List<E> list2) {
        if (list1 instanceof RandomAccess && list2 instanceof RandomAccess) {
            return new RandomAccessUnionList<>(list1, list2);
        } else {
            return new UnionList<>(list1, list2);
        }
    }

    private static class UnionList<E> extends AbstractList<E> {

        private List<E> mList1;
        private List<E> mList2;

        public UnionList(List<E> list1, List<E> list2) {
            mList1 = list1;
            mList2 = list2;
        }

        @Override
        public E get(int location) {
            int list1Size = mList1.size();
            return location < list1Size ? mList1.get(location) : mList2.get(location - list1Size);
        }

        @Override
        public int size() {
            return mList1.size() + mList2.size();
        }
    }

    private static class RandomAccessUnionList<E> extends UnionList<E> implements RandomAccess {

        public RandomAccessUnionList(List<E> list1, List<E> list2) {
            super(list1, list2);
        }
    }
}

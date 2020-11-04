package org.wrkr.clb.common.util.collections;

public abstract class ExtendedArrayUtils {

    public static Object[] buildArrayOfElement(Object element, int size) {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = element;
        }
        return result;
    }
}

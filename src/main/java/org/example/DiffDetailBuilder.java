package org.example;


import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

import static org.example.PathUtils.join;

@Getter
public class DiffDetailBuilder<T> {
    private final List<DiffPair<?>> diffs;
    private final T left;
    private final T right;
    private final String rootName;
    private final List<ReflectionDiffBuilder<?>> reflectionDiffBuilders = new ArrayList<>();


    public DiffDetailBuilder(String rootName, final T left, final T right) {
        this.diffs = new ArrayList<>();
        this.left = left;
        this.right = right;
        this.rootName = rootName;
    }

    public DiffDetailResult<T> build() {
        return new DiffDetailResult<>(rootName, null,
                left, right, diffs, reflectionDiffBuilders);
    }

    /**
     * Test if two {@link Objects}s are equal.
     *
     * @param fieldName the field name
     * @param lhs       the left-hand {@link Object}
     * @param rhs       the right-hand {@link Object}
     * @return this
     * @throws NullPointerException if field name is {@code null}
     */
    public DiffDetailBuilder<T> append(final String path, final String fieldName, final Object lhs,
                                       final Object rhs) {
        validateFieldNameNotNull(fieldName);

        if (lhs == rhs) {
            return this;
        }

        final Object objectToTest;
        // rhs cannot be null, as lhs != rhs
        objectToTest = Objects.requireNonNullElse(lhs, rhs);

        if (ObjectUtils.isArray(objectToTest)) {
            if (objectToTest instanceof boolean[]) {
                return append(path, fieldName, (boolean[]) lhs, (boolean[]) rhs);
            }
            if (objectToTest instanceof byte[]) {
                return append(path, fieldName, (byte[]) lhs, (byte[]) rhs);
            }
            if (objectToTest instanceof char[]) {
                return append(path, fieldName, (char[]) lhs, (char[]) rhs);
            }
            if (objectToTest instanceof double[]) {
                return append(path, fieldName, (double[]) lhs, (double[]) rhs);
            }
            if (objectToTest instanceof float[]) {
                return append(path, fieldName, (float[]) lhs, (float[]) rhs);
            }
            if (objectToTest instanceof int[]) {
                return append(path, fieldName, (int[]) lhs, (int[]) rhs);
            }
            if (objectToTest instanceof long[]) {
                return append(path, fieldName, (long[]) lhs, (long[]) rhs);
            }
            if (objectToTest instanceof short[]) {
                return append(path, fieldName, (short[]) lhs, (short[]) rhs);
            }

            return append(path, fieldName, (Object[]) lhs, (Object[]) rhs);
        }

        // Not array type
        if (Objects.equals(lhs, rhs)) {
            return this;
        }
        DiffPair<Object> pair = new DiffPair<>(path + fieldName, lhs, rhs);
        if (objectToTest instanceof Number || objectToTest instanceof CharSequence) {
            this.diffs.add(pair);
            return this;
        }
        if (objectToTest instanceof Comparable) {
            if (lhs != null) {
                @SuppressWarnings("unchecked") int i = ((Comparable<Object>) lhs).compareTo(rhs);
                if (i != 0) {
                    this.diffs.add(pair);
                    return this;
                }
            }
        }
        // 如果是集合
        if (objectToTest instanceof Collection<?>) {
            if (left == null) {
                if (right == null) {
                    return this;
                }
                @SuppressWarnings("unchecked") Collection<Object> right1 = (Collection<Object>) right;
                if (right1.isEmpty()) {
                    return this;
                }
                for (Object next : right1) {
                    append(path, fieldName, null, next);
                }
                return this;
            } else if (right == null) {
                @SuppressWarnings("unchecked") Collection<Object> left1 = (Collection<Object>) left;
                if (left1.isEmpty()) {
                    return this;
                }
                for (Object next : left1) {
                    append(path, fieldName, next, null);
                }
                return this;
            } else {
                @SuppressWarnings("unchecked") Collection<Object> right1 = (Collection<Object>) lhs;
                @SuppressWarnings("unchecked") Collection<Object> left1 = (Collection<Object>) rhs;
                int rightSize = right1.size();
                int leftSize = left1.size();
                Object[] rightArray = right1.toArray();
                Object[] leftArray = left1.toArray();
                if (leftSize <= rightSize) {
                    for (int i = 0; i < leftSize; i++) {
                        if (Objects.equals(leftArray[i], rightArray[i])) {
                            return this;
                        }
                        append(path, fieldName, leftArray[i], rightArray[i]);
                    }
                    for (; leftSize < rightSize; leftSize++) {
                        append(path, fieldName, null, rightArray[leftSize]);
                    }
                } else {
                    for (int i = 0; i < rightSize; i++) {
                        if (Objects.equals(leftArray[i], rightArray[i])) {
                            return this;
                        }
                        append(path, fieldName, leftArray[i], rightArray[i]);
                    }
                    for (; rightSize < leftSize; rightSize++) {
                        append(path, fieldName, leftArray[rightSize], null);
                    }
                }
                return this;
            }
        }

        // 最后如果是普通对象，应该进一步拆分
        ReflectionDiffBuilder<?> reflectionDiffBuilder = new ReflectionDiffBuilder<>(join("-", rootName, path,fieldName), lhs, rhs);
        reflectionDiffBuilders.add(reflectionDiffBuilder);
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final boolean[] lhs,
                                       final boolean[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final byte[] lhs,
                                       final byte[] rhs) {
        validateFieldNameNotNull(fieldName);
        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final char[] lhs,
                                       final char[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final int[] lhs,
                                       final int[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final double[] lhs,
                                       final double[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final float[] lhs,
                                       final float[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final long[] lhs,
                                       final long[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final Object[] lhs,
                                       final Object[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), lhs, rhs));
        }
        return this;
    }

    public DiffDetailBuilder<T> append(final String path, final String fieldName, final short[] lhs,
                                       final short[] rhs) {
        validateFieldNameNotNull(fieldName);

        if (!Arrays.equals(lhs, rhs)) {
            diffs.add(new DiffPair<>(join("-", rootName, path,fieldName), ArrayUtils.toObject(lhs), ArrayUtils.toObject(rhs)));
        }
        return this;
    }

    private void validateFieldNameNotNull(final String fieldName) {
        Objects.requireNonNull(fieldName, "fieldName");
    }

}

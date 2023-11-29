package org.example;

import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class AdapteReflectionDiffBuilder<T> extends ReflectionDiffBuilder<T> implements Builder<DiffResult<T>> {
    private final T left;
    private final T right;
    private final DiffBuilder<T> diffBuilder;


    /**
     * Field names to exclude from output. Intended for fields like {@code "password"} or {@code "lastModificationDate"}.
     *
     * @since 3.13.0
     */
    private String[] excludeFieldNames;

    /**
     * Constructs a builder for the specified objects with the specified style.
     *
     * <p>
     * If {@code lhs == rhs} or {@code lhs.equals(rhs)} then the builder will
     * not evaluate any calls to {@code append(...)} and will return an empty
     * {@link DiffResult} when {@link #build()} is executed.
     * </p>
     *
     * @param lhs   {@code this} object
     * @param rhs   the object to diff against
     * @param style the style will use when outputting the objects, {@code null}
     *              uses the default
     * @throws IllegalArgumentException if {@code lhs} or {@code rhs} is {@code null}
     */
    public AdapteReflectionDiffBuilder(T lhs, T rhs, ToStringStyle style) {
        super(lhs, rhs, style);
        this.left = lhs;
        this.right = rhs;
        this.diffBuilder = new DiffBuilder<>(lhs, rhs, style);
    }

    @Override
    public DiffResult<T> build() {
        if (left.equals(right)) {
            return diffBuilder.build();
        }

        appendFields(left.getClass());
        return diffBuilder.build();
    }

    private void appendFields(final Class<?> clazz) {
        for (final Field field : FieldUtils.getAllFields(clazz)) {
            if (accept(field)) {
                try {
                    diffBuilder.append(invokeName(field), FieldUtils.readField(field, left, true), FieldUtils.readField(field, right, true));
                } catch (final IllegalAccessException e) {
                    // this can't happen. Would get a Security exception instead
                    // throw a runtime exception in case the impossible happens.
                    throw new IllegalArgumentException("Unexpected IllegalAccessException: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 获取名称信息
     * @param field 字段
     * @return 字段展示时的信息
     */
    public static String invokeName(Field field) {
        FieldSCName annotation = field.getAnnotation(FieldSCName.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return field.getName();
        }
    }

    private boolean accept(final Field field) {
        if (field.getName().indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR) != -1) {
            return false;
        }
        if (Modifier.isTransient(field.getModifiers())) {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if (this.excludeFieldNames != null
                && Arrays.binarySearch(this.excludeFieldNames, field.getName()) >= 0) {
            // Reject fields from the getExcludeFieldNames list.
            return false;
        }
        return !field.isAnnotationPresent(DiffExclude.class);
    }
}

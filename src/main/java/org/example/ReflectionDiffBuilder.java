package org.example;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.example.PathUtils.join;

public class ReflectionDiffBuilder<T> {

    private final T left;
    private final T right;
    private final DiffDetailBuilder<T> diffBuilder;

    private final String documentName;

    private final Class<T> clazz;

    public ReflectionDiffBuilder(T lhs, T rhs, final Class<T> clazz) {
        this.left = lhs;
        this.right = rhs;
        this.clazz = clazz;
        this.documentName = invokeName(clazz);
        this.diffBuilder = new DiffDetailBuilder<>(documentName, lhs, rhs);
    }

    public ReflectionDiffBuilder(String documentName, T lhs, T rhs, final Class<T> clazz) {
        this.left = lhs;
        this.right = rhs;
        this.clazz = clazz;
        if (StringUtils.isNotBlank(documentName)) {
            this.documentName = documentName;
        } else {
            this.documentName = invokeName(clazz);
        }
        this.diffBuilder = new DiffDetailBuilder<>(documentName, lhs, rhs);
    }

    @SuppressWarnings("unchecked")
    public ReflectionDiffBuilder(T lhs, T rhs) {
        this.left = lhs;
        this.right = rhs;
        this.clazz = (Class<T>) lhs.getClass();
        this.documentName = invokeName(clazz);
        this.diffBuilder = new DiffDetailBuilder<>(documentName, lhs, rhs);
    }

    @SuppressWarnings("unchecked")
    public ReflectionDiffBuilder(String documentName, T lhs, T rhs) {
        this.left = lhs;
        this.right = rhs;
        this.clazz = (Class<T>) lhs.getClass();
        if (StringUtils.isNotBlank(documentName)) {
            this.documentName = documentName;
        } else {
            this.documentName = invokeName(clazz);
        }
        this.diffBuilder = new DiffDetailBuilder<>(documentName, lhs, rhs);
    }

    public DiffDetailResult<T> build() {
        if (left.equals(right)) {
            return diffBuilder.build();
        }
        appendFields();
        return diffBuilder.build();
    }

    private void appendFields() {
        for (final Field field : FieldUtils.getAllFields(clazz)) {
            if (accept(field)) {
                try {
                    diffBuilder.append(join("-", this.documentName, invokeName(field)), FieldUtils.readField(field, left, true), FieldUtils.readField(field, right, true));
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
     *
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

    /**
     * 获取名称信息
     *
     * @param clazz 类
     * @return 字段展示时的信息
     */
    public static String invokeName(Class<?> clazz) {
        FieldSCName annotation = clazz.getAnnotation(FieldSCName.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return "";
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
//        Arrays.binarySearch(this.excludeFieldNames, field.getName())
        return !field.isAnnotationPresent(DiffExclude.class);
    }


}

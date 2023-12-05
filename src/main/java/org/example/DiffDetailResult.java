package org.example;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class DiffDetailResult<T> implements Diff<T>, Path, Iterable<Diff<?>> {

    /**
     * The field type.
     */
    private final Type type;

    /**
     * The field name.
     */
    private final String fieldName;

    private final T left;

    private final T right;

    private final String path;

    private final List<DiffPair<?>> diffs;

    protected DiffDetailResult(final String fieldName, final String path, T left, T right, List<DiffPair<?>> diffs,List<ReflectionDiffBuilder<?>> reflectionDiffBuilders) {
        super();
        this.type = ObjectUtils.defaultIfNull(
                TypeUtils.getTypeArguments(getClass(), Diff.class).get(
                        Diff.class.getTypeParameters()[0]), Object.class);
        this.fieldName = fieldName;
        this.right = right;
        this.left = left;
        this.path = path;
        this.diffs = diffs;
        if (reflectionDiffBuilders != null && !reflectionDiffBuilders.isEmpty()) {
            for (ReflectionDiffBuilder<?> reflectionDiffBuilder : reflectionDiffBuilders) {
                DiffDetailResult<?> build = reflectionDiffBuilder.build();
                this.diffs.addAll(build.getDiffs());
            }
        }
    }


    public final List<DiffPair<?>> getDiffs() {
        return diffs;
    }
    /**
     * Gets the type of the field.
     *
     * @return the field type
     */
    public final Type getType() {
        return type;
    }

    /**
     * Gets the name of the field.
     *
     * @return the field name
     */
    public final String getFieldName() {
        return fieldName;
    }

    @Override
    public T getLeft() {
        return this.left;
    }

    @Override
    public T getRight() {
        return this.right;
    }

    /**
     * Returns a {@link String} representation of the {@link Diff}, with the
     * following format:
     *
     * <pre>
     * [fieldname: left-value, right-value]
     * </pre>
     *
     * @return the string representation
     */
    @Override
    public final String toString() {
        return String.format("[%s: %s, %s]", fieldName, getLeft(), getRight());
    }


    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Iterator<Diff<?>> iterator() {
        return null;
    }
}

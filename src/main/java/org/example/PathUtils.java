package org.example;

public enum PathUtils {
    ;

    PathUtils() {

    }

    public static String resolve(String parentPath, Object clazz) {
        return parentPath + clazz.getClass().getName();
    }

    public static String join(CharSequence delimiter, CharSequence... elements) {
        var delim = delimiter.toString();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            CharSequence element = elements[i];
            if (element != null && element != "") {
                stringBuilder.append(element);
                if (i != elements.length - 1) {
                    stringBuilder.append(delim);
                }
            }
        }
        return stringBuilder.toString();
    }
}

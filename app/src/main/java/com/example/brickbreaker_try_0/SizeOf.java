package com.example.brickbreaker_try_0;

public class SizeOf {
    /*
     * A perfect way of creating confusing method name, sizeof and sizeOf
     * this method take advantage of SIZE constant from wrapper class
     */
    public static int sizeOf(Class dataType) {
        if (dataType == null) {
            throw new NullPointerException();
        }
        if (dataType == byte.class || dataType == Byte.class) {
            return Byte.BYTES;
        }
        if (dataType == short.class || dataType == Short.class) {
            return Short.BYTES;
        }
        if (dataType == char.class || dataType == Character.class) {
            return Character.BYTES;
        }
        if (dataType == int.class || dataType == Integer.class) {
            return Integer.BYTES;
        }
        if (dataType == long.class || dataType == Long.class) {
            return Long.BYTES;
        }
        if (dataType == float.class || dataType == Float.class) {
            return Float.BYTES;
        }
        if (dataType == double.class || dataType == Double.class) {
            return Double.BYTES;
        }
        return 4; // default for 32-bit memory pointer
    }
}

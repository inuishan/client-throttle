package com.ishan.base;

/**
 * @author ishanjain
 * @since 21/03/18
 */
public class ExceptionUtils {

    public static RuntimeException wrapInRuntimeExceptionIfNecessary(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }
}
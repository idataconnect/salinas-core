/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.data;

import com.idataconnect.salinas.function.Function;
import com.idataconnect.salinas.parser.SalinasNode;
import java.math.BigDecimal;

/**
 * A type of value that Salinas can represent.
 */
public abstract class SalinasType {

    /**
     * The null type.
     */
    public static final SalinasType NULL = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            if (destType == NULL) {
                return null;
            } else if (destType == UNDEFINED) {
                return null;
            } else if (destType == NUMBER) {
                return BigDecimal.ZERO;
            } else if (destType == STRING) {
                return "";
            } else if (destType == BOOLEAN) {
                return Boolean.FALSE;
            } else if (destType == DATE) {
                return null; // TODO finish date
            } else {
                throw new IllegalArgumentException("Cannot convert from type "
                        + getClass().getName() + " to type "
                        + destType.getClass().getName());
            }
        }

        @Override
        public String toString() {
            return "Null";
        }
    };

    /**
     * Undefined type, mostly used to mark a lack of a specific type.
     */
    public static final SalinasType UNDEFINED = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            return NULL.convert(destType, value);
        }

        @Override
        public String toString() {
            return "Undefined";
        }
    };

    /**
     * A number type, representing numeric values.
     */
    public static final SalinasType NUMBER = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            if (destType == NULL) {
                return null;
            } else if (destType == UNDEFINED) {
                return null;
            } else if (destType == NUMBER) {
                return value;
            } else if (destType == STRING) {
                return value.toString();
            } else if (destType == BOOLEAN) {
                return ((BigDecimal) value).signum() != 0;
            } else if (destType == DATE) {
                return null; // TODO finish date
            } else {
                throw new IllegalArgumentException("Cannot convert from type "
                        + getClass().getName() + " to type "
                        + destType.getClass().getName());
            }
        }

        @Override
        public String toString() {
            return "Number";
        }
    };

    /**
     * A string type, representing character string values.
     */
    public static final SalinasType STRING = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            if (destType == NULL || destType == null) {
                return SalinasValue.NULL;
            } else if (destType == UNDEFINED) {
                return SalinasValue.NULL;
            } else if (destType == NUMBER) {
                try {
                    return new BigDecimal((String) value);
                } catch (NumberFormatException ex) {
                    return BigDecimal.ZERO;
                }
            } else if (destType == STRING) {
                return value;
            } else if (destType == BOOLEAN) {
                return Boolean.valueOf(((String) value).length() != 0);
            } else if (destType == DATE) {
                return null; // TODO finish date
            } else {
                throw new IllegalArgumentException("Cannot convert from type "
                        + getClass().getName() + " to type "
                        + destType.getClass().getName());
            }
        }

        @Override
        public String toString() {
            return "String";
        }
    };

    /**
     * A boolean type, representing <code>true</code> or <code>false</code>
     * logical values.
     */
    public static final SalinasType BOOLEAN = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            if (destType == NULL) {
                return null;
            } else if (destType == UNDEFINED) {
                return null;
            } else if (destType == NUMBER) {
                return ((Boolean) value).booleanValue()
                        ? BigDecimal.ONE : BigDecimal.ZERO;
            } else if (destType == STRING) {
                return ((Boolean) value).booleanValue() ? "1" : "";
            } else if (destType == BOOLEAN) {
                return value;
            } else if (destType == DATE) {
                return null; // TODO finish date
            } else {
                throw new IllegalArgumentException("Cannot convert from type "
                        + getClass().getName() + " to type "
                        + destType.getClass().getName());
            }
        }

        @Override
        public String toString() {
            return "Boolean";
        }
    };

    /**
     * A date type, representing date values without a time zone.
     */
    public static final SalinasType DATE = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String toString() {
            return "Date";
        }
    };

    /**
     * A function type, representing callable functions.
     */
    public static final SalinasType FUNCTION = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            if (destType != FUNCTION) {
                raiseTypeConversionError(FUNCTION, destType);
            }

            return value;
        }

        @Override
        public String toString() {
            return "Function";
        }
    };

    /**
     * The Salinas array type.
     */
    public static final SalinasType ARRAY = new SalinasType() {

        @Override
        public Object convert(SalinasType destType, Object value)
                throws ConversionException {
            if (destType != ARRAY) {
                raiseTypeConversionError(ARRAY, destType);
            }

            return value;
        }

        @Override
        public String toString() {
            return "Array";
        }
    };

    private static void raiseTypeConversionError(SalinasType sourceType,
            SalinasType destType) throws ConversionException {
        try {
            if (System.getProperty("salinas.debug") != null) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for (StackTraceElement ste : stackTrace) {
                    System.out.println("\tat " + ste);
                }
            }
        } catch (Exception ex) {}
        throw new ConversionException("Cannot convert from "
                + sourceType + " to "
                + destType);
    }

    /**
     * Infers the Salinas type based on the given object class.
     *
     * @param objectClass the class of object which would be held in a
     * {@link SalinasValue}'s <code>value</code> field.
     * @return the inferred type, or <code>SalinasType.UNDEFINED</code> if the
     * object class was unknown.
     */
    public static SalinasType valueOf(Class<?> objectClass) {
        // TODO finish date
        if (objectClass == null) {
            return SalinasType.NULL;
        } else if (objectClass.equals(String.class)) {
            return STRING;
        } else if (objectClass.equals(BigDecimal.class)) {
            return NUMBER;
        } else if (objectClass.equals(Boolean.class)) {
            return BOOLEAN;
        } else if (objectClass.equals(Function.class)
                | objectClass.equals(SalinasNode.class)) {
            return FUNCTION;
        } else if (objectClass.equals(SalinasArrayMap.class)) {
            return ARRAY;
        } else {
            return UNDEFINED;
        }
    }

    /**
     * Converts from one type of value to another.
     *
     * @param destType the destination type
     * @param value the raw value to convert
     * @return a converted value, or the given value if conversion was
     * unnecessary
     */
    public abstract Object convert(SalinasType destType, Object value)
            throws ConversionException;
}

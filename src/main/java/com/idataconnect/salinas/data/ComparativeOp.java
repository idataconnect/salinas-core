/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.data;

import com.idataconnect.salinas.SalinasException;
import java.math.BigDecimal;

/**
 * An operator which compares two operands and produces a boolean result.
 */
public abstract class ComparativeOp {

    /**
     * Compares equality of the values once the first value
     * is converted to match the second value's type. This is used with the
     * <code>==</code> operator.
     */
    public static final ComparativeOp EQUAL_TO = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2)
                throws SalinasException {
            if (v1.getValue() == null && v2.getValue() == null) {
                return true;
            }

            Object v1Converted = v1.getCurrentType().convert(
                    v2.getCurrentType(), v1.getValue());

            if (v1Converted == null) {
                return false;
            }

            return v1Converted.equals(v2.getValue());
        }
    };

    /**
     * Compares equality of the values and that their
     * types are also identical. This is used with the <code>===</code>
     * operator.
     */
    public static final ComparativeOp EQUAL_TO_EXACT = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2)
                throws SalinasException {
            if (v1.getCurrentType() != v2.getCurrentType()) {
                return false;
            } else {
                return EQUAL_TO.apply(v1, v2);
            }
        }
    };

    /**
     * The exact opposite of the implementation
     * used by {@link #EQUAL_TO}. This is used with the
     * <code><></code> and the <code>#</code> operators.
     */
    public static final ComparativeOp NOT_EQUAL_TO = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2)
                throws SalinasException {
            return !EQUAL_TO.apply(v1, v2);
        }
    };

    /**
     * The exact opposite of the implementation
     * used by {@link #EQUAL_TO_EXACT}. This is used with the <code>!==</code>
     * operator.
     */
    public static final ComparativeOp NOT_EQUAL_TO_EXACT = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2)
                throws SalinasException {
            return !EQUAL_TO_EXACT.apply(v1, v2);
        }
    };

    /**
     * Checks that the first value is less than the second value. This is used
     * with the <code>&lt;</code> operator.
     */
    public static final ComparativeOp LESS_THAN = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2)
                throws SalinasException {
            if (v1.getValue() == null && v2.getValue() == null) {
                return false;
            }

            Object v1Converted = v1.getCurrentType().convert(
                    v2.getCurrentType(), v1.getValue());

            if (v1Converted == null) {
                return false;
            }

            if (v2.getCurrentType() == SalinasType.BOOLEAN) {
                return (v1.getValue() == Boolean.FALSE)
                        && (v2.getValue() == Boolean.TRUE);
            } else if (v2.getCurrentType() == SalinasType.NULL) {
                return v1.getCurrentType() != SalinasType.NULL;
            } else if (v2.getCurrentType() == SalinasType.NUMBER) {
                return ((BigDecimal) v1Converted)
                        .compareTo((BigDecimal) v2.getValue()) < 0;
            } else if (v2.getCurrentType() == SalinasType.STRING) {
                return ((String) v1Converted)
                        .compareTo((String) v2.getValue()) < 0;
            } else if (v2.getCurrentType() == SalinasType.DATE) {

                throw new UnsupportedOperationException("Not yet supported");
            } else {
                throw new IllegalArgumentException("Unknown type during compare: "
                        + v2.getCurrentType().getClass().getName());
            }
        }
    };

    /**
     * Checks that the first value is greater than the second value. This
     * is used with the <code>&gt;</code> operator.
     */
    public static final ComparativeOp GREATER_THAN = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2)
                throws SalinasException {
            return !LESS_THAN.apply(v1, v2) && !EQUAL_TO.apply(v1, v2);
        }
    };

    /**
     * Starts with, for strings, or equal to, for other types. For strings,
     * this operator will return <code>true</code> if the second operand
     * starts with the first. For other types, this operator returns the
     * same as the {@link #EQUAL_TO} operator. This is used with the
     * <code>=</code> operator, when it is not used as an assignment operator,
     * or, in other words, when the line does not start with
     * <code>&lt;identifier&gt; =</code>.
     */
    public static final ComparativeOp STARTS_WITH = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2) throws SalinasException {
            if (v1.getCurrentType() == SalinasType.STRING
                    || v2.getCurrentType() == SalinasType.STRING) {
                return ((String) v2.asType(SalinasType.STRING))
                        .startsWith((String) v1.asType(SalinasType.STRING));
            } else {
                return EQUAL_TO.apply(v1, v2);
            }
        }
    };

    /**
     * The exact opposite of the implementation
     * used by {@link #STARTS_WITH}. This is used with the <code>&lt;&gt;</code>
     * and the <code>#</code> operators.
     */
    public static final ComparativeOp NOT_STARTS_WITH = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2) throws SalinasException {
            return !STARTS_WITH.apply(v1, v2);
        }
    };

    /**
     * Checks that the first value is less than or equal to the second value.
     * This is used with the <code>&lt;=</code> operator.
     */
    public static final ComparativeOp LESS_THAN_OR_EQUAL_TO = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2) throws SalinasException {
            return LESS_THAN.apply(v1, v2) || EQUAL_TO.apply(v1, v2);
        }
    };

    /**
     * Checks that the first value is greater than or equal to the second value.
     * This is used with the <code>&gt;=</code> operator.
     */
    public static final ComparativeOp GREATER_THAN_OR_EQUAL_TO = new ComparativeOp() {

        @Override
        public boolean apply(SalinasValue v1, SalinasValue v2) throws SalinasException {
            return GREATER_THAN.apply(v1, v2) || EQUAL_TO.apply(v1, v2);
        }
    };

    /**
     * Applies the comparative op to both values.
     *
     * @param v1 the first value
     * @param v2 the second value
     * @return whether the value applied
     */
    public abstract boolean apply(SalinasValue v1, SalinasValue v2)
            throws SalinasException;
}

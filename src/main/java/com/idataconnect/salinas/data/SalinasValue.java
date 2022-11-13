/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.data;

import com.idataconnect.salinas.SalinasConfig;
import com.idataconnect.salinas.SalinasException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.script.ScriptContext;

/**
 * Holder object for Salinas values which may be strongly or dynamically typed.
 * <p>
 * The value is considered dynamically typed when the <code>strongType</code>
 * property is set to <code>SalinasType.UNDEFINED</code>, otherwise the value
 * is considered to be strongly typed.
 * <p>
 * When the value holder is dynamically typed, the <code>currentType</code>
 * property will be set to the type of the most recent value to be assigned
 * to the holder.
 * <p>
 * When the value holder is strongly typed, any value that is assigned will
 * be automatically converted to the strong type.
 */
public class SalinasValue {

    /**
     * The immutable null type.
     */
    public static final SalinasValue NULL = new SalinasValue() {

        @Override
        public SalinasType getStrongType() {
            return SalinasType.NULL;
        }

        @Override
        public SalinasType getCurrentType() {
            return getStrongType();
        }

        @Override
        public Object getValue() {
            return null;
        }
    };

    /**
     * The immutable undefined type.
     */
    public static final SalinasValue UNDEFINED = new SalinasValue() {

        @Override
        public SalinasType getStrongType() {
            return SalinasType.UNDEFINED;
        }

        @Override
        public SalinasType getCurrentType() {
            return getStrongType();
        }

        @Override
        public Object getValue() {
            return null;
        }
    };

    /**
     * The immutable true type.
     */
    public static final SalinasValue TRUE = new SalinasValue() {

        @Override
        public SalinasType getStrongType() {
            return SalinasType.BOOLEAN;
        }

        @Override
        public SalinasType getCurrentType() {
            return getStrongType();
        }

        @Override
        public Object getValue() {
            return Boolean.TRUE;
        }
    };

    /**
     * The immutable false value.
     */
    public static final SalinasValue FALSE = new SalinasValue() {

        @Override
        public SalinasType getStrongType() {
            return SalinasType.BOOLEAN;
        }

        @Override
        public SalinasType getCurrentType() {
            return getStrongType();
        }

        @Override
        public Object getValue() {
            return Boolean.FALSE;
        }
    };

    /**
     * The immutable PI value.
     */
    public static final SalinasValue PI = new SalinasValue() {

        @Override
        public SalinasType getStrongType() {
            return SalinasType.NUMBER;
        }

        @Override
        public SalinasType getCurrentType() {
            return getStrongType();
        }

        @Override
        public Object getValue() {
            return BigDecimal.valueOf(Math.PI);
        }
    };

    private SalinasType strongType = SalinasType.UNDEFINED;
    private SalinasType currentType = SalinasType.UNDEFINED;
    private Object value;

    /**
     * Creates a new, empty, dynamically-typed Salinas Value.
     */
    public SalinasValue() {
    }

    /**
     * Creates a new, dynamically-typed Salinas value and initializes its raw value
     * to the given value. The type is inferred from the value.
     * <p>
     * The value will be dynamically typed.
     *
     * @param value the raw value
     */
    public SalinasValue(Object value) {
        this.value = value;
        try {
            setCurrentType(SalinasType.valueOf(value.getClass()));
        } catch (SalinasException ex) {
            throw new IllegalArgumentException("Error during initial value creation.", ex);
        }
    }

    /**
     * Creates a new, dynamically-typed value with the given value and type.
     * <p>
     * If the type is known
     * beforehand, this constructor will generally be more efficient than
     * the constructor which only takes the value
     *
     * @param value the raw value
     * @param currentType the value's type
     */
    public SalinasValue(Object value, SalinasType currentType) {
        this(value, currentType, false);
    }

    /**
     * Creates a new, optionally strongly typed value with the given value and
     * type. If <code>strong</code> is <code>true</code>, the strong type
     * will be the type specified by <code>currentType</code>. If
     * <code>strong</code> is <code>false</code>, the value's initial dynamic
     * type will be set to the type specified by <code>currentType</code>.
     *
     * @param value the raw value
     * @param currentType the current type
     * @param strong whether this value should be a strong typed value
     */
    public SalinasValue(Object value, SalinasType currentType, boolean strong) {
        this.value = value;
        if (strong) {
            strongType = currentType;
        }
        this.currentType = currentType;
    }

    /**
     * Gets the current type, which was the last type of value to be inserted
     * into this holder. If this holder is strongly typed, this type will
     * always be the strong type as returned by {@link #getStrongType}.
     *
     * @return the current type
     */
    public SalinasType getCurrentType() {
        return currentType;
    }

    /**
     * Sets the current type. This will also convert the value to the new type,
     * if necessary.
     *
     * @param currentType the current type in the <code>value</code> field
     * @throws ConversionException if an error occurs while converting the current
     * value to the new type
     */
    public void setCurrentType(SalinasType currentType) throws ConversionException {
        if (currentType != this.currentType) {
            value = getCurrentType().convert(currentType, value);
        }
        this.currentType = currentType;
    }

    /**
     * Gets the strong type, which is the type that values will be converted
     * to when being set into this holder.
     *
     * @return the strong type that this holder is using, or
     * <code>SalinasType.UNDEFINED</code> if a strong type is not set
     */
    public SalinasType getStrongType() {
        return strongType;
    }

    /**
     * Sets the strong type, which will cause all subsequent values to be
     * set to the given type when being set into this holder. Setting the type
     * to {@link SalinasType#UNDEFINED} has the effect of removing the strong
     * type.
     *
     * @param strongType the strong type to use for this holder, or
     * <code>SalinasType.UNDEFINED</code> to remove the strong type
     * @throws ConversionException if the strong type is not compatible with
     * the current value
     */
    public void setStrongType(SalinasType strongType) throws ConversionException {
        this.strongType = strongType;
        setCurrentType(strongType);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation returns a generic version of the value.
     * {@link #getDisplayValue} should be preferred to this method.
     */
    @Override
    public String toString() {
        return getValue() == null ? "<null>" : getValue().toString();
    }

    /**
     * Gets the string value to be displayed to the user, using the settings
     * from the given <strong>context</strong>.
     * <p>
     * While {@link #toString()} will give a generic string representation of
     * any value, this method will return a string that the user expects based
     * on their current context configuration. For example, numbers will be
     * formatted with the number of decimal points that have been set with
     * a <code>SET DECIMALS</code> command.
     *
     * @param context the current script context
     * @return the string value
     */
    public String getDisplayValue(ScriptContext context) {
        final SalinasConfig config = (SalinasConfig) context.getAttribute("salinasConfig");

        if (getCurrentType().equals(SalinasType.NUMBER)) {
            if (getValue() == null) {
                return "0";
            } else {
                return ((BigDecimal) getValue()).setScale(config.getDecimals(),
                        RoundingMode.HALF_EVEN).toString();
            }
        } else if (getValue() == null) {
            if (this == SalinasValue.UNDEFINED) {
                return "<undefined>";
            } else {
                return "<null>";
            }
        } else {
            return toString();
        }
    }

    /**
     * Gets the value as its current type. To fetch the value dynamically,
     * use {@link #asType}.
     *
     * @return the raw value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value to the given value. The type of the given value is
     * inferred. If this value is not currently the type of the inferred value,
     * it is converted automatically, unless this value is strongly typed.
     *
     * @param newValue the new value
     * @throws ConversionException if the value could not be converted to the
     * type of the given value
     */
    public void setValue(Object newValue) throws ConversionException {
        if (getStrongType() != SalinasType.UNDEFINED) {
            // Value is strongly typed; convert
            value = SalinasType.valueOf(newValue.getClass())
                    .convert(getStrongType(), newValue);
        } else {
            setCurrentType(SalinasType.valueOf(newValue.getClass()));
            value = newValue;
        }
    }

    /**
     * Sets the value to the value held in <code>newValue</code>. If this
     * value is not currently the type of <code>newValue</code>, it is
     * converted automatically, unless this value is strongly typed.
     *
     * @param newValue the holder containing the new value
     * @throws ConversionException if the value could not be converted to the
     * type of the given value
     */
    public void setValue(SalinasValue newValue) throws ConversionException {
        if (getStrongType() != SalinasType.UNDEFINED) {
            // Value is strongly typed; convert
            value = newValue.asType(getStrongType());
        } else {
            setCurrentType(newValue.getCurrentType());
            value = newValue.getValue();
        }
    }

    /**
     * Gets the value as the given <code>type</code>. If the value is not
     * currently the given type, it will be converted automatically, unless this
     * value is strongly typed.
     *
     * @param type the type to return the value as
     * @return the value as the given type
     * @throws ConversionException if the value could not be converted to the
     * given type
     */
    public Object asType(SalinasType type) throws ConversionException {
        if (type == getCurrentType()) {
            return getValue();
        } else {
            return getCurrentType().convert(type, value);
        }
    }

    /**
     * Convenience method for returning a value as a number, represented as
     * a {@code BigDecimal}.
     * @return the raw BigDecimal value
     * @throws ConversionException if the value could not be converted to
     * a number, either because its type is incompatible, or it is a different
     * type and strong typing is enabled for this value
     */
    public BigDecimal asNumber() throws ConversionException {
        return (BigDecimal) asType(SalinasType.NUMBER);
    }

    /**
     * Convenience method for returning a value as a boolean, represented as
     * a {@code Boolean}.
     * @return the raw Boolean value
     * @throws ConversionException if the value could not be converted to
     * a boolean, either because its type is incompatible, or it is a different
     * type and strong typing is enabled for this value
     */
    public Boolean asBoolean() throws ConversionException {
        return (Boolean) asType(SalinasType.BOOLEAN);
    }

    /**
     * Convenience method for returning a value as a string, represented as
     * a {@code String}.
     * @return the raw String value
     * @throws ConversionException if the value could not be converted to
     * a string, either because its type is incompatible, or it is a different
     * type and strong typing is enabled for this value
     */
    public String asString() throws ConversionException {
        return (String) asType(SalinasType.STRING);
    }

    /**
     * Returns an appropriate salinas value based on the given value. This
     * method is generally preferred over the constructor because it may
     * avoid additional object creation.
     * @param value the value to convert to a <code>SalinasValue</code>
     * @return a <code>SalinasValue</code> representing the given value
     * @throws IllegalArgumentException if the value cannot be represented
     * by a <code>SalinasValue</code>
     */
    public static SalinasValue valueOf(Object value) {
        if (value == null) {
            return SalinasValue.NULL;
        } else if (value instanceof SalinasValue) {
            return (SalinasValue) value;
        } else if (value == Boolean.TRUE) {
            return SalinasValue.TRUE;
        } else if (value == Boolean.FALSE) {
            return SalinasValue.FALSE;
        } else {
            return new SalinasValue(value);
        }
    }
}

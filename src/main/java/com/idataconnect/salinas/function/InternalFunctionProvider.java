/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.SalinasConfig;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.script.ScriptContext;

/**
 * A provider which contains internal (built in) functions, most of which
 * are implemented in Java.
 */
public class InternalFunctionProvider extends FunctionProvider {
    
    private static final Map<String, Function> functionMap
            = new HashMap<String, Function>(128);

    /**
     * Convert string to upper case.
     */
    public static final Function UPPER = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws FunctionCallException {
            checkParameterCount("UPPER", 1, parameters);
            try {
                String upperString = ((String) parameters[0]
                        .asType(SalinasType.STRING)).toUpperCase();
                return new SalinasValue(upperString, SalinasType.STRING);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Convert string to lower case.
     */
    public static final Function LOWER = new Function() {
        
        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws FunctionCallException {
            checkParameterCount("LOWER", 1, parameters);
            try {
                String upperString = ((String) parameters[0]
                        .asType(SalinasType.STRING)).toLowerCase();
                return new SalinasValue(upperString, SalinasType.STRING);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Return the first <em>x</em> number of characters from the string.
     */
    public static final Function LEFT = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("LEFT", 2, parameters);
            try {
                final String originalString = (String) parameters[0].asType(
                        SalinasType.STRING);
                final int amount = ((BigDecimal) parameters[1]
                        .asType(SalinasType.NUMBER)).intValue();
                if (amount >= originalString.length()) {
                    return parameters[0];
                } else {
                    return new SalinasValue(originalString.substring(0, amount),
                            SalinasType.STRING);
                }
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Return the last <em>x</em> number of characters from the string.
     */
    public static final Function RIGHT = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("RIGHT", 2, parameters);
            try {
                final String originalString = (String) parameters[0].asType(
                        SalinasType.STRING);
                final int amount = ((BigDecimal) parameters[1]
                        .asType(SalinasType.NUMBER)).intValue();
                if (amount >= originalString.length()) {
                    return parameters[0];
                } else {
                    return new SalinasValue(originalString.substring(
                            originalString.length() - amount, originalString.length()),
                            SalinasType.STRING);
                }
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Return the middle <em>y</em> number of characters from the string,
     * starting at the character <em>x</em>.
     */
    public static final Function SUBSTR = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("SUBSTR", 3, parameters);
            try {
                final String originalString = (String) parameters[0].asType(
                        SalinasType.STRING);
                final int left = Math.min(((BigDecimal) parameters[1]
                        .asType(SalinasType.NUMBER)).intValue(), originalString.length());
                int amount = ((BigDecimal) parameters[2]
                        .asType(SalinasType.NUMBER)).intValue();
                final int remaining = originalString.length() - left;
                amount = Math.min(amount, remaining);
                return new SalinasValue(originalString.substring(left - 1,
                        left + amount - 1),
                        SalinasType.STRING);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Return the ASCII ordinal of the first character in the given string
     * parameter.
     */
    public static final Function ASC = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("ASC", 1, parameters);
            try {
                final String charString = (String) parameters[0].asType(
                        SalinasType.STRING);
                if (charString.length() == 0) {
                    return SalinasValue.NULL;
                }
                return new SalinasValue(BigDecimal.valueOf(charString.codePointAt(0)),
                        SalinasType.NUMBER);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Return the absolute value of the given number parameter.
     */
    public static final Function ABS = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("ABS", 1, parameters);
            try {
                final BigDecimal number = (BigDecimal) parameters[0].asType(
                        SalinasType.NUMBER);
                return new SalinasValue(number.abs());
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Takes the number at the beginning of the given string parameter and
     * turns it into a numeric type. This function mostly exists for legacy
     * reasons since string variables will normally convert between string
     * and numeric types automatically. This function has the additional
     * feature that extra garbage at the end will be stripped before the
     * conversion.
     */
    public static final Function VAL = new Function() {
        
        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("VAL", 1, parameters);
            try {
                final String stringValue = ((String) parameters[0].asType(
                        SalinasType.STRING)).replaceAll("\\D+$", "");
                try {
                    return new SalinasValue(new BigDecimal(stringValue),
                            SalinasType.NUMBER);
                } catch (NumberFormatException ex) {
                    return SalinasValue.NULL;
                }
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Generates a random floating point number between 0 (inclusive) and 1
     * (exclusive). To obtain a random number from <em>1</em> to <em>500</em>,
     * the following may be used: <code>INT(RANDOM() * 500) + 1</code>.
     */
    public static final Function RANDOM = new Function() {
        
        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("RANDOM", 0, 1, parameters);
            final SalinasConfig config = (SalinasConfig) context
                    .getAttribute("salinasConfig");
            try {
                if (parameters.length == 1) {
                    final long seed = ((BigDecimal) parameters[0].asType(
                            SalinasType.NUMBER)).longValue();
                    if (seed < 0) {
                        config.setCurrentRandomSeed(-1);
                        config.setCurrentRandom(new Random());
                    } else if (seed != 0 && config.getCurrentRandomSeed() != seed) {
                        config.setCurrentRandomSeed(seed);
                        config.setCurrentRandom(new Random(config.getCurrentRandomSeed()));
                    }
                }
                return new SalinasValue(BigDecimal.valueOf(config.getCurrentRandom().nextDouble()),
                        SalinasType.NUMBER);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Returns the given numeric parameter with the integers chopped off. In
     * other words, this function rounds the number down, towards zero.
     */
    public static final Function INT = new Function() {
        
        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("INT", 1, parameters);
            try {
                return new SalinasValue(((BigDecimal) parameters[0].asType(
                        SalinasType.NUMBER)).setScale(0, RoundingMode.DOWN),
                        SalinasType.NUMBER);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Returns the given number, rounded to the given number of decimals.
     * <p>If the number of decimals is not given, zero is assumed, and the given
     * number will be rounded to the closest whole number.</p>
     * <p>If the given number of decimals is negative, the number will be
     * rounded as if the decimal point were moved to the left - for example,
     * <code>-1</code> will round to the nearest <strong>tens</strong> and
     * <code>-2</code> will round to the nearest <strong>hundreds</strong>.</p>
     */
    public static final Function ROUND = new Function() {
        
        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("ROUND", 1, 2, parameters);
            final int decimals;
            if (parameters.length == 2) {
                decimals = ((BigDecimal) parameters[1].asType(SalinasType.NUMBER))
                        .intValue();
            } else {
                decimals = 0;
            }
            try {
                if (decimals >= 0) {
                    return new SalinasValue(((BigDecimal) parameters[0]
                            .asType(SalinasType.NUMBER))
                            .setScale(decimals, RoundingMode.HALF_UP),
                            SalinasType.NUMBER);
                } else {
                    // Negative decimals
                    return new SalinasValue(((BigDecimal) parameters[0].asType(SalinasType.NUMBER))
                            .divide(BigDecimal.valueOf(Math.pow(10, Math.abs(decimals)))).setScale(0, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(Math.pow(10, Math.abs(decimals)))),
                            SalinasType.NUMBER);
                }
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * Returns the value of PI, to 20 decimal places.
     */
    public static final Function PI = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("PI", 0, parameters);
            return SalinasValue.PI;
        }
    };

    /**
     * Return the 1-based position of the search string in the target string.
     */
    public static final Function AT = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("AT", 2, 3, parameters);
            try {
                final String needle = (String) parameters[0].asType(
                        SalinasType.STRING);
                if (needle.length() == 0) {
                    return new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER);
                }
                
                final String haystack = (String) parameters[1].asType(
                        SalinasType.STRING);
                if (haystack.length() == 0) {
                    return new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER);
                }

                int matches;
                if (parameters.length == 3) {
                    matches = ((BigDecimal) parameters[2].asType(SalinasType.NUMBER))
                            .intValue();
                } else {
                    matches = 1;
                }
                int pos = -1;
                while (matches > 0) {
                    pos = haystack.indexOf(needle, pos + 1);
                    if (pos != -1) {
                        matches--;
                    } else {
                        return new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER);
                    }
                }
                return new SalinasValue(BigDecimal.valueOf(Math.max(pos + 1, 0)),
                        SalinasType.NUMBER);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };
    
    /**
     * Return the 1-based position of the search string in the target string,
     * starting from the right hand side of the string and moving left.
     */
    public static final Function RAT = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("RAT", 2, 3, parameters);
            try {
                final String needle = (String) parameters[0].asType(
                        SalinasType.STRING);
                if (needle.length() == 0) {
                    return new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER);
                }
                
                final String haystack = (String) parameters[1].asType(
                        SalinasType.STRING);
                if (haystack.length() == 0) {
                    return new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER);
                }

                int matches;
                if (parameters.length == 3) {
                    matches = ((BigDecimal) parameters[2].asType(SalinasType.NUMBER))
                            .intValue();
                } else {
                    matches = 1;
                }
                int pos = haystack.length();
                while (matches > 0) {
                    pos = haystack.lastIndexOf(needle, pos - 1);
                    if (pos != -1) {
                        matches--;
                    } else {
                        return new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER);
                    }
                }
                return new SalinasValue(BigDecimal.valueOf(Math.max(pos + 1, 0)),
                        SalinasType.NUMBER);
            } catch (SalinasException ex) {
                throw new FunctionCallException(ex, ex.getFilename(),
                        ex.getBeginLine(), ex.getBeginColumn());
            }
        }
    };

    /**
     * If the first parameter evaluates to <code>true</code>, return the
     * second parameter - otherwise return the third parameter.
     */
    public static final Function IIF = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("IIF", 3, parameters);
            return ((Boolean) parameters[0].asType(SalinasType.BOOLEAN)).booleanValue()
                    ? parameters[1] : parameters[2];
        }
    };

    public static final Function CHR = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("CHR", 1, parameters);
            return new SalinasValue(new String(new int[] {((BigDecimal) parameters[0]
                    .asType(SalinasType.NUMBER)).intValue()}, 0, 1),
                    SalinasType.STRING);
        }
    };

    public static final Function CENTER = new Function() {

        @Override
        public SalinasValue call(ScriptContext context, SalinasValue... parameters)
                throws SalinasException {
            checkParameterCount("CENTER", 1, 3, parameters);
            String s = (String) parameters[0].asType(SalinasType.STRING);
            final int columns;
            if (parameters.length >= 2) {
                columns = ((BigDecimal) parameters[1].asType(SalinasType.NUMBER))
                        .intValue();
            } else {
                columns = 20;
            }
            final String padWith;
            if (parameters.length >= 3) {
                padWith = new String(new int[] {
                        ((String) parameters[2].asType(SalinasType.STRING))
                        .codePointAt(0)
                        }, 0, 1);
            } else {
                padWith = " ";
            }

            // TODO need better displayable metrics
            int displayableStringWidth = s.codePointCount(0, s.length());

            if (displayableStringWidth >= columns) {
                return parameters[0];
            }

            int extraColumns = columns - displayableStringWidth;
            int padding = (int) (columns / 2f - displayableStringWidth / 2f);
            StringBuilder sb = new StringBuilder(columns);
            for (int x = 0; x < padding; x++) {
                sb.append(padWith);
            }
            sb.append(s);
            for (int x = 0; x < extraColumns - padding; x++) {
                sb.append(padWith);
            }

            return new SalinasValue(sb.toString(), SalinasType.STRING);
        }
    };

    private static void checkParameterCount(String functionName, int length,
            SalinasValue... parameters) throws FunctionCallException {
        if (length != parameters.length) {
            throw new FunctionCallException("Function " + functionName
                    + " must be called with " + length + " parameters");
        }
    }

    private static void checkParameterCount(String functionName, int fromLength,
            int toLength, SalinasValue... parameters) throws FunctionCallException {
        if (parameters.length < fromLength || parameters.length > toLength) {
            throw new FunctionCallException("Function " + functionName
                    + " must be called with between " + fromLength + " and "
                    + toLength + " parameters");
        }
    }

    static {
        // String
        functionMap.put("UPPER", UPPER);
        functionMap.put("LOWER", LOWER);
        functionMap.put("LEFT", LEFT);
        functionMap.put("RIGHT", RIGHT);
        functionMap.put("SUBSTR", SUBSTR);
        functionMap.put("AT", AT);
        functionMap.put("RAT", RAT);
        functionMap.put("CHR", CHR);
        functionMap.put("CENTER", CENTER);

        // Numeric
        functionMap.put("ASC", ASC);
        functionMap.put("ABS", ABS);
        functionMap.put("VAL", VAL);
        functionMap.put("RANDOM", RANDOM);
        functionMap.put("INT", INT);
        functionMap.put("ROUND", ROUND);
        functionMap.put("PI", PI);

        // Conditional
        functionMap.put("IIF", IIF);
    }

    @Override
    public Function getFunction(String name, SalinasNode node) {
        return functionMap.get(name.toUpperCase());
    }
}

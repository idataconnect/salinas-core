/*
 * Copyright 2011-2021 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasConfig;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ComparativeOp;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasArrayMap;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParserConstants;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.script.ScriptContext;

// TODO this is currently a catch-all for too many types

/**
 * Interpreter delegate implementation for expression nodes.
 */
public class ExpressionInterpreter implements InterpreterDelegate {

    private static ExpressionInterpreter instance;

    /**
     * Gets a singleton instance of the expression interpreter.
     * @return a singleton instance
     */
    public static ExpressionInterpreter getInstance() {
        if (instance == null) {
            instance = new ExpressionInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        SalinasConfig config;
        try {
            config = (SalinasConfig) context.getAttribute("salinasConfig");
        } catch (ClassCastException ex) {
            System.err.println("Invalid configuration: " + ex.getMessage());
            context.setAttribute("salinasConfig", config = new SalinasConfig(), ScriptContext.ENGINE_SCOPE);
        }

        SalinasValue returnValue;

        List<? extends Integer> opTypes;
        switch (node.getId()) {
            case JJTADDITIVE:
                opTypes = (List<? extends Integer>) node.jjtGetValue();
                Iterator<? extends Integer> i = opTypes.iterator();
                boolean addStrings = false;
                boolean moveSpacesToEnd = false;
                int numChildren = node.jjtGetNumChildren();
                List<SalinasValue> values = new ArrayList<>(numChildren);
                for (int count = 0; count < numChildren; count++) {
                    Integer opType = null;
                    if (i.hasNext()) {
                        opType = i.next();
                    }
                    final SalinasValue value = SalinasInterpreter.interpret(
                            node.getChild(count), context);
                    values.add(value);
                    if (value != null && opType != null) {
                        if (!moveSpacesToEnd) {
                            moveSpacesToEnd = opType.equals(SalinasParserConstants.MINUS);
                        }
                    }
                    if (value != null) {
                        if (!addStrings) {
                            addStrings = value.getCurrentType().equals(SalinasType.STRING);
                        }
                    }
                }

                final StringBuilder sb = new StringBuilder(values.size() * 64);
                if (addStrings) {
                    int spacesToAdd = 0;
                    for (SalinasValue value : values) {
                        String stringValue
                                = (String) value.asType(SalinasType.STRING);
                        if (moveSpacesToEnd) {
                            final int currentLength = stringValue.length();
                            stringValue = stringValue.trim();
                            spacesToAdd += currentLength - stringValue.length();
                        }

                        sb.append(stringValue);
                    }

                    if (spacesToAdd > 0) {
                        for (int count = 0; count < spacesToAdd; count++) {
                            sb.append(' ');
                        }
                    }

                    returnValue = new SalinasValue(sb.toString(), SalinasType.STRING);
                } else {
                    i = opTypes.iterator();
                    final Iterator<SalinasValue> vi = values.iterator();
                    BigDecimal currentValue = (BigDecimal) vi.next()
                            .asType(SalinasType.NUMBER);
                    for (int count = 1; i.hasNext(); count++) {
                        final Integer opType = i.next();
                        if (count == 1 && !vi.hasNext()) {
                            // Handle unary minus expression (e.g. "-1")
                            currentValue = BigDecimal.ZERO.subtract(currentValue);
                        } else {
                            if (opType == SalinasParserConstants.PLUS) {
                                currentValue = currentValue.add((BigDecimal) vi.next()
                                        .asType(SalinasType.NUMBER));
                            } else if (opType == SalinasParserConstants.MINUS) {
                                currentValue = currentValue.subtract((BigDecimal) vi.next()
                                        .asType(SalinasType.NUMBER));
                            }
                        }
                    }

                    returnValue = new SalinasValue(currentValue, SalinasType.NUMBER);
                }
                break;
            case JJTMULTIPLICATIVE:
            case JJTEXPONENT:
                returnValue = numericCalculation(node, context);
                break;
            case JJTASSIGN: {
                // Assign
                // L Identifier
                // L Expression
                //
                // Assign
                // L ArrayAccess
                //   L Identifier
                //   L ArrayAccessSegment
                //     L Expression
                //   L ArrayAccessSegment
                //     L Expression
                //   L ...
                // L Expression
                assert node.jjtGetNumChildren() == 2;
                final SalinasNode variableNode
                        = (SalinasNode) node.jjtGetChild(0);
                final SalinasNode expressionNode
                        = (SalinasNode) node.jjtGetChild(1);
                final SalinasNode identifierNode;

                final SalinasValue expressionValue
                        = SalinasInterpreter.interpret(expressionNode, context);

                Optional<SalinasValue> existingVar;

                switch (variableNode.getId()) {
                    case JJTIDENTIFIER:
                        // Assign to variable
                        existingVar = node.getVariable(
                                (String) variableNode.jjtGetValue(), context);
                        identifierNode = variableNode;
                        break;
                    case JJTARRAYACCESS:
                        // Assign inside array

                        // First child is the identifier of the array
                        identifierNode = variableNode.getChild(0);
                        existingVar = node.getVariable(
                                (String) identifierNode.jjtGetValue(), context);
                        if (existingVar.isEmpty()) {
                            // The array doesn't exist; create it
                            existingVar = Optional.of(new SalinasValue(
                                    new SalinasArrayMap(), SalinasType.ARRAY));
                        } else if (existingVar.get().getCurrentType() != SalinasType.ARRAY) {
                            throw new SalinasException("Array access attempted on "
                                    + ((SalinasNode) variableNode.jjtGetChild(0))
                                            .jjtGetValue() + "which is of type "
                                    + existingVar.get().getCurrentType(),
                                    variableNode.getFilename(),
                                    variableNode.getBeginLine(),
                                    variableNode.getBeginColumn());
                        }
                        SalinasValue previousVar = existingVar.get();
                        // Other children are of type ArrayAccessSegment
                        SalinasArrayMap arrayMap;
                        SalinasNode childNode;
                        Object indexValue;
                        // Chain previous array indices together
                        for (int count = 1; count < variableNode.jjtGetNumChildren(); count++) {
                            arrayMap = (SalinasArrayMap) previousVar.getValue();
                            childNode = variableNode.getChild(count).getChild(0);
                            indexValue = SalinasInterpreter
                                    .interpret(childNode, context)
                                    .getValue();

                            if (count == variableNode.jjtGetNumChildren() - 1) {
                                // Store the expression value to the last array index
                                arrayMap.put(indexValue, expressionValue);
                            } else {
                                // Check if the array index exists
                                if (arrayMap.containsKey(indexValue)) {
                                    // Array index already exists
                                    previousVar = arrayMap.get(indexValue);
                                } else {
                                    // Array index does not exist; Create it
                                    previousVar = new SalinasValue(
                                            new SalinasArrayMap(),
                                            SalinasType.ARRAY);

                                    arrayMap.put(indexValue, previousVar);
                                }
                            }
                        }
                        break;
                    default:
                        throw new SalinasException("Unexpected node after assign: " + variableNode);
                }

                // Assign
                if (existingVar.isPresent()) {
                    if (variableNode.getId() == JJTIDENTIFIER) {
                        existingVar.get().setValue(expressionValue);
                    }
                    returnValue = existingVar.get();
                } else {
                    returnValue = expressionValue;
                }

                // Apply modifiers
                boolean isPublic = false;
                for (int count = 0; count < variableNode.jjtGetNumChildren(); count++) {
                    final SalinasNode childNode = (SalinasNode) variableNode.jjtGetChild(count);
                    if (childNode.getId() == JJTDATATYPE) {
                        final SalinasType dataType
                                = (SalinasType) ((SalinasNode) variableNode.jjtGetChild(count))
                                .jjtGetValue();
                        assert dataType != null && dataType instanceof SalinasType;
                        returnValue.setStrongType(dataType);
                    } else if (childNode.getId() == JJTMODIFIERS) {
                        for (int cc = 0; cc < childNode.jjtGetNumChildren(); cc++) {
                            final SalinasNode m = (SalinasNode) childNode.jjtGetChild(cc);
                            if (m.getId() == JJTPUBLIC) {
                                isPublic = true;
                            } else if (m.getId() == JJTSTATIC) {
                                // TODO finish static
                            }
                        }
                    }
                }
                if (isPublic) {
                    context.getBindings(ScriptContext.ENGINE_SCOPE)
                            .put(identifierNode.jjtGetValue().toString().toUpperCase(),
                            returnValue);
                } else {
                    node.getFirstVariableHolder()
                            .setVariable((String) identifierNode.jjtGetValue(),
                            returnValue, context);
                }

                if (variableNode.getId() == JJTARRAYACCESS) {
                    // Special handling for arrays. Consider using
                    // returnValue = ExpressionInterpreter.interpret(returnValue);
                    // instead, however this works now and performs better.
                    returnValue = expressionValue;
                }

                break;
            }
            case JJTIDENTIFIER: {
                // TODO consolidate variable setting routines
                Optional<SalinasValue> existingVar = node.getVariable(
                        (String) node.jjtGetValue(), context);
                if (existingVar.isPresent()) {
                    returnValue = existingVar.get();
                } else {
                    returnValue = new SalinasValue(null, SalinasType.NULL);
                }
                if (node.jjtGetNumChildren() > 0) {
                    assert ((SalinasNode) node.jjtGetChild(0)).getId()
                            == JJTDATATYPE;
                    final SalinasType dataType
                            = (SalinasType) ((SalinasNode) node.jjtGetChild(0))
                            .jjtGetValue();
                    returnValue.setStrongType(dataType);
                }
                break;
            }
            case JJTCOMPARE:
            case JJTEQUALITY:
                returnValue = comparativeEvaluation(node, context);
                break;
            default:
                returnValue = SalinasInterpreter.interpret(node, context);
                break;
        }
        if (returnValue == null) {
            returnValue = new SalinasValue(null, SalinasType.NULL);
        }
        return returnValue;
    }

    private SalinasValue numericCalculation(SalinasNode node, ScriptContext context)
            throws SalinasException {
        final SalinasConfig config = (SalinasConfig) context.getAttribute("salinasConfig");

        final List<SalinasValue> values = new ArrayList<>(4);
        for (int count = 0; count < node.jjtGetNumChildren(); count++) {
            final SalinasValue value = SalinasInterpreter.interpret(
                    (SalinasNode) node.jjtGetChild(count), context);
            if (value == null) {
                values.add(SalinasValue.NULL);
            } else {
                values.add(value);
            }
        }

        List<? extends Integer> opTypes = (List<? extends Integer>) node.jjtGetValue();
        Iterator<? extends Integer> i = opTypes.iterator();
        final Iterator<SalinasValue> vi = values.iterator();
        BigDecimal currentValue = (BigDecimal) vi.next().asType(SalinasType.NUMBER);
        while (i.hasNext()) {
            try {
                final Integer opType = i.next();
                assert opType != null;
                switch (opType) {
                    case SalinasParserConstants.MULT:
                        currentValue = currentValue.multiply((BigDecimal) vi.next()
                                .asType(SalinasType.NUMBER));
                        break;
                    case SalinasParserConstants.DIV:
                        currentValue = currentValue.divide((BigDecimal) vi.next()
                                .asType(SalinasType.NUMBER), config.getPrecision(), RoundingMode.HALF_EVEN);
                        break;
                    case SalinasParserConstants.MOD:
                        currentValue = currentValue.remainder((BigDecimal) vi.next()
                                .asType(SalinasType.NUMBER));
                        break;
                    case SalinasParserConstants.EXP:
                        currentValue = currentValue.pow(((BigDecimal) vi.next()
                                .asType(SalinasType.NUMBER)).intValue());
                        break;
                }
            } catch (ArithmeticException ex) {
                throw new SalinasException("Arithmetic error: " + ex.getMessage(),
                        ex, node.getFilename(), node.getBeginLine(),
                        node.getBeginColumn());
            }
        }

        return new SalinasValue(currentValue, SalinasType.NUMBER);
    }

    private SalinasValue comparativeEvaluation(SalinasNode node, ScriptContext context)
            throws SalinasException {
        boolean usingStrings = false;
        List<? extends ComparativeOp> opTypes = (List<? extends ComparativeOp>) node.jjtGetValue();
        Iterator<? extends ComparativeOp> i = opTypes.iterator();
        final List<SalinasValue> values = new ArrayList<>(4);
        for (int count = 0; count < node.jjtGetNumChildren(); count++) {
            SalinasValue value = SalinasInterpreter.interpret(
                    (SalinasNode) node.jjtGetChild(count), context);
            if (value == null) {
                value = SalinasValue.NULL;
            }
            values.add(value);

            if (value.getCurrentType() == SalinasType.STRING) {
                usingStrings = true;
            }
        }

        final Iterator<SalinasValue> vi = values.iterator();
        SalinasValue currentValue = vi.next();
        if (usingStrings) {
            final ComparativeOp firstOp = opTypes.get(0);
            if (firstOp != ComparativeOp.EQUAL_TO_EXACT
                    && firstOp != ComparativeOp.NOT_EQUAL_TO_EXACT) {
                currentValue = new SalinasValue(currentValue.asType(SalinasType.STRING),
                        SalinasType.STRING);
            }
        }
        while (i.hasNext()) {
            SalinasValue nextValue = vi.next();
            final ComparativeOp op = i.next();
            if (usingStrings) {
                if (op != ComparativeOp.EQUAL_TO_EXACT
                        && op != ComparativeOp.NOT_EQUAL_TO_EXACT) {
                    nextValue = new SalinasValue(nextValue.asType(
                            SalinasType.STRING), SalinasType.STRING);
                }
            }
            currentValue = new SalinasValue(op.apply(currentValue, nextValue),
                    SalinasType.BOOLEAN);
        }

        return currentValue;
    }

    public void applyDecimals(SalinasValue numericValue, SalinasConfig config)
            throws ConversionException {
        numericValue.setValue(numericValue.asNumber().setScale(config.getPrecision()));
    }
}

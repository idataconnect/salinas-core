package com.idataconnect.salinas.interpreter;

import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTARRAYACCESS;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTDATATYPE;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTIDENTIFIER;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTMODIFIERS;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTPUBLIC;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTSTATIC;

import java.util.Optional;

import javax.script.ScriptContext;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasArrayMap;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;

public class AssignInterpreter implements InterpreterDelegate {

    private static AssignInterpreter instance;

    /**
     * Gets a singleton instance of the assign interpreter.
     * @return a singleton instance
     */
    public static AssignInterpreter getInstance() {
        if (instance == null) {
            instance = new AssignInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
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

        SalinasValue returnValue;

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

        return returnValue;
    }

}

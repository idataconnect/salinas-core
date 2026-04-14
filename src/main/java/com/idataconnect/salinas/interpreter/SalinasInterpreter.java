/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParserTreeConstants;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



/**
 * Interprets nodes using a script context. This is the main entry point for
 * interpreting Salinas code.
 */
public class SalinasInterpreter {

    /**
     * Array of interpreter delegates assigned to interpret the
     * node. When more node types are added to salinas.jjt, delegates should
     * be added to this array. Some nodes are handled by the delegates directly,
     * and do not need to be added to this array.
     */
    private static final InterpreterDelegate[] delegates = new InterpreterDelegate[SalinasParserTreeConstants.jjtNodeName.length];

    static {
        delegates[JJTSALINASSCRIPT] = StatementInterpreter.getInstance();
        delegates[JJTSTATEMENT] = StatementInterpreter.getInstance();
        delegates[JJTIDENTIFIER] = ExpressionInterpreter.getInstance();
        delegates[JJTMULTIPLICATIVE] = ExpressionInterpreter.getInstance();
        delegates[JJTADDITIVE] = ExpressionInterpreter.getInstance();
        delegates[JJTASSIGN] = AssignInterpreter.getInstance();
        delegates[JJTEQUALITY] = ExpressionInterpreter.getInstance();
        delegates[JJTCOMPARE] = ExpressionInterpreter.getInstance();
        delegates[JJTNUMBER] = LiteralInterpreter.getInstance();
        delegates[JJTSTRING] = LiteralInterpreter.getInstance();
        delegates[JJTBOOLEAN] = LiteralInterpreter.getInstance();
        delegates[JJTDATE] = LiteralInterpreter.getInstance();
        delegates[JJTNULL] = LiteralInterpreter.getInstance();
        delegates[JJTPRINT] = PrintInterpreter.getInstance();
        delegates[JJTPRINTLN] = PrintInterpreter.getInstance();
        delegates[JJTPRINTPRELN] = PrintInterpreter.getInstance();
        delegates[JJTCONTAINS] = ContainsInterpreter.getInstance();
        delegates[JJTAND] = BooleanInterpreter.getInstance();
        delegates[JJTOR] = BooleanInterpreter.getInstance();
        delegates[JJTBOOLEANNOT] = BooleanInterpreter.getInstance();
        delegates[JJTIFBLOCK] = IfInterpreter.getInstance();
        delegates[JJTFORLOOP] = ForInterpreter.getInstance();
        delegates[JJTCASEBLOCK] = CaseInterpreter.getInstance();
        delegates[JJTWHILELOOP] = WhileInterpreter.getInstance();
        delegates[JJTFUNCTIONDECLARATION] = FunctionDeclInterpreter.getInstance();
        delegates[JJTRETURN] = ReturnInterpreter.getInstance();
        delegates[JJTFUNCTIONCALL] = FunctionCallInterpreter.getInstance();
        delegates[JJTARRAYACCESS] = ArrayAccessInterpreter.getInstance();
        delegates[JJTARRAYLITERAL] = ArrayLiteralInterpreter.getInstance();
        delegates[JJTSET] = SetInterpreter.getInstance();
        delegates[JJTSETEXPRESSION] = SetInterpreter.getInstance();
        delegates[JJTEXPONENT] = ExpressionInterpreter.getInstance();
        delegates[JJTUSE] = UseInterpreter.getInstance();
        delegates[JJTREPLACE] = ReplaceInterpreter.getInstance();
        delegates[JJTAPPEND] = AppendInterpreter.getInstance();
        delegates[JJTINDEX] = IndexInterpreter.getInstance();
        delegates[JJTGOTO] = GotoInterpreter.getInstance();
        delegates[JJTDISPLAY] = DisplayInterpreter.getInstance();
        delegates[JJTLIST] = ListInterpreter.getInstance();
        delegates[JJTDO] = DoInterpreter.getInstance();
        delegates[JJTATSAY] = AtSayInterpreter.getInstance();
    }

    private SalinasInterpreter() {}

    /**
     * Interprets the given node using state from the given context. Sub-nodes
     * will be interpreted recursively as required, by each interpreter delegate.
     *
     * @param node the node to interpret
     * @param context the execution context
     * @return the result from interpreting the node, as a value
     * @throws SalinasException if an error occurs
     */
    public static SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        SalinasValue returning = context.getReturning();
        if (returning != null) {
            return returning;
        }

        InterpreterDelegate delegate = getDelegate(node.getId());
        assert delegate != null : "No interpreter delegate for " + SalinasParserTreeConstants.jjtNodeName[node.getId()];

        return delegate.interpret(node, context);
    }

    static InterpreterDelegate getDelegate(int nodeId) {
        if (nodeId >= delegates.length || nodeId < 0) {
            return null;
        }
        return delegates[nodeId];
    }
}

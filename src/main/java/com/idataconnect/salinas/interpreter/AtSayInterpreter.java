package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParserTreeConstants;

/**
 * Interpreter delegate implementation for @ SAY/GET command.
 */
public class AtSayInterpreter implements InterpreterDelegate {

    private static final AtSayInterpreter INSTANCE = new AtSayInterpreter();

    public static AtSayInterpreter getInstance() {
        return INSTANCE;
    }

    private AtSayInterpreter() {}

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context) throws SalinasException {
        // node has 3 children: rowExp, colExp, textExp
        if (node.jjtGetNumChildren() < 3) {
            throw new SalinasException("Internal Error: @ SAY/GET requires 3 expressions.");
        }

        SalinasValue rowVal = SalinasInterpreter.interpret(node.getChild(0), context);
        SalinasValue colVal = SalinasInterpreter.interpret(node.getChild(1), context);
        SalinasValue textVal = SalinasInterpreter.interpret(node.getChild(2), context);

        int row = rowVal.asNumber().intValue();
        int col = colVal.asNumber().intValue();
        String text = textVal.asString();

        com.idataconnect.salinas.SalinasUI ui = (com.idataconnect.salinas.SalinasUI) 
                context.getScriptContext().getAttribute("salinasUI");
        if (ui != null) {
            ui.say(row, col, text);
        }

        return SalinasValue.NULL;
    }
}

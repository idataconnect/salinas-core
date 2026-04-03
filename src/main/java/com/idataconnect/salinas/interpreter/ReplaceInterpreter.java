package com.idataconnect.salinas.interpreter;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.data.WorkAreaManager;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interpreter delegate implementation for REPLACE statement.
 */
public class ReplaceInterpreter implements InterpreterDelegate {

    private static ReplaceInterpreter instance;

    public static ReplaceInterpreter getInstance() {
        if (instance == null) {
            instance = new ReplaceInterpreter();
        }
        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        
        WorkAreaManager wam = context.getWorkAreaManager();
        if (wam == null) {
            throw new SalinasException("No work area manager available");
        }

        // ReplaceStatement
        // L Identifier (field)
        // L Expression (value)
        // L (optional) In (alias/id)

        assert node.jjtGetNumChildren() >= 2;
        
        SalinasNode fieldNode = (SalinasNode) node.jjtGetChild(0);
        String fieldName = (String) fieldNode.jjtGetValue();
        
        SalinasNode valueNode = (SalinasNode) node.jjtGetChild(1);
        SalinasValue newValue = SalinasInterpreter.interpret(valueNode, context);
        
        Optional<WorkArea> targetWa = Optional.empty();
        
        if (node.jjtGetNumChildren() > 2) {
            SalinasNode inNode = (SalinasNode) node.jjtGetChild(2);
            SalinasValue inValue = SalinasInterpreter.interpret((SalinasNode) inNode.jjtGetChild(0), context);
            if (inValue.getCurrentType() == SalinasType.NUMBER) {
                targetWa = wam.getWorkArea(((BigDecimal) inValue.getValue()).intValue());
            } else {
                targetWa = wam.getWorkArea(inValue.toString());
            }
        } else {
            targetWa = wam.getCurrentWorkArea();
        }

        if (targetWa.isEmpty()) {
            throw new SalinasException("Target work area not found for REPLACE");
        }

        try {
            DBF dbf = targetWa.get().getDbf();
            dbf.replace(fieldName, newValue.getValue());
        } catch (IOException ex) {
            throw new SalinasException("Error during REPLACE operation", ex);
        }

        return SalinasValue.NULL;
    }
}

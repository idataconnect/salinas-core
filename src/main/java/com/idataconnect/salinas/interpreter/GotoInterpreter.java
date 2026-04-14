package com.idataconnect.salinas.interpreter;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.parser.SalinasNode;
import java.io.IOException;
import java.util.Optional;

/**
 * Interpreter for GOTO statement.
 */
public class GotoInterpreter implements InterpreterDelegate {

    private static final GotoInterpreter INSTANCE = new GotoInterpreter();

    public static GotoInterpreter getInstance() {
        return INSTANCE;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        
        Optional<WorkArea> currentAreaOpt = context.getWorkAreaManager().getCurrentWorkArea();
        if (currentAreaOpt.isEmpty()) {
            throw new SalinasException("No table is open in the current work area.");
        }

        WorkArea currentArea = currentAreaOpt.get();
        DBF dbf = currentArea.getDbf();
        Object value = node.jjtGetValue();

        try {
            if ("TOP".equals(value)) {
                dbf.gotoRecord(1);
            } else if ("BOTTOM".equals(value)) {
                dbf.gotoRecord(dbf.getStructure().getNumberOfRecords());
            } else {
                // Should be an Expression
                SalinasNode exprNode = (SalinasNode) node.jjtGetChild(0);
                SalinasValue recVal = SalinasInterpreter.interpret(exprNode, context);
                int recno = ((Number) recVal.getValue()).intValue();
                dbf.gotoRecord(recno);
            }
        } catch (IOException ex) {
            throw new SalinasException("Error moving record pointer", ex);
        } catch (Exception ex) {
            throw new SalinasException("Invalid record number", ex);
        }

        return null;
    }
}

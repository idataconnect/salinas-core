package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.io.IOException;

/**
 * Interpreter delegate implementation for APPEND command.
 */
public class AppendInterpreter implements InterpreterDelegate {

    private static final AppendInterpreter INSTANCE = new AppendInterpreter();

    public static AppendInterpreter getInstance() {
        return INSTANCE;
    }

    private AppendInterpreter() {}

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context) throws SalinasException {
        context.getWorkAreaManager().getCurrentWorkArea().ifPresentOrElse(wa -> {
            try {
                wa.getDbf().appendBlank();
                wa.getDbf().gotoRecord(wa.getDbf().getStructure().getNumberOfRecords());
                
                // If it was just APPEND (not APPEND BLANK), trigger a browse
                if (node.jjtGetValue() == null) {
                    com.idataconnect.salinas.SalinasUI ui = (com.idataconnect.salinas.SalinasUI) 
                            context.getScriptContext().getAttribute("salinasUI");
                    if (ui != null) {
                        ui.browse(wa.getAlias());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error performing APPEND", e);
            }
        }, () -> {
             throw new RuntimeException("No database in use.");
        });
        
        return SalinasValue.NULL;
    }
}

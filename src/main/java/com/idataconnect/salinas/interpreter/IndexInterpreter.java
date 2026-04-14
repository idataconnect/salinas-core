package com.idataconnect.salinas.interpreter;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.parser.IndexOptions;
import com.idataconnect.salinas.parser.SalinasNode;
import java.io.IOException;
import java.util.Optional;

/**
 * Interpreter delegate implementation for INDEX statement.
 */
public class IndexInterpreter implements InterpreterDelegate {

    private static final IndexInterpreter INSTANCE = new IndexInterpreter();

    public static IndexInterpreter getInstance() {
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
        IndexOptions options = (IndexOptions) node.jjtGetValue();
        SalinasNode expressionNode = (SalinasNode) node.jjtGetChild(0);
        
        try {
            if (options.getTagName() != null) {
                // INDEX ON ... TAG <name> (MDX)
                dbf.index(options.getTagName(), options.getExpressionSource(), options.isUnique(), options.isDescending());
            } else {
                // INDEX ON ... TO <filename> (NDX)
                SalinasNode filenameNode = (SalinasNode) node.jjtGetChild(1);
                SalinasValue filenameVal = SalinasInterpreter.interpret(filenameNode, context);
                String filename = filenameVal.asString();
                
                java.io.File file = new java.io.File(filename);
                if (!file.isAbsolute() && context.getConfig().getCurrentDirectory() != null) {
                    file = new java.io.File(context.getConfig().getCurrentDirectory(), filename);
                }
                
                dbf.indexTo(file.getAbsolutePath(), options.getExpressionSource(), options.isUnique(), options.isDescending());
            }
        } catch (IOException ex) {
            String target = options.getTagName() != null ? options.getTagName() : "file";
            throw new SalinasException("Error creating index: " + target, ex);
        }

        return null;
    }
}

package com.idataconnect.salinas.interpreter;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.salinas.SalinasConfig;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.data.WorkAreaManager;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interpreter delegate implementation for USE statement.
 */
public class UseInterpreter implements InterpreterDelegate {

    private static UseInterpreter instance;

    public static UseInterpreter getInstance() {
        if (instance == null) {
            instance = new UseInterpreter();
        }
        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        
        WorkAreaManager wam = context.getWorkAreaManager();
        SalinasConfig config = context.getConfig();

        if (node.jjtGetNumChildren() == 0) {
            // USE with no arguments closes current work area
            try {
                Optional<WorkArea> current = wam.getCurrentWorkArea();
                if (current.isPresent()) {
                    current.get().getDbf().close();
                    // Need a way to remove it from wam, but wam.use with null could work if I implement it
                    // For now, let's just close it.
                }
            } catch (IOException ex) {
                throw new SalinasException("Error closing DBF", ex);
            }
            return null;
        }

        SalinasValue filenameValue = SalinasInterpreter.interpret((SalinasNode) node.jjtGetChild(0), context);
        String filename = (String) filenameValue.asType(SalinasType.STRING);
        
        int workAreaId = wam.getCurrentWorkAreaId();
        String alias = new File(filename).getName();
        if (alias.toLowerCase().endsWith(".dbf")) {
            alias = alias.substring(0, alias.length() - 4);
        }

        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            SalinasNode child = (SalinasNode) node.jjtGetChild(i);
            if (child.getId() == JJTIN) {
                SalinasValue inValue = SalinasInterpreter.interpret((SalinasNode) child.jjtGetChild(0), context);
                workAreaId = ((BigDecimal) inValue.asType(SalinasType.NUMBER)).intValue();
            } else if (child.getId() == JJTALIAS) {
                alias = (String) child.jjtGetValue();
            }
        }

        try {
            File dbfFile = new File(filename);
            if (!dbfFile.isAbsolute() && config.getCurrentDirectory() != null) {
                dbfFile = new File(config.getCurrentDirectory(), filename);
            }
            DBF dbf = DBF.use(dbfFile);
            wam.use(workAreaId, new WorkArea(alias, dbf));
        } catch (IOException ex) {
            throw new SalinasException("Error opening DBF: " + filename, ex);
        }

        return null;
    }
}

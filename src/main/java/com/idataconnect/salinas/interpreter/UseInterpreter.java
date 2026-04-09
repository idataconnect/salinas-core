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

    private static final UseInterpreter INSTANCE = new UseInterpreter();

    public static UseInterpreter getInstance() {
        return INSTANCE;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        
        WorkAreaManager wam = context.getWorkAreaManager();
        SalinasConfig config = context.getConfig();

        String filename = null;
        int workAreaId = wam.getCurrentWorkAreaId();
        String alias = null;
        int nextChild = 0;

        // Check if the first child is a filename (not an IN or ALIAS node)
        if (node.jjtGetNumChildren() > 0) {
            SalinasNode firstChild = (SalinasNode) node.jjtGetChild(0);
            if (firstChild.getId() != JJTIN && firstChild.getId() != JJTALIAS) {
                SalinasValue filenameValue = SalinasInterpreter.interpret(firstChild, context);
                filename = (String) filenameValue.asType(SalinasType.STRING);
                nextChild = 1;
                
                alias = new File(filename).getName();
                if (alias.toLowerCase().endsWith(".dbf")) {
                    alias = alias.substring(0, alias.length() - 4);
                }
            }
        }

        // Process remaining children (IN and ALIAS)
        for (int i = nextChild; i < node.jjtGetNumChildren(); i++) {
            SalinasNode child = (SalinasNode) node.jjtGetChild(i);
            if (child.getId() == JJTIN) {
                SalinasValue inValue = SalinasInterpreter.interpret((SalinasNode) child.jjtGetChild(0), context);
                workAreaId = ((BigDecimal) inValue.asType(SalinasType.NUMBER)).intValue();
                if (workAreaId == 0) {
                    workAreaId = wam.getNextAvailableId();
                }
            } else if (child.getId() == JJTALIAS) {
                alias = (String) child.jjtGetValue();
            }
        }

        if (filename == null) {
            // USE IN <area> with no filename closes the specified work area
            try {
                wam.use(workAreaId, null);
            } catch (IOException ex) {
                throw new SalinasException("Error closing DBF in area " + workAreaId, ex);
            }
            return null;
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

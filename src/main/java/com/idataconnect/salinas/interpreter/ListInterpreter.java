package com.idataconnect.salinas.interpreter;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.jdbfdriver.DBFField;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.parser.SalinasNode;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Interpreter for LIST statement.
 */
public class ListInterpreter implements InterpreterDelegate {

    private static final ListInterpreter INSTANCE = new ListInterpreter();

    public static ListInterpreter getInstance() {
        return INSTANCE;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        
        // LIST is often just DISPLAY with all records.
        // For STATUS/STRUCTURE it's identical to DISPLAY.
        Object value = node.jjtGetValue();
        if ("STRUCTURE".equals(value) || "STATUS".equals(value)) {
            return DisplayInterpreter.getInstance().interpret(node, context);
        }

        Optional<WorkArea> currentAreaOpt = context.getWorkAreaManager().getCurrentWorkArea();
        if (currentAreaOpt.isEmpty()) {
            throw new SalinasException("No table is open in the current work area.");
        }

        WorkArea wa = currentAreaOpt.get();
        DBF dbf = wa.getDbf();

        try {
            StringBuilder sb = new StringBuilder();
            List<DBFField> fields = dbf.getStructure().getFields();
            
            // Header
            sb.append(String.format("%7s ", "Record#"));
            for (DBFField f : fields) {
                sb.append(String.format("%-15s ", f.getFieldName()));
            }
            sb.append("\n");
            sb.append("-".repeat(7 + 1 + fields.size() * 16)).append("\n");

            // Records (cap at 100 for now to prevent terminal bloat)
            int count = 0;
            dbf.gotoRecord(1);
            while (!dbf.eof() && count < 100) {
                sb.append(String.format("%7d ", dbf.recno()));
                for (DBFField f : fields) {
                    String val = dbf.getValue(f.getFieldName()).getString();
                    if (val.length() > 15) val = val.substring(0, 12) + "...";
                    sb.append(String.format("%-15s ", val));
                }
                sb.append("\n");
                dbf.skip();
                count++;
            }
            context.getWriter().write(sb.toString() + "\n");
            context.getWriter().flush();
        } catch (IOException ex) {
            throw new SalinasException("I/O error during output", ex);
        }

        return null;
    }
}

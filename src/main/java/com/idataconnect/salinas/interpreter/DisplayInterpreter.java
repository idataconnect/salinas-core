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
 * Interpreter for DISPLAY statement.
 */
public class DisplayInterpreter implements InterpreterDelegate {

    private static final DisplayInterpreter INSTANCE = new DisplayInterpreter();

    public static DisplayInterpreter getInstance() {
        return INSTANCE;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        
        Object value = node.jjtGetValue();
        Optional<WorkArea> currentAreaOpt = context.getWorkAreaManager().getCurrentWorkArea();

        try {
            if ("STRUCTURE".equals(value)) {
                if (currentAreaOpt.isEmpty()) {
                    throw new SalinasException("No table is open in the current work area.");
                }
                WorkArea wa = currentAreaOpt.get();
                DBF dbf = wa.getDbf();
                StringBuilder sb = new StringBuilder("Structure for database: " + wa.getAlias() + "\n");
                sb.append(String.format("%-4s %-11s %-5s %6s %6s\n", "Pos", "Field Name", "Type", "Len", "Dec"));
                sb.append("-".repeat(40)).append("\n");
                List<DBFField> fields = dbf.getStructure().getFields();
                for (int i = 0; i < fields.size(); i++) {
                    DBFField f = fields.get(i);
                    sb.append(String.format("%-4d %-11s %-5s %6d %6d\n", 
                        i+1, f.getFieldName(), f.getFieldType().name().substring(0, 1), 
                        f.getFieldLength(), f.getDecimalLength()));
                }
                context.getWriter().write(sb.toString() + "\n");
            } else if ("STATUS".equals(value)) {
                // Delegate back to a helper or just implement here
                context.getWriter().write("Salinas Work Area Status:\n");
                context.getWriter().write(String.format("%-4s %-15s %-40s %-10s\n", "ID", "Alias", "File Path", "Records"));
                context.getWriter().write("-".repeat(74) + "\n");
                for (int i = 1; i <= 10; i++) {
                    final int id = i;
                    Optional<WorkArea> waOpt = context.getWorkAreaManager().getWorkArea(id);
                    if (waOpt.isPresent()) {
                        WorkArea w = waOpt.get();
                        String marker = (id == context.getWorkAreaManager().getCurrentWorkAreaId()) ? ">" : " ";
                        try {
                            context.getWriter().write(String.format("%s%-3d %-15s %-40s %-10d\n", 
                                marker, id, w.getAlias(), w.getDbf().getFile().getName(), 
                                w.getDbf().getStructure().getNumberOfRecords()));
                        } catch (Exception e) {
                            context.getWriter().write(String.format("%s%-3d %-15s %-40s %-10s\n", marker, id, w.getAlias(), "Unknown", "ERR"));
                        }
                    }
                }
                context.getWriter().write("\n");
            } else {
                // DISPLAY (current record)
                if (currentAreaOpt.isEmpty()) {
                    throw new SalinasException("No table is open in the current work area.");
                }
                WorkArea wa = currentAreaOpt.get();
                DBF dbf = wa.getDbf();
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Record# %d\n", dbf.recno()));
                for (DBFField field : dbf.getStructure().getFields()) {
                    sb.append(String.format("%-11s: %s\n", field.getFieldName(), dbf.getValue(field.getFieldName()).getString()));
                }
                context.getWriter().write(sb.toString() + "\n");
            }
            context.getWriter().flush();
        } catch (IOException ex) {
            throw new SalinasException("I/O error during output", ex);
        }

        return null;
    }
}

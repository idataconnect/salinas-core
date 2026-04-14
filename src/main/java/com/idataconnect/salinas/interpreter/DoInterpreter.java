package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.SalinasScriptEngine;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.io.File;
import java.io.FileReader;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for DO command.
 * Executes another Salinas program file.
 */
public class DoInterpreter implements InterpreterDelegate {

    private static final DoInterpreter INSTANCE = new DoInterpreter();

    public static DoInterpreter getInstance() {
        return INSTANCE;
    }

    private DoInterpreter() {}

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context) throws SalinasException {
        // Evaluate the filename expression
        SalinasValue fileVal = SalinasInterpreter.interpret(node.getChild(0), context);
        String filename = fileVal.asString();

        // Resolve file
        File file = resolveFile(filename, context);
        if (!file.exists()) {
            throw new SalinasException("File not found: " + filename);
        }

        try (FileReader reader = new FileReader(file)) {
            // Execute the script in the current context
            // We use the same engine and context to share variables, as per dBase behavior
            SalinasScriptEngine engine = (SalinasScriptEngine) context.getScriptContext().getAttribute("javax.script.engine", ScriptContext.ENGINE_SCOPE);
            if (engine == null) {
                // Fallback if not set (should be set by JSR-233)
                engine = new SalinasScriptEngine();
            }

            engine.eval(reader, context.getScriptContext());
        } catch (Exception e) {
            throw new SalinasException("Error executing program " + filename + ": " + e.getMessage(), e);
        }

        return SalinasValue.NULL;
    }

    private File resolveFile(String filename, SalinasExecutionContext context) {
        File currentDir = context.getConfig().getCurrentDirectory();

        File file = new File(filename);
        if (!file.isAbsolute() && currentDir != null) {
            file = new File(currentDir, filename);
        }

        if (!file.exists()) {
            // Try with extensions
            File withPrg = new File(file.getAbsolutePath() + ".prg");
            if (withPrg.exists()) return withPrg;

            File withSal = new File(file.getAbsolutePath() + ".sal");
            if (withSal.exists()) return withSal;
        }

        return file;
    }
}

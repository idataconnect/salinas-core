package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import javax.script.ScriptContext;

public class CreateInterpreter implements InterpreterDelegate {

    private static final CreateInterpreter INSTANCE = new CreateInterpreter();

    public static CreateInterpreter getInstance() {
        return INSTANCE;
    }

    private CreateInterpreter() {}

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context) throws SalinasException {
        ScriptContext sc = context.getScriptContext();
        Object app = sc.getAttribute("salinasApp", ScriptContext.ENGINE_SCOPE);
        if (app != null) {
            try {
                java.lang.reflect.Method method = app.getClass().getMethod("showFieldEditor");
                method.invoke(app);
            } catch (Exception e) {
                throw new SalinasException("Error invoking showFieldEditor", e);
            }
        } else {
            throw new SalinasException("UI application not found in context");
        }
        return SalinasValue.NULL;
    }
}

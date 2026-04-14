package com.idataconnect.salinas.interpreter;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.jdbfdriver.index.XBaseInterpreter;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.data.WorkAreaManager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Salinas implementation of the jdbfdriver XBaseInterpreter service.
 */
public class SalinasXBaseInterpreter implements XBaseInterpreter {

    private final ScriptEngine engine;

    public SalinasXBaseInterpreter() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("salinas");
    }

    @Override
    public Object evaluate(String expression, DBF dbf) throws Exception {
        if (engine == null) {
            throw new IllegalStateException("Salinas script engine not found");
        }

        // Setup the WorkArea context for the engine
        WorkAreaManager wam = new WorkAreaManager();
        WorkArea wa = new WorkArea("TEMP", dbf);
        wam.use(1, wa);

        engine.getContext().setAttribute("salinasWorkAreaManager", wam, javax.script.ScriptContext.ENGINE_SCOPE);

        return engine.eval(expression);
    }

    @Override
    public int getPriority() {
        return 10; // High priority (before simple)
    }
}

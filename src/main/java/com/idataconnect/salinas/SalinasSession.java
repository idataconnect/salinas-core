package com.idataconnect.salinas;

import com.idataconnect.salinas.data.WorkAreaManager;
import com.idataconnect.salinas.function.CallStack;
import com.idataconnect.salinas.function.FunctionContext;
import com.idataconnect.salinas.interpreter.SalinasExecutionContext;

import javax.script.ScriptContext;
import java.io.File;

/**
 * Encapsulates the headless state and engine of a Salinas workspace.
 * This class is independent of any UI implementation.
 */
public class SalinasSession {

    private final WorkAreaManager workAreaManager = new WorkAreaManager();
    private final SalinasScriptEngine engine = new SalinasScriptEngine();
    private final SalinasExecutionContext execContext;
    private String currentPath;

    public SalinasSession() {
        SalinasConfig config = new SalinasConfig();
        this.currentPath = new File(".").getAbsolutePath();
        config.setCurrentDirectory(new File(currentPath));

        // Initialize persistent execution context
        ScriptContext sc = engine.getContext();
        sc.setAttribute("salinasConfig", config, ScriptContext.ENGINE_SCOPE);
        sc.setAttribute("salinasCallStack", new CallStack(), ScriptContext.ENGINE_SCOPE);
        sc.setAttribute("salinasFunctionContext", new FunctionContext(sc), ScriptContext.ENGINE_SCOPE);
        sc.setAttribute("salinasWorkAreaManager", workAreaManager, ScriptContext.ENGINE_SCOPE);
        sc.setAttribute("salinasCurrentPath", currentPath, ScriptContext.ENGINE_SCOPE);
        // User should set "salinasApp" or other UI-specific attributes externally
        this.execContext = new SalinasExecutionContext(sc);
    }

    public WorkAreaManager getWorkAreaManager() {
        return workAreaManager;
    }

    public SalinasScriptEngine getEngine() {
        return engine;
    }

    public SalinasExecutionContext getExecContext() {
        return execContext;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
        if (execContext != null && execContext.getConfig() != null) {
            execContext.getConfig().setCurrentDirectory(new File(currentPath));
        }
        engine.getContext().setAttribute("salinasCurrentPath", currentPath, ScriptContext.ENGINE_SCOPE);
    }
}

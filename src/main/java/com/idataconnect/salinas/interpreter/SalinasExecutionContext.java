package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasConfig;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.data.WorkArea;
import com.idataconnect.salinas.data.WorkAreaManager;
import com.idataconnect.salinas.function.CallStack;
import com.idataconnect.salinas.function.FunctionContext;
import java.util.Optional;
import javax.script.ScriptContext;

/**
 * A context for the execution of a Salinas script.
 */
public class SalinasExecutionContext {
    private final ScriptContext scriptContext;
    private SalinasScope globalScope;
    private SalinasScope currentScope;

    public SalinasExecutionContext(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
        // Start with a scope that delegates to the ScriptContext's bindings
        this.globalScope = new SalinasScope(null) {
            @Override
            public Optional<SalinasValue> getVariable(String name) {
                Object value = scriptContext.getAttribute(name.toUpperCase(), ScriptContext.ENGINE_SCOPE);
                if (value == null) {
                    value = scriptContext.getAttribute(name.toUpperCase(), ScriptContext.GLOBAL_SCOPE);
                }
                if (value == null) {
                    // Check WorkAreaManager for fields
                    WorkAreaManager wam = getWorkAreaManager();
                    if (wam != null) {
                        Optional<WorkArea> wa = wam.getCurrentWorkArea();
                        if (wa.isPresent()) {
                            com.idataconnect.jdbfdriver.DBF dbf = wa.get().getDbf();
                            int fieldNum = dbf.getFieldNumberByName(name);
                            if (fieldNum > 0) {
                                try {
                                    Object val = dbf.getValue(fieldNum).getValue();
                                    return Optional.of(SalinasValue.valueOf(val));
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                    return Optional.empty();
                }
                return Optional.of(SalinasValue.valueOf(value));
            }

            @Override
            public void setVariable(String name, SalinasValue value) {
                scriptContext.setAttribute(name.toUpperCase(), value, ScriptContext.ENGINE_SCOPE);
            }
        };
        this.currentScope = globalScope;
    }

    public SalinasScope getCurrentScope() {
        return currentScope;
    }

    public void pushScope() {
        currentScope = new SalinasScope(currentScope);
    }

    public void popScope() {
        if (currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        }
    }

    public ScriptContext getScriptContext() {
        return scriptContext;
    }

    public java.io.Writer getWriter() {
        return scriptContext.getWriter();
    }

    public SalinasConfig getConfig() {
        return (SalinasConfig) scriptContext.getAttribute("salinasConfig");
    }

    public CallStack getCallStack() {
        return (CallStack) scriptContext.getAttribute("salinasCallStack");
    }

    public FunctionContext getFunctionContext() {
        return (FunctionContext) scriptContext.getAttribute("salinasFunctionContext");
    }

    public WorkAreaManager getWorkAreaManager() {
        return (WorkAreaManager) scriptContext.getAttribute("salinasWorkAreaManager");
    }

    public Optional<SalinasValue> getVariable(String name) {
        return currentScope.getVariable(name);
    }

    public void setVariable(String name, SalinasValue value) {
        currentScope.setVariable(name, value);
    }

    public void setGlobalVariable(String name, SalinasValue value) {
        globalScope.setVariable(name, value);
    }

    public SalinasValue getReturning() {
        return (SalinasValue) scriptContext.getAttribute("returning", ScriptContext.ENGINE_SCOPE);
    }

    public void setReturning(SalinasValue value) {
        scriptContext.setAttribute("returning", value, ScriptContext.ENGINE_SCOPE);
    }

    public void clearReturning() {
        scriptContext.removeAttribute("returning", ScriptContext.ENGINE_SCOPE);
    }
}

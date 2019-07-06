/*
 * Copyright 2011-2013 i Data Connect!
 */

package com.idataconnect.salinas;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * JSR-233 script engine factory implementation for Salinas.
 */
public class SalinasScriptEngineFactory implements ScriptEngineFactory{

    /** An instance of the newline character(s) for the current system. */
    public static final String NEW_LINE = System.getProperty("line.separator");

    private static final SalinasScriptEngineFactory INSTANCE =
            new SalinasScriptEngineFactory();

    @Override
    public String getEngineName() {
        return "Salinas Engine";
    }

    @Override
    public String getEngineVersion() {
        return "1.0";
    }

    @Override
    public List<String> getExtensions() {
        return Collections.singletonList("prg");
    }

    @Override
    public List<String> getMimeTypes() {
        return Arrays.asList(new String[] {
            "application/x-salinas-script",
            "application/x-xbase-script",
        });
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList(new String[] {
            "Salinas",
            "salinas",
            "xbase",
            "xBase",
        });
    }

    @Override
    public String getLanguageName() {
        return "Salinas";
    }

    @Override
    public String getLanguageVersion() {
        String implVersion = getClass().getPackage().getImplementationVersion();
        return implVersion != null ? implVersion : "UNKNOWN";
    }

    @Override
    public Object getParameter(String key) {
        if (key.equals(ScriptEngine.ENGINE)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        } else if (key.equals(ScriptEngine.NAME)) {
            return getNames();
        } else if (key.equals(ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        } else if (key.equals("THREADING")) {
            return null;//"MULTITHREADED";
        } else {
            return null;
        }
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        StringBuilder sb = new StringBuilder(32);
        sb.append(m)
                .append('(');
        for (int count = 0; count < args.length; count++) {
            if (count != 0) {
                sb.append(',');
            }
            sb.append(args[count]);
        }
        return sb.append(')').toString();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "?? " + toDisplay;
    }

    @Override
    public String getProgram(String... statements) {
        StringBuilder sb = new StringBuilder(statements.length * 40);
        for (String statement : statements) {
            sb.append(statement).append(NEW_LINE);
        }

        return sb.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new SalinasScriptEngine(this);
    }

    /**
     * Gets a singleton instance of the Salinas script engine factory.
     *
     * @return a factory instance
     */
    public static SalinasScriptEngineFactory getInstance() {
        return INSTANCE;
    }
}

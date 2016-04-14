/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasArrayMap;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.math.BigDecimal;
import javax.script.ScriptContext;

/**
 * An interpreter delegate for array literal nodes.
 */
public class ArrayLiteralInterpreter implements InterpreterDelegate {
    
    private static ArrayLiteralInterpreter instance;

    public static ArrayLiteralInterpreter getInstance() {
        if (instance == null) {
            instance = new ArrayLiteralInterpreter();
        }

        return instance;
    }
    
    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context) throws SalinasException {
        SalinasArrayMap arrayMap = new SalinasArrayMap(Math.max((int) (node.jjtGetNumChildren() * 1.5), 16));
        SalinasValue array = new SalinasValue(arrayMap, SalinasType.ARRAY);
        SalinasNode n;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            n = node.getChild(i);
            arrayMap.put(BigDecimal.valueOf(i), SalinasInterpreter.interpret(n, context));
        }
        arrayMap.setCurrentIndex(node.jjtGetNumChildren());

        return array;
    }
}

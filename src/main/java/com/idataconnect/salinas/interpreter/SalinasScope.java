package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.data.SalinasValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a lexical scope in Salinas.
 */
public class SalinasScope {
    private final SalinasScope parent;
    private final Map<String, SalinasValue> variables = new HashMap<>();

    public SalinasScope(SalinasScope parent) {
        this.parent = parent;
    }

    public void setVariable(String name, SalinasValue value) {
        variables.put(name.toUpperCase(), value);
    }

    public Optional<SalinasValue> getVariable(String name) {
        String key = name.toUpperCase();
        SalinasValue value = variables.get(key);
        if (value != null) {
            return Optional.of(value);
        }
        if (parent != null) {
            return parent.getVariable(name);
        }
        return Optional.empty();
    }

    public SalinasValue unsetVariable(String name) {
        return variables.remove(name.toUpperCase());
    }

    public SalinasScope getParent() {
        return parent;
    }
}

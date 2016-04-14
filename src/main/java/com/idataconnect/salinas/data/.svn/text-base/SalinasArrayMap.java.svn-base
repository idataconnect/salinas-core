/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.data;

import java.util.LinkedHashMap;

/**
 * Map implementation which maintains insertion order and also holds other
 * required Salinas array attributes.
 * <p>
 * This is currently implemented as a subclass of <code>LinkedHashMap</code>.
 */
public class SalinasArrayMap extends LinkedHashMap<Object, SalinasValue> {

    private int currentIndex = 0;

    /**
     * Creates a new array map with the initial capacity of <em>16</em>.
     */
    public SalinasArrayMap() {
        super();
    }

    /**
     * Creates a new array map with the given initial capacity.
     *
     * @param initialCapacity the initial capacity for the map
     */
    public SalinasArrayMap(int initialCapacity) {
        super(initialCapacity);
    }
    
    /**
     * Gets the current index of the array. This determines which numeric index
     * an element will be inserted when none is explicitly specified.
     *
     * @return the current array index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the current index of the array. This determines which numeric index
     * an element will be inserted when none is explicitly specified.
     *
     * @param currentIndex the current array index
     */
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}

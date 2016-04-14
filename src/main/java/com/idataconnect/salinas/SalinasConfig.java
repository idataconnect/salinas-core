/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

/**
 * A configuration DTO which holds settings for how the Salinas engine
 * should execute.
 */
public class SalinasConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int decimals = 2;
    private int precision = 16;
    private File currentDirectory;
    private long currentRandomSeed = 179757L;
    private Random currentRandom = new Random(currentRandomSeed);


    /**
     * Gets the number of decimal places used when displaying numeric values.
     * @return the number of decimals displayed
     */
    public int getDecimals() {
        return decimals;
    }

    /**
     * Sets the number of decimal places used when displaying numeric values.
     * @param decimals the number of decimals displayed
     */
    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    /**
     * Gets the number of decimals used in numeric calculations.
     * @return the number of decimals used
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the number of decimals used in numeric calculations.
     * @param precision the number of decimals used
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * Gets the current directory for use with relative file operations.
     *
     * @return the current directory
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Sets the current directory for use with relative file operations.
     *
     * @param currentDirectory the current directory
     */
    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    /**
     * Gets the current random seed. If the user set the random seed to a
     * negative number using the <code>RANDOM</code> function, this will be
     * set to <code>-1</code>.
     *
     * @return the current random seed, or <code>-1</code> if the random seed
     * was set to a negative number
     */
    public long getCurrentRandomSeed() {
        return currentRandomSeed;
    }

    /**
     * Sets the current random seed. Setting the seed to a negative number
     * will always set it to <code>-1</code>.
     *
     * @param currentRandomSeed the random seed, or <code>-1</code> to
     * use a seed selected by the <code>Random</code> implementation
     */
    public void setCurrentRandomSeed(long currentRandomSeed) {
        if (currentRandomSeed < 0) {
            this.currentRandomSeed = -1;
        } else {
            this.currentRandomSeed = currentRandomSeed;
        }
    }

    public Random getCurrentRandom() {
        return currentRandom;
    }

    public void setCurrentRandom(Random currentRandom) {
        this.currentRandom = currentRandom;
    }
}


package com.google.refine.sampling;

/**
 * Abstract base class for implementing sampling strategies.
 */
public abstract class AbstractSampler implements Sampler {

    protected int limitLines = -1;

    public AbstractSampler(Integer limitLines, Number parameter) {
        this.limitLines = limitLines;
    }
}

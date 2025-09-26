
package com.google.refine.sampling;

/**
 * Interface to be implemented by Samplers. See also {@link AbstractSampler}.
 */
public interface Sampler {

    public final int SKIP = -1;
    public final int END = -2;

    /**
     *
     * @return an integer representing the next row index for the given sampling strategy.
     */
    public int nextIndex();

}

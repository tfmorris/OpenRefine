
package com.google.refine.sampling;

/**
 * Systematic sampling selects every k-th item from a list or sequence.
 */
public class SystematicSampler extends AbstractSampler implements Sampler {

    private int index = 0;
    private int count = 0;
    private int stepSize;

    public SystematicSampler(Integer limit, Number stepSize) {
        super(limit, stepSize);
        if (stepSize.intValue() <= 0) {
            throw new IllegalArgumentException("Sampling factor (step size) can not be less than or equal to zero");
        }
        this.stepSize = stepSize.intValue();
    }

    /**
     * @return
     */
    @Override
    public int nextIndex() {
        count++;
        if (limitLines > 0 && count >= limitLines) {
            return END;
        }
        if ((count - 1) % stepSize == 0) {
            return index++;
        } else {
            return Sampler.SKIP;
        }
    }
}

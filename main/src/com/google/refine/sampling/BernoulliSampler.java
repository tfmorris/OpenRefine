
package com.google.refine.sampling;

import java.util.Random;

/**
 * Bernoulli Sampling selects a subset of items from a list or sequence such that each item has an equal probability of
 * being chosen, independently of the others.
 */
public class BernoulliSampler extends AbstractSampler implements Sampler {

    private int index = 0;
    private Random random = new Random();
    private double percentage;

    public BernoulliSampler(Integer limitLines, Number percentage) {
        super(limitLines, percentage);
        double percent = percentage.doubleValue();
        if (percent < 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("Sampling factor (percentage) must be between 0 and 100");
        }
        this.percentage = percent;
    }

    /**
     * @return
     */
    @Override
    public int nextIndex() {
        if (percentage == 0.0) {
            return END;
        }
        if (random.nextDouble() <= percentage || percentage == 1.0) {
            return index++;
        }
        return SKIP;
    }
}

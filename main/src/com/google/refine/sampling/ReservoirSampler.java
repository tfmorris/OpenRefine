
package com.google.refine.sampling;

import java.util.Random;

/**
 * Reservoir sampling selects k items uniformly at random from a list or sequence.
 */
public class ReservoirSampler extends AbstractSampler implements Sampler {

    private final int reservoirSize;
    private int index = 0;
    private int count = -1;
    private Random random = new Random();

    public ReservoirSampler(Integer limit, Number reservoirSize) {
        super(limit, reservoirSize);
        if (reservoirSize.intValue() < 0) {
            // TODO: Do we *really* want to allow a reservoir size of zero?
            throw new IllegalArgumentException("Sampling factor (reservoir size) must be greater than 0.");
        }
        this.reservoirSize = reservoirSize.intValue();
    }

    /**
     * @return
     */
    @Override
    public int nextIndex() {
        if (reservoirSize == 0) {
            return END;
        }
        if (++count < reservoirSize) {
            return index++;
        }
        int replaceIndex = random.nextInt(count);
        if (replaceIndex < reservoirSize) {
            return replaceIndex;
        }
        return SKIP;
    }
}

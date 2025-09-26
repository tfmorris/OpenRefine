
package com.google.refine.sampling;

public class DefaultSampler extends AbstractSampler {

    private int index = 0;

    public DefaultSampler(Integer limitLines, Number parameter) {
        super(limitLines, parameter);
    }

    /**
     * @return
     */
    @Override
    public int nextIndex() {
        if (limitLines > 0 && index >= limitLines) {
            return END;
        }
        return index++;
    }
}

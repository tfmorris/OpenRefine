
package com.google.refine.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SystematicSamplerTest {

    @Test
    public void testBasicSampling() {
        // given
        int step = 11;
        SystematicSampler sampler = new SystematicSampler(-1, step);
        List<Integer> list = createList(100);

        // when
        List<Integer> sample = sample(sampler, list);

        // then
        List<Integer> expectedSample = List.of(0, 11, 22, 33, 44, 55, 66, 77, 88, 99);
        Assert.assertEquals(sample, expectedSample);
        Assert.assertTrue(list.containsAll(sample));
    }

    // ------------------ edge cases ----------------
    @Test
    public void testSamplingWithListSizeSmallerThenStepSize() {
        // given
        int step = 100;
        SystematicSampler sampler = new SystematicSampler(-1, step);
        List<Integer> list = createList(10);

        // when
        List<Integer> sample = sample(sampler, list);

        // then only first element (index 0) is in sample
        Assert.assertEquals(sample.size(), 1);
        Assert.assertTrue(sample.contains(list.get(0)));
    }

    @Test
    public void testSamplingWithListSizeEqualToStepSize() {
        // given
        int step = 100;
        SystematicSampler sampler = new SystematicSampler(-1, step);
        List<Integer> list = createList(100);

        // when
        List<Integer> sample = sample(sampler, list);

        // then only first element (index 0) is in sample
        Assert.assertEquals(sample.size(), 1);
        Assert.assertTrue(sample.contains(list.get(0)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSamplingWithStepSizeZero() {
        // given
        int step = 0;
        SystematicSampler sampler = new SystematicSampler(-1, step);

        // then throw IllegalArgumentException (see test header)
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSamplingWithNegativeStepSize() {
        // given
        int step = -1;
        SystematicSampler sampler = new SystematicSampler(-1, step);

        // then throw IllegalArgumentException (see test header)
    }

    @Test
    public void testSamplingOnEmptyList() {
        // given
        int step = 10;
        SystematicSampler sampler = new SystematicSampler(-1, step);
        List<Integer> list = new ArrayList<>();

        // when
        List<Integer> sample = sample(sampler, list);

        // then sample should be empty
        Assert.assertTrue(sample.isEmpty());
    }

    // ----------- Utils --------------
    private List<Integer> createList(int range) {
        return IntStream.range(0, range)
                .boxed()
                .collect(Collectors.toList());
    }

    private List<Integer> sample(Sampler sampler, List<Integer> target) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < target.size(); i++) {
            int value = sampler.nextIndex();
            if (value == Sampler.END) {
                break;
            }
            if (value != Sampler.SKIP) {
                result.add(target.get(i));
            }
        }
        return result;
    }
}

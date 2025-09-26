
package com.google.refine.sampling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReservoirSamplerTest {

    @Test
    public void testBasicSampling() {
        // given
        int reservoirSize = 10;
        ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);
        List<Integer> list = createList(100);

        // when
        List<Integer> sample = sample(sampler, list);

        // then
        Assert.assertEquals(sample.size(), reservoirSize); // sample should always be size of reservoir
        Assert.assertTrue(list.containsAll(sample)); // all elements in sample are taken from list
        Assert.assertEquals(sample, sample.stream().distinct().collect(Collectors.toList())); // no duplicates in sample
    }

    @Test
    public void testSamplingResultsInEqualProbabilityForEachElementToBeIncludedInSample() {
        // given
        int reservoirSize = 10;
        List<Integer> list = createList(100);

        // Run reservoir sampling multiple times and count occurrence of each element in sample
        int runs = 10000;
        long[] observedCounts = new long[list.size()];
        List<Integer> sample;
        for (int run = 0; run < runs; run++) {
            ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);
            sample = sample(sampler, list);
            for (int sampledItem : sample) {
                observedCounts[sampledItem]++;
            }
        }

        // Expected count per item assuming uniform distribution
        double expectedCount = (runs * reservoirSize) / (double) list.size();
        double[] expectedCounts = new double[list.size()];
        Arrays.fill(expectedCounts, expectedCount);

        // Perform chi-square test
        ChiSquareTest chiSquareTest = new ChiSquareTest();
        double pValue = chiSquareTest.chiSquareTest(expectedCounts, observedCounts);

        // Assert p-value is high enough (no significant deviation from uniformity)
        Assert.assertTrue(pValue > 0.05, "Reservoir sampling failed uniformity test: p-value=" + pValue);
    }

    // ------------------ edge cases ----------------
    @Test
    public void testSamplingWithListSizeSmallerThenReservoirSize() {
        // given
        int reservoirSize = 100;
        ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);
        List<Integer> list = createList(10);

        // when
        List<Integer> sample = sample(sampler, list);

        // then reservoir = original list
        Assert.assertEquals(sample.size(), list.size());
        Assert.assertEquals(sample, list);
    }

    @Test
    public void testSamplingWithListSizeEqualToReservoirSize() {
        // given
        int reservoirSize = 10;
        ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);
        List<Integer> list = createList(reservoirSize);

        // when
        List<Integer> sample = sample(sampler, list);

        // then reservoir = original list
        Assert.assertEquals(sample.size(), reservoirSize);
        Assert.assertEquals(sample, list);
    }

    @Test
    public void testSamplingWithReservoirSizeZero() {
        // given
        int reservoirSize = 0;
        ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);
        List<Integer> list = createList(10);

        // when
        List<Integer> sample = sample(sampler, list);

        // then reservoir should be empty
        Assert.assertTrue(sample.isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSamplingWithNegativeReservoirSize() {
        // given
        int reservoirSize = -1;
        ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);

        // then throw IllegalArgumentException (see test header)
    }

    @Test
    public void testSamplingOnEmptyList() {
        // given
        int reservoirSize = 10;
        ReservoirSampler sampler = new ReservoirSampler(-1, reservoirSize);
        List<Integer> list = new ArrayList<>();

        // when
        List<Integer> sample = sample(sampler, list);

        // then reservoir should be empty
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
                if (value == result.size()) {
                    result.add(target.get(i));
                } else {
                    result.set(value, target.get(i));
                }
            }
        }
        return result;
    }
}

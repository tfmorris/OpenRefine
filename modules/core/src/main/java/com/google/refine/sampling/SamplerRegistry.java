
package com.google.refine.sampling;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamplerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SamplerRegistry.class);

    static final private Map<String, Class<? extends Sampler>> nameToSampler = new HashMap<String, Class<? extends Sampler>>();

    static public void registerSampler(String name, Class<? extends Sampler> sampler) {
        nameToSampler.put(name.toLowerCase(), sampler);
    }

    static public Sampler getSampler(String name, int limit, Number factor) {
        Class<? extends Sampler> samplerClass = nameToSampler.get(name.toLowerCase());
        if (samplerClass == null) {
            throw new IllegalArgumentException("Unknown sampling method: " + name);
        }

        try {
            return samplerClass.getDeclaredConstructor(Integer.class, Number.class).newInstance(limit, factor);
        } catch (NoSuchMethodException e) {
            logger.error("No constructor for {}(Integer, Number) for registration {}", samplerClass.getCanonicalName(), name, e);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            logger.error("Failed to instantiate sampler using  {}(Integer, Number) ", samplerClass.getCanonicalName(), e);
        }

        return null;
    }
}

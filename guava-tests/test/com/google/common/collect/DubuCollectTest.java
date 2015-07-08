package com.google.common.collect;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by rigel on 7/8/15.
 */
public class DubuCollectTest {


    private static final Logger logger = Logger.getLogger("com.google.common.collect.dubu.BimapTest");

    @Test
    public void testag() {

        logger.info("Afda");

        ImmutableSet<String> set = ImmutableSet.of("a", "b", "c");
        List bb = Lists.newArrayList("a", "b", "c", 1, 2, 32);
        Assert.assertEquals(set, bb);

    }

}
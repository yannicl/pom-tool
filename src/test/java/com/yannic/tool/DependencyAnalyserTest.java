package com.yannic.tool;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yannic on 24/10/17.
 */

public class DependencyAnalyserTest {

    DependencyAnalyser analyser = new DependencyAnalyser();

    void testAnalyse() {

    }

    void testPrecheckDependencies() {

    }

    void testPrecheckLabels() {

    }

    @Test
    public void testIsIllegalLabel() {
        Assert.assertFalse(analyser.isIllegalLabel("a.depot1:child1:1.0.0"));
        Assert.assertTrue(analyser.isIllegalLabel(" a.depot1:child1:1.0.0"));
        Assert.assertTrue(analyser.isIllegalLabel("\na.depot1:child1:1.0.0"));

    }

    void testAnalyzeProjetRepo() {

    }

    void testAnalyzeDependencies() {

    }
}

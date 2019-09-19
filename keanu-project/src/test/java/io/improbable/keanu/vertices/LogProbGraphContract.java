package io.improbable.keanu.vertices;

import io.improbable.keanu.tensor.TensorMatchers;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.DoubleVertex;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LogProbGraphContract {

    public static void matchesKnownLogDensity(LogProbGraph logProbGraph, double expectedLogDensity) {
        DoubleVertex logProbGraphOutput = logProbGraph.getOutput();
        double actualDensity = logProbGraphOutput.getValue().sumNumber();
        assertEquals(expectedLogDensity, actualDensity, 1e-5);
    }

    public static void equal(LogProbGraph actual, LogProbGraph expected) {
        assertThat(actual.getOutput().getValue(), equalTo(expected.getOutput().getValue()));
    }

    public static void equalTensor(LogProbGraph logProbGraph, DoubleTensor expectedLogDensityTensor) {
        DoubleTensor logProbGraphOutputTensor = logProbGraph.getOutput().getValue();
        assertThat(expectedLogDensityTensor, TensorMatchers.valuesAndShapesMatch(logProbGraphOutputTensor));
    }
}

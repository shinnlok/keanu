package io.improbable.keanu.vertices.dbl;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.tensor.dbl.Nd4jDoubleTensor;
import io.improbable.keanu.vertices.ConstantVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.UniformVertex;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import org.junit.Before;
import org.junit.Test;

import static io.improbable.keanu.tensor.TensorMatchers.valuesAndShapesMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DoubleVertexTest {

    private DoubleTensor vectorA;
    private DoubleTensor vectorB;

    @Before
    public void initVectors() {
        vectorA = DoubleTensor.create(new double[]{1., 2.}, 2);
        vectorB = DoubleTensor.create(new double[]{1., 2., 3., 4., 5., 6.}, 6);
    }

    @Test
    public void canObserveArrayOfValues() {
        DoubleVertex gaussianVertex = new GaussianVertex(0, 1);
        double[] observation = new double[]{1, 2, 3};
        gaussianVertex.observe(observation);
        assertArrayEquals(observation, gaussianVertex.getValue().asFlatDoubleArray(), 0.0);
    }

    @Test
    public void canObserveTensor() {
        DoubleVertex gaussianVertex = new GaussianVertex(0, 1);
        DoubleTensor observation = Nd4jDoubleTensor.create(new double[]{1, 2, 3, 4}, new long[]{2, 2});
        gaussianVertex.observe(observation);
        assertArrayEquals(observation.asFlatDoubleArray(), gaussianVertex.getValue().asFlatDoubleArray(), 0.0);
        assertArrayEquals(observation.getShape(), gaussianVertex.getShape());
    }

    @Test
    public void canSetAndCascadeArrayOfValues() {
        DoubleVertex gaussianVertex = new GaussianVertex(0, 1);
        double[] values = new double[]{1, 2, 3};
        gaussianVertex.setAndCascade(values);
        assertArrayEquals(values, gaussianVertex.getValue().asFlatDoubleArray(), 0.0);
    }

    @Test
    public void canSetValueArrayOfValues() {
        DoubleVertex gaussianVertex = new GaussianVertex(0, 1);
        double[] values = new double[]{1, 2, 3};
        gaussianVertex.setValue(values);
        assertArrayEquals(values, gaussianVertex.getValue().asFlatDoubleArray(), 0.0);
    }

    @Test
    public void canSetValueAsScalarOnNonScalarVertex() {
        DoubleVertex gaussianVertex = new GaussianVertex(new long[]{1, 2}, 0, 1);
        gaussianVertex.setValue(2);
        assertArrayEquals(new double[]{2}, gaussianVertex.getValue().asFlatDoubleArray(), 0.0);
    }

    @Test
    public void canSetAndCascadeAsScalarOnNonScalarVertex() {
        DoubleVertex gaussianVertex = new GaussianVertex(new long[]{1, 2}, 0, 1);
        gaussianVertex.setAndCascade(2);
        assertArrayEquals(new double[]{2}, gaussianVertex.getValue().asFlatDoubleArray(), 0.0);
    }

    @Test
    public void canTakeValue() {
        DoubleVertex gaussianVertex = new GaussianVertex(0, 1);
        double[] values = new double[]{1, 2, 3};
        gaussianVertex.setAndCascade(values);
        assertEquals(1, gaussianVertex.take(0).getValue().scalar(), 0.0);
    }

    @Test
    public void canReshape() {
        DoubleVertex gaussianVertex = new GaussianVertex(0, 1);
        gaussianVertex.setAndCascade(DoubleTensor.ones(2, 2));
        assertArrayEquals(gaussianVertex.getShape(), new long[]{2, 2});
        DoubleVertex reshaped = gaussianVertex.reshape(4, 1);
        assertArrayEquals(reshaped.getShape(), new long[]{4, 1});
    }

    @Test
    public void canConcat() {
        DoubleVertex A = new UniformVertex(0, 1);
        A.setValue(DoubleTensor.arange(1, 5).reshape(2, 2));

        DoubleVertex B = new UniformVertex(0, 1);
        B.setValue(DoubleTensor.arange(5, 9).reshape(2, 2));

        DoubleVertex concatDimZero = DoubleVertex.concat(0, A, B);
        assertArrayEquals(concatDimZero.getShape(), new long[]{4, 2});

        DoubleVertex concatDimOne = DoubleVertex.concat(1, A, B);
        assertArrayEquals(concatDimOne.getShape(), new long[]{2, 4});
    }

    @Test
    public void canCastToIntegerVertex() {
        DoubleVertex v1 = new ConstantDoubleVertex(3.6);
        IntegerVertex intV1 = v1.toInteger();
        assertEquals(3, intV1.getValue().scalar().longValue());
    }

    @Test
    public void canMatrixMultiplyVectors() {
        DoubleVertex vectorsMultiplied = ConstantVertex.of(vectorA).matrixMultiply(ConstantVertex.of(vectorA));
        DoubleTensor result = vectorsMultiplied.lazyEval();
        DoubleTensor expectedResult = DoubleTensor.scalar(5.);
        assertThat(result, valuesAndShapesMatch(expectedResult));
    }

    @Test
    public void canMatrixMultiplyVectorAndMatrix() {
        DoubleVertex vectorsMultiplied = ConstantVertex.of(vectorA).matrixMultiply(ConstantVertex.of(vectorB.reshape(2, 3)));
        DoubleTensor result = vectorsMultiplied.lazyEval();
        DoubleTensor expectedResult = DoubleTensor.create(new double[]{9., 12., 15.}, 3);
        assertThat(result, valuesAndShapesMatch(expectedResult));
    }

    @Test
    public void canMatrixMultiplyMatrixAndVector() {
        DoubleVertex vectorsMultiplied = ConstantVertex.of(vectorB.reshape(3, 2)).matrixMultiply(ConstantVertex.of(vectorA));
        DoubleTensor result = vectorsMultiplied.lazyEval();
        DoubleTensor expectedResult = DoubleTensor.create(new double[]{5., 11., 17.}, 3);
        assertThat(result, valuesAndShapesMatch(expectedResult));
    }

    @Test
    public void canMatrixMultiplyMatrices() {
        DoubleVertex vectorsMultiplied = ConstantVertex.of(vectorB.reshape(3, 2)).matrixMultiply(ConstantVertex.of(vectorB.reshape(2, 3)));
        DoubleTensor result = vectorsMultiplied.lazyEval();
        DoubleTensor expectedResult = DoubleTensor.create(
            new double[]{
                 9., 12., 15.,
                19., 26., 33.,
                29., 40., 51.
            }, 3, 3);
        assertThat(result, valuesAndShapesMatch(expectedResult));
    }
}

package io.improbable.keanu.vertices.tensor.number.fixed.intgr.nonprobabilistic.operators.multiple;

import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.tensor.number.fixed.intgr.IntegerVertex;
import io.improbable.keanu.vertices.tensor.number.fixed.intgr.nonprobabilistic.ConstantIntegerVertex;
import org.junit.Assert;
import org.junit.Test;

public class IntegerConcatenationVertexTest {

    @Test
    public void canConcatVectorsOfSameSize() {
        ConstantIntegerVertex a = new ConstantIntegerVertex(IntegerTensor.create(new int[]{1, 2, 3}, 1, 3));
        ConstantIntegerVertex b = new ConstantIntegerVertex(IntegerTensor.create(new int[]{4, 5, 6}, 1, 3));
        ConstantIntegerVertex c = new ConstantIntegerVertex(IntegerTensor.create(new int[]{7, 8, 9}, 1, 3));

        IntegerConcatenationVertex concatZero = new IntegerConcatenationVertex(0, a, b);
        IntegerConcatenationVertex concatOne = new IntegerConcatenationVertex(1, a, b, c);

        Assert.assertArrayEquals(new long[]{2, 3}, concatZero.getShape());
        Assert.assertArrayEquals(new long[]{1, 9}, concatOne.getShape());

        Assert.assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6}, concatZero.getValue().asFlatDoubleArray(), 0.001);
        Assert.assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, concatOne.getValue().asFlatDoubleArray(), 0.001);

    }

    @Test
    public void canConcatVectorsOfDifferentSize() {
        ConstantIntegerVertex a = new ConstantIntegerVertex(new int[]{1, 2, 3});
        ConstantIntegerVertex b = new ConstantIntegerVertex(new int[]{4, 5, 6, 7, 8, 9});

        IntegerConcatenationVertex concatZero = new IntegerConcatenationVertex(0, a, b);

        Assert.assertArrayEquals(new long[]{9}, concatZero.getShape());
        Assert.assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, concatZero.getValue().asFlatDoubleArray(), 0.001);
    }

    @Test
    public void canConcatScalarToVector() {
        ConstantIntegerVertex a = new ConstantIntegerVertex(new int[]{1, 2, 3});
        ConstantIntegerVertex b = new ConstantIntegerVertex(new int[]{4});

        IntegerConcatenationVertex concat = new IntegerConcatenationVertex(0, a, b);

        Assert.assertArrayEquals(new long[]{4}, concat.getShape());
        Assert.assertArrayEquals(new double[]{1, 2, 3, 4}, concat.getValue().asFlatDoubleArray(), 0.001);
    }

    @Test
    public void canConcatVectorToScalar() {
        ConstantIntegerVertex a = new ConstantIntegerVertex(new int[]{1});
        ConstantIntegerVertex b = new ConstantIntegerVertex(new int[]{2, 3, 4});

        IntegerConcatenationVertex concat = new IntegerConcatenationVertex(0, a, b);

        Assert.assertArrayEquals(new long[]{4}, concat.getShape());
        Assert.assertArrayEquals(new double[]{1, 2, 3, 4}, concat.getValue().asFlatDoubleArray(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorThrownOnConcatOfWrongSize() {
        ConstantIntegerVertex a = new ConstantIntegerVertex(IntegerTensor.create(new int[]{1, 2, 3}, 1, 3));
        ConstantIntegerVertex b = new ConstantIntegerVertex(IntegerTensor.create(new int[]{4, 5, 6, 7, 8, 9}, 1, 6));

        new IntegerConcatenationVertex(0, a, b);
    }

    @Test
    public void canConcatMatricesOfSameSize() {
        IntegerVertex m = new ConstantIntegerVertex(IntegerTensor.create(new int[]{1, 2, 3, 4}, new long[]{2, 2}));
        IntegerVertex a = new ConstantIntegerVertex(IntegerTensor.create(new int[]{10, 15, 20, 25}, new long[]{2, 2}));

        IntegerConcatenationVertex concatZero = new IntegerConcatenationVertex(0, m, a);

        Assert.assertArrayEquals(new long[]{4, 2}, concatZero.getShape());
        Assert.assertArrayEquals(new double[]{1, 2, 3, 4, 10, 15, 20, 25}, concatZero.getValue().asFlatDoubleArray(), 0.001);

        IntegerConcatenationVertex concatOne = new IntegerConcatenationVertex(1, m, a);

        Assert.assertArrayEquals(new long[]{2, 4}, concatOne.getShape());
        Assert.assertArrayEquals(new double[]{1, 2, 10, 15, 3, 4, 20, 25}, concatOne.getValue().asFlatDoubleArray(), 0.001);
    }

    @Test
    public void canConcatHighDimensionalShapes() {
        IntegerVertex a = new ConstantIntegerVertex(IntegerTensor.create(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, new long[]{2, 2, 2}));
        IntegerVertex b = new ConstantIntegerVertex(IntegerTensor.create(new int[]{10, 20, 30, 40, 50, 60, 70, 80}, new long[]{2, 2, 2}));

        IntegerConcatenationVertex concatZero = new IntegerConcatenationVertex(0, a, b);

        Assert.assertArrayEquals(new long[]{4, 2, 2}, concatZero.getShape());
        Assert.assertArrayEquals(
            new double[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 20, 30, 40, 50, 60, 70, 80},
            concatZero.getValue().asFlatDoubleArray(),
            0.001
        );

        IntegerConcatenationVertex concatThree = new IntegerConcatenationVertex(2, a, b);
        Assert.assertArrayEquals(new long[]{2, 2, 4}, concatThree.getShape());
        Assert.assertArrayEquals(
            new double[]{1, 2, 10, 20, 3, 4, 30, 40, 5, 6, 50, 60, 7, 8, 70, 80},
            concatThree.getValue().asFlatDoubleArray(),
            0.001
        );
    }

}

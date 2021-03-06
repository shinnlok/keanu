package io.improbable.keanu.tensor.bool;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.tensor.generic.GenericTensor;
import io.improbable.keanu.tensor.intgr.IntegerTensor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static io.improbable.keanu.tensor.TensorMatchers.hasValue;
import static io.improbable.keanu.tensor.TensorMatchers.valuesAndShapesMatch;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

public class JVMBooleanTensorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void youCanCreateARankZeroTensor() {
        BooleanTensor scalarTrue = BooleanTensor.create(new boolean[]{true}, new long[]{});
        assertTrue(scalarTrue.scalar());
        assertEquals(0, scalarTrue.getRank());
    }

    @Test
    public void youCanCreateARankOneTensor() {
        BooleanTensor booleanVector = BooleanTensor.create(new boolean[]{true, false, false, true, true}, new long[]{5});
        assertTrue(booleanVector.getValue(3));
        assertEquals(1, booleanVector.getRank());
    }

    @Test
    public void doesElementwiseAnd() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        BooleanTensor matrixB = BooleanTensor.create(new boolean[]{false, false, true, true}, new long[]{2, 2});
        BooleanTensor result = matrixA.and(matrixB);
        Boolean[] expected = new Boolean[]{false, false, true, false};
        assertArrayEquals(expected, result.asFlatArray());
        assertFalse(Arrays.equals(expected, matrixA.asFlatArray()));

        BooleanTensor resultInPlace = matrixA.andInPlace(matrixB);
        assertArrayEquals(expected, resultInPlace.asFlatArray());
        assertArrayEquals(expected, matrixA.asFlatArray());
    }

    @Test
    public void doesElementwiseOr() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        BooleanTensor matrixB = BooleanTensor.create(new boolean[]{false, false, true, true}, new long[]{2, 2});
        BooleanTensor result = matrixA.or(matrixB);
        Boolean[] expected = new Boolean[]{true, false, true, true};
        assertArrayEquals(expected, result.asFlatArray());
        assertFalse(Arrays.equals(expected, matrixA.asFlatArray()));

        BooleanTensor resultInPlace = matrixA.orInPlace(matrixB);
        assertArrayEquals(expected, resultInPlace.asFlatArray());
        assertArrayEquals(expected, matrixA.asFlatArray());
    }

    @Test
    public void doesElementwiseNot() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        BooleanTensor result = matrixA.not();
        Boolean[] expected = new Boolean[]{false, true, false, true};
        assertArrayEquals(expected, result.asFlatArray());
        assertFalse(Arrays.equals(expected, matrixA.asFlatArray()));

        BooleanTensor resultInPlace = matrixA.notInPlace();
        assertArrayEquals(expected, resultInPlace.asFlatArray());
        assertArrayEquals(expected, matrixA.asFlatArray());
    }

    @Test
    public void doesSetDoubleIf() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});

        DoubleTensor trueCase = DoubleTensor.create(new double[]{1.5, 2.0, 3.3, 4.65}, new long[]{2, 2});
        DoubleTensor falseCase = DoubleTensor.create(new double[]{5.1, 7.2, 11.4, 23.22}, new long[]{2, 2});

        DoubleTensor result = trueCase.where(matrixA, falseCase);
        assertArrayEquals(new double[]{1.5, 7.2, 3.3, 23.22}, result.asFlatDoubleArray(), 0.0);
    }

    @Test
    public void doesSetIntegerIf() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        IntegerTensor trueCase = IntegerTensor.create(new int[]{1, 2, 3, 4}, new long[]{2, 2});
        IntegerTensor falseCase = IntegerTensor.create(new int[]{5, 7, 11, 23}, new long[]{2, 2});

        IntegerTensor result = trueCase.where(matrixA, falseCase);
        assertArrayEquals(new int[]{1, 7, 3, 23}, result.asFlatIntegerArray());
    }

    @Test
    public void doesSetBooleanIf() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        BooleanTensor matrixB = BooleanTensor.create(new boolean[]{false, false, true, true}, new long[]{2, 2});
        BooleanTensor matrixC = BooleanTensor.create(new boolean[]{true, true, true, false}, new long[]{2, 2});
        BooleanTensor result = matrixB.where(matrixA, matrixC);
        assertArrayEquals(new Boolean[]{false, true, true, false}, result.asFlatArray());
    }

    enum Something {
        A, B, C, D
    }

    @Test
    public void doesWhereWithNonScalarTensors() {

        GenericTensor<Something> trueCase = GenericTensor.create(
            new Something[]{Something.A, Something.B, Something.C, Something.D},
            new long[]{2, 2}
        );

        GenericTensor<Something> falseCase = GenericTensor.create(
            new Something[]{Something.D, Something.C, Something.C, Something.A},
            new long[]{2, 2}
        );

        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        GenericTensor<Something> result = trueCase.where(matrixA, falseCase);
        assertArrayEquals(
            new Something[]{Something.A, Something.C, Something.C, Something.A},
            result.asFlatArray()
        );
    }

    @Test
    public void doesWhereWithScalarTensors() {

        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});

        GenericTensor<Something> trueCase = GenericTensor.scalar(Something.A);
        GenericTensor<Something> falseCase = GenericTensor.scalar(Something.C);

        GenericTensor<Something> result = trueCase.where(matrixA, falseCase);
        assertArrayEquals(
            new Something[]{Something.A, Something.C, Something.A, Something.C},
            result.asFlatArray()
        );
    }

    @Test
    public void doesAllTrue() {
        assertFalse(BooleanTensor.create(new boolean[]{false, true, false, false}, new long[]{2, 2}).allTrue().scalar());
        assertTrue(BooleanTensor.create(new boolean[]{true, true, true, true}, new long[]{2, 2}).allTrue().scalar());
    }

    @Test
    public void doesAllFalse() {
        assertFalse(BooleanTensor.create(new boolean[]{false, true, false, false}, new long[]{2, 2}).allFalse().scalar());
        assertTrue(BooleanTensor.create(new boolean[]{false, false, false, false}, new long[]{2, 2}).allFalse().scalar());
    }

    @Test
    public void doesAnyTrue() {
        assertTrue(BooleanTensor.create(new boolean[]{false, true, false, false}, new long[]{2, 2}).anyTrue().scalar());
        assertFalse(BooleanTensor.create(new boolean[]{false, false, false, false}, new long[]{2, 2}).anyTrue().scalar());
    }

    @Test
    public void doesAnyFalse() {
        assertTrue(BooleanTensor.create(new boolean[]{false, true, false, false}, new long[]{2, 2}).anyTrue().scalar());
        assertFalse(BooleanTensor.create(new boolean[]{true, true, true, true}, new long[]{2, 2}).anyFalse().scalar());
    }

    @Test
    public void canElementwiseEqualsAScalarValue() {
        boolean value = true;
        BooleanTensor allTheSame = BooleanTensor.create(value, new long[]{2, 3});
        BooleanTensor notAllTheSame = allTheSame.duplicate();
        notAllTheSame.setValue(!value, 1, 1);

        assertThat(allTheSame.elementwiseEquals(value).allTrue().scalar(), equalTo(true));
        assertThat(notAllTheSame.elementwiseEquals(value), hasValue(true, true, true, true, false, true));
    }

    @Test
    public void canGetRandomAccessValue() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        assertTrue(matrixA.getValue(0, 0));
        assertFalse(matrixA.getValue(0, 1));
        assertTrue(matrixA.getValue(1, 0));
        assertFalse(matrixA.getValue(1, 1));
    }

    @Test
    public void canSetRandomAccessValue() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});

        matrixA.setValue(false, 0, 0);
        matrixA.setValue(true, 0, 1);
        matrixA.setValue(false, 1, 0);
        matrixA.setValue(true, 1, 1);

        assertArrayEquals(new Boolean[]{false, true, false, true}, matrixA.asFlatArray());
    }

    @Test
    public void canConvertToDoubles() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        assertArrayEquals(new double[]{1.0, 0.0, 1.0, 0.0}, matrixA.asFlatDoubleArray(), 0.0);
    }

    @Test
    public void canConvertToIntegers() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});

        assertArrayEquals(new int[]{1, 0, 1, 0}, matrixA.asFlatIntegerArray());
    }

    @Test
    public void canReshape() {
        BooleanTensor matrixA = BooleanTensor.create(new boolean[]{true, false, true, false}, new long[]{2, 2});
        BooleanTensor reshaped = matrixA.reshape(4, 1);
        assertArrayEquals(reshaped.asFlatIntegerArray(), matrixA.asFlatIntegerArray());
        assertArrayEquals(new long[]{4, 1}, reshaped.getShape());

        matrixA.setValue(true, 0, 1);
        assertTrue(matrixA.getValue(0, 1));
        assertFalse(reshaped.getValue(1));

    }

    @Test
    public void canStackScalars() {
        BooleanTensor x = BooleanTensor.scalar(true);
        BooleanTensor y = BooleanTensor.scalar(true);

        assertThat(BooleanTensor.create(true, true), valuesAndShapesMatch(BooleanTensor.stack(0, x, y)));
    }

    @Test
    public void canStackVectors() {
        BooleanTensor x = BooleanTensor.create(true, false);
        BooleanTensor y = BooleanTensor.create(true, false);

        assertEquals(BooleanTensor.create(true, false, true, false).reshape(2, 2), BooleanTensor.stack(0, x, y));
        assertEquals(BooleanTensor.create(true, true, false, false).reshape(2, 2), BooleanTensor.stack(1, x, y));
    }

    @Test
    public void canStackMatrices() {
        BooleanTensor matrixD = BooleanTensor.create(new boolean[]{true, false}, 1, 2);
        assertThat(BooleanTensor.create(true, false, true, false).reshape(2, 1, 2), valuesAndShapesMatch(BooleanTensor.stack(0, matrixD, matrixD)));
        assertThat(BooleanTensor.create(true, false, true, false).reshape(1, 2, 2), valuesAndShapesMatch(BooleanTensor.stack(1, matrixD, matrixD)));
        assertThat(BooleanTensor.create(true, true, false, false).reshape(1, 2, 2), valuesAndShapesMatch(BooleanTensor.stack(2, matrixD, matrixD)));
    }

    @Test
    public void canStackIfDimensionIsNegative() {
        BooleanTensor matrixD = BooleanTensor.create(new boolean[]{true, false}, 1, 2);
        assertThat(BooleanTensor.create(true, false, true, false).reshape(2, 1, 2), valuesAndShapesMatch(BooleanTensor.stack(-3, matrixD, matrixD)));
        assertThat(BooleanTensor.create(true, false, true, false).reshape(1, 2, 2), valuesAndShapesMatch(BooleanTensor.stack(-2, matrixD, matrixD)));
        assertThat(BooleanTensor.create(true, true, false, false).reshape(1, 2, 2), valuesAndShapesMatch(BooleanTensor.stack(-1, matrixD, matrixD)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotStackIfPositiveDimensionIsOutOfBounds() {
        BooleanTensor matrixD = BooleanTensor.create(new boolean[]{true, false}, 1, 2);
        BooleanTensor.stack(3, matrixD, matrixD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotStackIfNegativeDimensionIsOutOfBounds() {
        BooleanTensor matrixD = BooleanTensor.create(new boolean[]{true, false}, 1, 2);
        BooleanTensor.stack(-4, matrixD, matrixD);
    }

    @Test
    public void canBroadcastAnd() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, true, true
        ).reshape(2, 3);

        BooleanTensor b = BooleanTensor.create(true, false, true);

        BooleanTensor actual = a.and(b);
        BooleanTensor expected = BooleanTensor.create(
            true, false, false,
            true, false, true
        ).reshape(2, 3);

        assertThat(actual, valuesAndShapesMatch(expected));
    }

    @Test
    public void canBroadcastOr() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, false, true
        ).reshape(2, 3);

        BooleanTensor b = BooleanTensor.create(true, false, true);

        BooleanTensor actual = a.or(b);
        BooleanTensor expected = BooleanTensor.create(
            true, true, true,
            true, false, true
        ).reshape(2, 3);

        assertThat(actual, valuesAndShapesMatch(expected));
    }

    @Test
    public void canBroadcastXor() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, false, true
        ).reshape(2, 3);

        BooleanTensor b = BooleanTensor.create(true, false, true);

        BooleanTensor actualFromLeft = a.xor(b);
        BooleanTensor actualFromRight = b.xor(a);
        BooleanTensor expected = BooleanTensor.create(
            false, true, true,
            false, false, false
        ).reshape(2, 3);

        assertThat(actualFromLeft, valuesAndShapesMatch(expected));
        assertThat(actualFromRight, valuesAndShapesMatch(expected));
    }

    @Test
    public void canConvertToIntegerMask() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, false, true
        ).reshape(2, 3);

        IntegerTensor actual = a.toIntegerMask();
        IntegerTensor expected = IntegerTensor.create(
            1, 1, 0,
            1, 0, 1
        ).reshape(2, 3);

        assertThat(actual, valuesAndShapesMatch(expected));
    }

    @Test
    public void canConvertToDoubleMask() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, false, true
        ).reshape(2, 3);

        DoubleTensor actual = a.toDoubleMask();
        DoubleTensor expected = DoubleTensor.create(
            1., 1., 0.,
            1., 0., 1.
        ).reshape(2, 3);

        assertThat(actual, valuesAndShapesMatch(expected));
    }

    @Test
    public void canSliceBooleanTensor() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, false, true
        ).reshape(2, 3);

        assertThat(a.slice(0, 0), valuesAndShapesMatch(BooleanTensor.create(true, true, false)));
        assertThat(a.slice(0, 1), valuesAndShapesMatch(BooleanTensor.create(true, false, true)));
        assertThat(a.slice(1, 0), valuesAndShapesMatch(BooleanTensor.create(true, true)));
        assertThat(a.slice(1, 1), valuesAndShapesMatch(BooleanTensor.create(true, false)));
        assertThat(a.slice(1, 2), valuesAndShapesMatch(BooleanTensor.create(false, true)));
    }

    @Test
    public void canTakeBooleanTensor() {
        BooleanTensor a = BooleanTensor.create(
            true, true, false,
            true, false, true
        ).reshape(2, 3);

        assertThat(a.take(0, 0), valuesAndShapesMatch(BooleanTensor.scalar(true)));
        assertThat(a.take(0, 1), valuesAndShapesMatch(BooleanTensor.scalar(true)));
        assertThat(a.take(0, 2), valuesAndShapesMatch(BooleanTensor.scalar(false)));
        assertThat(a.take(1, 0), valuesAndShapesMatch(BooleanTensor.scalar(true)));
        assertThat(a.take(1, 1), valuesAndShapesMatch(BooleanTensor.scalar(false)));
        assertThat(a.take(1, 2), valuesAndShapesMatch(BooleanTensor.scalar(true)));
    }

    @Test
    public void canBroadcastToShape() {
        BooleanTensor a = BooleanTensor.create(
            true, false, true
        ).reshape(3);

        BooleanTensor expectedByRow = BooleanTensor.create(
            true, false, true,
            true, false, true,
            true, false, true
        ).reshape(3, 3);

        Assert.assertThat(a.broadcast(3, 3), valuesAndShapesMatch(expectedByRow));

        BooleanTensor expectedByColumn = BooleanTensor.create(
            true, true, true,
            false, false, false,
            true, true, true
        ).reshape(3, 3);

        Assert.assertThat(a.reshape(3, 1).broadcast(3, 3), valuesAndShapesMatch(expectedByColumn));
    }

    @Test
    public void canBroadcastScalarToShape() {
        BooleanTensor a = BooleanTensor.scalar(true);

        BooleanTensor expected = BooleanTensor.create(
            true, true, true,
            true, true, true,
            true, true, true
        ).reshape(3, 3);

        Assert.assertThat(a.broadcast(3, 3), valuesAndShapesMatch(expected));
    }

    @Test
    public void canDiagFromVector() {
        BooleanTensor expected = BooleanTensor.create(new boolean[]{true, false, false, false, true, false, false, false, false}, 3, 3);
        BooleanTensor actual = BooleanTensor.create(true, true, false).diag();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void canDiagFromMatrix() {
        BooleanTensor actual = BooleanTensor.create(new boolean[]{true, false, false, false, true, false, false, false, false}, 3, 3).diagPart();
        BooleanTensor expected = BooleanTensor.create(true, true, false);

        Assert.assertEquals(expected, actual);
    }

}

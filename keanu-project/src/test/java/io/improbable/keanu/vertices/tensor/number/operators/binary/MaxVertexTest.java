package io.improbable.keanu.vertices.tensor.number.operators.binary;

import com.google.common.collect.ImmutableList;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.DoubleVertex;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.probabilistic.UniformVertex;
import org.junit.Test;

import static io.improbable.keanu.vertices.tensor.number.BinaryOperationTestHelpers.operatesOnTwo2x2MatrixVertexValues;
import static io.improbable.keanu.vertices.tensor.number.BinaryOperationTestHelpers.operatesOnTwoScalarVertexValues;
import static io.improbable.keanu.vertices.tensor.number.TensorTestOperations.finiteDifferenceMatchesForwardAndReverseModeGradient;

public class MaxVertexTest {

    @Test
    public void maxOfTwoScalarValues() {
        operatesOnTwoScalarVertexValues(2.0, 3.0, 3.0, (l, r) -> l.max(r));
    }

    @Test
    public void maxOfTwoMatrixVertexValues() {
        operatesOnTwo2x2MatrixVertexValues(
            new double[]{1.0, 2.0, 6.0, 4.0},
            new double[]{2.0, 4.0, 3.0, 4.0},
            new double[]{2.0, 4.0, 6.0, 4.0},
            (l, r) -> l.max(r)
        );
    }

    @Test
    public void changesMatchGradient() {
        UniformVertex A = new UniformVertex(new long[]{2, 2, 2}, -10.0, 10.0);
        A.setValue(DoubleTensor.create(1, 2, 3, 4, 5, 6, 7, 8));
        UniformVertex B = new UniformVertex(new long[]{2, 2, 2}, -10.0, 10.0);
        B.setValue(DoubleTensor.create(8, 7, 6, 5, 4, 3, 2, 1));
        DoubleVertex C = DoubleVertex.max(A, B);

        finiteDifferenceMatchesForwardAndReverseModeGradient(ImmutableList.of(A, B), C, 1e-6, 1e-10);
    }

}

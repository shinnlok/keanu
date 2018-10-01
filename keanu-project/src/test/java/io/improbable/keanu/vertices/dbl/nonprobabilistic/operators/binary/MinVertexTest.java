package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary;

import com.google.common.collect.ImmutableList;
import io.improbable.keanu.DeterministicRule;
import io.improbable.keanu.algorithms.variational.optimizer.gradient.GradientOptimizer;
import io.improbable.keanu.distributions.continuous.Uniform;
import io.improbable.keanu.network.BayesianNetwork;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.ConstantVertex;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.ConstantBoolVertex;
import io.improbable.keanu.vertices.dbl.Differentiator;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.UniformVertex;
import io.improbable.keanu.vertices.generic.nonprobabilistic.If;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.TensorTestOperations.finiteDifferenceMatchesGradient;
import static io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary.BinaryOperationTestHelpers.operatesOnTwo2x2MatrixVertexValues;
import static io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary.BinaryOperationTestHelpers.operatesOnTwoScalarVertexValues;
import static org.junit.Assert.assertArrayEquals;

public class MinVertexTest {

    @Test
    public void minOfTwoScalarValues() {
        operatesOnTwoScalarVertexValues(2.0, 3.0, 2.0, DoubleVertex::min);
    }

    @Test
    public void MinOfTwoMatrixVertexValues() {
        operatesOnTwo2x2MatrixVertexValues(
            new double[]{1.0, 2.0, 6.0, 4.0},
            new double[]{2.0, 4.0, 3.0, 4.0},
            new double[]{1.0, 2.0, 3.0, 4.0},
            DoubleVertex::min
        );
    }

    @Test
    public void changesMatchGradient() {
        DoubleVertex A = new UniformVertex(new int[]{2, 2, 2}, -10.0, 10.0);
        DoubleVertex B = new UniformVertex(new int[]{2, 2, 2}, -10.0, 10.0);
        DoubleVertex C = DoubleVertex.min(A, B);

        finiteDifferenceMatchesGradient(ImmutableList.of(A, B), C, 1e-6, 1e-10, true);
    }

}

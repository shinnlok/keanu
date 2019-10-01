package io.improbable.keanu.algorithms.variational.optimizer.gradient.testcase;

import io.improbable.keanu.algorithms.Variable;
import io.improbable.keanu.algorithms.variational.optimizer.FitnessFunction;
import io.improbable.keanu.algorithms.variational.optimizer.FitnessFunctionGradient;
import io.improbable.keanu.algorithms.variational.optimizer.OptimizedResult;
import io.improbable.keanu.algorithms.variational.optimizer.gradient.GradientOptimizationAlgorithm;
import io.improbable.keanu.tensor.dbl.DoubleTensor;

import java.util.List;

public interface GradientOptimizationAlgorithmTestCase {

    FitnessFunction getFitnessFunction();

    FitnessFunctionGradient getFitnessFunctionGradient();

    List<? extends Variable<DoubleTensor, ?>> getVariables();

    void assertResult(OptimizedResult result);

    default void assertUsingOptimizer(GradientOptimizationAlgorithm algorithm) {

        OptimizedResult result = algorithm.optimize(getVariables(), getFitnessFunctionGradient());
        assertResult(result);
    }
}

package io.improbable.keanu.algorithms.variational.optimizer;

import io.improbable.keanu.algorithms.VariableReference;
import io.improbable.keanu.tensor.dbl.DoubleTensor;

import java.util.Map;

public interface FitnessFunctionGradient extends FitnessFunction {

    /**
     * @param values the values of the variables in the fitness function.
     * @return The gradient of the fitness function with respect to each of the variables specified in values at the
     * values specified.
     */
    Map<VariableReference, DoubleTensor> getGradientsAt(Map<VariableReference, DoubleTensor> values);

    FitnessAndGradient getFitnessAndGradientsAt(Map<VariableReference, DoubleTensor> values);
}

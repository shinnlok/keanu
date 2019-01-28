package io.improbable.keanu.algorithms.variational.optimizer.nongradient;

import io.improbable.keanu.algorithms.variational.optimizer.*;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.util.ProgressBar;
import io.improbable.keanu.vertices.ProbabilityCalculator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static io.improbable.keanu.algorithms.variational.optimizer.Optimizer.getAsDoubleTensors;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.math3.optim.nonlinear.scalar.GoalType.MAXIMIZE;

/**
 * This class can be used to construct a BOBYQA non-gradient optimizer.
 * This will use a quadratic approximation of the gradient to perform optimization without derivatives.
 *
 * @see <a href="http://www.damtp.cam.ac.uk/user/na/NA_papers/NA2009_06.pdf">BOBYQA Optimizer</a>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NonGradientOptimizer implements Optimizer {

    private final ProbabilisticGraph probabilisticGraph;

    /**
     * maxEvaluations the maximum number of objective function evaluations before throwing an exception
     * indicating convergence failure.
     */
    private int maxEvaluations;

    /**
     * bounding box around starting point
     */
    private final double boundsRange;

    /**
     * bounds for each specific continuous latent vertex
     */
    private final OptimizerBounds optimizerBounds;

    /**
     * radius around region to start testing points
     */
    private final double initialTrustRegionRadius;

    /**
     * stopping trust region radius
     */
    private final double stoppingTrustRegionRadius;

    private final List<BiConsumer<double[], Double>> onFitnessCalculations = new ArrayList<>();

    public static NonGradientOptimizerBuilder builder() {
        return new NonGradientOptimizerBuilder();
    }

    @Override
    public void addFitnessCalculationHandler(BiConsumer<double[], Double> fitnessCalculationHandler) {
        this.onFitnessCalculations.add(fitnessCalculationHandler);
    }

    @Override
    public void removeFitnessCalculationHandler(BiConsumer<double[], Double> fitnessCalculationHandler) {
        this.onFitnessCalculations.remove(fitnessCalculationHandler);
    }

    private void handleFitnessCalculation(double[] point, Double fitness) {
        for (BiConsumer<double[], Double> fitnessCalculationHandler : onFitnessCalculations) {
            fitnessCalculationHandler.accept(point, fitness);
        }
    }

    private OptimizedResult optimize(boolean useMLE) {
        FitnessFunction fitnessFunction = new FitnessFunction(
            probabilisticGraph,
            useMLE,
            this::handleFitnessCalculation
        );

        return optimize(fitnessFunction);
    }

    private OptimizedResult optimize(FitnessFunction fitnessFunction) {

        ProgressBar progressBar = Optimizer.createFitnessProgressBar(this);

        List<long[]> shapes = probabilisticGraph.getLatentVariables().stream()
            .map(Variable::getShape)
            .collect(toList());

        BOBYQAOptimizer optimizer = new BOBYQAOptimizer(
            getNumInterpolationPoints(shapes),
            initialTrustRegionRadius,
            stoppingTrustRegionRadius
        );

        double[] startPoint = Optimizer.convertToPoint(getAsDoubleTensors(probabilisticGraph.getLatentVariables()));

        double initialFitness = fitnessFunction.value(startPoint);

        if (ProbabilityCalculator.isImpossibleLogProb(initialFitness)) {
            throw new IllegalArgumentException("Cannot start optimizer on zero probability network");
        }

        ApacheMathSimpleBoundsCalculator boundsCalculator = new ApacheMathSimpleBoundsCalculator(boundsRange, optimizerBounds);
        SimpleBounds bounds = boundsCalculator.getBounds(probabilisticGraph.getLatentVariables(), startPoint);

        PointValuePair pointValuePair = optimizer.optimize(
            new MaxEval(maxEvaluations),
            new ObjectiveFunction(fitnessFunction),
            bounds,
            MAXIMIZE,
            new InitialGuess(startPoint)
        );

        progressBar.finish();

        Map<VariableReference, DoubleTensor> optimizedValues = Optimizer
            .convertFromPoint(pointValuePair.getPoint(), probabilisticGraph.getLatentVariables());

        return new OptimizedResult(optimizedValues, pointValuePair.getValue());
    }

    private int getNumInterpolationPoints(List<long[]> latentVariableShapes) {
        return (int) (2 * Optimizer.totalNumberOfLatentDimensions(latentVariableShapes) + 1);
    }

    @Override
    public OptimizedResult maxAPosteriori() {
        return optimize(false);
    }

    @Override
    public OptimizedResult maxLikelihood() {
        return optimize(true);
    }

    public static class NonGradientOptimizerBuilder {

        private ProbabilisticGraph probabilisticGraph;

        private int maxEvaluations = Integer.MAX_VALUE;
        private double boundsRange = Double.POSITIVE_INFINITY;
        private OptimizerBounds optimizerBounds = new OptimizerBounds();
        private double initialTrustRegionRadius = BOBYQAOptimizer.DEFAULT_INITIAL_RADIUS;
        private double stoppingTrustRegionRadius = BOBYQAOptimizer.DEFAULT_STOPPING_RADIUS;

        NonGradientOptimizerBuilder() {
        }


        public NonGradientOptimizerBuilder bayesianNetwork(ProbabilisticGraph probabilisticGraph) {
            this.probabilisticGraph = probabilisticGraph;
            return this;
        }

        public NonGradientOptimizerBuilder maxEvaluations(int maxEvaluations) {
            this.maxEvaluations = maxEvaluations;
            return this;
        }

        public NonGradientOptimizerBuilder boundsRange(double boundsRange) {
            this.boundsRange = boundsRange;
            return this;
        }

        public NonGradientOptimizerBuilder optimizerBounds(OptimizerBounds optimizerBounds) {
            this.optimizerBounds = optimizerBounds;
            return this;
        }

        public NonGradientOptimizerBuilder initialTrustRegionRadius(double initialTrustRegionRadius) {
            this.initialTrustRegionRadius = initialTrustRegionRadius;
            return this;
        }

        public NonGradientOptimizerBuilder stoppingTrustRegionRadius(double stoppingTrustRegionRadius) {
            this.stoppingTrustRegionRadius = stoppingTrustRegionRadius;
            return this;
        }

        public NonGradientOptimizer build() {
            if (probabilisticGraph == null) {
                throw new IllegalStateException("Cannot build optimizer without specifying network to optimize.");
            }
            return new NonGradientOptimizer(
                probabilisticGraph,
                maxEvaluations,
                boundsRange,
                optimizerBounds,
                initialTrustRegionRadius,
                stoppingTrustRegionRadius
            );
        }

        public String toString() {
            return "NonGradientOptimizer.NonGradientOptimizerBuilder(probabilisticGraph=" + this.probabilisticGraph + ", maxEvaluations=" + this.maxEvaluations + ", boundsRange=" + this.boundsRange + ", optimizerBounds=" + this.optimizerBounds + ", initialTrustRegionRadius=" + this.initialTrustRegionRadius + ", stoppingTrustRegionRadius=" + this.stoppingTrustRegionRadius + ")";
        }
    }
}
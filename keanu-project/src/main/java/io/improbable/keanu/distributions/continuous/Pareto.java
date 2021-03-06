package io.improbable.keanu.distributions.continuous;

import io.improbable.keanu.KeanuRandom;
import io.improbable.keanu.distributions.ContinuousDistribution;
import io.improbable.keanu.distributions.hyperparam.Diffs;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.DoublePlaceholderVertex;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.DoubleVertex;

import static io.improbable.keanu.distributions.hyperparam.Diffs.L;
import static io.improbable.keanu.distributions.hyperparam.Diffs.S;
import static io.improbable.keanu.distributions.hyperparam.Diffs.X;

public class Pareto implements ContinuousDistribution {

    private final DoubleTensor location;
    private final DoubleTensor scale;

    public static Pareto withParameters(DoubleTensor location, DoubleTensor scale) {
        return new Pareto(location, scale);
    }

    private Pareto(DoubleTensor location, DoubleTensor scale) {
        this.location = location;
        this.scale = scale;
    }

    public Diffs dLogProb(DoubleTensor x) {
        DoubleTensor dLogPdx = scale.plus(1.0).divInPlace(x).unaryMinusInPlace();
        DoubleTensor dLogPdLocation = scale.div(location).broadcast(x.getShape());
        DoubleTensor dLogPdScale = scale.reciprocal().plusInPlace(location.log()).minusInPlace(x.log());

        return new Diffs()
            .put(X, dLogPdx)
            .put(L, dLogPdLocation)
            .put(S, dLogPdScale);
    }

    @Override
    public DoubleTensor sample(long[] shape, KeanuRandom random) {
        return random.nextDouble(shape).unaryMinusInPlace().plusInPlace(1.0).powInPlace(scale.reciprocal())
            .reciprocalInPlace().timesInPlace(location);
    }

    @Override
    public DoubleTensor logProb(DoubleTensor x) {
        if (checkParamsAreValid()) {
            DoubleTensor result = scale.log().plusInPlace(location.log().timesInPlace(scale))
                .minusInPlace(scale.plus(1.0).timesInPlace(x.log()));

            return setProbToZeroForInvalidX(x, result);
        } else {
            return DoubleTensor.create(Double.NEGATIVE_INFINITY, x.getShape());
        }
    }

    public static DoubleVertex logProbOutput(DoublePlaceholderVertex x, DoublePlaceholderVertex location, DoublePlaceholderVertex scale) {
        final DoubleVertex invalidXMask = x.greaterThanMask(location)
            .times(location.greaterThanMask(0.))
            .times(scale.greaterThanMask(0.))
            .unaryMinus()
            .plus(1.);
        final DoubleVertex ifValid = scale.log().plus(location.log().times(scale))
            .minus(scale.plus(1.).times(x.log()));
        return ifValid.setWithMask(invalidXMask, Double.NEGATIVE_INFINITY);
    }

    private boolean checkParamsAreValid() {
        return location.greaterThan(0.0).allTrue().scalar() && scale.greaterThan(0.0).allTrue().scalar();
    }

    private DoubleTensor setProbToZeroForInvalidX(DoubleTensor x, DoubleTensor result) {
        DoubleTensor invalids = x.lessThanOrEqualToMask(location);
        result.setWithMaskInPlace(invalids, Double.NEGATIVE_INFINITY);

        return result;
    }
}

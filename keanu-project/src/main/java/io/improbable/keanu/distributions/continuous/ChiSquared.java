package io.improbable.keanu.distributions.continuous;

import io.improbable.keanu.KeanuRandom;
import io.improbable.keanu.distributions.ContinuousDistribution;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.tensor.number.fixed.intgr.IntegerPlaceholderVertex;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.DoublePlaceholderVertex;
import io.improbable.keanu.vertices.tensor.number.floating.dbl.DoubleVertex;

public class ChiSquared implements ContinuousDistribution {

    private static final double LOG_TWO = Math.log(2);
    private final IntegerTensor k;

    public static ChiSquared withParameters(IntegerTensor k) {
        return new ChiSquared(k);
    }

    private ChiSquared(IntegerTensor k) {
        this.k = k;
    }

    @Override
    public DoubleTensor sample(long[] shape, KeanuRandom random) {
        return random.nextGamma(shape, DoubleTensor.scalar(2.0), k.toDouble().div(2));
    }

    @Override
    public DoubleTensor logProb(DoubleTensor x) {
        final DoubleTensor halfK = k.toDouble().divInPlace(2.);
        final DoubleTensor numerator = x.safeLogTimes(halfK.minus(1.)).minusInPlace(x.div(2.));
        final DoubleTensor denominator = halfK.times(LOG_TWO).plusInPlace(halfK.logGamma());
        return numerator.minusInPlace(denominator);
    }

    public static DoubleVertex logProbOutput(DoublePlaceholderVertex x, IntegerPlaceholderVertex k) {
        final DoubleVertex halfK = k.toDouble().div(2.);
        final DoubleVertex numerator = halfK.minus(1.).times(x.log()).minus(x.div(2.));
        final DoubleVertex denominator = halfK.times(LOG_TWO).plus(halfK.logGamma());
        return numerator.minus(denominator);
    }

    public DoubleTensor[] dLogProb(DoubleTensor x, boolean wrtX) {
        DoubleTensor[] diffs = new DoubleTensor[1];

        if (wrtX) {
            diffs[0] = k.toDouble().divInPlace(2.0).minusInPlace(1.0).divInPlace(x).minusInPlace(0.5);
        }

        return diffs;
    }
}
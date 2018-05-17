package io.improbable.keanu.distributions.tensors.continuous;

import io.improbable.keanu.vertices.dbltensor.DoubleTensor;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;

public class TensorExponential {

    public static DoubleTensor sample(int[] shape, DoubleTensor a, DoubleTensor b, KeanuRandom random) {
        return a.minus(b).times(random.nextDouble(shape).logInPlace());
    }

    public static DoubleTensor logPdf(DoubleTensor a, DoubleTensor b, DoubleTensor x) {
        final DoubleTensor logOfWithinBounds = x.minus(a).unaryMinus().div(b).minus(b.log());
        logOfWithinBounds.applyWhere(a.getLessThanMask(x), 0.0);
        return logOfWithinBounds;
    }

    public static Diff dlnPdf(DoubleTensor a, DoubleTensor b, DoubleTensor x) {
        final DoubleTensor dPda = b.reciprocal();
        final DoubleTensor dPdb = a.plus(b).minus(x).div(b.pow(2)).unaryMinus();
        return new Diff(dPda, dPdb, dPda.unaryMinus());
    }

    public static class Diff {
        public final DoubleTensor dPda;
        public final DoubleTensor dPdb;
        public final DoubleTensor dPdx;

        public Diff(DoubleTensor dPda, DoubleTensor dPdb, DoubleTensor dPdx) {
            this.dPda = dPda;
            this.dPdb = dPdb;
            this.dPdx = dPdx;
        }
    }

}

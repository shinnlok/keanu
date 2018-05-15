package io.improbable.keanu.vertices.dbl.probabilistic;

import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.DualNumber;

import java.util.Map;

public abstract class ProbabilisticDouble extends DoubleVertex {

    @Override
    public Double updateValue() {
        return getValue();
    }

    @Override
    public Double lazyEval() {
        if (!hasValue()) {
            setValue(sample(Vertex.DEFAULT_RANDOM_SUPPLIER.get()));
        }
        return getValue();
    }

    @Override
    public boolean isProbabilistic() {
        return true;
    }

    @Override
    public DualNumber calculateDualNumber(Map<Vertex, DualNumber> dualNumbers) {
        if (isObserved()) {
            return DualNumber.createConstant(getValue());
        } else {
            return DualNumber.createWithRespectToSelf(getId(), getValue());
        }
    }

}

package io.improbable.keanu.vertices.intgr.nonprobabilistic.operators.binary;

import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.intgr.IntegerVertex;

import static io.improbable.keanu.tensor.TensorShapeValidation.checkHasSingleNonScalarShapeOrAllScalar;

public class IntegerMultiplicationVertex extends IntegerBinaryOpVertex {

    public IntegerMultiplicationVertex(IntegerVertex a, IntegerVertex b) {
        super(checkHasSingleNonScalarShapeOrAllScalar(a.getShape(), b.getShape()), a, b);
    }

    @Override
    protected IntegerTensor op(IntegerTensor a, IntegerTensor b) {
        return a.times(b);
    }
}

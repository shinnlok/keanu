package io.improbable.keanu.vertices.intgr.nonprobabilistic.operators.unary;

import io.improbable.keanu.annotation.ExportVertexToPythonBindings;
import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.LoadVertexParam;
import io.improbable.keanu.vertices.intgr.IntegerVertex;

public class IntegerMaxUnaryVertex extends IntegerUnaryOpVertex {
    @ExportVertexToPythonBindings
    public IntegerMaxUnaryVertex(@LoadVertexParam(INPUT_NAME) IntegerVertex inputVertex) {
        super(inputVertex.getShape(), inputVertex);
    }

    @Override
    protected IntegerTensor op(IntegerTensor value) {
        return value.max();
    }
}
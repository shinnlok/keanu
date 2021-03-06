package io.improbable.keanu.vertices.tensor.bool.nonprobabilistic.operators.unary;

import io.improbable.keanu.annotation.ExportVertexToPythonBindings;
import io.improbable.keanu.tensor.FloatingPointTensor;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.LoadVertexParam;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.tensor.TensorVertex;

public class IsNegativeInfinityVertex<T extends Number, TENSOR extends FloatingPointTensor<T, TENSOR>, VERTEX extends TensorVertex<T, TENSOR, VERTEX>>
    extends BooleanUnaryOpVertex<TENSOR> {

    @ExportVertexToPythonBindings
    public IsNegativeInfinityVertex(@LoadVertexParam(INPUT_NAME) Vertex<TENSOR, VERTEX> inputVertex) {
        super(inputVertex);
    }

    @Override
    protected BooleanTensor op(TENSOR inputVertex) {
        return inputVertex.isNegativeInfinity();
    }
}

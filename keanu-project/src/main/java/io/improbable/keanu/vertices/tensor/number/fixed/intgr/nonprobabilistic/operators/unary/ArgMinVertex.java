package io.improbable.keanu.vertices.tensor.number.fixed.intgr.nonprobabilistic.operators.unary;

import io.improbable.keanu.annotation.ExportVertexToPythonBindings;
import io.improbable.keanu.tensor.NumberTensor;
import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.LoadVertexParam;
import io.improbable.keanu.vertices.SaveVertexParam;
import io.improbable.keanu.vertices.tensor.TensorVertex;

public class ArgMinVertex<T extends Number, TENSOR extends NumberTensor<T, TENSOR>, VERTEX extends TensorVertex<T, TENSOR, VERTEX>>
    extends IntegerUnaryOpVertex<T, TENSOR, VERTEX> {

    private static final String AXIS = "axis";
    private final Integer axis;

    public ArgMinVertex(TensorVertex<T, TENSOR, VERTEX> inputVertex) {
        this(inputVertex, null);
    }

    @ExportVertexToPythonBindings
    public ArgMinVertex(@LoadVertexParam(INPUT_NAME) TensorVertex<T, TENSOR, VERTEX> inputVertex,
                        @LoadVertexParam(AXIS) Integer axis) {
        super(inputVertex);
        this.axis = axis;
    }

    @Override
    protected IntegerTensor op(TENSOR value) {
        if (axis != null) {
            return value.argMin(axis);
        } else {
            return value.argMin();
        }
    }

    @SaveVertexParam(AXIS)
    public Integer getAxis() {
        return axis;
    }
}

package io.improbable.keanu.vertices.dbl.nonprobabilistic;

import com.google.common.collect.Iterables;
import io.improbable.keanu.annotation.ExportVertexToPythonBindings;
import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.IVertex;
import io.improbable.keanu.vertices.LoadShape;
import io.improbable.keanu.vertices.LoadVertexParam;
import io.improbable.keanu.vertices.NonProbabilistic;
import io.improbable.keanu.vertices.ProxyVertex;
import io.improbable.keanu.vertices.SaveVertexParam;
import io.improbable.keanu.vertices.VertexImpl;
import io.improbable.keanu.vertices.VertexLabel;
import io.improbable.keanu.vertices.dbl.Differentiable;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.PartialDerivative;

import java.util.Collections;
import java.util.Map;

import static io.improbable.keanu.tensor.TensorShapeValidation.checkTensorsMatchNonLengthOneShapeOrAreLengthOne;

public class DoubleProxyVertex extends VertexImpl<DoubleTensor> implements DoubleVertex,  Differentiable, ProxyVertex<DoubleVertex>, NonProbabilistic<DoubleTensor> {

    private static final String LABEL_PARAM_NAME = "label";
    private static final String PARENT_NAME = "parent";

    /**
     * This vertex acts as a "Proxy" to allow a BayesNet to be built up before parents are explicitly known (ie for
     * model in model scenarios) but allows linking at a later point in time.
     *
     * @param label The label for this Vertex (all Proxy Vertices must be labelled)
     */
    public DoubleProxyVertex(VertexLabel label) {
        this(Tensor.SCALAR_SHAPE, label);
    }

    @ExportVertexToPythonBindings
    public DoubleProxyVertex(long[] shape, VertexLabel label) {
        super(shape);
        setLabel(label);
    }

    public DoubleProxyVertex(@LoadShape long[] shape, @LoadVertexParam(LABEL_PARAM_NAME) String labelString, @LoadVertexParam(value = PARENT_NAME, isNullable = true) DoubleVertex parent) {
        super(shape);
        VertexLabel vertexLabel = VertexLabel.parseLabel(labelString);
        setLabel(vertexLabel);
        if (parent != null) {
            setParent(parent);
        }
    }

    @Override
    public <V extends IVertex<DoubleTensor>> V setLabel(VertexLabel label) {
        if (this.getLabel() != null && !this.getLabel().getUnqualifiedName().equals(label.getUnqualifiedName())) {
            throw new RuntimeException("You should not change the label on a Proxy Vertex");
        }
        return super.setLabel(label);
    }

    public DoubleProxyVertex(long[] shape, String label) {
        this(shape, new VertexLabel(label));
    }

    @Override
    public DoubleTensor calculate() {
        return getParent().getValue();
    }

    @Override
    public void setParent(DoubleVertex newParent) {
        checkTensorsMatchNonLengthOneShapeOrAreLengthOne(getShape(), newParent.getShape());
        setParents(newParent);
    }

    @SaveVertexParam(value = PARENT_NAME, isNullable = true)
    public DoubleVertex getParent() {
        return (DoubleVertex) Iterables.getOnlyElement(getParents(), null);
    }

    @Override
    public boolean hasParent() {
        return !getParents().isEmpty();
    }

    @Override
    public PartialDerivative forwardModeAutoDifferentiation(Map<IVertex, PartialDerivative> derivativeOfParentsWithRespectToInput) {
        return derivativeOfParentsWithRespectToInput.get(getParent());
    }

    @Override
    public Map<IVertex, PartialDerivative> reverseModeAutoDifferentiation(PartialDerivative derivativeOfOutputWithRespectToSelf) {
        return Collections.singletonMap(getParent(), derivativeOfOutputWithRespectToSelf);
    }

    @SaveVertexParam(LABEL_PARAM_NAME)
    public String getLabelParameter() {
        return getLabel().toString();
    }

}

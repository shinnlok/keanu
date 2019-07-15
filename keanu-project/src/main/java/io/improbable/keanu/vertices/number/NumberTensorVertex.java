package io.improbable.keanu.vertices.number;

import io.improbable.keanu.BaseNumberTensor;
import io.improbable.keanu.kotlin.NumberOperators;
import io.improbable.keanu.tensor.NumberTensor;
import io.improbable.keanu.vertices.ConstantVertex;
import io.improbable.keanu.vertices.bool.BooleanVertex;
import io.improbable.keanu.vertices.bool.CastNumberToBooleanVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary.compare.GreaterThanOrEqualVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary.compare.GreaterThanVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary.compare.LessThanOrEqualVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary.compare.LessThanVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary.compare.NumericalEqualsVertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.CastNumberToDoubleVertex;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.CastNumberToIntegerVertex;
import io.improbable.keanu.vertices.number.operators.binary.DivisionVertex;
import io.improbable.keanu.vertices.number.operators.binary.GreaterThanMaskVertex;
import io.improbable.keanu.vertices.number.operators.binary.GreaterThanOrEqualToMaskVertex;
import io.improbable.keanu.vertices.number.operators.binary.LessThanMaskVertex;
import io.improbable.keanu.vertices.number.operators.binary.LessThanOrEqualToMaskVertex;
import io.improbable.keanu.vertices.number.operators.binary.MultiplicationVertex;
import io.improbable.keanu.vertices.number.operators.binary.NumberAdditionVertex;
import io.improbable.keanu.vertices.number.operators.binary.NumberDifferenceVertex;
import io.improbable.keanu.vertices.number.operators.ternary.SetWithMaskVertex;
import io.improbable.keanu.vertices.number.operators.unary.AbsVertex;
import io.improbable.keanu.vertices.number.operators.unary.SumVertex;
import io.improbable.keanu.vertices.tensor.TensorVertex;

public interface NumberTensorVertex<T extends Number, TENSOR extends NumberTensor<T, TENSOR>, VERTEX extends NumberTensorVertex<T, TENSOR, VERTEX>>
    extends TensorVertex<T, TENSOR, VERTEX>, BaseNumberTensor<BooleanVertex, IntegerVertex, DoubleVertex, T, VERTEX>, NumberOperators<VERTEX> {

    @Override
    default VERTEX minus(VERTEX that) {
        return wrap(new NumberDifferenceVertex<>(this, that));
    }

    @Override
    default VERTEX reverseMinus(VERTEX that) {
        return wrap(new NumberDifferenceVertex<>(that, this));
    }

    @Override
    default VERTEX plus(VERTEX that) {
        return wrap(new NumberAdditionVertex<>(this, that));
    }

    @Override
    default VERTEX times(VERTEX that) {
        return wrap(new MultiplicationVertex<>(this, that));
    }

    @Override
    default VERTEX div(VERTEX that) {
        return wrap(new DivisionVertex<>(this, that));
    }

    @Override
    default VERTEX reverseDiv(VERTEX that) {
        return wrap(new DivisionVertex<>(that, this));
    }

    @Override
    default VERTEX abs() {
        return wrap(new AbsVertex<>(this));
    }

    @Override
    default VERTEX sum() {
        return wrap(new SumVertex<>(this));
    }

    @Override
    default VERTEX sum(int... sumOverDimensions) {
        return wrap(new SumVertex<>(this, sumOverDimensions));
    }

    @Override
    default BooleanVertex toBoolean() {
        return new CastNumberToBooleanVertex<>(this);
    }

    @Override
    default DoubleVertex toDouble() {
        return new CastNumberToDoubleVertex<>(this);
    }

    @Override
    default IntegerVertex toInteger() {
        return new CastNumberToIntegerVertex<>(this);
    }

    @Override
    default BooleanVertex equalsWithinEpsilon(VERTEX other, T epsilon) {
        return new NumericalEqualsVertex<>(this, other, epsilon);
    }

    @Override
    default BooleanVertex greaterThan(VERTEX rhs) {
        return new GreaterThanVertex<>(this, rhs);
    }

    @Override
    default BooleanVertex greaterThanOrEqual(VERTEX rhs) {
        return new GreaterThanOrEqualVertex<>(this, rhs);
    }

    @Override
    default BooleanVertex lessThan(VERTEX rhs) {
        return new LessThanVertex<>(this, rhs);
    }

    @Override
    default BooleanVertex lessThanOrEqual(VERTEX rhs) {
        return new LessThanOrEqualVertex<>(this, rhs);
    }

    @Override
    default BooleanVertex lessThan(T value) {
        return lessThan((VERTEX) ConstantVertex.scalar(value));
    }

    @Override
    default BooleanVertex lessThanOrEqual(T value) {
        return lessThanOrEqual((VERTEX) ConstantVertex.scalar(value));
    }

    @Override
    default BooleanVertex greaterThan(T value) {
        return greaterThan((VERTEX) ConstantVertex.scalar(value));
    }

    @Override
    default BooleanVertex greaterThanOrEqual(T value) {
        return greaterThanOrEqual((VERTEX) ConstantVertex.scalar(value));
    }

    @Override
    default VERTEX greaterThanMask(VERTEX rhs) {
        return wrap(new GreaterThanMaskVertex<>(this, rhs));
    }

    @Override
    default VERTEX greaterThanOrEqualToMask(VERTEX rhs) {
        return wrap(new GreaterThanOrEqualToMaskVertex<>(this, rhs));
    }

    @Override
    default VERTEX lessThanMask(VERTEX rhs) {
        return wrap(new LessThanMaskVertex<>(this, rhs));
    }

    @Override
    default VERTEX lessThanOrEqualToMask(VERTEX rhs) {
        return wrap(new LessThanOrEqualToMaskVertex<>(this, rhs));
    }

    @Override
    default VERTEX setWithMask(VERTEX mask, T value) {
        return wrap(new SetWithMaskVertex<>(this, mask, ConstantVertex.scalar(value)));
    }

    default VERTEX setWithMask(VERTEX mask, VERTEX value) {
        return wrap(new SetWithMaskVertex<>(this, mask, value));
    }
}
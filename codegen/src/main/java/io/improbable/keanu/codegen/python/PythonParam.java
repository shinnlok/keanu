package io.improbable.keanu.codegen.python;

import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Encapsulates data required for defining a python parameter
 */
public class PythonParam {

    @Getter
    private final String name;

    @Getter
    private final Class klass;

    @Getter
    private final Type genericParameterType;

    @Getter
    private final String defaultValue;

    public PythonParam(String name, Class klass, Type genericParameterType, String defaultValue) {
        this.name = ParamStringProcessor.toLowerUnderscore(name);
        this.klass = klass;
        this.genericParameterType = genericParameterType;
        this.defaultValue = defaultValue;
    }

    public PythonParam(String name, Class klass, Type genericParameterType) {
        this(name, klass, genericParameterType, null);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        PythonParam that = (PythonParam) other;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
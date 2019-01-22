import pytest
from keanu.vertex import VertexLabel


@pytest.fixture
def label1() -> VertexLabel:
    return VertexLabel("label1", ["inner", "outer"])


@pytest.fixture
def label1_clone() -> VertexLabel:
    return VertexLabel("label1", ["inner", "outer"])


def test_equality(label1: VertexLabel, label1_clone: VertexLabel) -> None:
    assert label1 == label1_clone


def test_inequality(label1: VertexLabel) -> None:
    label2 = VertexLabel("label2", ["inner", "outer"])
    assert label1 != label2


def test_equality_str(label1: VertexLabel) -> None:
    return label1 == "label1"


def test_inequality_str(label1: VertexLabel) -> None:
    return label1 != "label2"


def test_is_in_namespace(label1: VertexLabel) -> None:
    assert label1.is_in_namespace(["inner", "outer"])


def test_is_not_in_namespace(label1: VertexLabel) -> None:
    assert not label1.is_in_namespace(["outer", "inner"])


def test_with_extra_name_space(label1: VertexLabel) -> None:
    top_level = "top_level"
    expected = VertexLabel("label1", ["inner", "outer", top_level])
    assert label1.with_extra_namespace(top_level) == expected


def test_without_outer_namespace(label1: VertexLabel) -> None:
    expected = VertexLabel("label1", ["inner"])
    assert label1.without_outer_namespace() == expected


def test_get_outer_namespace(label1: VertexLabel) -> None:
    assert label1.get_outer_namespace() == "outer"


def test_unqualified_name(label1: VertexLabel) -> None:
    assert label1.get_unqualified_name() == "label1"


def test_qualified_name(label1: VertexLabel) -> None:
    assert label1.get_qualified_name() == "outer.inner.label1"

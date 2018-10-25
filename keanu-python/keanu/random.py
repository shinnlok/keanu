from py4j.java_gateway import java_import
from keanu.context import KeanuContext
from keanu.base import JavaObjectWrapper

k = KeanuContext().jvm_view()
java_import(k, "io.improbable.keanu.vertices.dbl.KeanuRandom")


class KeanuRandom(JavaObjectWrapper):
    def __init__(self, seed=None):
        if seed is None:
            super(KeanuRandom, self).__init__(k.KeanuRandom())
        else:
            super(KeanuRandom, self).__init__(k.KeanuRandom(seed))

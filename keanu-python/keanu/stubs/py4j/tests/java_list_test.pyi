# Stubs for py4j.tests.java_list_test (Python 3.6)
#
# NOTE: This dynamically typed stub was automatically generated by stubgen.

import unittest
from typing import Any

def get_list(count: Any): ...

class AutoConvertTest(unittest.TestCase):
    p: Any = ...
    gateway: Any = ...
    def setUp(self) -> None: ...
    def tearDown(self) -> None: ...
    def testAutoConvert(self) -> None: ...
    def testAutoConvertConstructor(self) -> None: ...
    def testAutoConvertNotByteArray(self) -> None: ...

class ListTest(unittest.TestCase):
    p: Any = ...
    gateway: Any = ...
    def setUp(self) -> None: ...
    def tearDown(self) -> None: ...
    def testJavaListProtocol(self) -> None: ...
    def testJavaListProtocol2(self) -> None: ...
    def testJavaListGetSlice(self) -> None: ...
    def testJavaListDelSlice(self) -> None: ...
    def testJavaListSetSlice(self) -> None: ...
    def testJavaList(self) -> None: ...
    def testRemove(self) -> None: ...
    def testBinaryOp(self) -> None: ...
    def testException(self) -> None: ...

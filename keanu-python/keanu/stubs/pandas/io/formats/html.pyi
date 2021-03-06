# Stubs for pandas.io.formats.html (Python 3.6)
#
# NOTE: This dynamically typed stub was automatically generated by stubgen.

from pandas.io.formats.format import TableFormatter
from typing import Any, Optional

class HTMLFormatter(TableFormatter):
    indent_delta: int = ...
    fmt: Any = ...
    classes: Any = ...
    frame: Any = ...
    columns: Any = ...
    elements: Any = ...
    bold_rows: Any = ...
    escape: Any = ...
    max_rows: Any = ...
    max_cols: Any = ...
    show_dimensions: Any = ...
    is_truncated: Any = ...
    notebook: Any = ...
    border: Any = ...
    table_id: Any = ...
    def __init__(self, formatter: Any, classes: Optional[Any] = ..., max_rows: Optional[Any] = ..., max_cols: Optional[Any] = ..., notebook: bool = ..., border: Optional[Any] = ..., table_id: Optional[Any] = ...) -> None: ...
    def write(self, s: Any, indent: int = ...) -> None: ...
    def write_th(self, s: Any, indent: int = ..., tags: Optional[Any] = ...): ...
    def write_td(self, s: Any, indent: int = ..., tags: Optional[Any] = ...): ...
    def write_tr(self, line: Any, indent: int = ..., indent_delta: int = ..., header: bool = ..., align: Optional[Any] = ..., tags: Optional[Any] = ..., nindex_levels: int = ...) -> None: ...
    def write_style(self): ...
    def write_result(self, buf: Any) -> None: ...

def single_column_table(column: Any, align: Optional[Any] = ..., style: Optional[Any] = ...): ...
def single_row_table(row: Any): ...

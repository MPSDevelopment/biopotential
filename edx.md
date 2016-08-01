+---------------------------------+---------+---------------------------------+
| magic                           | u32, le | Must be 0x49584445,             |
|                                 |         | ASCII "EDXI"                    |
+---------------------------------+---------+---------------------------------+
| version                         | u32, le | Usually 10000000 or 00000001    |
|                                 |         | This field can usually be       |
|                                 |         | ignored.                        |
+---------------------------------+---------+---------------------------------+
| magic2                          | u64, le | Must be 0x2020207366666F2E,     |
|                                 |         | ASCII ".offs   "                |
+---------------------------------+---------+---------------------------------+
| section_table_size              | u32, le | Section table size, in bytes.   |
+---------------------------------+---------+---------------------------------+
| section_table  | $section_table_size,     | N of sections =                 |
|                | 16 bytes each entry      |   $section_table_size           |
|                |                          | / size of section (16 bytes)    |
|                +--------------------------+---------------------------------+
|                | section_name   | char[8] | Name of a section, ASCII, not   |
|                |                |         | NULL terminated, right-padded   |
|                |                |         | with spaces.                    |
|                +----------------+---------+---------------------------------+
|                | section_offset | u32, le | Beginning of a section, offset  |
|                |                |         | from beginning of the file.     |
|                +----------------+---------+---------------------------------+
|                | section_size   | u32, le | Length of data in section,      |
|                |                |         | excluding section_header (12    |
|                |                |         | bytes)                          |
+----------------+----------------+---------+---------------------------------+
|                                   EDX data                                  |
+-----------------------------------------------------------------------------+
| section_header | 12 bytes                 | Each section has one at the     |
|                |                          | beginning.                      |
|                +--------------------------+---------------------------------+
|                | section_name   | char[8] | Name of a section, ASCII, not   |
|                |                |         | NULL terminated, right-padded   |
|                |                |         | with spaces.                    |
|                +----------------+---------+---------------------------------+
|                | section_size   | u32, le | Length of data in section,      |
|                |                |         | excluding section_header (12    |
|                |                |         | bytes)                          |
+----------------+----------------+---------+---------------------------------+
| section_data   | $section_size            | Raw section data.               |
+----------------+--------------------------+---------------------------------+

Refer to edxr.c as to reference implementation

SECTIONS

Each section may or may not exist in a specific file.

".dbgmsg "
an UTF-8 string left by program that generated the EDX file, not NULL-
terminated.

".hash   "
SHA1 hashes for each other section in the file (QCryptographicHash)

".hmr    "
unknown

".meta   "
another UTF-8 string left by program that generated the EDX file, not NULL-
terminated. 

".mullist"
unknown

".sqnlist"
contains precalculated waw and _the values_ in it, exact format is not known

".orig   "
original pcm (8 bit unsigned) data

".waz    "
unknown

".waw    "
WAW data

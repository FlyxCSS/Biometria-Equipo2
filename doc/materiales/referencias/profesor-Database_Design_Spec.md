

Format for the design of a table in a relational database

====================================================================================
TABLE: [Table Name]
DESCRIPTION: [Brief overview of what this table stores]

COLUMNS:

+ [column_name] | [DATA_TYPE] | [NULL / NOT NULL] | [DEFAULT VALUE]

PRIMARY KEY: [column_name]

FOREIGN KEYS:

+ [column_name] -> referenced_table

CONSTRAINTS:

+ [Rule]
====================================================================================

Example: products Table

====================================================================================
TABLE: products

DESCRIPTION: Stores inventory items available for purchase.

COLUMNS:

+ id | INT | NOT NULL | Auto-Increment
+ name | VARCHAR(150) | NOT NULL
+ price | DECIMAL(10,2) | NOT NULL | 0.00
+ category_id | INT | NOT NULL

PRIMARY KEY: id

FOREIGN KEYS:

+ category_id -> categories(id)

CONSTRAINTS:

+ CHECK (price >= 0)
====================================================================================


# Logical Design  Software Engineering Specification, Logical Desgin
*A Language-Agnostic Formal Notation for Designing or Extracting Program Structure*

> **Core Definition:** In this framework, **reverse engineering** is defined as the process of discovering and formalizing the high-level **logical design** of an existing software system, completely independent of programming language, syntax, memory layout, or low-level implementation details.

---

## 1. Fundamental Data Types

All data constructs must be mapped to formal abstract types rather than language-specific primitives:

| Symbol / Syntax | Logical Type | Target Representation |
| :--- | :--- | :--- |
| `N` | Natural Numbers | Unsigned Integers (e.g., `unsigned int`, `uint32_t`, `size_t`) |
| `Z` | Integers | Signed Integers (e.g., `int`, `int32_t`, `int8_t`, `long`) |
| `R` | Real Numbers | Floating-Point Numbers (e.g., `float`, `double`) |
| `B` | Booleans | Boolean values: `B = { true, false }` |
| `Text` | Strings | Textual sequence data (e.g., `std::string`, `const char *`) |

---

## 2. Compound & Algebraic Types

### 2.1 Enumerations (Sum Types)
Enumerations represent a finite set of discrete values. Assign an explicit type name for clarity:
```text
TypeName = { VALUE_1, VALUE_2, ..., VALUE_N }

Example:
Light = { GREEN, YELLOW, RED }
```

### 2.2 Aggregations (Product Types / Structs / Objects)
Aggregations group related fields together under a single composite type:
```text
NewType = ( variable_1: Type_1, variable_2: Type_2, ... )

Example:
Person = ( name: Text, age: N )
```

### 2.3 Collections & Arrays
Dynamic lists and fixed-size arrays use square bracket notation:
* **Unbounded List:** `[ Type ]` — *Example:* `[ R ]` *(List of real numbers)*
* **Fixed-Size Array:** `[ Type ]_n` — *Example:* `[ R ]_3` *(Array of 3 real numbers)* or `[ N ]_16`
* **Named Type Alias:** `Point_3D = [ R ]_3`

---

## 3. Variable Naming Convention

Variables strictly follow lower-case naming for identifiers and upper-case/PascalCase for types:
```text
variable_name: TYPE
```

---

## 4. Mathematical Function Signatures

Functions represent pure mathematical transformations. Only specify **logical inputs and outputs**. Implementation mechanics (such as pointers, reference mutability, pass-by-value/reference, or callbacks) are explicitly omitted.

```text
input_variables --> function_name() --> output_variables
```

### Formatting Rules for Signatures:
* **No Input/Output Data:** If there are no input parameters or output values, omit the corresponding arrow on the left side entirely.
* **Logical Extraction:** Strips language-specific syntax like pointers (`*`), references (`&`), or callbacks in favor of purely logical input/output types.

**Example:** The logical design `a: N --> twice() --> N` represents all of the following equivalent implementations:

```c
// C Implementation (Pointers)
void twice(int *p) { *p = 2 * (*p); }
```

```javascript
// JavaScript Implementation (Callbacks)
function twice(n, callback) { callback(2 * n); }
```

```java
// Java Implementation (Standard Functional Return)
static int twice(int n) { return 2 * n; }
```

---

## 5. Object-Oriented Classes & Encapsulation

Classes are visually bounded using structured ASCII blocks to distinguish internal implementation (private) from exposed interfaces (public):

### Structural Rules for Class Diagrams:
1. **Private Members:** Fully enclosed inside the visual box boundary.
2. **Public Members:** Positioned across the boundary wall (the method name lies directly centered on the vertical boundary line `|`).
3. **Left-Side Arrows:** Use `-->` to point toward the method when input data is present, and `<--` to point away from the method when output data is returned. If a method has no input or output data, omit the corresponding left arrow.
4. **Visual Separation:** Place **two vertical lines (`|`)** between every two consecutive methods to improve visual clarity and spacing.
5. **State Interaction Arrows (Right-hand side):**
   * `-->` **Mutating (non-const):** Modifies instance state or side effects.
   * `<--` **Read-Only (const):** Inspects state without modification.
   * `--x` **Static:** Independent of instance state.

```text
                 --------- Point ------------------
                 |
                 | x: R
                 | y: R
                 |
                 |
x: R, y: R   --> Point() -->
                 |
                 |
            R <-- get_x() <--
                 |
                 |
  x: R       --> set_x() -->
                 |
                 |
  p: Point   --> add() <--
  r: Point   <--
                 |
                 |
  n: N       --> twice() --x
  r: N       <--
                 |
                 --------------------------------------
```

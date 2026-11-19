# Specification for developing a software component

  Sections:
    + Design of architecture & algorithms
    + Target language
    + Guidelines for code generation

## 🔵 Design of architecture & algorithms

  ### Polynomial
  A class for representing a mathematical polynomial

🧩
```
  class Polinomio -------------------------
    # private
    - coeficientes: [R] # coeficientes[i] is for x^i

    # public

    + coeficientes: [R] --> constructor() -->

    + coeficientes: Text --> constructor() -->

    +         get_grado() <--
        N <--

    +   x: R -->  evaluar() <--
        R <--
  -----------------------------------------
```

  #### Clarifications:
    - You are allowed to add private attributes and methods as long as they are consistent with the design, but they must be kept to a minimum and only if they are necessary.
    - You choose the algorithms for this class.
    - No relationships (inheritance, etc.) are required.
    - The class must be immutable (as specified by having no mutating --> methods) and thread-safe.


## 🔵 Target language
      Python

## 🔵 General guidelines for code generation

  1. **The Specification is the Single Source of Truth**:

   - The notation used in the specification is described below (Notation & Architecture Context Guide)

   - Strictly honor all architecture, algorithm, interface, and property constraints.

   - **Mutability vs. Immutability**:
     * Immutability is a desirable feature, but it is **defined and stated by the specification** (i.e. having no mutating `-->` arrows in any of its methods).
     * If the specification defines mutating methods (`-->`), the object can and must be mutated by those methods.
     * Thus, mutability depends strictly on each class' design as stated in its spec.

   - **Thread-Safety is ALWAYS Mandatory**:
     * Thread-safety must always be guaranteed for all objects without exception (inherent for immutable classes, or achieved via explicit thread-safe synchronization/locks for mutable classes).

   - **Visibility Constraints (`+` vs `-`)**:
     * `+` = **public**: Only items marked with `+` may be part of the public interface.
     * `-` = **private**: Private attributes and methods must never be exposed (do not generate public getters/properties for `-` fields unless explicitly specified with `+`).

   - Do not invent behavior or add public methods that deviate from the specification.

   - You are allowed to add private attributes and methods (helpers and internal data structures) if they preserve invariants and single responsibility, but they must be kept to a minimum and only if they are necessary.

2. **Strict Adherence to Guidelines**:

   - SOLID principles must be always followed.

   - Respect method length constraints (e.g., around 10 lines per method/function) and the Single Responsibility Principle (SRP).

   - **Code Clarity**: The code must be so clear and readable that comments become unnecessary. However, comments must still be written whenever they genuinely aid understanding.

   - Format method and function docstrings with the specified logical/mathematical design notation as described below.

3. **Mandatory Testing & Strict Encapsulation**:

   - Always produce a companion test suite testing constructors, normal operations, boundary conditions, mutability or immutability (as specified), thread safety, and error handling.

   - **Never access private (`-`) fields in tests**: Tests must interact with components exclusively through the public interface (`+`). State correctness must be verified via public observation methods (e.g. `evaluar()`, `get_grado()`), not by inspecting private storage.



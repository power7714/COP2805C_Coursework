# COP2805C_Coursework

Coursework and assignments completed for **COP2805C** at Valencia College. This repository contains standalone Java programs covering GUI development, networking, multithreading, generics, collections, file I/O, JDBC, and object-oriented design.

> **Note:** All files are proprietary/confidential coursework submissions and are shared here for portfolio and coursework-tracking purposes only.

---

## Contents

### GUI & Event-Driven Programming
| File | Description |
|---|---|
| `SimpleCalc.java` | Swing-based calculator GUI. Builds the window and lays out components using `GridBagLayout`. |
| `CalcListener.java` | `ActionListener` implementation for `SimpleCalc`. Handles input parsing, routes to the correct arithmetic operation, and updates the results label. |
| `CommCalc.java` | Swing GUI commission calculator. Computes tiered commission income across a range of sales amounts and prints a formatted report. |

### Networking / Client-Server
| File | Description |
|---|---|
| `AgentClient.java` | Swing-based "Sterling Intelligence Service" client. Loads an encrypted file, sends it to a server for decryption via HTTP POST, and displays the result in a styled LCD-panel UI. Includes built-in unit tests for the cipher logic. |
| `AgentServer.java` | Local socket server companion to `AgentClient`. Listens for a client connection, decrypts an incoming Caesar-cipher message, and returns the result. Includes a `shutdown` command and an inner unit test suite. |

### Multithreading
| File | Description |
|---|---|
| `FibonacciThread.java` | Abstract base class (extends `Thread`) shared by both Fibonacci solvers. Handles timing, result storage, and formatted output; subclasses supply the algorithm. |
| `RecursiveFibonacci.java` | Naive recursive Fibonacci implementation. |
| `DynamicFibonacci.java` | Iterative (dynamic programming) Fibonacci implementation using constant space. |
| `MainThread.java` | Runs both Fibonacci solvers concurrently, waits for completion, and prints a performance comparison summary. |

### Object-Oriented Design / Generics
| File | Description |
|---|---|
| `Shape3D.java` | Abstract base class for 3D shapes. Declares an abstract `Volume()` method and implements `Comparable<Shape3D>` for volume-based comparisons. |
| `Cuboid.java` | 3D rectangular box; computes volume from width, depth, and height. |
| `Cylinder.java` | 3D cylinder; computes volume from radius and height. |
| `ShapesTest.java` | Demonstrates creating shape instances, calculating their volumes, and comparing them via `compareTo()`. |
| `FindMin.java` | Generic method for finding the minimum element in an array of any `Comparable` type, with example usage for strings, integers, and doubles. |

### Collections & File I/O
| File | Description |
|---|---|
| `ArrayListManager.java` | Demonstrates populating, printing, sorting, searching, and clearing an `ArrayList<Double>` using the Java Collections framework. |
| `ReadFile.java` | Reads numeric values from a text file into a `List<Double>`, sorts them, writes the sorted results to a new file, and verifies the output. |

### Databases
| File | Description |
|---|---|
| `JDBCreader.java` | Connects to a local MySQL database (via XAMPP) using JDBC and prints all records from an `EMPLOYEES` table in a formatted layout. |

---

## Requirements

- **Java 17+**
- `JDBCreader.java` requires a running MySQL instance (e.g. via XAMPP) with a `cop2805` database and the MySQL Connector/J driver on the classpath.
- `AgentClient.java` expects a background image (`bg4.jpg`) in the project root and connects to a live PHP endpoint for decryption; `AgentServer.java` can be used as a local stand-in server for testing (see the notes at the bottom of `AgentServer.java` for how to switch `AgentClient` over to it).

## Running

Each file is a self-contained program with its own `main()` method. Compile and run individually, e.g.:

```bash
javac -d out src/cop2805/*.java
java -cp out cop2805.SimpleCalc
```

---

## Author

**Steve Curtis**
Six Actual Studios / Valencia College

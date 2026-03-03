# Ring Buffer (Single Writer, Multiple Readers)

## Project Overview

This project implements a Ring Buffer in Java that supports:

- Single Writer
- Multiple Readers
- Independent reading positions for each reader
- Overwriting of oldest data when the buffer becomes full

The buffer has a fixed capacity and follows circular indexing logic.

---

## Design Overview

### RingBuffer<T>

Responsible for:
- Storing elements in a fixed-size circular structure
- Managing the global write sequence
- Handling overwrite logic
- Creating Writer and Reader objects

---

### Writer<T>

Responsible for:
- Writing elements into the RingBuffer
- Delegating write operations to the buffer

Only one Writer instance can be created

---

### Reader<T>

Responsible for:
- Maintaining its own reading sequence
- Reading data independently from other readers
- Detecting and handling overwritten (missed) data

Reading does not remove elements from the buffer

---

## How Overwriting Works

- When the buffer becomes full, new writes overwrite the oldest data
- In the code, if a reader is too slow and its read position points to overwritten data, the reader is automatically moved forward to the oldest valid element
- As a result, slow readers may miss some items, but reading never throws errors due to stale data

---

## UML Diagrams

### UML Class Diagram
<img width="491" height="401" alt="Class Diagram OOAD drawio (1)" src="https://github.com/user-attachments/assets/81fbcd03-b7c4-415d-bf69-30f634fd076b" />


### UML Sequence Diagram for `write()`
<img width="500" height="341" alt="Seq Diagram 1 OOAD drawio" src="https://github.com/user-attachments/assets/944a5ee2-1cda-4f72-bbaf-921a4ab31d4b" />


### UML Sequence Diagram for `read()`
<img width="532" height="371" alt="Seq Diagram 2 OOAD drawio" src="https://github.com/user-attachments/assets/97cda4ef-1e81-4f7f-97b4-1b992142b8c2" />


---

## How to Run

1. Clone the repository
2. Open the project in your IDE (e.g., VS Code or IntelliJ)
3. Compile the project:
   ```
   javac *.java
   ```
4. Run the main class:
   ```
   java Main
   ```

---

## Author

- Khavar Baghirova
- CSCI-3612
- CRN: 20964

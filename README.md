# RISCV Processor

A basic single-cycle riscv processor implemented in Chisel and Scala. Instructions implemented include arithmetic & logical operations, branches, and jumps, etc. ECALL and CSR instructions are not yet supported.

### Directory
```
- src
    - hex
        - test riscv programs in hexadecimal format...
    - main
        - scala
            - source files...
    -test
        - scala
            - Chisel tests...
```

### Running Chisel Tests
```bash
sbt test
```
# A BNF - CNF Converter and a CNF Solver
## The program contains both a BNF (Bachus-Naur Form) - CNF (Conjunctive Normal Form)) Converter and a CNF Solver
Sample input: </br>
    P & ! Q => W <=> A | B & C</br>
    A => B</br>
    C | B & A</br>
</br>
This would be converted to CNF. The converter algorithm adapts from https://github.com/ldkrsi/cnf_py using the following steps:
* Add brackets to match order of precedence</br>
* Convert "if only if" into "implies"</br>
* Replace "implies" by "and", "or", "not"</br>
* Doing de morgan</br>
* Doing distributive</br>
The resulting CNF are in the form of </br>
    !A !P Q W</br>
    !B !C !P Q W</br>
    B A !W</br>
    C A !W</br>
    B A P</br>
    C A P</br>
    B A !Q</br>
    C A !Q</br>
    !A B</br>
    C A</br>
    B A</br>
</br>
The CNF solver using the Davis-Putnam algorithm to solve the following resulting CNF clauses</br>
To run the program, type
```bash
make solver
java Solver xxxx.txt
java Solver xxxx.txt
```
where xxxx.txt is the name of the input file.
To use the converter (the input file is BNF), type
```bash
java Solver xxxx.txt -converter
```
To clean the whole program, type 
```bash
make clean
```
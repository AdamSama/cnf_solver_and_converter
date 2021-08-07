Make:
	solver clean
solver:
	javac Solver.java CNF.java
clean:
	rm Solver.class CNF.class
all: compile run

compile: Implementation/*.java
	mkdir -p bin
	javac -d bin Implementation/*.java

run: compile
	java -cp bin assembler/Tester

clean: 
	rm -rf bin
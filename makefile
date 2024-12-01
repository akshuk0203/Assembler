all: compile run

compile: assembler/*.java
	mkdir -p bin
	javac -d bin assembler/*.java

run: compile
	java -cp bin assembler/Tester

clean: 
	rm -rf bin
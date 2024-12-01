all: compile run

compile: Implementation/*.java
	mkdir -p bin
	javac -d bin Implementation/*.java

run: compile
	java -cp bin Implementation/Tester

clean: 
	rm -rf bin
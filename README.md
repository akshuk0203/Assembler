Files:

1. OpcodeEntry.java : Structure for opcode table entry.

	Structure of Opcode table -mnemonic, opcode, size, numOperands, firstOperandType, secondOperandType, isModRM, rd

2. OpcodeLoader.java : Responsible for loading opcode table into primary memory from file.
	HashMap is used to store the entries in opcode table where each entry is of type opcodeEntry.

3. SymbolTable.java : Structure for symbol table entry.

	Structure of Symbol table - name, val, equihex, address, size, section, defined

4. IntermediateGen.java :  Input files- Assembly code, Opcode table Information
							Output files- Intermediate file
	1. Load the opcode table from a file.
	2. Parse the assembly code line by line.
	3. Generate the intermediate file and symbol table.
	4. Generating error for invalid line structure.  

	Structure of intermediate file - LineNo LocationCounter OpcodeIndex SourceLine

5. IntermediateToLST.java : 	Input file- Intermediate File
								Output file- LST file
			
	1. Fetch the equivalent code equivalent to mnemonic.
	2. Calculate Mod R/M if applicable.
	3. Fetch Memory address for symbols from symbol table.  
	4. Calculate forward and backward jump addresses and generate the offset accordingly.

6. Tester.java : The Tester.java file serves as the main driver program for the assembler. It automates the entire assembly process by integrating the two passes of the 							assembler.It Combines the two passes seamlessly, ensuring the output of the first pass is correctly processed by the second pass.	

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------	

Supported Instrctions:
ADD, SUB, MUL, DIV, INC, DEC, XOR, MOV, JMP, JNZ, JZ
  				
Registers:
eax, ebx, ecx, edx, esi, esp, ebp, edi, ax, bx, cx, dx, al, bl, cl


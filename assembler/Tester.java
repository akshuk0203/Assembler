package assembler;

import java.io.*;

public class Tester{

    public static void main(String[] args) {
        opcodeTable table=new opcodeTable();
        symbolTable symboltable=new symbolTable();
        opcodeLoader load=new opcodeLoader(table);
        String filename="/home/akshada-don/Desktop/MscSem3/Systems-2/Assembler/opcodelist";
        try{
            load.loadFromFile(filename);
            //table.printAllEntries();
        } catch (IOException e) {
            System.err.println("Error loading opcode data: " + e.getMessage());
        }
        intermediateGen generator = new intermediateGen(table,symboltable);
        if(generator.processFile("/home/akshada-don/Desktop/MscSem3/Systems-2/Assembler/input.asm"))
        {
            symboltable.printSymbolTable();

            IntermediateToLST assemble=new IntermediateToLST(table,symboltable);
            assemble.generateLSTFile("/home/akshada-don/Desktop/MscSem3/Systems-2/Assembler/intermediate_code.txt");
        }
    }
}

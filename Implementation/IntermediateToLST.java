package assembler;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.*;

class IntermediateToLST{

    public static final Set<String> opCodes = new HashSet<>(Arrays.asList("add", "sub", "xor", "mul", "div", "mov", "inc", "dec", "jmp", "jnz"));
    public static final Set<String> branchOpcodes = new HashSet<>(Arrays.asList("jz", "jmp", "jnz"));
    public static final Set<String> Datadirectives = new HashSet<>(Arrays.asList("db", "dw", "dd")); 
    public  static final Set<String> BSSdirectives = new HashSet<>(Arrays.asList("resb", "resw", "resd")); 
    public  static final Set<String> registers = new HashSet<>(Arrays.asList("eax","ebx","ecx","edx","esi","esp","ebp","edi","ax", "bx", "cx", "dx","al", "bl", "cl", "dl"));


    private opcodeTable opcodetable;
    private symbolTable symboltable;
    String line;
    int lineNumber;
    String address;
    int opcodeIndex;
    String sourceLine;


    IntermediateToLST(opcodeTable opcodetable, symbolTable symboltable){
        this.opcodetable = opcodetable;
        this.symboltable = symboltable;
    }

    public void generateLSTFile(String filename){
        try (BufferedReader reader = new BufferedReader(new FileReader(filename));
             BufferedWriter writer = new BufferedWriter(new FileWriter("lst.txt"))) {


            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s{2,}"); 
                if(parts.length<=2)  
                {
                    lineNumber = Integer.parseInt(parts[0].trim());
                    sourceLine = parts.length<2? "" :parts[1].trim();
                    writer.write(String.format(
                        "%-60d %s%n",
                        lineNumber,
                        sourceLine
                    ));
                    continue;
                }

                lineNumber = Integer.parseInt(parts[0].trim());
                address = parts[1].trim();
                opcodeIndex = Integer.parseInt(parts[2].trim());
                sourceLine = parts[3].trim();

                if(sourceLine.contains(":")){   //line where only label definition exist.No need to generate object code 
                    writer.write(String.format(
                        "%-60d %s%n",
                        lineNumber,
                        sourceLine
                    ));
                    continue;
                }

                String objectCode=generateObjectCode(opcodeIndex, sourceLine);
                
                writer.write(String.format(
                    "%-8d %-12s %-40s %s%n",
                    lineNumber,
                    address,
                    objectCode,
                    sourceLine
                ));
            }
            System.out.println("File written to: " + new File("lst.txt").getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateObjectCode(int opcodeIndex, String sourceLine){
        modRMCal modrm=new modRMCal();
        String result="";
        int size=-1;
        if(opcodeIndex==-1){   // data and bss section
            String[] parts = sourceLine.split("\\s+");
            if(Datadirectives.contains(parts[1])){  
                symbolEntry entry =symboltable.getSymbol(parts[0]);
                if(sourceLine.contains("dd"))
                    size=4;
                else if(sourceLine.contains("db"))
                    size=1;
                else if(sourceLine.contains("dw"))
                    size=2;
                if(size==-1){
                    System.out.println("error parsing symbol from symbol table");
                    result="";
                    return result;
                }
                return convertLittleIndian(entry.getEquihex(),size);
            }
            else if(BSSdirectives.contains(parts[1])){
                symbolEntry entry =symboltable.getSymbol(parts[0]);
                size= entry!=null? entry.getSize(): -1;
                if(size==-1){
                    System.out.println("error parsing symbol from symbol table");
                    result="";
                    return result;
                }
                else if(size<=8)
                {
                    while(size>0)
                    {
                        result= result+"??";
                        size--;
                    }
                    return result;
                }
                else{
                    result= "<res "+String.format("%X", size)+"h>";
                }
            }
            else{
                result= "\n";
            }
        }
        else{ // text section
            opcodeEntry entry=opcodetable.getOpcodeEntry(opcodeIndex);
            symbolEntry symbolEntry=null;
            result=result+entry.getOpcode();

            Integer modRM=modrm.calculateModRM(sourceLine);
            if(modRM != null)
                result=result+String.format("%02X", modRM);

            String[] parts = sourceLine.split("\\s+",2);
            String[] operands= parts[1].split(",");

            if(operands[0].contains("["))
            {
                result=result+"[";
                symbolEntry = symboltable.getSymbol(operands[0].substring(operands[0].indexOf("[")+1,operands[0].indexOf("]")));
                result= result+convertLittleIndian(symbolEntry.getAddress(),4);
                result= result+"]";
            }else if(symboltable.getSymbol(operands[0])!=null){
                int symbolAddress=Integer.parseInt(symboltable.getSymbol(operands[0]).getAddress(),16);
                int currentAddress=Integer.parseInt(address,16);
                currentAddress= currentAddress+entry.getSize();
                int offset = symbolAddress- currentAddress;
                result= result+String.format("%02X",(byte)offset);
            }

            if(operands.length >= 2)
            {
                if(operands[1].matches("\\d+"))
                {
                    
                    size = operands[1].length() <= 2 ? 2 : 8;

                    String hexValue = String.format("%X", Integer.parseInt(operands[1])); // immediate value will be either one byte or four bytes
                    int length = hexValue.length();
                    int desiredLength = length <= 2 ? 2 : 8;

                    String formattedHexValue = String.format("%0" + desiredLength + "X", Integer.parseInt(operands[1]));

                    result=result+ convertLittleIndian(formattedHexValue, size);
                }else if(operands[1].contains("[")){
                    result=result+"[";
                    symbolEntry = symboltable.getSymbol(operands[1].substring(operands[1].indexOf("[")+1,operands[1].indexOf("]")));
                    result= result+convertLittleIndian(symbolEntry.getAddress(),4);
                    result= result+"]";
                }
            }
        }
        return result;
    }
    public String convertLittleIndian(String input,int size){
        StringBuilder littleEndian = new StringBuilder();

        for (int chunkStart = 0; chunkStart < input.length(); chunkStart += size * 2) {
            String chunk = input.substring(chunkStart, Math.min(chunkStart + size * 2, input.length()));
            StringBuilder chunkReversed = new StringBuilder();

            for (int i = chunk.length(); i >= 2; i -= 2) {
                chunkReversed.append(chunk.substring(i - 2, i));
            }
            

            littleEndian.append(chunkReversed);
        }
        return littleEndian.toString();
    }
}

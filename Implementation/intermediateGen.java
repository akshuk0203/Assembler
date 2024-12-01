package Implementation;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.*;

/*structure of intermediate file: 
lineNo opcodeIndex symbolIndex locationCounter sourceLine*/

class intermediateGen{

    public static final Set<String> opCodes = new HashSet<>(Arrays.asList("add", "sub", "xor", "mul", "div", "mov", "inc", "dec", "jmp", "jnz","jz"));
    public static final Set<String> branchOpcodes = new HashSet<>(Arrays.asList("jz", "jmp", "jnz"));
    public static final Set<String> Datadirectives = new HashSet<>(Arrays.asList("db", "dw", "dd")); 
    public  static final Set<String> BSSdirectives = new HashSet<>(Arrays.asList("resb", "resw", "resd")); 
    public  static final Set<String> registers = new HashSet<>(Arrays.asList("eax","ebx","ecx","edx","esi","esp","ebp","edi","ax", "bx", "cx", "dx","al", "bl", "cl", "dl"));


    private opcodeTable opcodetable;
    private symbolTable symboltable;
    
    private static int addressCounter = 0;
    private boolean inTextSection = false;
    private boolean analyzeLine = true;

    String line;
    int lineNumber = 1;
    int lcValue;

    public intermediateGen(opcodeTable opcodetable, symbolTable symboltable) {
        this.opcodetable = opcodetable;
        this.symboltable = symboltable;
    }

    public boolean processFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter("intermediate_code.txt"))) {

            while ((line = reader.readLine()) != null) {
                boolean success = processLine(line.trim(), lineNumber, writer);
                if (!success) {
                    System.out.println("Error: Invalid line at line number " + lineNumber);
                    return false;
                }
                lineNumber++;
            }

            System.out.println("Intermediate code successfully written to intermediate_code.txt");
            System.out.println("File written to: " + new File("intermediate_code.txt").getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean processLine(String line, int lineNumber, BufferedWriter writer) throws IOException {
        analyzeLine = true;
        if (line.startsWith("section .data") || line.startsWith("section .bss")|| line.startsWith("section .text")) {
            addressCounter = 0;
            analyzeLine = false;
        }
        if (line.startsWith("section .text")) {
            inTextSection = true;
        }

		if (inTextSection) {
            if ((line.startsWith("section") && (!line.contains("text"))) || line.startsWith("main:")) {
                inTextSection = false;
            }
            analyzeLine = false;
        }
		if (line.isEmpty() || line.startsWith(";")||line.startsWith("main:")) {
            analyzeLine = false;
        }
        
        if(analyzeLine)
        	return analyzeline(line, lineNumber, writer);
        else
        {
            writer.write(
                String.format(      
                    "%-25d %-100s%n", 
                    lineNumber,          
                    line                                         
                )
            );
        }
        return true;
    }

    private boolean analyzeline(String line, int lineNumber, BufferedWriter writer) throws IOException
	{
		String label = null, instruction = null, operand = null;
        lcValue = addressCounter;
        int opcodePointer=-1;
        String recordType = null;

        String[] parts = line.split("\\s+", 3);
        String[] operands = new String[2];
        Arrays.fill(operands, " ");

        if (parts.length == 3) {
            label = parts[0];
            instruction = parts[1];
            operand = parts[2];
        } else if (parts.length == 2) {
            instruction = parts[0];
            operand = parts[1];
        } else if (parts.length == 1) {
            label = parts[0];
        } else
        	return false;

        if((parts.length == 1 && label.contains(":"))) {
            label=label.substring(0,label.length()-1);
        	recordType = "LABEL";
            symbolEntry entry = new symbolEntry(label, "-1", "-1", String.format("%08X", lcValue), 0, "main", true);
            if(!symboltable.addSymbolbyDef(label,entry)) 
            {
                System.out.println("Symbol denition conflict occur at " + lineNumber);
                return false;    
            }
        }
        else if(parts.length == 3 && label.contains(":")) {
            label=label.substring(0,label.length()-1);
            symbolEntry entry = new symbolEntry(label, "-1", "-1", String.format("%08X", lcValue), 0, "main", true);
            if(!symboltable.addSymbolbyDef(label,entry)) 
            {
                System.out.println("Symbol denition conflict occur at " + lineNumber);
                return false;    
            }
            if (opCodes.contains(instruction.toLowerCase())) {
                recordType = "INSTRUCTION"; //if record type is instruction then search it in a opcode table(here store operand types also) and return index from opcode table where that instruction lie(take size from that index to add it into addresscounter)
            } 
            else
                return false;
        }
        else if(parts.length == 2 && opCodes.contains(instruction.toLowerCase())) {
            recordType = "INSTRUCTION";
            if(branchOpcodes.contains(instruction.toLowerCase()))
            {
                symbolEntry entry = new symbolEntry(operand,"-1", "-1", "-1", 0, "main", false);
                symboltable.addSymbolbyCall(operand,entry); 
            }
        }
        else if(parts.length == 3 && (Datadirectives.contains(instruction.toLowerCase())|| BSSdirectives.contains(instruction.toLowerCase()))){
            recordType = " DIRECTIVE";
            handleDataOrBssDirective(label, instruction.toLowerCase(), operand);
        }
        else
            return false;
        
        if(recordType=="INSTRUCTION"){
            operands= operand.split(",");
            if(operands.length == 1)
                operands = new String[] { operands[0], " " };
            else if(operands.length > 2 || operands.length < 1)
                return false;
        
            assignOperandTypes(operands);

            opcodePointer=opcodetable.getOpcodeIndex(instruction, operands[0], operands[1]);
            addressCounter+=opcodetable.getOpcodeSize(instruction, operands[0], operands[1]);
        }
        writer.write(
            String.format(
                "%-5d %-10s %-10s %-100s%n", 
                lineNumber,                      
                String.format("%08X", lcValue),   
                opcodePointer,                    
                line                             
            )
        );
                 
        return true;
	}
	
    private boolean handleDataOrBssDirective(String label, String directive, String operand) {

        if (label == null) return false;
        int size = 0;
        StringBuilder hexequi=new StringBuilder();
        StringBuilder originalVal=new StringBuilder();
        symbolEntry entry=null;
        String parts[]=operand.split(",");

        String section=Datadirectives.contains(directive)? "Data": "BSS";
        boolean isDefined=Datadirectives.contains(directive)? true: false;
        switch (directive){
            case "db": size = 1; break;
            case "dw": size = 2; break;
            case "dd": size = 4; break;
            case "resb": size = Integer.parseInt(operand.trim()); break;
            case "resw": size = Integer.parseInt(operand.trim()) * 2; break;
            case "resd": size = Integer.parseInt(operand.trim()) * 4; break;
        }

        if(Datadirectives.contains(directive))
        {
            int operandsize=0;
            boolean isString = operand.matches(".*[^\\d,\\s].*");
            if(!isString)
            {
                for(String part:parts){
                    hexequi.append(String.format("%0" + size*2 + "X", Integer.parseInt(part.trim())));

                    originalVal=(originalVal.length()>0)?originalVal.append(", "):originalVal;
                    originalVal.append(Integer.parseInt(part.trim()));
                    addressCounter += size;
                    operandsize+=size;
                }
            }
            else{
                String temp=operand;
                String operand2= null;
                int start= operand.indexOf('"');
                int end= operand.indexOf('"', start + 1);
                operand= (start != -1 && end != -1) ? operand.substring(start + 1, end) : null;
                operand2= (end != -1 && end<temp.length()-2)? temp.substring(end+1): null;
                String[] operandparts= operand2!=null ? operand2.split(","): null;
                for (char c : operand.toCharArray()) {
                    hexequi.append(String.format("%02X", (int) c));
                    originalVal.append(c);
                    addressCounter += size;
                    operandsize+=size;
                }
                if(operandparts!=null)
                {
                    for (String copyoperand: operandparts)
                    {
                        if(!copyoperand.matches("\\d+"))
                            continue;
                        hexequi.append(String.format("%02X", Integer.parseInt(copyoperand)));
                        originalVal.append(copyoperand);
                        addressCounter += size;
                        operandsize+=size;
                    }
                }
            }
            entry = new symbolEntry(label, originalVal.toString(),hexequi.toString(),String.format("%08X", lcValue), operandsize, section, isDefined);
                
            if(!symboltable.addSymbolDataBSS(label,entry)){
                    System.out.println("Symbol denition conflict occur at " + lineNumber);
                    return false;    
            }
        }
        else if(BSSdirectives.contains(directive) && parts.length==1)
        {
            entry = new symbolEntry(label, "-1","-1",String.format("%08X", lcValue), size, section, isDefined);   
            if(!symboltable.addSymbolDataBSS(label,entry)){
                System.out.println("Symbol denition conflict occur at " + lineNumber);
                return false;    
            }
            addressCounter += size;
        }
        else
            return false;

        return true;
    }

    private static void assignOperandTypes(String[] operands) {
        for (int i = 0; i < operands.length; i++) {
            String operand = operands[i];
            if(operand.matches("\\d+")) 
                operands[i] = Integer.parseInt(operand) <= 255 ? "imm8" : "imm32";
            else if(registers.contains(operand))
                operands[i] = "reg32";
            else if(operand.equals(" "))
                operands[i] = "";
            else
                operands[i] = "mem32";
        }
    }
}
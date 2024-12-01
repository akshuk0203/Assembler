package assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class opcodeLoader{
    private opcodeTable opcodeTable;

    public opcodeLoader(opcodeTable opcodeTable) {
        this.opcodeTable = opcodeTable;
    }
    
    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String instruction = parts[0].trim();
                    String opcode = parts[1].trim();  
                    int size = Integer.parseInt(parts[5].trim());
                    int numOperands = Integer.parseInt(parts[2].trim());
                    String firstOperandType = parts[3].trim();
                    String secondOperandType = parts[4].trim();
                    boolean isModRM = Boolean.parseBoolean(parts[6].trim());
                    boolean isrd= Boolean.parseBoolean(parts[7].trim());

                    opcodeEntry entry = new opcodeEntry(instruction, opcode, size, numOperands, firstOperandType, secondOperandType, isModRM, isrd);
                    opcodeTable.addOpcodeEntry(entry);
                }
            }
        }
    }
}
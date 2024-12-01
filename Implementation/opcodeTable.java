package Implementation;

import java.util.HashMap;
import java.util.Map;

public class opcodeTable {
    private Map<Integer, opcodeEntry> opcodeTable;
    private static int currentIndex = 0;

    public opcodeTable() {
        opcodeTable = new HashMap<>();
    }

    public void addOpcodeEntry(opcodeEntry entry) {
        int index = currentIndex++; 
        opcodeTable.put(index, entry);
    }


    public opcodeEntry getOpcodeEntry(int index)
    {
        return opcodeTable.get(index);
    }
    
   public int getOpcodeIndex(String mnemonic, String firstOperandType, String secondOperandType) {
        mnemonic=mnemonic.toUpperCase();
        for (Map.Entry<Integer, opcodeEntry> entry : opcodeTable.entrySet()) {
            opcodeEntry opcodeEntry = entry.getValue();
            if (opcodeEntry.getInstruction().equals(mnemonic) &&
                opcodeEntry.getFirstOperandType().equals(firstOperandType) &&
                opcodeEntry.getSecondOperandType().equals(secondOperandType)) {
                return entry.getKey();  // Return the index
            }
        }
        return -1; // Return null if not found
    }

    public int getOpcodeSize(String mnemonic, String firstOperandType, String secondOperandType) {
        mnemonic=mnemonic.toUpperCase();
        for (Map.Entry<Integer, opcodeEntry> entry : opcodeTable.entrySet()) {
            opcodeEntry opcodeEntry = entry.getValue();
            if (opcodeEntry.getInstruction().equals(mnemonic) &&
                opcodeEntry.getFirstOperandType().equals(firstOperandType) &&
                opcodeEntry.getSecondOperandType().equals(secondOperandType)) {
                return opcodeEntry.getSize(); // Return the size
            }
        }
        return -1; // Return null if not found
    }

    public void printAllEntries() {
        for (Map.Entry<Integer, opcodeEntry> entry : opcodeTable.entrySet()) {
            int index = entry.getKey();
            opcodeEntry opcodeEntry = entry.getValue();
            System.out.println("Index: " + index + ", Instruction: " + opcodeEntry.getInstruction() +
                               ", Size: " + opcodeEntry.getOpcode() +
                               ", No of operands: " + opcodeEntry.getSize() +
                               ", Num Operands: " + opcodeEntry.getNumOperands() +
                               ", Operand Types: " + opcodeEntry.getFirstOperandType() +
                               ", " + opcodeEntry.getSecondOperandType());
        }
    }
}
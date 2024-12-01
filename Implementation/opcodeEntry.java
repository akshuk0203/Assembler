package Implementation;

public class opcodeEntry{
    private String mnemonic;
    private String opcode;
    private int size;
    private int numOperands;
    private String firstOperandType;
    private String secondOperandType;
    private boolean isModRM;
    private boolean rd;

    public opcodeEntry(String instruction, String opcode, int size, int numOperands,String firstOperandType, String secondOperandType,boolean isModRM,boolean rd) 
    {
        this.mnemonic = instruction;
        this.opcode = opcode;
        this.size = size;
        this.numOperands = numOperands;
        this.firstOperandType = firstOperandType;
        this.secondOperandType = secondOperandType;
        this.isModRM=isModRM;
        this.rd=rd;
    }

    public String getInstruction() { return mnemonic; }
    public String getOpcode() { return opcode; }
    public int getSize() { return size; }
    public int getNumOperands() { return numOperands; }
    public String getFirstOperandType() { return firstOperandType; }
    public String getSecondOperandType() { return secondOperandType; }
    public boolean getisModRM(){return isModRM ;}
    public boolean getrd(){return rd ;}
}
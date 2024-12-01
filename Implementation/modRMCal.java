package assembler;

import java.util.HashMap;
import java.util.Map;

public class modRMCal{

    private static final Map<String, Integer> registers = new HashMap<>();
    static {
        registers.put("eax", 0);
        registers.put("ecx", 1);
        registers.put("edx", 2);
        registers.put("ebx", 3);
        registers.put("esp", 4);
        registers.put("ebp", 5);
        registers.put("esi", 6);
        registers.put("edi", 7);
    }

    private static final String[] noModRMInstructions = {"nop", "push", "pop", "ret", "inc", "dec"};
    private static final String[] branchInstructions = {"jmp", "je", "jne","jnz"};
    private static final String[] arithmeticInstructions = {"add", "sub", "xor", "cmp", "mul", "div","inc","dec"};
    private static final String[] twoOpInstructions = {"add", "sub", "xor", "mov", "cmp"};
    private static final String[] oneOpInstructions = {"mul", "div"};

    public Integer calculateModRM(String instruction) {
        String[] parts = instruction.split(" ");
  
        String operation = parts[0].trim();
        String[] operands = parts[1].split(",");

        if (isBranchInstruction(operation) || isNoModRMInstruction(operation)) {
            return null;
        }

		int mod=0;
        int regField, rmField=0;
        if (operands.length == 2 && istwoOpInstructions(operation)) {
            String firstOperand = operands[0].trim();
            String secondOperand = operands[1].trim();

            if (isArithmeticInstruction(operation) && secondOperand.matches("\\d+")) { //immediate operand
                regField = getOpcodeExtension(operation); //+rd
                if(isRegister(firstOperand))
                {
                	mod = 0b11; 
                	rmField = getRegisterCode(firstOperand);
                }
                else if(isMemoryAccess(firstOperand))
                {
                	 mod = 0b00; 
                	rmField = 0b101;
                }
                else
                	System.out.println("Error processing instruction");
            }
			else if (isMemoryAccess(firstOperand) || isMemoryAccess(secondOperand)) { // indicating direct memory access
				mod = 0b00; 
				regField =(isMemoryAccess(firstOperand))?getRegisterCode(secondOperand):getRegisterCode(firstOperand);
				rmField= 0b101; 
			}
			else if(isRegister(firstOperand) && isRegister(secondOperand)) { // if both are registers then operands order changes.
                regField = getRegisterCode(secondOperand);
                rmField = getRegisterCode(firstOperand);
                mod = 0b11; 
            } else {
                    mod = 0b00; 
					regField = getRegisterCode(firstOperand);
                	rmField = getRegisterCode(secondOperand);
            }
        }
        else if(operands.length == 1 && isoneOpInstructions(operation))
        {
        	String firstOperand = operands[0].trim();
        	regField = operation.equals("mul") ? 0b100 : 0b110;
        	if(isMemoryAccess(firstOperand)){ // direct memory access
        		mod = 0b00;
        		rmField = 0b101;
        	}
        	else{ //register
        		mod = 0b11;
        		rmField = getRegisterCode(firstOperand);
        	}
        }
        else
        	throw new IllegalArgumentException("Invalid operand format.");
        	
        
        return (mod << 6) | (regField << 3) | rmField;
    }

	private static boolean isMemoryAccess(String operand) {
        return operand.contains("[") && operand.contains("]");
    }
    
	 private static boolean istwoOpInstructions(String operation) {
        for (String branch : twoOpInstructions) {
            if (operation.equalsIgnoreCase(branch)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isoneOpInstructions(String operation) {
        for (String branch : oneOpInstructions) {
            if (operation.equalsIgnoreCase(branch)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isBranchInstruction(String operation) {
        for (String branch : branchInstructions) {
            if (operation.equalsIgnoreCase(branch)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNoModRMInstruction(String operation) {
        for (String instr : noModRMInstructions) {
            if (operation.equalsIgnoreCase(instr)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isArithmeticInstruction(String operation) {
        for (String arith : arithmeticInstructions) {
            if (operation.equalsIgnoreCase(arith)) {
                return true;
            }
        }
        return false;
    }
  
    private static int getOpcodeExtension(String operation) {
        switch (operation.toLowerCase()) {
            case "add": return 0;
            case "sub": return 5;
            case "xor": return 6;
            case "cmp": return 7;
            default: throw new IllegalArgumentException("Unsupported arithmetic operation: " + operation);
        }
    }

    private static int getRegisterCode(String reg) {
        if (registers.containsKey(reg)) {
            return registers.get(reg);
        }
        throw new IllegalArgumentException("Invalid register: " + reg);
    }

    private static boolean isRegister(String operand) {
        return registers.containsKey(operand);
    }

}


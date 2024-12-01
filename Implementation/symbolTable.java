package Implementation;

import java.util.HashMap;
import java.util.Map;

public class symbolTable{
    private Map<String, symbolEntry> symbolTable;

    public symbolTable() {
        symbolTable = new HashMap<>();
    }
            // search for a entry in symbol table
            // if found and defined is false then update defined section from false to true;
            // if not found then add new entry into symbol table
            // if entry found with true then throw error as double definition for same symbol.
    public boolean addSymbolbyDef(String name,symbolEntry entry) {
        if(symbolTable.get(name)==null)
            symbolTable.put(name,entry);
        else if(symbolTable.get(name)!=null){
            if(symbolTable.get(name).isDefined()==false)
            {
                entry.setDefined(true);
                symbolTable.get(name).setAddress(entry.getAddress());
            }
            else if(symbolTable.get(name).isDefined()==true)
                return false;
        }
        
        return true; 
    }

            //search for a symbol entry in a table
            // if not found then call constructor with undefined(false) entry in symbol table.
            // later work- if found then fetch the size to assemble the instruction in pass two(finding offset from symbol defition i.e.address)
    public  void addSymbolbyCall(String name,symbolEntry entry) {
        if(symbolTable.get(name)==null)
            symbolTable.put(name,entry);
    }

            //search whether already exist or not
            // if yes then throw error.
    public  boolean addSymbolDataBSS(String name,symbolEntry entry) {
        if(symbolTable.get(name)==null)
            symbolTable.put(name,entry);
        else
            return false;
        return true;
    }


   public symbolEntry getSymbol(String name) {
       return symbolTable.get(name);
    }

    public void printSymbolTable() {
        System.out.println("Symbol Table:");
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-10s %-10s %-10s %-15s %-15s%n", "Name", "Address", "Size", "Section","Value","Hex Equivalent");
        System.out.println("----------------------------------------------------------------------------------------------");
    
        for (Map.Entry<String, symbolEntry> entry : symbolTable.entrySet()) {
            symbolEntry record = entry.getValue();
            System.out.printf("%-15s %-10s %-10d %-10s %-15s %-15s%n", 
                              record.getName(), 
                              record.getAddress(), 
                              record.getSize(), 
                              record.getSection(),
                              record.getVal(),
                              record.getEquihex());
        }
    
        System.out.println("----------------------------------------------------------------------------------------");
    }
}
package Implementation;

public class symbolEntry{
    private String name;
    private String val;
    private String equihex;
    private String address;
    private int size;
    private String section;
    private boolean defined;

    symbolEntry(String name, String val, String equihex, String address, int size, String section, boolean defined) {
        this.name = name;
        this.val = val;
        this.equihex= equihex;
        this.address = address;
        this.size = size;
        this.section = section;
        this.defined= defined;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVal() { return val; }
    public void setVal(String val) { this.val = val; }

    public String getEquihex() { return equihex; }
    public void setEquihex(String equihex) { this.equihex = equihex; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public boolean isDefined() { return defined; }
    public void setDefined(boolean defined) { this.defined = defined; }
}
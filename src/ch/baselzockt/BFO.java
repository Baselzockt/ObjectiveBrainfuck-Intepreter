package ch.baselzockt;

public class BFO {

    private int[] variables;
    private BFO[] objects;
    private String[] methods;
    private int pointer;

    public BFO(int memorySize){
        this.variables = new int[memorySize];
        this.objects = new BFO[memorySize];
        this.methods = new String[memorySize];
        this.pointer = 0;
    }

    public void addMethod(String methode){
        for(int i = 0; i < this.methods.length -1; ++i){
            if(this.methods[i] == null){
                this.methods[i] = methode;
               break;
            }
        }
    }

    public int[] getVariables() {
        return variables;
    }

    public void addObject(BFO bfo){
        for(int i = 0; i < objects.length -1; ++i){
            if(objects[i] == null){
                objects[i] = bfo;
                i = objects.length;
            }
        }
    }

    public BFO getObject(int index){
        return this.objects[index];
    }

    public void setVariables(int[] variables) {
        this.variables = variables;
    }

    public BFO[] getObjects() {
        return objects;
    }

    public void setObjects(BFO[] objects) {
        this.objects = objects;
    }

    public String[] getMethods() {
        return methods;
    }

    public void setMethods(String[] methods) {
        this.methods = methods;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }


}

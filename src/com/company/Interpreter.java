package com.company;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;

public class Interpreter {
    private InputStream in;
    private  OutputStream out;
    private int[] fields;
    private String[] objectDefinitions;
    private static final int MEMORYSIZE = 32768;
    private LinkedList<Integer> openedLoops;
    private BFO bfo;

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public int[] getFields() {
        return fields;
    }

    public void setFields(int[] fields) {
        this.fields = fields;
    }

    public String[] getObjectDefinitions() {
        return objectDefinitions;
    }

    public void setObjectDefinitions(String[] objectDefinitions) {
        this.objectDefinitions = objectDefinitions;
    }

    public LinkedList<Integer> getOpenedLoops() {
        return openedLoops;
    }

    public void setOpenedLoops(LinkedList<Integer> openedLoops) {
        this.openedLoops = openedLoops;
    }

    public BFO getBfo() {
        return bfo;
    }

    public void setBfo(BFO bfo) {
        this.bfo = bfo;
    }

    public Interpreter(InputStream input, OutputStream output, String[] definitions, HashSet<IPlugin> plugins){
        this(input,output,MEMORYSIZE,definitions,plugins);
    }

    private HashSet<IPlugin> plugins;

    public Interpreter(InputStream input,OutputStream output, int memorySize,String[] definitions, HashSet<IPlugin> plugins){
        this.in = input;
        this.out = output;
        this.fields = new int[memorySize];
        this.openedLoops = new LinkedList<>();
        this.objectDefinitions = definitions;
        this.plugins = plugins;
    }
    private int pointer;

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public void interpret(char[] arr, BFO bfo) {
        this.bfo = bfo;
        this.fields = bfo.getVariables();
        this.openedLoops.clear();
         pointer = bfo.getPointer();
        try {
            for (int i = 0; i < arr.length;i++) {
               boolean returnValue = false;
                switch (arr[i]) {

                    case '/':
                        i++;
                        StringBuilder numberTmp = new StringBuilder();
                        while(arr[i] != '\\'){
                            numberTmp.append(arr[i]);
                            i++;
                        }
                        int methodId = Integer.parseInt(numberTmp.toString());
                        this.interpret(this.bfo.getMethods()[methodId].toCharArray(),this.bfo);
                        break;
                    case '&':
                         returnValue = true;
                         i++;
                    case '{':
                        numberTmp = new StringBuilder();
                        i++;
                        while(arr[i] != '}'){
                            numberTmp.append(arr[i]);
                            i++;
                        }
                        int objIndex;
                        if(numberTmp.toString().contains("*")){
                            String tmp = numberTmp.toString().replace("*","");
                            objIndex = this.fields[Integer.parseInt(tmp)];
                        }else {
                            objIndex = Integer.parseInt(numberTmp.toString());
                        }
                        i++;
                        numberTmp = new StringBuilder();
                        i++;
                        while(i < arr.length && arr[i] != '}' ){
                            numberTmp.append(arr[i]);
                            i++;
                        }
                          ++i;
                        if(i >= arr.length){i = arr.length-1;}
                        if(arr[i] == '('){
                            i++;
                            int[] tmp = this.bfo.getObject(objIndex).getVariables();
                            int a = 0;
                            while(arr[i] != ')'){

                                StringBuilder varNumberTmp = new StringBuilder();
                                while(arr[i] != '|' && arr[i] != ')'){
                                    varNumberTmp.append(arr[i]);
                                    i++;
                                }

                                if(!varNumberTmp.toString().isEmpty()) {
                                    tmp[a] = this.fields[Integer.parseInt(varNumberTmp.toString())];
                                    a++;
                                }
                                if(arr[i] == '|'){
                                    i++;
                                }
                            }
                            i++;
                        }
                        if(!numberTmp.toString().isEmpty()) {
                            int methodIndex;
                            if(numberTmp.toString().contains("*")){
                                String tmp = numberTmp.toString().replace("*","");
                                methodIndex = this.fields[Integer.parseInt(tmp)];
                            }else {
                                methodIndex = Integer.parseInt(numberTmp.toString());
                            }
                            Interpreter interpreter = new Interpreter(this.in, this.out, this.fields.length - 1, this.objectDefinitions,this.plugins);
                            interpreter.interpret(this.bfo.getObject(objIndex).getMethods()[methodIndex].toCharArray(), this.bfo.getObject(objIndex));
                            if (returnValue) {
                                this.fields[pointer] = this.bfo.getObject(objIndex).getVariables()[0];
                            }
                            i--;
                        }
                        break;
                    case '@':
                        pointer = 0;
                        break;
                    case '$':
                        this.fields = new int[fields.length-1];
                        break;
                    case '?':
                        i++;
                        if(arr[i] == '{'){
                             numberTmp = new StringBuilder();
                             i++;
                            while(arr[i] != '}'){
                                numberTmp.append(arr[i]);
                                i++;
                            }
                            int number = Integer.parseInt(numberTmp.toString());
                            this.bfo.addObject(createObject(this.objectDefinitions[number]));
                        }
                        break;
                    default:
                        if(plugins != null) {
                            for (IPlugin plugin : plugins) {
                                i = plugin.interpret(arr, i, this);
                            }
                        }
                        break;
                }
            }
        }catch  (IOException e){
            throw new RuntimeException("Can't read or write", e);
        }catch(NotImplementedException e){
            throw new RuntimeException("Not yet implementet",e);
        }catch(Exception e){
            throw new RuntimeException("Syntaxerror", e);
        }
        bfo.setVariables(this.fields);
    }

    public void run(){
        BFO bfo = createObject(this.objectDefinitions[0]);
        this.bfo = bfo;
        this.interpret(bfo.getMethods()[0].toCharArray(),bfo);
    }

    private BFO createObject(String objectDefinition){

        char[] arr = objectDefinition.toCharArray();
        BFO tmpBfo = new BFO(this.fields.length-1);

        for (int i = 0; i < arr.length-1;i++) {
            if(arr[i] == '¦'){
                ++i;
                StringBuilder methode = new StringBuilder();
                while(arr[i] != '¦'){
                    methode.append(arr[i]);
                    ++i;
                }
                tmpBfo.addMethod(methode.toString());
            }
        }
        tmpBfo.setPointer(0);

        return tmpBfo;
    }

}

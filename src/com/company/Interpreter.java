package com.company;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class Interpreter {
    private InputStream in;
    private  OutputStream out;
    private int[] fields;
    private String[] objectDefinitions;
    private static final int MEMORYSIZE = 32768;
    private LinkedList<Integer> openedLoops;
    private BFO bfo;





    public Interpreter(InputStream input, OutputStream output,String[] definitions){
        this(input,output,MEMORYSIZE,definitions);
    }

    public Interpreter(InputStream input,OutputStream output, int memorySize,String[] definitions){
        this.in = input;
        this.out = output;
        this.fields = new int[memorySize];
        this.openedLoops = new LinkedList<Integer>();
        this.objectDefinitions = definitions;
    }

    public void interpret(char[] arr,BFO bfo) {
        this.bfo = bfo;
        this.fields = bfo.getVariables();
        this.openedLoops.clear();
        int pointer = bfo.getPointer();
        try {
            for (int i = 0; i < arr.length-1;i++) {
                switch (arr[i]) {
                    case '+':
                        this.fields[pointer] +=1;
                        if(this.fields[pointer] > 255){
                            this.fields[pointer] = 0;
                        }
                        break;
                    case '-':
                        this.fields[pointer] -=1;
                        if(this.fields[pointer] < 0){
                            this.fields[pointer] = 255;
                        }
                        break;
                    case '<':
                        pointer--;
                        if(pointer < 0){
                            pointer = this.fields.length - 1;
                        }
                        break;
                    case '>':
                        pointer ++;
                        if(pointer >= this.fields.length){
                            pointer = 0;
                        }
                        break;
                    case '[':
                        if(this.fields[pointer] == 0){
                            i++;
                            for(int cnt = 1;; i++){
                                if(arr[i] == ']'){
                                    cnt--;
                                }else if (arr[i] == '['){
                                    cnt++;
                                }

                                if(cnt == 0){
                                    break;
                                }
                            }
                        }else{
                            this.openedLoops.offerFirst(i);
                        }
                        break;
                    case ']':
                        i = this.openedLoops.pollFirst() -1;
                        break;
                    case '.':
                        this.out.write(this.fields[pointer]);
                        break;
                    case ',':
                        this.fields[pointer] = this.in.read();
                        if(this.fields[pointer] > 255){
                            this.fields[pointer] = 255;
                        }else if(this.fields[pointer] < 0){
                            this.fields[pointer] = 0;
                        }
                        break;
                    case '/':
                        i++;
                        String numberTmp = "";
                        while(arr[i] != '\\'){
                            numberTmp+=arr[i];
                            i++;
                        }
                        int methodId = Integer.parseInt(numberTmp);
                        this.interpret(this.bfo.getMethods()[methodId].toCharArray(),this.bfo);
                        break;
                    case '{':
                        numberTmp = "";
                        i++;
                        while(arr[i] != '}'){
                            numberTmp+=arr[i];
                            i++;
                        }
                        int objIndex = Integer.parseInt(numberTmp);
                        i++;
                        numberTmp = "";
                        i++;
                        while(arr[i] != '}'){
                            numberTmp+=arr[i];
                            i++;
                        }
                        int methodIndex = Integer.parseInt(numberTmp);
                        Interpreter interpreter = new Interpreter(this.in,this.out,this.fields.length-1,this.objectDefinitions);
                        interpreter.interpret(this.bfo.getMethods()[methodIndex].toCharArray(),this.bfo.getObject(objIndex));

                        break;
                    case '@':
                        pointer = 0;
                        break;
                    case '$':
                        this.fields = new int[fields.length-1];
                        break;
                    case ':':
                        i++;
                        if(arr[i] == '{'){
                             numberTmp = "";
                             i++;
                            while(arr[i] != '}'){
                                numberTmp+=arr[i];
                                i++;
                            }
                            int number = Integer.parseInt(numberTmp);
                            this.bfo.addObject(createObject(this.objectDefinitions[number]));
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
    }

    public void run(){
        BFO bfo = createObject(this.objectDefinitions[0]);
        this.bfo = bfo;
        this.interpret(bfo.getMethods()[0].toCharArray(),bfo);
    }

    private BFO createObject(String objectDefinition){

        char[] arr = objectDefinition.toCharArray();
        int methodCount = 0;
        BFO tmpBfo = new BFO(this.fields.length-1);

        for (int i = 0; i < arr.length-1;i++) {
            if(arr[i] == '#'){
                ++i;
                String methode = "";
                while(arr[i] != '#'){
                    methode+= arr[i];
                    ++i;
                }
                tmpBfo.addMethod(methode);
                methodCount++;
            }
        }
        tmpBfo.setPointer(0);

        return tmpBfo;
    }

}

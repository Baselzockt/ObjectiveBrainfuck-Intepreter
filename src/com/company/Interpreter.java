package com.company;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

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
        this.openedLoops = new LinkedList<>();
        this.objectDefinitions = definitions;
    }

    public void interpret(char[] arr,BFO bfo) {
        this.bfo = bfo;
        this.fields = bfo.getVariables();
        this.openedLoops.clear();
        int pointer = bfo.getPointer();
        try {
            for (int i = 0; i < arr.length;i++) {
               boolean returnValue = false;
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
                            i = this.openedLoops.pollFirst() - 1;
                        break;
                    case '.':
                        this.out.write(this.fields[pointer]);
                        this.out.flush();
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
                        int objIndex = Integer.parseInt(numberTmp.toString());
                        i++;
                        numberTmp = new StringBuilder();
                        i++;
                        while(arr[i] != '}'){
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
                        int methodIndex = Integer.parseInt(numberTmp.toString());
                        Interpreter interpreter = new Interpreter(this.in,this.out,this.fields.length-1,this.objectDefinitions);
                        interpreter.interpret(this.bfo.getObject(objIndex).getMethods()[methodIndex].toCharArray(),this.bfo.getObject(objIndex));
                        if(returnValue){
                            this.fields[pointer] = this.bfo.getObject(objIndex).getVariables()[0];
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
                    case '#':
                    case ';':
                    case ':':
                    case '%':
                    case '^':
                    case '!':
                        throw new NotImplementedException();
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

package com.company;

import java.io.IOException;

public interface IPlugin {
    /**
     * Takes the array of chacracters to be interpretet, the current index and a reference to the Interpreter and Interprets them.
     * Note that IPlugin.interpret gets called last and can't be used to override default implementations.
     * @param arr
     * @param i
     * @param ip
     * @return returns the current index.
     */
    public int interpret(char[] arr,int i,Interpreter ip) throws Exception;
    default void initialize(){
        System.out.println("Initialized "+this.getClass().getName());
    }
}

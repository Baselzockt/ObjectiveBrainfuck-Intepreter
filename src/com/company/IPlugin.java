package com.company;

import java.io.IOException;

public interface IPlugin {
    /**
     * Takes the array of characters to be interpreted, the current index and a reference to the Interpreter and Interprets them.
     * Note that Plugins dont get called in a fixed order and can't be used to override other implementations.
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

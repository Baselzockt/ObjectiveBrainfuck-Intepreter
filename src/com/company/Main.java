package com.company;

public class Main {

    public static void main(String[] args)  {
        String[] definitions = new String[]{"¦+++?{1}[{0}{*1}(2000)>+<-]¦",
                "¦$@>++++++++[-<+++++++++>]<.>>+>-[+]++>++>+++[>[->+++<<+++>]<<]>-----.>->+++..+++.>-.<<+[>[+>+]>>]<--------------.>>.+++.------.--------.>+.>+.¦ ¦++..¦ ¦$@+[,.]¦"};
        Interpreter ip = new Interpreter(System.in, System.out, definitions);
        ip.run();
        System.out.flush();
    }

}
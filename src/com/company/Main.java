package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String[] definitions = new String[]{"¦?{1}{0}{0}(0).&{0}{1}(0|0|2)..{0}{3}¦","¦$>++++++++[-<+++++++++>]<.>>+>-[+]++>++>+++[>[->+++<<+++>]<<]>-----.>->+++..+++.>-.<<+[>[+>+]>>]<--------------.>>.+++.------.--------.>+.>+.¦ ¦+++++++++++++++++++++++++++++++++++++¦ ¦..++..¦ ¦$@,+[-.,+]¦"};
        Interpreter ip = new Interpreter(System.in,System.out,definitions);
        ip.run();
        System.out.flush();
    }
}

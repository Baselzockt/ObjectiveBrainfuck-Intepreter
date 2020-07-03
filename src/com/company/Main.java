package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String[] definitions = new String[]{"#:{1}+-{0}{0}......{0}{0}","#+[-[<<[+[--->]-[<<<]]]>>>-]>-.---.>..>.<<<<-.<+.>>>>>.>.<<.<-..# #+++...#"};


        Interpreter ip = new Interpreter(System.in,System.out,definitions);
        BFO bfo = new BFO(32768);
        String[] methodes = new String[]{definitions[0]};
        bfo.setMethods(methodes);
        ip.interpret("/0\\".toCharArray(),bfo);
        System.out.flush();
    }
}

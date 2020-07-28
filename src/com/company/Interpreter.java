package com.company;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.jar.JarFile;

public class Interpreter {
    private InputStream in;
    private  OutputStream out;
    private int[] fields;
    private String[] objectDefinitions;
    private static final int MEMORYSIZE = 32768;
    private LinkedList<Integer> openedLoops;
    private BFO bfo;
    private HashSet<IPlugin> plugins;
    private Properties properties;
    private static String configFile = "app.config";

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

    public Interpreter(InputStream input, OutputStream output, String[] definitions){
        this(input,output,MEMORYSIZE,definitions);
    }


    public HashSet<IPlugin> getPlugins() {
        return plugins;
    }

    public Interpreter(InputStream input, OutputStream output, int memorySize, String[] definitions,HashSet<IPlugin> plugins) {
        this.in = input;
        this.out = output;
        this.fields = new int[memorySize];
        this.openedLoops = new LinkedList<>();
        this.objectDefinitions = definitions;
        this.plugins = plugins;
    }

    public Interpreter(InputStream input, OutputStream output, int memorySize, String[] definitions){
        try {
            loadProperties();
            loadPlugins();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.in = input;
        this.out = output;
        this.fields = new int[memorySize];
        this.openedLoops = new LinkedList<>();
        this.objectDefinitions = definitions;
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
                if(plugins != null) {
                    for (IPlugin plugin : plugins) {
                        i = plugin.interpret(arr, i, this);
                    }
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

    public BFO createObject(String objectDefinition){

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

    private void loadProperties() throws IOException {
        properties = new Properties();
        File file = new File(configFile);
        if(!file.exists()){
            FileWriter writer = new FileWriter(file);
            writer.write("PLUGIN_DIRECTORY=plugin");
            writer.close();
        }
        properties.load(new FileInputStream(file));
        System.out.println("Loaded Properties");
    }

    private void loadPlugins() throws IOException {
        plugins= new HashSet<>();
        File pluginDirectory = new File(properties.getProperty("PLUGIN_DIRECTORY"));
        if(!pluginDirectory.exists())pluginDirectory.mkdir();
        File[] files = pluginDirectory.listFiles((dir,name)-> name.endsWith(".jar"));

        if(files!=null && files.length > 0){
            ArrayList<String> classes=new ArrayList<>();
            ArrayList<URL> urls=new ArrayList<>(files.length);
            for(File file:files)
            {
                JarFile jar=new JarFile(file);
                jar.stream().forEach(jarEntry -> {
                    if(jarEntry.getName().endsWith(".class"))
                    {
                        classes.add(jarEntry.getName());
                    }
                });
                URL url=file.toURI().toURL();
                urls.add(url);
            }
            URLClassLoader urlClassLoader=new URLClassLoader(urls.toArray(new URL[urls.size()]));
            classes.forEach(className->{
                try
                {
                    Class cls=urlClassLoader.loadClass(className.replaceAll("/",".").replace(".class","")); //transforming to binary name
                    Class[] interfaces=cls.getInterfaces();
                    for(Class intface:interfaces)
                    {
                        if(intface.equals(IPlugin.class)) //checking presence of Plugin interface
                        {
                            IPlugin IPlugin =(IPlugin) cls.newInstance(); //instantiating the Plugin
                            plugins.add(IPlugin);
                            break;
                        }
                    }
                }
                catch (Exception e){e.printStackTrace();}
            });
            plugins.forEach(IPlugin::initialize);
        }
    }

}

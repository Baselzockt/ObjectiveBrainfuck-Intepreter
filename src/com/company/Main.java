package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.jar.JarFile;

public class Main {

    private HashSet<IPlugin> plugins;
    private Properties properties;
    private static String configFile = "app.config";

    public static void main(String[] args) throws IOException {
       Main main = new Main();
    }
    Main() throws IOException {
        try {
            loadProperties();
            loadPlugins();
        }catch(Exception e){
            e.printStackTrace();
        }
        String[] definitions = new String[]{"¦>?{1}{*0}{*1}¦","¦$>++++++++[-<+++++++++>]<.>>+>-[+]++>++>+++[>[->+++<<+++>]<<]>-----.>->+++..+++.>-.<<+[>[+>+]>>]<--------------.>>.+++.------.--------.>+.>+.¦ ¦++..¦"};
        Interpreter ip = new Interpreter(System.in,System.out,definitions,plugins);
        ip.run();
        System.out.flush();
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
            System.out.println("Initialized "+ plugins.size()+" plugins");
        }
    }
}

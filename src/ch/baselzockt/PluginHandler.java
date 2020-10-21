package ch.baselzockt;

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

public class PluginHandler {

    private Properties properties;
    private static String configFile = "app.config";

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

    public HashSet<IPlugin> loadPlugins() throws IOException {
        this.loadProperties();
        HashSet<IPlugin> plugins= new HashSet<>();
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
        return plugins;
    }
}

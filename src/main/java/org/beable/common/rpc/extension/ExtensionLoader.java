package org.beable.common.rpc.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qing.wu
 */
@Slf4j
public final class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>,ExtensionLoader<?>> EXTENSION_LOADER_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>,Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();
    private final Class<?> type;
    private final Map<String,Holder<T>> cacheInstance = new ConcurrentHashMap<>();
    private final Holder<Map<String,Class<?>>> cacheClasses = new Holder<>();

    public ExtensionLoader(Class<?> type){
        this.type = type;
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        if (type == null){
            throw new IllegalArgumentException("Extension type should not be null");
        }
        if (!type.isInterface()){
            throw new IllegalArgumentException("Extension type must be an interface");
        }
        if (type.getAnnotation(SPI.class) == null){
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // first get from cache, if not hit, create one
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADER_MAP.get(type);
        if (extensionLoader == null){
            EXTENSION_LOADER_MAP.putIfAbsent(type,new ExtensionLoader<>(type));
            extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADER_MAP.get(type);
        }
        return extensionLoader;
    }


    public T getExtension(String name){
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        // firstly get from cache, if not hit, create one
        Holder<T> holder = cacheInstance.get(name);
        if(holder == null){
            cacheInstance.putIfAbsent(name,new Holder<>());
            holder = cacheInstance.get(name);
        }
        // create a singleton if no instance exists
        T instance = holder.get();
        if (instance == null){
            synchronized (holder){
                instance = holder.get();
                if (instance == null){
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return instance;
    }

    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null){
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance  = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null){
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz,clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }


    private Map<String,Class<?>> getExtensionClasses(){
        // get the loaded extension class from the cache
        Map<String,Class<?>> classes = cacheClasses.get();
        // double check
        if (classes == null){
            synchronized (cacheClasses){
                classes = cacheClasses.get();
                if (classes == null){
                    classes = new HashMap<>(2);
                    // load all extensions from our extensions directory
                    loadDirectory(classes);
                    cacheClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> resources = classLoader.getResources(fileName);
            if (resources != null){
                while (resources.hasMoreElements()){
                    URL url = resources.nextElement();
                    loadResource(extensionClasses, classLoader, url);
                }
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL url) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null){
                // get index of comment
                int ci = line.indexOf('#');
                if (ci >= 0){
                    // string after # is comment, so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() == 0 ){
                    continue;
                }
                int ei = line.indexOf('=');
                String name = line.substring(0,ei).trim();
                String clazzName = line.substring(ei + 1).trim();
                if (name.length() == 0 || clazzName.length() == 0){
                    continue;
                }
                try {
                    Class<?> clazz = classLoader.loadClass(clazzName);
                    extensionClasses.put(name,clazz);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}

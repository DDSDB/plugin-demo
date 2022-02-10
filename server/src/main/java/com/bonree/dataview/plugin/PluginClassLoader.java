package com.bonree.dataview.plugin;

import com.google.common.collect.Iterators;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PluginClassLoader extends URLClassLoader {

    private final ClassLoader dataviewLoader;

    public PluginClassLoader(List<URL> urls, ClassLoader dataviewLoader) {
        this(urls.toArray(new URL[urls.size()]), dataviewLoader);
    }

    public PluginClassLoader(URL[] urls, ClassLoader dataviewLoader) {
        super(urls, null);
        this.dataviewLoader = dataviewLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded;
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    return dataviewLoader.loadClass(name);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    @Override
    public URL getResource(final String name) {
        final URL resourceFromExtension = super.getResource(name);
        if (resourceFromExtension != null) {
            return resourceFromExtension;
        } else {
            return dataviewLoader.getResource(name);
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final List<URL> urls = new ArrayList<>();
        Iterators.addAll(urls, Iterators.forEnumeration(super.getResources(name)));
        Iterators.addAll(urls, Iterators.forEnumeration(dataviewLoader.getResources(name)));
        return Iterators.asEnumeration(urls.iterator());
    }
}

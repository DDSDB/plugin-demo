package com.bonree.dataview.plugin;

import com.google.common.collect.*;
import io.airlift.resolver.ArtifactResolver;
import io.airlift.resolver.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class PluginInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(PluginInitializer.class);

    public static <T> Set<T> loadPlugins(Class<T> serviceCls, PluginConfig config) {
        return Streams.concat(buildPluginClassLoaderFromDirectory(config).stream(),
                buildPluginClassLoaderFromBundle(config).stream())
                .map(classLoader -> loadPluginsFromClassLoader(classLoader, serviceCls)).flatMap(Set::stream)
                .collect(ImmutableSet.toImmutableSet());
    }

    private static List<ClassLoader> buildPluginClassLoaderFromDirectory(PluginConfig config) {
        Path pluginRoot = config.getPluginDir();
        return config.getLoadList().stream().map(pluginRoot::resolve).map(Path::toFile).map(dir -> {
            LOG.info("loading plugin from [{}]", dir);
            try {
                return buildClassLoaderFromDirectory(dir);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(ImmutableList.toImmutableList());
    }

    private static List<ClassLoader> buildPluginClassLoaderFromBundle(PluginConfig config) {
        ArtifactResolver resolver = new ArtifactResolver(
                config.getMavenLocalRepository(),
                config.getMavenRemoteRepository());
        return config.getPluginBundles()
                .stream()
                .map(plugin -> {
                    LOG.info("loading plugin from [{}].", plugin);
                    try {
                        return buildClassLoader(plugin, resolver);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(ImmutableList.toImmutableList());

    }

    private static <T> Set<T> loadPluginsFromClassLoader(ClassLoader classLoader, Class<T> serviceCls) {
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        ServiceLoader.load(serviceCls, classLoader).forEach(builder::add);

        ImmutableSet<T> plugins = builder.build();
        LOG.info("load plugins: [{}]", Iterables.transform(plugins, Object::getClass));

        return plugins;
    }

    private static URLClassLoader buildClassLoader(String plugin, ArtifactResolver resolver) throws Exception {
        File file = new File(plugin);
        if (file.isFile() && (file.getName().equals("pom.xml") || file.getName().endsWith(".pom"))) {
            return buildClassLoaderFromPom(file, resolver);
        }
        if (file.isDirectory()) {
            return buildClassLoaderFromDirectory(file);
        }
        return buildClassLoaderFromCoordinates(plugin, resolver);
    }

    private static URLClassLoader buildClassLoaderFromDirectory(File dir) throws Exception {
        LOG.info("Classpath for [{}].", dir.getName());
        List<URL> urls = new ArrayList<>();
        for (File file : listFiles(dir)) {
            urls.add(file.toURI().toURL());
        }
        return createClassLoader(urls);
    }

    private static URLClassLoader buildClassLoaderFromCoordinates(
            String coordinates,
            ArtifactResolver resolver) throws Exception {
        DefaultArtifact rootArtifact = new DefaultArtifact(coordinates);
        List<Artifact> artifacts = resolver.resolveArtifacts(rootArtifact);
        return createClassLoader(artifacts, rootArtifact.toString());
    }

    private static URLClassLoader buildClassLoaderFromPom(File pomFile, ArtifactResolver resolver) throws Exception {
        List<Artifact> artifacts = resolver.resolvePom(pomFile);
        URLClassLoader classLoader = createClassLoader(artifacts, pomFile.getPath());

        Artifact artifact = artifacts.get(0);
        Set<String> plugins = PluginDiscovery.discoveryPlugins(artifact, classLoader);
        if (plugins.isEmpty()) {
            PluginDiscovery.writePluginServices(plugins, artifact.getFile());
        }
        return classLoader;
    }

    private static URLClassLoader createClassLoader(List<Artifact> artifacts, String name) throws IOException {
        List<URL> urls = new ArrayList<>();
        for (Artifact artifact : sortedArtifacts(artifacts)) {
            if (artifact.getFile() == null) {
                throw new RuntimeException("Could not resolve artifact: " + artifact);
            }
            File file = artifact.getFile().getCanonicalFile();
            urls.add(file.toURI().toURL());
        }
        return createClassLoader(urls);
    }

    private static URLClassLoader createClassLoader(List<URL> urls) {
        return new PluginClassLoader(urls, PluginInitializer.class.getClassLoader());
    }

    private static List<Artifact> sortedArtifacts(List<Artifact> artifacts) {
        List<Artifact> list = new ArrayList<>(artifacts);
        Collections.sort(list, Ordering.natural().nullsLast().onResultOf(Artifact::getFile));
        return list;
    }


    private static List<File> listFiles(File installedPluginsDir) {
        if (installedPluginsDir != null && installedPluginsDir.isDirectory()) {
            File[] files = installedPluginsDir.listFiles();
            if (files != null) {
                Arrays.sort(files);
                return ImmutableList.copyOf(files);
            }
        }
        return ImmutableList.of();
    }

    private PluginInitializer() {

    }
}

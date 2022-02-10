package com.bonree.dataview.plugin;

import com.google.common.collect.ImmutableList;
import io.airlift.resolver.ArtifactResolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PluginConfig {
    private String pluginDir = "plugins";

    private List<String> loadList = ImmutableList.of();

    private List<String> pluginBundles = ImmutableList.of();

    private String mavenLocalRepository = ArtifactResolver.USER_LOCAL_REPO;

    private List<String> mavenRemoteRepository = ImmutableList.of(ArtifactResolver.MAVEN_CENTRAL_URI);

    public Path getPluginDir() {
        return Paths.get(pluginDir);
    }

    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    public List<String> getLoadList() {
        return loadList;
    }

    public void setLoadList(List<String> loadList) {
        this.loadList = loadList;
    }

    public String getMavenLocalRepository() {
        return mavenLocalRepository;
    }

    public void setMavenLocalRepository(String mavenLocalRepository) {
        this.mavenLocalRepository = mavenLocalRepository;
    }

    public List<String> getMavenRemoteRepository() {
        return mavenRemoteRepository;
    }

    public void setMavenRemoteRepository(List<String> mavenRemoteRepository) {
        this.mavenRemoteRepository = mavenRemoteRepository;
    }

    public List<String> getPluginBundles() {
        return pluginBundles;
    }

    public void setPluginBundles(List<String> pluginBundles) {
        this.pluginBundles = pluginBundles;
    }
}

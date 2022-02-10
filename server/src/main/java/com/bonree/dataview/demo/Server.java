package com.bonree.dataview.demo;

import com.bonree.dataview.plugin.PluginConfig;
import com.bonree.dataview.plugin.PluginInitializer;
import com.google.common.collect.Lists;

import java.util.Set;

public class Server {
    public static void main(String[] args) {
        PluginConfig config = new PluginConfig();
        config.setPluginDir("D:\\project\\test\\plugins\\plugin");
//        config.setPluginDir("D:\\project\\out-plugin");
        config.setLoadList(Lists.newArrayList("target"));
        Set<Greeting> greetings = PluginInitializer.loadPlugins(Greeting.class, config);
        for (Greeting greeting : greetings) {
            System.out.println(greeting.getGreeting());
        }

    }
}

package com.bonree.dataview.demo;

public class GreetingDemo implements Greeting{
    @Override
    public String getGreeting() {
        System.out.println("start greeting demo");
        return "GreetingDemo say hello;";
    }
}

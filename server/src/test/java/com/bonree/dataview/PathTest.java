package com.bonree.dataview;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathTest {

    @Test
    public void testResolvePath() {
        ArrayList<String> loadList = Lists.newArrayList("target");
        Path path = Paths.get("D:\\project\\test\\server");
        ImmutableList<Path> collect = loadList.stream().map(path::resolve).collect(ImmutableList.toImmutableList());
        for (Path footPrint : collect) {
            System.out.println(footPrint);
        }
    }
}

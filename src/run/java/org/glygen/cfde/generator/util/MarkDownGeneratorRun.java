package org.glygen.cfde.generator.util;

import java.io.IOException;
import java.util.HashMap;

public class MarkDownGeneratorRun
{

    public static void main(String[] args) throws IOException
    {
        MarkDownGenerator t_generator = new MarkDownGenerator();

        HashMap<String, String> t_glycans = new HashMap<>();
        t_glycans.put("G1", "G1");
        t_glycans.put("G2", "123");
        t_generator.writeGlycanFile("./data/mapping/glycan.template.md", t_glycans, "./test.out");
    }

}

package org.glygen.cfde.generator.csv;

import java.io.IOException;

public class MetadataHandlerRun
{

    public static void main(String[] args) throws IOException
    {
        String[] t_heading = { "", "1", "uniprotkb_canonical_ac" };
        MetadataHandler a = MetadataHandler.fromString(
                "file:uniprotkb_canonical_ac:uniprotkb_canonical_ac2ensembl.csv", t_heading,
                "./data/mapping/");
        System.out.println(a);
    }

}

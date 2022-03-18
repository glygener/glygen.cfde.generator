package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class CollectionSubstanceFile extends TSVFile
{
    public CollectionSubstanceFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id",
                "substance" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_substance.tsv");
    }
}

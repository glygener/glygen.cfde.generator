package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class BiosampleInCollectionFile extends TSVFile
{
    public BiosampleInCollectionFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "biosample_id_namespace", "biosample_local_id",
                "collection_id_namespace", "collection_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "biosample_in_collection.tsv");
    }
}

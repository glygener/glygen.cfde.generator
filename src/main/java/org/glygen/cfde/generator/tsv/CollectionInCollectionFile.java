package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class CollectionInCollectionFile extends TSVFile
{
    public CollectionInCollectionFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "superset_collection_id_namespace",
                "superset_collection_local_id", "subset_collection_id_namespace",
                "subset_collection_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_in_collection.tsv");
    }

}

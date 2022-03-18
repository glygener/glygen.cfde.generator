package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class FileInCollectionFile extends TSVFile
{
    public FileInCollectionFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "file_id_namespace", "file_local_id",
                "collection_id_namespace", "collection_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "file_in_collection.tsv");
    }

}

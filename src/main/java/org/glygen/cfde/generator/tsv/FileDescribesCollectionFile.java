package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class FileDescribesCollectionFile extends TSVFile
{
    public FileDescribesCollectionFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "file_id_namespace", "file_local_id",
                "collection_id_namespace", "collection_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "file_describes_collection.tsv");
    }

    public void write(String a_collectionID, String a_fileID)
    {
        String[] t_line = new String[4];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_fileID);
        t_line[2] = this.addString(this.m_namespace);
        t_line[3] = this.addString(a_collectionID);
        this.m_csvWriter.writeNext(t_line);
    }

}

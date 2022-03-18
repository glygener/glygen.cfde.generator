package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class FileDescribesBiosampleFile extends TSVFile
{
    public FileDescribesBiosampleFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "file_id_namespace", "file_local_id",
                "biosample_id_namespace", "biosample_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "file_describes_biosample.tsv");
    }

}

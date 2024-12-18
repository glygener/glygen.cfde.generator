package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class BiosampleFile extends TSVFile
{
    public BiosampleFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "id_namespace", "local_id", "project_id_namespace",
                "project_local_id", "persistent_id", "creation_time", "sample_prep_method",
                "anatomy", "biofluid" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "biosample.tsv");
    }
}

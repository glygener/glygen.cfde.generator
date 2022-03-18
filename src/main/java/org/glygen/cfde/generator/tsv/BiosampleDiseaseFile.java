package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class BiosampleDiseaseFile extends TSVFile
{
    public BiosampleDiseaseFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "biosample_id_namespace", "biosample_local_id",
                "association_type", "disease" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "biosample_disease.tsv");
    }
}

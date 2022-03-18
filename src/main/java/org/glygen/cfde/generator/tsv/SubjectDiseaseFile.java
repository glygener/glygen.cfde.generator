package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class SubjectDiseaseFile extends TSVFile
{
    public SubjectDiseaseFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "subject_id_namespace", "subject_local_id",
                "association_type", "disease" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "subject_disease.tsv");
    }
}

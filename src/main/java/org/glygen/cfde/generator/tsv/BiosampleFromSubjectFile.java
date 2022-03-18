package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class BiosampleFromSubjectFile extends TSVFile
{
    public BiosampleFromSubjectFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "biosample_id_namespace", "biosample_local_id",
                "subject_id_namespace", "subject_local_id", "age_at_sampling" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "biosample_from_subject.tsv");
    }
}

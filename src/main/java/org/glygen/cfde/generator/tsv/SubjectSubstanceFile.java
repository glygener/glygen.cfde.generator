package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class SubjectSubstanceFile extends TSVFile
{
    public SubjectSubstanceFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "subject_id_namespace", "subject_local_id", "substance" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "subject_substance.tsv");
    }
}

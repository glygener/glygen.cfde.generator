package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class SubjectRaceFile extends TSVFile
{
    public SubjectRaceFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "subject_id_namespace", "subject_local_id", "race" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "subject_race.tsv");
    }
}

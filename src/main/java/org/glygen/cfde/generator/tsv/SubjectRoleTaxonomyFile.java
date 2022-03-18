package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class SubjectRoleTaxonomyFile extends TSVFile
{
    public SubjectRoleTaxonomyFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "subject_id_namespace", "subject_local_id", "role_id",
                "taxonomy_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "subject_role_taxonomy.tsv");
    }
}

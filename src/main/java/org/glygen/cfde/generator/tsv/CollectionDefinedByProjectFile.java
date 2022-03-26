package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class CollectionDefinedByProjectFile extends TSVFile
{
    public CollectionDefinedByProjectFile(String a_folderPath, String a_namespace)
            throws IOException
    {
        super();
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id",
                "project_id_namespace", "project_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_defined_by_project.tsv");
    }

    public void write(String a_collectionID, String a_projectID)
    {
        String[] t_line = new String[4];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_collectionID);
        t_line[2] = this.addString(this.m_namespace);
        t_line[3] = this.addString(a_projectID);
        this.m_csvWriter.writeNext(t_line);
    }

}

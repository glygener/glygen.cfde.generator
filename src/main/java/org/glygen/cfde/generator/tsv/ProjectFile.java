package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.om.Project;

public class ProjectFile extends TSVFile
{
    public ProjectFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "id_namespace", "local_id", "persistent_id", "creation_time",
                "abbreviation", "name", "description" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "project.tsv");
    }

    public void write(Project a_project)
    {
        String[] t_line = new String[7];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_project.getId());
        t_line[2] = this.addString(a_project.getPersistent());
        t_line[3] = this.addDate(a_project.getCreationTime());
        t_line[4] = this.addString(a_project.getAbbr());
        t_line[5] = this.addString(a_project.getName());
        t_line[6] = this.addString(a_project.getDescription());
        this.m_csvWriter.writeNext(t_line);
    }

}

package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.om.Project;

public class ProjectInProjectFile extends TSVFile
{
    public ProjectInProjectFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "parent_project_id_namespace", "parent_project_local_id",
                "child_project_id_namespace", "child_project_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "project_in_project.tsv");
    }

    public void write(Project a_parent, Project a_child)
    {
        String[] t_line = new String[4];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_parent.getId());
        t_line[2] = this.addString(this.m_namespace);
        t_line[3] = this.addString(a_child.getId());
        this.m_csvWriter.writeNext(t_line);
    }

}

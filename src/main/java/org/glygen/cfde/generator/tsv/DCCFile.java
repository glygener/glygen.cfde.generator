package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.om.DCC;
import org.glygen.cfde.generator.om.Project;

public class DCCFile extends TSVFile
{
    public DCCFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "id", "dcc_name", "dcc_abbreviation", "dcc_description",
                "contact_email", "contact_name", "dcc_url", "project_id_namespace",
                "project_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "dcc.tsv");
    }

    public void write(DCC a_dcc, Project a_rootProject)
    {
        String[] t_line = new String[9];
        t_line[0] = this.addString(a_dcc.getId());
        t_line[1] = this.addString(a_dcc.getName());
        t_line[2] = this.addString(a_dcc.getAbbr());
        t_line[3] = this.addString(a_dcc.getDescription());
        t_line[4] = this.addString(a_dcc.getEmail());
        t_line[5] = this.addString(a_dcc.getContact());
        t_line[6] = this.addString(a_dcc.getUrl());
        t_line[7] = this.addString(this.m_namespace);
        t_line[8] = this.addString(a_rootProject.getId());
        this.m_csvWriter.writeNext(t_line);
    }

}

package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.om.Namespace;

public class IdNamespaceFile extends TSVFile
{
    public IdNamespaceFile(String a_folderPath) throws IOException
    {
        super();
        this.m_header = new String[] { "id", "abbreviation", "name", "description" };
        this.openFile(a_folderPath + File.separator + "id_namespace.tsv");
    }

    public void write(Namespace a_namespace)
    {
        String[] t_line = new String[4];
        t_line[0] = this.addString(a_namespace.getId());
        t_line[1] = this.addString(a_namespace.getAbbr());
        t_line[2] = this.addString(a_namespace.getName());
        t_line[3] = this.addString(a_namespace.getDescription());
        this.m_csvWriter.writeNext(t_line);
    }
}

package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class DomainLocationFile extends TSVFile
{
    public DomainLocationFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "id", "name", "description", "synonyms" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "domain_location.tsv");
    }

}

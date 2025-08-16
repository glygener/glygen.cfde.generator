package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class PtmSubTypeFile extends TSVFile
{
    public PtmSubTypeFile(String a_folderPath) throws IOException
    {
        super();
        this.m_header = new String[] { "id", "name", "description", "synonyms" };
        this.openFile(a_folderPath + File.separator + "ptm_subtype.tsv");
    }

    public void write(String a_id, String a_name, String a_description, String a_synonyms)
    {
        String[] t_line = new String[4];
        t_line[0] = this.addString(a_id);
        t_line[1] = this.addString(a_name);
        t_line[2] = this.addString(a_description);
        t_line[3] = this.addString(a_synonyms);
        this.m_csvWriter.writeNext(t_line);
    }

}

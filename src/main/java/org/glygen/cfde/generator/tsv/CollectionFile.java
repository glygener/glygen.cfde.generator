package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class CollectionFile extends TSVFile
{
    public CollectionFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "id_namespace", "local_id", "persistent_id", "creation_time",
                "abbreviation", "name", "description", "has_time_series_data" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection.tsv");
    }

    public void write(String a_localId, String a_name, String a_description)
    {
        String[] t_line = new String[8];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_localId);
        t_line[2] = this.addString("");
        t_line[3] = this.addDate(new Date());
        t_line[4] = this.addString("");
        t_line[5] = this.addString(a_name);
        t_line[6] = this.addString(a_description);
        t_line[7] = this.addString("");
        this.m_csvWriter.writeNext(t_line);
    }

}

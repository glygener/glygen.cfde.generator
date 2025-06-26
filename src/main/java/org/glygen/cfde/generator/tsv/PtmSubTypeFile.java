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

        // static file entries
        this.write("GO:0006486", "protein glycosylation",
                "A protein modification process that results in the addition of a carbohydrate or carbohydrate derivative unit to a protein amino acid, e.g., the addition of glycan chains to proteins. protein phosphorylation – The process of introducing a phosphate group onto a protein.",
                null);
        this.write("GO:0006468", "protein phosphorylation",
                "The process of introducing a phosphate group on to a protein.", null);
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

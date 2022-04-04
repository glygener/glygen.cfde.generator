package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.csv.CSVError;

public class CollectionAnatomyFile extends TSVFile
{
    private CSVError m_errorFile = null;

    public CollectionAnatomyFile(String a_folderPath, String a_namespace, CSVError a_errorFile)
            throws IOException
    {
        super();
        this.m_errorFile = a_errorFile;
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id",
                "anatomy" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_anatomy.tsv");
    }

    public void write(String a_collectionID, String a_uberonID)
    {
        String[] t_line = new String[3];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_collectionID);
        if (a_uberonID.startsWith("UBERON_"))
        {
            String t_uberon = a_uberonID.replace("UBERON_", "UBERON:");
            this.m_errorFile.writeWarning(a_collectionID, "Fixed UBERON ID",
                    a_uberonID + " to " + t_uberon);
            t_line[2] = this.addString(t_uberon);
        }
        else
        {
            t_line[2] = this.addString(a_uberonID);
        }
        this.m_csvWriter.writeNext(t_line);
    }

}

package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.csv.CSVError;

public class CollectionProteinFile extends TSVFile
{
    private CSVError m_errorFile = null;

    public CollectionProteinFile(String a_folderPath, String a_namespace, CSVError a_errorFile)
            throws IOException
    {
        super();
        this.m_errorFile = a_errorFile;
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id",
                "protein" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_protein.tsv");
    }

    public void write(String a_collectionID, String a_uniprotID)
    {
        String[] t_line = new String[3];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_collectionID);
        if (a_uniprotID.contains("-"))
        {
            String t_id = a_uniprotID.substring(0, a_uniprotID.indexOf("-"));
            this.m_errorFile
                    .writeWarning("Fixed UniProt accession from " + a_uniprotID + " to " + t_id);
            t_line[2] = this.addString(t_id);
        }
        else
        {
            t_line[2] = this.addString(a_uniprotID);
        }
        this.m_csvWriter.writeNext(t_line);
    }

}

package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.csv.CSVError;

public class CollectionTaxonomyFile extends TSVFile
{
    private CSVError m_errorFile = null;

    public CollectionTaxonomyFile(String a_folderPath, String a_namespace, CSVError a_errorFile)
            throws IOException
    {
        super();
        this.m_errorFile = a_errorFile;
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id", "taxon" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_taxonomy.tsv");
    }

    public void write(String a_collectionID, String a_taxon)
    {
        String[] t_line = new String[3];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_collectionID);
        if (a_taxon.startsWith("NCBI:txid"))
        {
            t_line[2] = this.addString(a_taxon);
        }
        else
        {
            this.m_errorFile.writeWarning("Incorrect taxon (" + a_collectionID + "): " + a_taxon);
            t_line[2] = this.addString("NCBI:txid" + a_taxon);
        }
        this.m_csvWriter.writeNext(t_line);
    }

}

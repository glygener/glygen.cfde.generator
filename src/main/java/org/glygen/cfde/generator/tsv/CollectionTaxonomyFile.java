package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

public class CollectionTaxonomyFile extends TSVFile
{
    public CollectionTaxonomyFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id", "taxon" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_taxonomy.tsv");
    }

    public void write(String a_collectionID, String a_taxon)
    {
        String[] t_line = new String[3];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_collectionID);
        t_line[2] = this.addString(a_taxon);
        this.m_csvWriter.writeNext(t_line);
    }

}

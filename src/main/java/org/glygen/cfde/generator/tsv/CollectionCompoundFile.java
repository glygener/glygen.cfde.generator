package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.glygen.cfde.generator.csv.MetadataHandler;

public class CollectionCompoundFile extends TSVFile
{
    private static final String GLYTOUCAN_PUBCHEM_MAPPING_FILE = "glytoucan2pubchem.csv";

    private HashMap<String, String> m_glyTouCanPubChemMapping = new HashMap<>();

    public CollectionCompoundFile(String a_folderPath, String a_namespace, String a_mappingFolder)
            throws IOException
    {
        super();
        this.m_header = new String[] { "collection_id_namespace", "collection_local_id",
                "compound" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "collection_compound.tsv");
        // load GlyTouCan to PubChem mapping
        this.m_glyTouCanPubChemMapping = MetadataHandler.loadMappingFile(a_mappingFolder,
                CollectionCompoundFile.GLYTOUCAN_PUBCHEM_MAPPING_FILE);
    }

    public void write(String a_collectionID, String a_compoundID)
    {
        String[] t_line = new String[3];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_collectionID);
        String t_pubchemID = this.m_glyTouCanPubChemMapping.get(a_compoundID);
        if (t_pubchemID == null)
        {
            t_line[2] = this.addString(a_compoundID);
        }
        else
        {
            t_line[2] = this.addString(t_pubchemID);
        }
        this.m_csvWriter.writeNext(t_line);
    }

}

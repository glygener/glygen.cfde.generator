package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.glygen.cfde.generator.csv.CSVError;
import org.glygen.cfde.generator.csv.MetadataHandler;

public class PtmFile extends TSVFile
{
    private static final String AMINO_ACID_MAPPING_FILE = "amino_acid_mapping.csv";

    private HashMap<String, String> m_aminoAcidMapping = new HashMap<>();

    public PtmFile(String a_folderPath, String a_namespace, String a_mappingFolder)
            throws IOException
    {
        super();
        this.m_header = new String[] { "id", "protein", "site_one", "aa_site_one", "site_two",
                "aa_site_two", "site_type", "ptm_type", "ptm_subtype", "domain_location",
                "domain_type" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "ptm.tsv");
        // load mapping
        this.m_aminoAcidMapping = MetadataHandler.loadMappingFile(a_mappingFolder,
                PtmFile.AMINO_ACID_MAPPING_FILE);
    }

    public boolean write(String a_localId, String a_protein, String a_siteOne, String a_siteOneAA,
            String a_siteTwo, String a_siteTwoAA, String a_siteType, String a_ptmType,
            String a_ptmSubType, CSVError a_errorlog)
    {
        String[] t_line = new String[11];
        t_line[0] = this.addString(a_localId);
        t_line[1] = this.addString(a_protein);
        t_line[2] = this.addString(a_siteOne);
        String t_aa = this.getAA(a_siteOneAA);
        if (t_aa == null)
        {
            a_errorlog.writeError("Invalid AA found", "Protein " + a_protein
                    + " has invalid site one AA not found in mapping file: " + a_siteOneAA);
            return false;
        }
        t_line[3] = this.addString(t_aa);
        t_line[4] = this.addString(a_siteTwo);
        t_aa = this.getAA(a_siteTwoAA);
        if (t_aa == null)
        {
            a_errorlog.writeError("Invalid AA found", "Protein " + a_protein
                    + " has invalid site two AA not found in mapping file: " + a_siteOneAA);
            return false;
        }
        t_line[5] = this.addString(t_aa);
        t_line[6] = this.addString(a_siteType);
        t_line[7] = this.addString(a_ptmType);
        t_line[8] = this.addString(a_ptmSubType);
        t_line[9] = this.addString("");
        t_line[10] = this.addString("");
        this.m_csvWriter.writeNext(t_line);
        return true;
    }

    private String getAA(String a_siteAA)
    {
        if (a_siteAA == "")
        {
            return a_siteAA;
        }
        return this.m_aminoAcidMapping.get(a_siteAA);
    }

}

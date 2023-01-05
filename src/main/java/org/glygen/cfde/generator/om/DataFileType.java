package org.glygen.cfde.generator.om;

public enum DataFileType
{
    GLYGEN_PROTEIN_DATA("glygen_protein_data"), GLYGEN_GLYCAN_DATA(
            "glygen_glycan_data"), GLYGEN_PROTEIN_NO_GENE_DATA(
                    "glygen_protein_no_gene_data"), GLYGEN_PROTEIN_GLYCAN_MIX_DATA(
                            "glygen_protein_glycan_mix_data"), GLYGEN_PROTEIN_GLYCAN_MIX_NO_GENE_DATA("glygen_protein_glycan_mix_no_gene_data");

    private String m_key;

    /** Private constructor, see the forName methods for external use. */
    private DataFileType(String a_key)
    {
        this.m_key = a_key;
    }

    public String getKey()
    {
        return this.m_key;
    }

    /**
     * Returns the appropriate object instance for the given key.
     */
    public static DataFileType forString(String a_key)
    {
        for (DataFileType a : DataFileType.values())
        {
            if (a_key.equalsIgnoreCase(a.m_key))
            {
                return a;
            }
        }
        return null;
    }

}

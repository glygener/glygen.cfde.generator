package org.glygen.cfde.generator.csv;

public enum MetadataType
{
    COLUMN("column"), STATIC("static"), MAPPING("file");

    private String m_key;

    /** Private constructor, see the forName methods for external use. */
    private MetadataType(String a_key)
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
    public static MetadataType forString(String a_key)
    {
        for (MetadataType a : MetadataType.values())
        {
            if (a_key.equalsIgnoreCase(a.m_key))
            {
                return a;
            }
        }
        return null;
    }

}

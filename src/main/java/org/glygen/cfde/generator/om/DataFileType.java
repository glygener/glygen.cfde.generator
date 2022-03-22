package org.glygen.cfde.generator.om;

public enum DataFileType
{
    GLYGEN_DATA("glygen_data"), SOMETHING_ELSE("something_else");

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

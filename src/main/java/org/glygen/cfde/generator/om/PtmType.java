package org.glygen.cfde.generator.om;

public enum PtmType
{
    GLYCOSYLATION("glycosylation", "glyco"), PHOSPHORYLATION("phosphorylation", "phospho");

    private String m_key;
    private String m_short;

    /** Private constructor, see the forName methods for external use. */
    private PtmType(String a_key, String a_short)
    {
        this.m_key = a_key;
        this.m_short = a_short;
    }

    public String getKey()
    {
        return this.m_key;
    }

    /**
     * Returns the appropriate object instance for the given key.
     */
    public static PtmType forString(String a_key)
    {
        for (PtmType a : PtmType.values())
        {
            if (a_key.equalsIgnoreCase(a.m_key))
            {
                return a;
            }
        }
        return null;
    }

    public String getShort()
    {
        return m_short;
    }

}

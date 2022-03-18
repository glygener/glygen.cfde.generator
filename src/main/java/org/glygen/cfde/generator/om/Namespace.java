package org.glygen.cfde.generator.om;

public class Namespace
{
    private String m_id = null;
    private String m_abbr = null;
    private String m_name = null;
    private String m_description = null;

    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    public String getAbbr()
    {
        return this.m_abbr;
    }

    public void setAbbr(String a_abbr)
    {
        this.m_abbr = a_abbr;
    }

    public String getName()
    {
        return this.m_name;
    }

    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

    public String getDescription()
    {
        return this.m_description;
    }

    public void setDescription(String a_description)
    {
        this.m_description = a_description;
    }
}

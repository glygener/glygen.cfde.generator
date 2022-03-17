package org.glygen.cfde.generator.om;

import java.util.Date;

public class Project
{
    private String m_namespace = null;
    private String m_id = null;
    private String m_persistent = null;
    private Date m_creationTime = null;
    private String m_abbr = null;
    private String m_name = null;
    private String m_description = null;

    public String getNamespace()
    {
        return this.m_namespace;
    }

    public void setNamespace(String a_namespace)
    {
        this.m_namespace = a_namespace;
    }

    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    public String getPersistent()
    {
        return this.m_persistent;
    }

    public void setPersistent(String a_persistent)
    {
        this.m_persistent = a_persistent;
    }

    public Date getCreationTime()
    {
        return this.m_creationTime;
    }

    public void setCreationTime(Date a_creationTime)
    {
        this.m_creationTime = a_creationTime;
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

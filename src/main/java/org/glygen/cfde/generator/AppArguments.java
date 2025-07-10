package org.glygen.cfde.generator;

public class AppArguments
{
    private String m_configFile = null;
    private String m_outputFolder = null;
    private String m_propertiesFile = null;
    private String m_mappingFolder = null;
    private boolean m_writeGeneLess = false;
    private boolean m_excludeArray = false;

    public String getConfigFile()
    {
        return this.m_configFile;
    }

    public void setConfigFile(String a_configFile)
    {
        this.m_configFile = a_configFile;
    }

    public String getOutputFolder()
    {
        return this.m_outputFolder;
    }

    public void setOutputFolder(String a_outputFolder)
    {
        this.m_outputFolder = a_outputFolder;
    }

    public String getPropertiesFile()
    {
        return this.m_propertiesFile;
    }

    public void setPropertiesFile(String a_propertiesFile)
    {
        this.m_propertiesFile = a_propertiesFile;
    }

    public String getMappingFolder()
    {
        return this.m_mappingFolder;
    }

    public void setMappingFolder(String a_mappingFolder)
    {
        this.m_mappingFolder = a_mappingFolder;
    }

    public boolean isWriteGeneLess()
    {
        return this.m_writeGeneLess;
    }

    public void setWriteGeneLess(boolean a_writeGeneLess)
    {
        this.m_writeGeneLess = a_writeGeneLess;
    }

    public boolean isExcludeArray()
    {
        return m_excludeArray;
    }

    public void setExcludeArray(boolean a_excludeArray)
    {
        this.m_excludeArray = a_excludeArray;
    }

}

package org.glygen.cfde.generator;

public class AppArguments
{
    private String m_configFile = null;
    private String m_outputFolder = null;
    private String m_propertiesFile = null;
    private String m_mappingFolder = null;

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
        return m_mappingFolder;
    }

    public void setMappingFolder(String a_mappingFolder)
    {
        this.m_mappingFolder = a_mappingFolder;
    }

}

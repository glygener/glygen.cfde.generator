package org.glygen.cfde.generator.om;

import java.util.Date;

public class CFDEFile
{
    private String m_id = null;
    private String m_persistentId = null;
    private Date m_creationTime = null;
    private Long m_fileSize = null;
    private String m_md5 = null;
    private String m_sha256 = null;
    private String m_filename = null;
    private String m_fileFormat = null;
    private String m_dataType = null;
    private String m_mimeType = null;
    private String m_assayType = null;
    private String m_analysisType = null;
    private String m_accessUrl = null;

    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    public String getPersistentId()
    {
        return this.m_persistentId;
    }

    public void setPersistentId(String a_persistentId)
    {
        this.m_persistentId = a_persistentId;
    }

    public Date getCreationTime()
    {
        return this.m_creationTime;
    }

    public void setCreationTime(Date a_creationTime)
    {
        this.m_creationTime = a_creationTime;
    }

    public Long getFileSize()
    {
        return this.m_fileSize;
    }

    public void setFileSize(Long a_fileSize)
    {
        this.m_fileSize = a_fileSize;
    }

    public String getMd5()
    {
        return this.m_md5;
    }

    public void setMd5(String a_md5)
    {
        this.m_md5 = a_md5;
    }

    public String getSha256()
    {
        return this.m_sha256;
    }

    public void setSha256(String a_sha256)
    {
        this.m_sha256 = a_sha256;
    }

    public String getFilename()
    {
        return this.m_filename;
    }

    public void setFilename(String a_filename)
    {
        this.m_filename = a_filename;
    }

    public String getFileFormat()
    {
        return this.m_fileFormat;
    }

    public void setFileFormat(String a_fileFormat)
    {
        this.m_fileFormat = a_fileFormat;
    }

    public String getDataType()
    {
        return this.m_dataType;
    }

    public void setDataType(String a_dataType)
    {
        this.m_dataType = a_dataType;
    }

    public String getMimeType()
    {
        return this.m_mimeType;
    }

    public void setMimeType(String a_mimeType)
    {
        this.m_mimeType = a_mimeType;
    }

    public String getAssayType()
    {
        return this.m_assayType;
    }

    public void setAssayType(String a_assayType)
    {
        this.m_assayType = a_assayType;
    }

    public String getAnalysisType()
    {
        return this.m_analysisType;
    }

    public void setAnalysisType(String a_analysisType)
    {
        this.m_analysisType = a_analysisType;
    }

    public String getAccessUrl()
    {
        return this.m_accessUrl;
    }

    public void setAccessUrl(String a_accessUrl)
    {
        this.m_accessUrl = a_accessUrl;
    }

}

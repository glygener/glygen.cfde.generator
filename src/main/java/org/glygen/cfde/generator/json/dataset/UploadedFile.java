package org.glygen.cfde.generator.json.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadedFile
{
    private String m_id = null;
    private String m_filename = null;
    private String m_format = null;
    private String m_fileFolder = null;

    @JsonProperty("fileFolder")
    public String getFileFolder()
    {
        return this.m_fileFolder;
    }

    public void setFileFolder(String a_fileFolder)
    {
        this.m_fileFolder = a_fileFolder;
    }

    @JsonProperty("identifier")
    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    @JsonProperty("originalName")
    public String getFilename()
    {
        return this.m_filename;
    }

    public void setFilename(String a_filename)
    {
        this.m_filename = a_filename;
    }

    @JsonProperty("fileFormat")
    public String getFormat()
    {
        return this.m_format;
    }

    public void setFormat(String a_format)
    {
        this.m_format = a_format;
    }
}

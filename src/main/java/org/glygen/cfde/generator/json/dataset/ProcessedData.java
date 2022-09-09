package org.glygen.cfde.generator.json.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedData
{
    private String m_id = null;
    private UploadedFile m_file = null;

    @JsonProperty("id")
    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    @JsonProperty("file")
    public UploadedFile getFile()
    {
        return this.m_file;
    }

    public void setFile(UploadedFile a_file)
    {
        this.m_file = a_file;
    }
}

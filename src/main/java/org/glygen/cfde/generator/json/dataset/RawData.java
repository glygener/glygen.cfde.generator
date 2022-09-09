package org.glygen.cfde.generator.json.dataset;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawData
{
    private String m_id = null;
    private UploadedFile m_file = null;
    private List<ProcessedData> m_processedData = new ArrayList<>();

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

    @JsonProperty("processedDataList")
    public List<ProcessedData> getProcessedData()
    {
        return this.m_processedData;
    }

    public void setProcessedData(List<ProcessedData> a_processedData)
    {
        this.m_processedData = a_processedData;
    }
}

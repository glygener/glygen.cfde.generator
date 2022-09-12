package org.glygen.cfde.generator.json.dataset;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset
{
    private String m_id = null;
    private Sample m_sample = null;
    private List<Slide> m_slides = new ArrayList<>();
    private String m_name = null;
    private String m_description = null;
    private String m_uri = null;
    private String m_creationDate = null;

    @JsonProperty("sample")
    public Sample getSample()
    {
        return this.m_sample;
    }

    public void setSample(Sample a_sample)
    {
        this.m_sample = a_sample;
    }

    @JsonProperty("slides")
    public List<Slide> getSlides()
    {
        return this.m_slides;
    }

    public void setSlides(List<Slide> a_slides)
    {
        this.m_slides = a_slides;
    }

    @JsonProperty("id")
    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    @JsonProperty("name")
    public String getName()
    {
        return this.m_name;
    }

    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

    @JsonProperty("description")
    public String getDescription()
    {
        return this.m_description;
    }

    public void setDescription(String a_description)
    {
        this.m_description = a_description;
    }

    @JsonProperty("uri")
    public String getUri()
    {
        return this.m_uri;
    }

    public void setUri(String a_uri)
    {
        this.m_uri = a_uri;
    }

    @JsonProperty("dateCreated")
    public String getCreationDate()
    {
        return this.m_creationDate;
    }

    public void setCreationDate(String a_creationDate)
    {
        this.m_creationDate = a_creationDate;
    }
}

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
}

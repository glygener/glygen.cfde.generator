package org.glygen.cfde.generator.json.glycanlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Glycan
{
    private String m_glyTouCanId = null;

    @JsonProperty("glytoucanId")
    public String getGlyTouCanId()
    {
        return this.m_glyTouCanId;
    }

    public void setGlyTouCanId(String a_glyTouCanId)
    {
        this.m_glyTouCanId = a_glyTouCanId;
    }
}

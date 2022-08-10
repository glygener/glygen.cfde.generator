package org.glygen.cfde.generator.json.glycan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Component
{
    private String m_residue = null;
    private Integer m_count = null;

    @JsonProperty("residue")
    public String getResidue()
    {
        return this.m_residue;
    }

    public void setResidue(String a_residue)
    {
        this.m_residue = a_residue;
    }

    @JsonProperty("count")
    public Integer getCount()
    {
        return this.m_count;
    }

    public void setCount(Integer a_count)
    {
        this.m_count = a_count;
    }
}

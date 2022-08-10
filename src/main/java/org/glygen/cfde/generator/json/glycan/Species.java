package org.glygen.cfde.generator.json.glycan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Species
{
    private String m_name = null;
    private Integer m_taxId = null;

    @JsonProperty("name")
    public String getName()
    {
        return this.m_name;
    }

    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

    @JsonProperty("taxid")
    public Integer getTaxId()
    {
        return this.m_taxId;
    }

    public void setTaxId(Integer a_taxId)
    {
        this.m_taxId = a_taxId;
    }
}

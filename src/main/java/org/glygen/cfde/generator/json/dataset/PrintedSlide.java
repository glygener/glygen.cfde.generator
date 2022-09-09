package org.glygen.cfde.generator.json.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintedSlide
{
    private String m_id = null;
    private String m_name = null;
    private Layout m_layout = null;

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

    @JsonProperty("layout")
    public Layout getLayout()
    {
        return this.m_layout;
    }

    public void setLayout(Layout a_layout)
    {
        this.m_layout = a_layout;
    }
}

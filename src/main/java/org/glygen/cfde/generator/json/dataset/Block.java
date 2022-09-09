package org.glygen.cfde.generator.json.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Block
{
    private String m_id = null;
    private BlockLayout m_layout = null;

    @JsonProperty("id")
    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    @JsonProperty("blockLayout")
    public BlockLayout getLayout()
    {
        return this.m_layout;
    }

    public void setLayout(BlockLayout a_layout)
    {
        this.m_layout = a_layout;
    }

}

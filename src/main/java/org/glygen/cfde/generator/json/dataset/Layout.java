package org.glygen.cfde.generator.json.dataset;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Layout
{
    private String m_id = null;
    private String m_name = null;
    private List<Block> m_blocks = new ArrayList<>();

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

    @JsonProperty("blocks")
    public List<Block> getBlocks()
    {
        return this.m_blocks;
    }

    public void setBlocks(List<Block> a_blocks)
    {
        this.m_blocks = a_blocks;
    }
}

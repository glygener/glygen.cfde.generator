package org.glygen.cfde.generator.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkDownEntry
{
    private String m_id = null;
    private String m_markdown = null;

    @JsonProperty("id")
    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    @JsonProperty("resource_markdown")
    public String getMarkdown()
    {
        return this.m_markdown;
    }

    public void setMarkdown(String a_markdown)
    {
        this.m_markdown = a_markdown;
    }
}

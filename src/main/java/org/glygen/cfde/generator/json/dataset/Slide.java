package org.glygen.cfde.generator.json.dataset;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Slide
{
    private String m_id = null;
    private PrintedSlide m_printedSlide = null;
    private List<Image> m_images = new ArrayList<>();
    private List<String> m_usedBlocks = new ArrayList<>();

    @JsonProperty("id")
    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    @JsonProperty("printedSlide")
    public PrintedSlide getPrintedSlide()
    {
        return this.m_printedSlide;
    }

    public void setPrintedSlide(PrintedSlide a_printedSlide)
    {
        this.m_printedSlide = a_printedSlide;
    }

    @JsonProperty("images")
    public List<Image> getImages()
    {
        return this.m_images;
    }

    public void setImages(List<Image> a_images)
    {
        this.m_images = a_images;
    }

    @JsonProperty("blocksUsed")
    public List<String> getUsedBlocks()
    {
        return this.m_usedBlocks;
    }

    public void setUsedBlocks(List<String> a_usedBlocks)
    {
        this.m_usedBlocks = a_usedBlocks;
    }

}

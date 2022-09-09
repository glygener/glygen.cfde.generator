package org.glygen.cfde.generator.json.glycanlist;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlycanList
{
    private Integer m_total = null;
    private Integer m_filteredTotal = null;
    private List<Glycan> m_glycanList = new ArrayList<>();

    @JsonProperty("total")
    public Integer getTotal()
    {
        return this.m_total;
    }

    public void setTotal(Integer a_total)
    {
        this.m_total = a_total;
    }

    @JsonProperty("filteredTotal")
    public Integer getFilteredTotal()
    {
        return this.m_filteredTotal;
    }

    public void setFilteredTotal(Integer a_filteredTotal)
    {
        this.m_filteredTotal = a_filteredTotal;
    }

    @JsonProperty("rows")
    public List<Glycan> getGlycanList()
    {
        return this.m_glycanList;
    }

    public void setGlycanList(List<Glycan> a_glycanList)
    {
        this.m_glycanList = a_glycanList;
    }

}

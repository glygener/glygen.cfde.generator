package org.glygen.cfde.generator.json.datasetlist;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatasetList
{
    private Integer m_total = null;
    private Integer m_filteredTotal = null;
    private List<DatasetSimple> m_datasetList = new ArrayList<>();

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
    public List<DatasetSimple> getDatasetList()
    {
        return this.m_datasetList;
    }

    public void setDatasetList(List<DatasetSimple> a_datasetList)
    {
        this.m_datasetList = a_datasetList;
    }
}

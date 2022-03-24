package org.glygen.cfde.generator.om;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Protein
{
    private String m_uniprotAcc = null;
    private Date m_creationTime = null;
    private String m_ensemblAcc = null;
    private List<String> m_anatomy = new ArrayList<>();
    private List<String> m_compound = new ArrayList<>();
    private List<String> m_disease = new ArrayList<>();
    private String m_species = null;

    public String getUniprotAcc()
    {
        return this.m_uniprotAcc;
    }

    public void setUniprotAcc(String a_uniprotAcc)
    {
        this.m_uniprotAcc = a_uniprotAcc;
    }

    public Date getCreationTime()
    {
        return this.m_creationTime;
    }

    public void setCreationTime(Date a_creationTime)
    {
        this.m_creationTime = a_creationTime;
    }

    public String getEnsemblAcc()
    {
        return this.m_ensemblAcc;
    }

    public void setEnsemblAcc(String a_ensemblAcc)
    {
        this.m_ensemblAcc = a_ensemblAcc;
    }

    public List<String> getAnatomy()
    {
        return this.m_anatomy;
    }

    public void setAnatomy(List<String> a_anatomy)
    {
        this.m_anatomy = a_anatomy;
    }

    public List<String> getCompound()
    {
        return this.m_compound;
    }

    public void setCompound(List<String> a_compound)
    {
        this.m_compound = a_compound;
    }

    public List<String> getDisease()
    {
        return this.m_disease;
    }

    public void setDisease(List<String> a_disease)
    {
        this.m_disease = a_disease;
    }

    public String getSpecies()
    {
        return this.m_species;
    }

    public void setSpecies(String a_species)
    {
        this.m_species = a_species;
    }
}

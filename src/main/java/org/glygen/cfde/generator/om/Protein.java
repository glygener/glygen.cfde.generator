package org.glygen.cfde.generator.om;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Protein
{
    public static final String PHOSPHORYLATION_SYMBOL = "P";
    public static final String UNKNOWN_GLYCAN_SYMBOL = "N/A";

    private String m_uniprotAcc = null;
    private Date m_creationTime = null;
    private String m_ensemblAcc = null;
    private HashSet<String> m_anatomy = new HashSet<>();
    private HashSet<String> m_glycans = new HashSet<>();
    private List<Site> m_ptmSites = new ArrayList<Site>();
    private HashSet<String> m_disease = new HashSet<>();
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

    public HashSet<String> getAnatomy()
    {
        return this.m_anatomy;
    }

    public void setAnatomy(HashSet<String> a_anatomy)
    {
        this.m_anatomy = a_anatomy;
    }

    public HashSet<String> getDisease()
    {
        return this.m_disease;
    }

    public void setDisease(HashSet<String> a_disease)
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

    public HashSet<String> getGlycans()
    {
        return this.m_glycans;
    }

    public void setGlycans(HashSet<String> a_glycans)
    {
        this.m_glycans = a_glycans;
    }

    public List<Site> getPtmSites()
    {
        return this.m_ptmSites;
    }

    public void setPtmSites(List<Site> a_ptmSites)
    {
        this.m_ptmSites = a_ptmSites;
    }
}

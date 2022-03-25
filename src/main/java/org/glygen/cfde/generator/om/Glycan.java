package org.glygen.cfde.generator.om;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Glycan
{
    private String m_glycanAcc = null;
    private Date m_creationTime = null;
    private HashSet<String> m_anatomy = new HashSet<>();
    private HashSet<String> m_disease = new HashSet<>();
    private HashSet<String> m_species = new HashSet<>();
    private HashMap<String, Protein> m_proteins = new HashMap<>();

    public Date getCreationTime()
    {
        return this.m_creationTime;
    }

    public void setCreationTime(Date a_creationTime)
    {
        this.m_creationTime = a_creationTime;
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

    public String getGlycanAcc()
    {
        return this.m_glycanAcc;
    }

    public void setGlycanAcc(String a_glycanAcc)
    {
        this.m_glycanAcc = a_glycanAcc;
    }

    public HashSet<String> getSpecies()
    {
        return this.m_species;
    }

    public void setSpecies(HashSet<String> a_species)
    {
        this.m_species = a_species;
    }

    public HashMap<String, Protein> getProteins()
    {
        return this.m_proteins;
    }

    public void setProteins(HashMap<String, Protein> a_proteins)
    {
        this.m_proteins = a_proteins;
    }

}

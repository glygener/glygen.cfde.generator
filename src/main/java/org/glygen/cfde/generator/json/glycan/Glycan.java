package org.glygen.cfde.generator.json.glycan;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Glycan
{
    private Double m_mass = null;
    private List<Species> m_species = new ArrayList<>();
    private List<Motif> m_motifs = new ArrayList<>();
    private List<Component> m_composition = new ArrayList<>();

    @JsonProperty("mass")
    public Double getMass()
    {
        return this.m_mass;
    }

    public void setMass(Double a_mass)
    {
        this.m_mass = a_mass;
    }

    @JsonProperty("species")
    public List<Species> getSpecies()
    {
        return this.m_species;
    }

    public void setSpecies(List<Species> a_species)
    {
        this.m_species = a_species;
    }

    @JsonProperty("motifs")
    public List<Motif> getMotifs()
    {
        return this.m_motifs;
    }

    public void setMotifs(List<Motif> a_motifs)
    {
        this.m_motifs = a_motifs;
    }

    @JsonProperty("composition")
    public List<Component> getComposition()
    {
        return this.m_composition;
    }

    public void setComposition(List<Component> a_composition)
    {
        this.m_composition = a_composition;
    }
}

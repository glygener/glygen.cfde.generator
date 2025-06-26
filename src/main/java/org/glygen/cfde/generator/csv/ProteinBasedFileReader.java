package org.glygen.cfde.generator.csv;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Protein;
import org.glygen.cfde.generator.om.PtmType;
import org.glygen.cfde.generator.om.Site;

public abstract class ProteinBasedFileReader
{

    protected ProteinFilter m_filterProtein = new ProteinFilter();
    protected GlycanFilter m_filterGlycan = new GlycanFilter();

    protected Integer m_lineLimit = Integer.MAX_VALUE;

    protected MetadataHandler m_handlerProtein = null;
    protected MetadataHandler m_handlerGene = null;
    protected MetadataHandler m_handlerGlycan = null;
    protected MetadataHandler m_handlerDisease = null;
    protected MetadataHandler m_handlerAnatomy = null;
    protected MetadataHandler m_handlerSpecies = null;
    protected MetadataHandler m_handlerSiteOne = null;
    protected MetadataHandler m_handlerSiteOneAA = null;
    protected MetadataHandler m_handlerSiteTwo = null;
    protected MetadataHandler m_handlerSiteTwoAA = null;

    protected HashMap<String, Protein> m_proteinMap = new HashMap<>();

    protected void createHandler(FileConfig a_config, String[] a_nextRecord, String a_mappingFolder,
            CSVError a_errorLog) throws IOException
    {
        // protein information
        if (a_config.getProteinColumn() == null)
        {
            throw new IOException("Protein type files need to have a protein column definition: "
                    + a_config.getFileUrl());
        }
        this.m_handlerProtein = MetadataHandler.fromString(a_config.getProteinColumn(),
                a_nextRecord, a_mappingFolder, a_errorLog, "protein");
        // gene information
        if (a_config.getGeneColumn() != null)
        {
            this.m_handlerGene = MetadataHandler.fromString(a_config.getGeneColumn(), a_nextRecord,
                    a_mappingFolder, a_errorLog, "gene");
        }
        // glycan information
        if (a_config.getGlycanColumn() != null)
        {
            this.m_handlerGlycan = MetadataHandler.fromString(a_config.getGlycanColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "glycan");
        }
        // disease information
        if (a_config.getDiseaseColumn() != null)
        {
            this.m_handlerDisease = MetadataHandler.fromString(a_config.getDiseaseColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "disease");
        }
        // anatomy
        if (a_config.getAnatomyColumn() != null)
        {
            this.m_handlerAnatomy = MetadataHandler.fromString(a_config.getAnatomyColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "anatomy");
        }
        // species
        if (a_config.getSpeciesColumn() == null)
        {
            throw new IOException("Protein type files need to have a species column definition: "
                    + a_config.getFileUrl());
        }
        this.m_handlerSpecies = MetadataHandler.fromString(a_config.getSpeciesColumn(),
                a_nextRecord, a_mappingFolder, a_errorLog, "species");
        // sites
        if (a_config.getSitePosOneColumn() != null)
        {
            this.m_handlerSiteOne = MetadataHandler.fromString(a_config.getSitePosOneColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "site one");
        }
        if (a_config.getSitePosTwoColumn() != null)
        {
            this.m_handlerSiteTwo = MetadataHandler.fromString(a_config.getSitePosTwoColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "site two");
        }
        if (a_config.getSitePosOneAAColumn() != null)
        {
            this.m_handlerSiteOneAA = MetadataHandler.fromString(a_config.getSitePosOneAAColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "site one aa");
        }
        if (a_config.getSitePosTwoAAColumn() != null)
        {
            this.m_handlerSiteTwoAA = MetadataHandler.fromString(a_config.getSitePosTwoAAColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "site two aa");
        }
    }

    protected void addSite(String[] a_row, Integer a_rowCounter, Protein a_protein,
            PtmType a_ptmType, CSVError a_errorLog) throws IOException
    {
        Site t_site = this.extractSiteInformation(a_row, a_rowCounter, a_errorLog, a_ptmType);
        if (t_site == null)
        {
            // something went wrong we will not add the site
            return;
        }
        // site object is filled
        List<Site> t_siteList = a_protein.getPtmSites();
        // site list exists make sure its not a dublicate
        if (!t_siteList.contains(t_site))
        {
            // not present
            t_siteList.add(t_site);
        }
    }

    protected Site extractSiteInformation(String[] a_row, Integer a_rowCounter, CSVError a_errorLog,
            PtmType a_ptmType) throws IOException
    {
        Site t_site = new Site(a_ptmType);
        if (this.m_handlerSiteOne != null && this.m_handlerSiteOneAA != null)
        {
            // there is site information, expected
            Integer t_siteOnePos = null;
            String t_siteOne = this.m_handlerSiteOne.processRow(a_row, a_rowCounter);
            if (t_siteOne == null || t_siteOne.trim().length() == 0)
            {
                t_siteOnePos = null;
            }
            else
            {
                try
                {
                    t_siteOnePos = Integer.parseInt(t_siteOne);
                    if (t_siteOnePos < 1)
                    {
                        a_errorLog.writeError(a_rowCounter,
                                "Not a positive number for PTM position one:" + t_siteOne);
                        return null;
                    }
                }
                catch (Exception t_e)
                {
                    a_errorLog.writeError(a_rowCounter,
                            "Not a number for PTM position one:" + t_siteOne);
                    return null;
                }
            }
            String t_siteOneAA = this.m_handlerSiteOneAA.processRow(a_row, a_rowCounter);
            if (t_siteOneAA == null || t_siteOneAA.trim().length() == 0)
            {
                t_siteOneAA = null;
            }
            if (t_siteOnePos == null && t_siteOneAA != null)
            {
                a_errorLog.writeError(a_rowCounter,
                        "PTM position one not provided but AA is present");
                return null;
            }
            if (t_siteOnePos != null && t_siteOneAA == null)
            {
                a_errorLog.writeError(a_rowCounter, "PTM position one provided but AA is missing");
                return null;
            }
            t_site.setPositionOne(t_siteOnePos);
            t_site.setPositionOneAA(t_siteOneAA);
            // is there a site two expected?
            if (this.m_handlerSiteTwo != null && this.m_handlerSiteTwoAA != null)
            {
                // yes
                Integer t_siteTwoPos = null;
                String t_siteTwo = this.m_handlerSiteTwo.processRow(a_row, a_rowCounter);
                if (t_siteTwo == null || t_siteTwo.trim().length() == 0)
                {
                    t_siteTwoPos = null;
                }
                else
                {
                    try
                    {
                        t_siteTwoPos = Integer.parseInt(t_siteTwo);
                        if (t_siteOnePos < 1)
                        {
                            a_errorLog.writeError(a_rowCounter,
                                    "Not a positive number for PTM position one:" + t_siteTwo);
                            return null;
                        }
                    }
                    catch (Exception t_e)
                    {
                        a_errorLog.writeError(a_rowCounter,
                                "Not a number for PTM position two:" + t_siteTwo);
                        return null;
                    }
                }
                String t_siteTwoAA = this.m_handlerSiteTwoAA.processRow(a_row, a_rowCounter);
                if (t_siteTwoAA == null || t_siteTwoAA.trim().length() == 0)
                {
                    t_siteTwoAA = null;
                }
                if (t_siteTwoPos == null && t_siteTwoAA != null)
                {
                    a_errorLog.writeError(a_rowCounter,
                            "PTM position two not provided but AA is present");
                    return null;
                }
                if (t_siteTwoPos != null && t_siteTwoAA == null)
                {
                    a_errorLog.writeError(a_rowCounter,
                            "PTM position two provided but AA is missing");
                    return null;
                }
                if (t_siteTwoPos != null && t_siteOnePos == null)
                {
                    a_errorLog.writeError(a_rowCounter,
                            "PTM position two provided but position one is missing");
                    return null;
                }
                t_site.setPositionTwo(t_siteTwoPos);
                t_site.setPositionTwoAA(t_siteTwoAA);
            }
        }
        return t_site;
    }

    protected Protein getProteinObject(String[] a_row, Integer a_rowCounter, CSVError a_errorLog)
            throws IOException
    {
        // get protein acc
        String t_proteinAcc = this.m_handlerProtein.processRow(a_row, a_rowCounter);
        if (t_proteinAcc == null || t_proteinAcc.trim().length() == 0)
        {
            a_errorLog.writeError(a_rowCounter, "Protein column value is empty");
            return null;
        }
        // check if it already exists otherwise create it and put it in map
        Protein t_protein = this.m_proteinMap.get(t_proteinAcc);
        if (t_protein == null)
        {
            t_protein = new Protein();
            t_protein.setUniprotAcc(t_proteinAcc);
            String t_temp = t_proteinAcc.substring(0, t_proteinAcc.indexOf("-"));
            if (!this.m_filterProtein.isIgnore(t_temp))
            {
                this.m_proteinMap.put(t_proteinAcc, t_protein);
            }
        }
        return t_protein;
    }

    protected void addCommonProteinInformation(String[] a_row, Integer a_rowCounter,
            CSVError a_errorLog, Protein a_protein) throws IOException
    {
        // disease
        if (this.m_handlerDisease != null)
        {
            String t_disease = this.m_handlerDisease.processRow(a_row, a_rowCounter);
            if (t_disease != null && t_disease.trim().length() != 0)
            {
                a_protein.getDisease().add(t_disease);
            }
        }
        // anatomy
        if (this.m_handlerAnatomy != null)
        {
            String t_anatomy = this.m_handlerAnatomy.processRow(a_row, a_rowCounter);
            if (t_anatomy != null && t_anatomy.trim().length() != 0)
            {
                a_protein.getAnatomy().add(t_anatomy);
            }
        }
        // species
        String t_species = this.m_handlerSpecies.processRow(a_row, a_rowCounter);
        if (t_species == null || t_species.trim().length() == 0)
        {
            a_errorLog.writeError(a_rowCounter, "Species value is empty");
            return;
        }
        a_protein.setSpecies(t_species);
    }

    protected void addGlycan(Protein a_protein, String a_glycan)
    {
        HashSet<String> t_glycans = a_protein.getGlycans();
        if (!t_glycans.contains(a_glycan))
        {
            t_glycans.add(a_glycan);
        }
    }
}

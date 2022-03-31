package org.glygen.cfde.generator.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Glycan;
import org.glygen.cfde.generator.om.Protein;

import com.opencsv.CSVReader;

public class GlycanFileReader
{
    private Integer m_lineLimit = Integer.MAX_VALUE;

    private MetadataHandler m_handlerProtein = null;
    private MetadataHandler m_handlerGene = null;
    private MetadataHandler m_handlerGlycan = null;
    private MetadataHandler m_handlerDisease = null;
    private MetadataHandler m_handlerAnatomy = null;
    private MetadataHandler m_handlerSpecies = null;

    private HashMap<String, Glycan> m_glycanMap = new HashMap<>();

    public GlycanFileReader(Integer a_lineLimit)
    {
        this.m_lineLimit = a_lineLimit;
    }

    public List<Glycan> loadFile(String a_csvFile, FileConfig a_config, String a_mappingFolder,
            CSVError a_errorLog) throws IOException
    {
        Integer t_rowCounter = 1;
        try
        {
            a_errorLog.setCurrentFile(a_config.getLocalId());
            // Create an object of filereader
            FileReader t_fileReader = new FileReader(a_csvFile);
            // create csvReader object passing
            CSVReader t_csvReader = new CSVReader(t_fileReader);
            // that should be the table heading
            String[] t_nextRecord = t_csvReader.readNext();
            // handle the columns
            this.createHandler(a_config, t_nextRecord, a_mappingFolder, a_errorLog);
            // read data line by line
            while ((t_nextRecord = t_csvReader.readNext()) != null)
            {
                t_rowCounter++;
                if (t_rowCounter <= this.m_lineLimit)
                {
                    this.parseRow(t_nextRecord, t_rowCounter, a_errorLog);
                }
            }
            t_csvReader.close();
        }
        catch (Exception e)
        {
            throw new IOException(
                    "Error parsing file (row " + t_rowCounter.toString() + "): " + e.getMessage());
        }
        List<Glycan> t_finalList = new ArrayList<>();
        for (Glycan t_glycan : this.m_glycanMap.values())
        {
            t_finalList.add(t_glycan);
        }
        return t_finalList;
    }

    private void createHandler(FileConfig a_config, String[] a_nextRecord, String a_mappingFolder,
            CSVError a_errorLog) throws IOException
    {
        // glycan information
        if (a_config.getGlycanColumn() == null)
        {
            throw new IOException("Glycan type files need to have a glycan column definition: "
                    + a_config.getFileUrl());
        }
        this.m_handlerGlycan = MetadataHandler.fromString(a_config.getGlycanColumn(), a_nextRecord,
                a_mappingFolder, a_errorLog, "glycan");
        // gene information
        if (a_config.getGeneColumn() != null && a_config.getProteinColumn() != null)
        {
            this.m_handlerGene = MetadataHandler.fromString(a_config.getGeneColumn(), a_nextRecord,
                    a_mappingFolder, a_errorLog, "gene");
            this.m_handlerProtein = MetadataHandler.fromString(a_config.getProteinColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "protein");
        }
        else if (a_config.getGeneColumn() == null && a_config.getProteinColumn() == null)
        {
            // thats fine
        }
        else
        {
            throw new IOException(
                    "Protein and gene column defintion has to be filled both. Only one is not valid: "
                            + a_config.getFileUrl());
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
        if (a_config.getSpeciesColumn() != null)
        {
            this.m_handlerSpecies = MetadataHandler.fromString(a_config.getSpeciesColumn(),
                    a_nextRecord, a_mappingFolder, a_errorLog, "species");
        }
    }

    private void parseRow(String[] a_row, Integer a_rowCounter, CSVError a_errorLog)
            throws IOException
    {
        // get glycan acc
        String t_glycanAcc = this.m_handlerGlycan.processRow(a_row, a_rowCounter);
        if (t_glycanAcc == null || t_glycanAcc.trim().length() == 0)
        {
            a_errorLog.writeEntry("error", a_rowCounter, "Glycan column value is empty");
            return;
        }
        // check if it already exists otherwise create it and put it in map
        Glycan t_glycan = this.m_glycanMap.get(t_glycanAcc);
        if (t_glycan == null)
        {
            t_glycan = new Glycan();
            t_glycan.setGlycanAcc(t_glycanAcc);
            this.m_glycanMap.put(t_glycanAcc, t_glycan);
        }
        // disease
        if (this.m_handlerDisease != null)
        {
            String t_disease = this.m_handlerDisease.processRow(a_row, a_rowCounter);
            if (t_disease != null && t_disease.trim().length() != 0)
            {
                t_glycan.getDisease().add(t_disease);
            }
        }
        // anatomy
        if (this.m_handlerAnatomy != null)
        {
            String t_anatomy = this.m_handlerAnatomy.processRow(a_row, a_rowCounter);
            if (t_anatomy != null && t_anatomy.trim().length() != 0)
            {
                t_glycan.getAnatomy().add(t_anatomy);
            }
        }
        // species
        if (this.m_handlerSpecies != null)
        {
            String t_species = this.m_handlerSpecies.processRow(a_row, a_rowCounter);
            if (t_species != null && t_species.trim().length() != 0)
            {
                t_glycan.getSpecies().add(t_species);
            }
        }
        // gene
        if (this.m_handlerProtein != null)
        {
            String t_protein = this.m_handlerProtein.processRow(a_row, a_rowCounter);
            String t_gene = this.m_handlerGene.processRow(a_row, a_rowCounter);
            if (t_protein == null || t_protein.trim().length() == 0)
            {
                a_errorLog.writeEntry("error", a_rowCounter, "Protein value is empty");
                return;
            }
            if (t_gene == null || t_gene.trim().length() == 0)
            {
                a_errorLog.writeEntry("warning", a_rowCounter, "Gene value is empty");
                return;
            }
            HashMap<String, Protein> t_map = t_glycan.getProteins();
            Protein t_proteinObject = t_map.get(t_protein);
            if (t_proteinObject == null)
            {
                t_proteinObject = new Protein();
                t_proteinObject.setUniprotAcc(t_protein);
                t_proteinObject.setEnsemblAcc(t_gene);
                t_map.put(t_protein, t_proteinObject);
            }
        }
    }

}

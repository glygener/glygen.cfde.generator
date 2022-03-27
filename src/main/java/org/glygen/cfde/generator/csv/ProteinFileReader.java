package org.glygen.cfde.generator.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Protein;

import com.opencsv.CSVReader;

public class ProteinFileReader
{
    private Integer m_lineLimit = Integer.MAX_VALUE;

    private MetadataHandler m_handlerProtein = null;
    private MetadataHandler m_handlerGene = null;
    private MetadataHandler m_handlerGlycan = null;
    private MetadataHandler m_handlerDisease = null;
    private MetadataHandler m_handlerAnatomy = null;
    private MetadataHandler m_handlerSpecies = null;

    private HashMap<String, Protein> m_proteinMap = new HashMap<>();

    public ProteinFileReader(Integer a_lineLimit)
    {
        this.m_lineLimit = a_lineLimit;
    }

    public List<Protein> loadFile(String a_csvFile, FileConfig a_config, String a_mappingFolder)
            throws IOException
    {
        Integer t_rowCounter = 1;
        try
        {
            // Create an object of filereader
            FileReader t_fileReader = new FileReader(a_csvFile);
            // create csvReader object passing
            CSVReader t_csvReader = new CSVReader(t_fileReader);
            // that should be the table heading
            String[] t_nextRecord = t_csvReader.readNext();
            // handle the columns
            this.createHandler(a_config, t_nextRecord, a_mappingFolder);
            // read data line by line
            while ((t_nextRecord = t_csvReader.readNext()) != null)
            {
                t_rowCounter++;
                if (t_rowCounter <= this.m_lineLimit)
                {
                    this.parseRow(t_nextRecord, t_rowCounter);
                }
            }
            t_csvReader.close();
        }
        catch (Exception e)
        {
            throw new IOException(
                    "Error parsing file (row " + t_rowCounter.toString() + "): " + e.getMessage());
        }
        List<Protein> t_finalList = new ArrayList<>();
        for (Protein t_protein : this.m_proteinMap.values())
        {
            t_finalList.add(t_protein);
        }
        return t_finalList;
    }

    private void createHandler(FileConfig a_config, String[] a_nextRecord, String a_mappingFolder)
            throws IOException
    {
        // protein information
        if (a_config.getProteinColumn() == null)
        {
            throw new IOException("Protein type files need to have a protein column definition: "
                    + a_config.getFileUrl());
        }
        this.m_handlerProtein = MetadataHandler.fromString(a_config.getProteinColumn(),
                a_nextRecord, a_mappingFolder);
        // gene information
        if (a_config.getGeneColumn() == null)
        {
            throw new IOException("Protein type files need to have a gene column definition: "
                    + a_config.getFileUrl());
        }
        this.m_handlerGene = MetadataHandler.fromString(a_config.getGeneColumn(), a_nextRecord,
                a_mappingFolder);
        // glycan information
        if (a_config.getGlycanColumn() != null)
        {
            this.m_handlerGlycan = MetadataHandler.fromString(a_config.getGlycanColumn(),
                    a_nextRecord, a_mappingFolder);
        }
        // disease information
        if (a_config.getDiseaseColumn() != null)
        {
            this.m_handlerDisease = MetadataHandler.fromString(a_config.getDiseaseColumn(),
                    a_nextRecord, a_mappingFolder);
        }
        // anatomy
        if (a_config.getAnatomyColumn() != null)
        {
            this.m_handlerAnatomy = MetadataHandler.fromString(a_config.getAnatomyColumn(),
                    a_nextRecord, a_mappingFolder);
        }
        // species
        if (a_config.getSpeciesColumn() == null)
        {
            throw new IOException("Protein type files need to have a species column definition: "
                    + a_config.getFileUrl());
        }
        this.m_handlerSpecies = MetadataHandler.fromString(a_config.getSpeciesColumn(),
                a_nextRecord, a_mappingFolder);
    }

    private void parseRow(String[] a_row, Integer a_rowCounter) throws IOException
    {
        // get protein acc
        String t_proteinAcc = this.m_handlerProtein.processRow(a_row, a_rowCounter);
        if (t_proteinAcc.trim().length() == 0)
        {
            throw new IOException("Protein column value is empty for row " + a_rowCounter);
        }
        // check if it already exists otherwise create it and put it in map
        Protein t_protein = this.m_proteinMap.get(t_proteinAcc);
        if (t_protein == null)
        {
            t_protein = new Protein();
            t_protein.setUniprotAcc(t_proteinAcc);
            this.m_proteinMap.put(t_proteinAcc, t_protein);
        }
        // gene
        String t_gene = this.m_handlerGene.processRow(a_row, a_rowCounter);
        if (t_proteinAcc.trim().length() == 0)
        {
            throw new IOException("Gene value is empty for row " + a_rowCounter);
        }
        t_protein.setEnsemblAcc(t_gene);
        // glycan
        if (this.m_handlerGlycan != null)
        {
            String t_glycan = this.m_handlerGlycan.processRow(a_row, a_rowCounter);
            if (t_glycan.trim().length() != 0)
            {
                t_protein.getCompound().add(t_glycan);
            }
        }
        // disease
        if (this.m_handlerDisease != null)
        {
            String t_disease = this.m_handlerDisease.processRow(a_row, a_rowCounter);
            if (t_disease.trim().length() != 0)
            {
                t_protein.getDisease().add(t_disease);
            }
        }
        // anatomy
        if (this.m_handlerAnatomy != null)
        {
            String t_anatomy = this.m_handlerAnatomy.processRow(a_row, a_rowCounter);
            if (t_anatomy.trim().length() != 0)
            {
                t_protein.getAnatomy().add(t_anatomy);
            }
        }
        // species
        String t_species = this.m_handlerSpecies.processRow(a_row, a_rowCounter);
        if (t_species.trim().length() == 0)
        {
            throw new IOException("Species value is empty for row " + a_rowCounter);
        }
        t_protein.setSpecies(t_species);
    }

}

package org.glygen.cfde.generator.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Glycan;
import org.glygen.cfde.generator.om.Protein;
import org.glygen.cfde.generator.om.PtmType;

import com.opencsv.CSVReader;

public class ProteinGlycanMixFileReader extends ProteinBasedFileReader
{

    private HashMap<String, Glycan> m_glycanMap = new HashMap<>();

    public ProteinGlycanMixFileReader(Integer a_lineLimit)
    {
        this.m_lineLimit = a_lineLimit;
    }

    public void loadFile(String a_csvFile, FileConfig a_config, String a_mappingFolder,
            CSVError a_errorLog) throws IOException
    {
        Integer t_rowCounter = 1;
        this.m_proteinMap = new HashMap<>();
        this.m_glycanMap = new HashMap<>();
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
            if (this.m_handlerGene == null)
            {
                t_csvReader.close();
                throw new IOException("Protein/Glycan mix type file need to have a Gene column.");
            }
            // there needs to be a glycan handler for mixed files
            if (this.m_handlerGlycan == null)
            {
                t_csvReader.close();
                throw new IOException(
                        "Protein/Glycan mix type files need to have a glycan column definition: "
                                + a_config.getFileUrl());
            }
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
    }

    private void parseRow(String[] a_row, Integer a_rowCounter, CSVError a_errorLog)
            throws IOException
    {
        // get protein acc
        String t_proteinAcc = this.m_handlerProtein.processRow(a_row, a_rowCounter);
        if (t_proteinAcc == null || t_proteinAcc.trim().length() == 0)
        {
            // no protein information => Glycan row
            this.parseGlycanRow(a_row, a_rowCounter, a_errorLog);
        }
        else
        {
            this.parseProteinRow(a_row, a_rowCounter, a_errorLog);
        }
    }

    public List<Protein> getProteinList()
    {
        List<Protein> t_finalList = new ArrayList<>();
        for (Protein t_protein : this.m_proteinMap.values())
        {
            t_finalList.add(t_protein);
        }
        return t_finalList;
    }

    public List<Glycan> getGlycanList()
    {
        List<Glycan> t_finalList = new ArrayList<>();
        for (Glycan t_glycan : this.m_glycanMap.values())
        {
            t_finalList.add(t_glycan);
        }
        return t_finalList;
    }

    private void parseGlycanRow(String[] a_row, Integer a_rowCounter, CSVError a_errorLog)
            throws IOException
    {
        // get glycan acc
        String t_glycanAcc = this.m_handlerGlycan.processRow(a_row, a_rowCounter);
        if (t_glycanAcc == null || t_glycanAcc.trim().length() == 0)
        {
            a_errorLog.writeError(a_rowCounter, "Glycan column value is empty");
            return;
        }
        // check if it already exists otherwise create it and put it in map
        Glycan t_glycan = this.m_glycanMap.get(t_glycanAcc);
        if (t_glycan == null)
        {
            t_glycan = new Glycan();
            t_glycan.setGlycanAcc(t_glycanAcc);
            if (!this.m_filterGlycan.isIgnore(t_glycanAcc))
            {
                this.m_glycanMap.put(t_glycanAcc, t_glycan);
            }
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
    }

    private void parseProteinRow(String[] a_row, Integer a_rowCounter, CSVError a_errorLog)
            throws IOException
    {
        Protein t_protein = this.getProteinObject(a_row, a_rowCounter, a_errorLog);
        this.addCommonProteinInformation(a_row, a_rowCounter, a_errorLog, t_protein);
        // gene
        String t_gene = this.m_handlerGene.processRow(a_row, a_rowCounter);
        if (t_gene == null || t_gene.trim().length() == 0)
        {
            a_errorLog.writeWarning(a_rowCounter, "Gene value is empty");
        }
        else
        {
            t_protein.setEnsemblAcc(t_gene);
        }
        // glycosylation
        if (this.m_handlerGlycan != null)
        {
            String t_glycan = this.m_handlerGlycan.processRow(a_row, a_rowCounter);
            if (t_glycan != null && t_glycan.trim().length() != 0)
            {
                // site with glycan
                if (!this.m_filterGlycan.isIgnore(t_glycan))
                {
                    this.addSite(a_row, a_rowCounter, t_protein, PtmType.GLYCOSYLATION, a_errorLog);
                    this.addGlycan(t_protein, t_glycan);
                }

            }
            else
            {
                // site without glycan
                this.addSite(a_row, a_rowCounter, t_protein, PtmType.GLYCOSYLATION, a_errorLog);
            }
        }
        else
        {
            // site without glycan
            this.addSite(a_row, a_rowCounter, t_protein, PtmType.GLYCOSYLATION, a_errorLog);
        }
    }

}

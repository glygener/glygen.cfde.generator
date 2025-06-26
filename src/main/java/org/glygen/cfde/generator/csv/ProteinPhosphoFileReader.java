package org.glygen.cfde.generator.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Protein;
import org.glygen.cfde.generator.om.PtmType;

import com.opencsv.CSVReader;

public class ProteinPhosphoFileReader extends ProteinBasedFileReader
{
    public ProteinPhosphoFileReader(Integer a_lineLimit)
    {
        this.m_lineLimit = a_lineLimit;
    }

    public void loadFile(String a_csvFile, FileConfig a_config, String a_mappingFolder,
            CSVError a_errorLog) throws IOException
    {
        Integer t_rowCounter = 1;
        this.m_proteinMap = new HashMap<>();
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
                throw new IOException("Protein type file need to have a Gene column.");
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

    public List<Protein> getProteinList()
    {
        List<Protein> t_finalList = new ArrayList<>();
        for (Protein t_protein : this.m_proteinMap.values())
        {
            t_finalList.add(t_protein);
        }
        return t_finalList;
    }

    private void parseRow(String[] a_row, Integer a_rowCounter, CSVError a_errorLog)
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
        // phosphorylation
        this.addSite(a_row, a_rowCounter, t_protein, PtmType.PHOSPHORYLATION, a_errorLog);
    }

}

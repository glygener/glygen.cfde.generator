package org.glygen.cfde.generator.util;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.glygen.cfde.generator.om.DataFileType;
import org.glygen.cfde.generator.om.FileConfig;

import com.opencsv.CSVReader;

public class ConfigFileParser
{
    public List<FileConfig> loadConfigFile(String a_configFile) throws IOException
    {
        List<FileConfig> t_result = new ArrayList<>();
        Integer t_rowCounter = 1;
        try
        {
            // Create an object of filereader
            FileReader t_fileReader = new FileReader(a_configFile);

            // create csvReader object passing
            CSVReader t_csvReader = new CSVReader(t_fileReader);
            // that should be the table heading
            String[] t_nextRecord = t_csvReader.readNext();

            // read data line by line
            while ((t_nextRecord = t_csvReader.readNext()) != null)
            {
                t_rowCounter++;
                FileConfig t_fileConfig = this.parseRow(t_nextRecord, t_rowCounter);
                if (t_fileConfig != null)
                {
                    t_result.add(t_fileConfig);
                }
            }
            t_csvReader.close();
        }
        catch (Exception e)
        {
            throw new IOException("Error parsing config file (row " + t_rowCounter.toString()
                    + "): " + e.getMessage());
        }
        return t_result;
    }

    private FileConfig parseRow(String[] a_row, Integer a_rowCounter) throws IOException
    {
        FileConfig t_fileConfig = new FileConfig();
        // generation_type
        String t_cellValue = this.getCell(a_row, 0);
        DataFileType t_type = DataFileType.forString(t_cellValue);
        if (t_type == null)
        {
            throw new IOException("Unknown data file type in row " + a_rowCounter.toString() + ": "
                    + t_cellValue);
        }
        t_fileConfig.setType(t_type);
        // file_url
        t_cellValue = this.getCell(a_row, 1);
        if (t_cellValue == null)
        {
            throw new IOException("Missing file url in row " + a_rowCounter.toString());
        }
        t_fileConfig.setFileUrl(t_cellValue);
        // bco_url
        t_cellValue = this.getCell(a_row, 2);
        t_fileConfig.setBcoUrl(t_cellValue);
        // local_id
        t_cellValue = this.getCell(a_row, 3);
        if (t_cellValue == null)
        {
            throw new IOException("Missing local ID in row " + a_rowCounter.toString());
        }
        t_fileConfig.setLocalId(t_cellValue);
        // persistent_id
        t_cellValue = this.getCell(a_row, 4);
        // if (t_cellValue == null)
        // {
        // throw new IOException("Missing persistent ID in row " +
        // a_rowCounter.toString());
        // }
        t_fileConfig.setPersitentId(t_cellValue);
        // creation_time
        t_cellValue = this.getCell(a_row, 5);
        if (t_cellValue == null)
        {
            throw new IOException("Missing creation time in row " + a_rowCounter.toString());
        }
        try
        {
            Date t_date = this.stringToDate(t_cellValue);
            t_fileConfig.setCreationTime(t_date);
        }
        catch (Exception e)
        {
            throw new IOException(
                    "Date format incorrect (MM/dd/yyy) in row " + a_rowCounter.toString());
        }
        // file_format
        t_cellValue = this.getCell(a_row, 6);
        if (t_cellValue == null)
        {
            throw new IOException("Missing file format in row " + a_rowCounter.toString());
        }
        t_fileConfig.setFileFormat(t_cellValue);
        // data_type
        t_cellValue = this.getCell(a_row, 7);
        t_fileConfig.setDataType(t_cellValue);
        // mime_type
        t_cellValue = this.getCell(a_row, 8);
        if (t_cellValue == null)
        {
            throw new IOException("Missing mime type in row " + a_rowCounter.toString());
        }
        t_fileConfig.setMimeType(t_cellValue);
        // protein
        t_cellValue = this.getCell(a_row, 9);
        t_fileConfig.setProteinColumn(t_cellValue);
        // gene
        t_cellValue = this.getCell(a_row, 10);
        t_fileConfig.setGeneColumn(t_cellValue);
        // glycan
        t_cellValue = this.getCell(a_row, 11);
        t_fileConfig.setGlycanColumn(t_cellValue);
        // disease
        t_cellValue = this.getCell(a_row, 12);
        t_fileConfig.setDiseaseColumn(t_cellValue);
        // anatomy
        t_cellValue = this.getCell(a_row, 13);
        t_fileConfig.setAnatomyColumn(t_cellValue);
        // species
        t_cellValue = this.getCell(a_row, 14);
        t_fileConfig.setSpeciesColumn(t_cellValue);
        // species
        t_cellValue = this.getCell(a_row, 15);
        t_fileConfig.setAccessUrl(t_cellValue);
        if (t_cellValue == null)
        {
            throw new IOException("Missing access URL in row " + a_rowCounter.toString());
        }
        if (t_fileConfig.getProteinColumn() == null && t_fileConfig.getGeneColumn() != null)
        {
            throw new IOException("Gene column is specified but protein column is missing in row "
                    + a_rowCounter.toString());
        }
        return t_fileConfig;
    }

    private Date stringToDate(String a_cellValue) throws ParseException
    {
        SimpleDateFormat t_formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date t_date = t_formatter.parse(a_cellValue);
        return t_date;
    }

    private String getCell(String[] a_row, int a_position)
    {
        if (a_position < a_row.length)
        {
            String t_value = a_row[a_position].trim();
            if (t_value.length() == 0)
            {
                return null;
            }
            return t_value;
        }
        return null;
    }

}

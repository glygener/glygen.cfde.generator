package org.glygen.cfde.generator.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class MetadataHandler
{
    private MetadataType m_type = null;
    private HashMap<String, String> m_dictionary = new HashMap<>();
    private String m_columnName = null;
    private Integer m_position = null;
    private String m_staticInformation = null;

    public MetadataType getType()
    {
        return this.m_type;
    }

    public void setType(MetadataType a_type)
    {
        this.m_type = a_type;
    }

    public HashMap<String, String> getDictionary()
    {
        return this.m_dictionary;
    }

    public void setDictionary(HashMap<String, String> a_dictionary)
    {
        this.m_dictionary = a_dictionary;
    }

    public String getColumnName()
    {
        return this.m_columnName;
    }

    public void setColumnName(String a_columnName)
    {
        this.m_columnName = a_columnName;
    }

    public String getStaticInformation()
    {
        return this.m_staticInformation;
    }

    public void setStaticInformation(String a_staticInformation)
    {
        this.m_staticInformation = a_staticInformation;
    }

    public Integer getPosition()
    {
        return m_position;
    }

    public void setPosition(Integer a_position)
    {
        this.m_position = a_position;
    }

    public static MetadataHandler fromString(String a_string, String[] a_heading,
            String a_mappingFolder) throws IOException
    {
        MetadataHandler t_handler = new MetadataHandler();
        if (a_string == null)
        {
            return null;
        }
        else if (a_string.startsWith("column:"))
        {
            // column based
            t_handler.setType(MetadataType.COLUMN);
            String t_columnName = a_string.substring(7);
            Integer t_postion = MetadataHandler.findColumnPosition(t_columnName, a_heading);
            t_handler.setColumnName(t_columnName);
            t_handler.setPosition(t_postion);
        }
        else if (a_string.startsWith("static:"))
        {
            // static information
            t_handler.setType(MetadataType.STATIC);
            t_handler.setStaticInformation(a_string.substring(7));
        }
        else if (a_string.startsWith("file:"))
        {
            // mapping file
            t_handler.setType(MetadataType.MAPPING);
            String t_part = a_string.substring(5);
            Integer t_splitPosition = t_part.indexOf(":");
            if (t_splitPosition.equals(-1))
            {
                throw new IOException(
                        "Information column of type file does not follow pattern 'file:<column name>:<file name>': "
                                + a_string);
            }
            String t_columnName = t_part.substring(0, t_splitPosition);
            Integer t_postion = MetadataHandler.findColumnPosition(t_columnName, a_heading);
            t_handler.setColumnName(t_columnName);
            t_handler.setPosition(t_postion);
            HashMap<String, String> t_mapping = MetadataHandler.loadMappingFile(a_mappingFolder,
                    t_part.substring(t_splitPosition + 1));
            t_handler.setDictionary(t_mapping);
        }
        return t_handler;
    }

    private static HashMap<String, String> loadMappingFile(String a_mappingFolder,
            String a_fileName) throws IOException
    {
        HashMap<String, String> t_mapping = new HashMap<>();
        try
        {
            // Create an object of filereader
            FileReader t_fileReader = new FileReader(a_mappingFolder + File.separator + a_fileName);
            // create csvReader object passing
            CSVReader t_csvReader = new CSVReader(t_fileReader);
            // read data line by line
            String[] t_nextRecord = null;
            Integer t_rowCounter = 0;
            while ((t_nextRecord = t_csvReader.readNext()) != null)
            {
                t_rowCounter++;
                if (t_nextRecord.length == 1)
                {
                    if (t_nextRecord[0].trim().length() != 0)
                    {
                        t_csvReader.close();
                        throw new IOException("Mapping file has only 1 column in line "
                                + t_rowCounter + ": " + a_fileName);
                    }
                    // empty line we can ignore it
                }
                else
                {
                    if (t_nextRecord.length != 2)
                    {
                        t_csvReader.close();
                        throw new IOException("Mapping file has not 2 columns in line "
                                + t_rowCounter + ": " + a_fileName);
                    }
                    if (t_mapping.get(t_nextRecord[0].trim()) != null)
                    {
                        t_csvReader.close();
                        throw new IOException(
                                "Mapping file (" + a_fileName + ") contains dublicated key in line "
                                        + t_rowCounter.toString() + ": " + t_nextRecord[0]);
                    }
                    if (t_nextRecord[0].trim().length() == 0)
                    {
                        t_csvReader.close();
                        throw new IOException("Mapping file (" + a_fileName
                                + ") contains empty key in line " + t_rowCounter.toString());
                    }
                    if (t_nextRecord[1].trim().length() == 0)
                    {
                        t_csvReader.close();
                        throw new IOException("Mapping file (" + a_fileName
                                + ") contains empty value in line " + t_rowCounter.toString());
                    }
                    t_mapping.put(t_nextRecord[0].trim(), t_nextRecord[1].trim());
                }
            }
            t_csvReader.close();
        }
        catch (CsvValidationException e)
        {
            throw new IOException("Mapping file " + a_fileName + ": " + e.getMessage(), e);
        }
        return t_mapping;
    }

    private static Integer findColumnPosition(String a_columnName, String[] a_heading)
            throws IOException
    {
        Integer t_position = 0;
        for (String t_columnHeading : a_heading)
        {
            if (t_columnHeading.trim().equals(a_columnName))
            {
                return t_position;
            }
            t_position++;
        }
        throw new IOException("Unable to find column heading: " + a_columnName);
    }
}

package org.glygen.cfde.generator.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class CSVError
{
    private CSVWriter m_csvWriter = null;
    private String m_currentFile = null;

    public CSVError(String a_fileNamePath) throws IOException
    {
        // first create file object for file
        File t_file = new File(a_fileNamePath);

        // create FileWriter object with file as parameter
        FileWriter t_fileWriter = new FileWriter(t_file);

        // create CSVWriter with tab as separator
        this.m_csvWriter = new CSVWriter(t_fileWriter, CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        String[] t_header = new String[] { "type", "file", "row", "message", "status" };
        // write the header
        this.m_csvWriter.writeNext(t_header);
    }

    public void closeFile() throws IOException
    {
        this.m_csvWriter.flush();
        this.m_csvWriter.close();
    }

    public void writeEntry(String a_type, String a_file, Integer a_rowNumber, String a_message,
            String a_status)
    {
        String[] t_line = new String[5];
        t_line[0] = a_type;
        t_line[1] = a_file;
        if (a_rowNumber == null)
        {
            t_line[2] = "";
        }
        else
        {
            t_line[2] = a_rowNumber.toString();
        }
        t_line[3] = a_message;
        t_line[4] = a_status;
        this.m_csvWriter.writeNext(t_line);
    }

    public void writeEntry(String a_type, Integer a_lineNumber, String a_message)
    {
        this.writeEntry(a_type, this.m_currentFile, a_lineNumber, a_message, "Skipped row");
    }

    public String getCurrentFile()
    {
        return m_currentFile;
    }

    public void setCurrentFile(String a_currentFile)
    {
        this.m_currentFile = a_currentFile;
    }

    public void writeEntry(String a_type, String a_message, String a_status)
    {
        this.writeEntry(a_type, this.m_currentFile, null, a_message, a_status);
    }
}

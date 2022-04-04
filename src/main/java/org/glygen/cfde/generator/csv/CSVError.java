package org.glygen.cfde.generator.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class CSVError
{
    private CSVWriter m_csvError = null;
    private CSVWriter m_csvWarning = null;
    private String m_currentFile = null;

    public CSVError(String a_fileNamePath) throws IOException
    {
        // first create file object for file
        File t_file = new File(a_fileNamePath + File.separator + "error-log.csv");

        // create FileWriter object with file as parameter
        FileWriter t_fileWriter = new FileWriter(t_file);

        // create CSVWriter with tab as separator
        this.m_csvError = new CSVWriter(t_fileWriter, CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        String[] t_header = new String[] { "file", "row", "message", "status" };
        // write the header
        this.m_csvError.writeNext(t_header);

        t_file = new File(a_fileNamePath + File.separator + "warning-log.csv");

        // create FileWriter object with file as parameter
        t_fileWriter = new FileWriter(t_file);

        // create CSVWriter with tab as separator
        this.m_csvWarning = new CSVWriter(t_fileWriter, CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        t_header = new String[] { "file", "row", "message" };
        // write the header
        this.m_csvWarning.writeNext(t_header);
    }

    public void closeFile() throws IOException
    {
        this.m_csvError.flush();
        this.m_csvError.close();
        this.m_csvWarning.flush();
        this.m_csvWarning.close();
    }

    public void writeError(String a_file, Integer a_rowNumber, String a_message, String a_status)
    {
        String[] t_line = new String[4];
        t_line[0] = a_file;
        if (a_rowNumber == null)
        {
            t_line[1] = "";
        }
        else
        {
            t_line[1] = a_rowNumber.toString();
        }
        t_line[2] = a_message;
        t_line[3] = a_status;
        this.m_csvError.writeNext(t_line);
    }

    public void writeError(Integer a_lineNumber, String a_message)
    {
        this.writeError(this.m_currentFile, a_lineNumber, a_message, "Skipped row");
    }

    public String getCurrentFile()
    {
        return this.m_currentFile;
    }

    public void setCurrentFile(String a_currentFile)
    {
        this.m_currentFile = a_currentFile;
    }

    public void writeError(String a_message, String a_status)
    {
        this.writeError(this.m_currentFile, null, a_message, a_status);
    }

    public void writeWarning(Integer a_rowNumber, String a_message)
    {
        this.writeWarning(this.m_currentFile, a_rowNumber, a_message);
    }

    public void writeWarning(String a_file, Integer a_rowNumber, String a_message)
    {
        String[] t_line = new String[3];
        t_line[0] = a_file;
        if (a_rowNumber == null)
        {
            t_line[1] = "";
        }
        else
        {
            t_line[1] = a_rowNumber.toString();
        }
        t_line[2] = a_message;
        this.m_csvWarning.writeNext(t_line);
    }

    public void writeWarning(String a_message)
    {
        this.writeWarning(this.m_currentFile, null, a_message);
    }
}

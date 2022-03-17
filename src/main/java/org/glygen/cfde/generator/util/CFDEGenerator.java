package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.om.DCC;
import org.glygen.cfde.generator.om.Project;

public class CFDEGenerator
{
    private static final String FOLDER_NAME_DOWNLOAD = "download";
    private static final String FOLDER_NAME_TSV = "tsv";

    private DCC m_dcc = null;
    private Project m_projectMaster = null;
    private Project m_projectGlyGen = null;
    private String m_namespace = null;

    public CFDEGenerator(DCC a_dcc, Project a_projectMaster, Project a_projectGlyGen,
            String a_namespace)
    {
        this.m_dcc = a_dcc;
        this.m_projectMaster = a_projectMaster;
        this.m_projectGlyGen = a_projectGlyGen;
        this.m_namespace = a_namespace;
    }

    public void createTSV(String a_configFile, String a_outputFolder, String a_mappingFolder)
            throws IOException
    {
        this.createSubFolders(a_outputFolder);
    }

    private void createSubFolders(String a_outputFolder) throws IOException
    {
        // download folder
        File t_file = new File(a_outputFolder + File.separator + FOLDER_NAME_DOWNLOAD);
        if (!t_file.mkdirs())
        {
            throw new IOException("Failed to create download folder: " + a_outputFolder
                    + File.separator + FOLDER_NAME_DOWNLOAD);
        }
        // tsv folder
        t_file = new File(a_outputFolder + File.separator + FOLDER_NAME_TSV);
        if (!t_file.mkdirs())
        {
            throw new IOException("Failed to create TSV folder: " + a_outputFolder + File.separator
                    + FOLDER_NAME_TSV);
        }
    }

}

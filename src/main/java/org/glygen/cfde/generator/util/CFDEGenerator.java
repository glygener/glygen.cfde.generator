package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.glygen.cfde.generator.csv.GlycanFileReader;
import org.glygen.cfde.generator.csv.ProteinFileReader;
import org.glygen.cfde.generator.om.CFDEFile;
import org.glygen.cfde.generator.om.DCC;
import org.glygen.cfde.generator.om.DataFileType;
import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Glycan;
import org.glygen.cfde.generator.om.Namespace;
import org.glygen.cfde.generator.om.Project;
import org.glygen.cfde.generator.om.Protein;
import org.glygen.cfde.generator.tsv.BiosampleDiseaseFile;
import org.glygen.cfde.generator.tsv.BiosampleFile;
import org.glygen.cfde.generator.tsv.BiosampleFromSubjectFile;
import org.glygen.cfde.generator.tsv.BiosampleGeneFile;
import org.glygen.cfde.generator.tsv.BiosampleInCollectionFile;
import org.glygen.cfde.generator.tsv.BiosampleSubstanceFile;
import org.glygen.cfde.generator.tsv.CollectionAnatomyFile;
import org.glygen.cfde.generator.tsv.CollectionCompoundFile;
import org.glygen.cfde.generator.tsv.CollectionDefinedByProjectFile;
import org.glygen.cfde.generator.tsv.CollectionDiseaseFile;
import org.glygen.cfde.generator.tsv.CollectionFile;
import org.glygen.cfde.generator.tsv.CollectionGeneFile;
import org.glygen.cfde.generator.tsv.CollectionPhenotypeFile;
import org.glygen.cfde.generator.tsv.CollectionProteinFile;
import org.glygen.cfde.generator.tsv.CollectionSubstanceFile;
import org.glygen.cfde.generator.tsv.CollectionTaxonomyFile;
import org.glygen.cfde.generator.tsv.DCCFile;
import org.glygen.cfde.generator.tsv.FileDescribesBiosampleFile;
import org.glygen.cfde.generator.tsv.FileDescribesCollectionFile;
import org.glygen.cfde.generator.tsv.FileDescribesSubjectFile;
import org.glygen.cfde.generator.tsv.FileFile;
import org.glygen.cfde.generator.tsv.FileInCollectionFile;
import org.glygen.cfde.generator.tsv.IdNamespaceFile;
import org.glygen.cfde.generator.tsv.ProjectFile;
import org.glygen.cfde.generator.tsv.ProjectInProjectFile;
import org.glygen.cfde.generator.tsv.SubjectDiseaseFile;
import org.glygen.cfde.generator.tsv.SubjectFile;
import org.glygen.cfde.generator.tsv.SubjectInCollectionFile;
import org.glygen.cfde.generator.tsv.SubjectPhenotypeFile;
import org.glygen.cfde.generator.tsv.SubjectRaceFile;
import org.glygen.cfde.generator.tsv.SubjectRoleTaxonomyFile;
import org.glygen.cfde.generator.tsv.SubjectSubstanceFile;

public class CFDEGenerator
{
    private static final String FOLDER_NAME_DOWNLOAD = "download";
    private static final String FOLDER_NAME_TSV = "tsv";
    private static final Integer LINE_LIMIT = 5;// Integer.MAX_VALUE;

    private DCC m_dcc = null;
    private Project m_projectMaster = null;
    private Project m_projectGlyGen = null;
    private Namespace m_namespace = null;

    private String m_outputFolder = null;
    private String m_mappingFolder = null;
    private String m_downloadFolder = null;

    // core files
    private DCCFile m_dccFile = null;
    private ProjectFile m_projectFile = null;
    private ProjectInProjectFile m_projectInProjectFile = null;
    private IdNamespaceFile m_idNamespaceFile = null;

    // GlyGen files
    private FileFile m_fileFile = null;
    private CollectionFile m_collectionFile = null;
    private FileDescribesCollectionFile m_fileDescribesCollectionFile = null;
    private CollectionDefinedByProjectFile m_collectionDefinedByProjectFile = null;
    private CollectionAnatomyFile m_collectionAnatomyFile = null;
    private CollectionDiseaseFile m_collectionDiseaseFile = null;
    private CollectionCompoundFile m_collectionCompoundFile = null;
    private CollectionProteinFile m_collectionProteinFile = null;
    private CollectionGeneFile m_collectionGeneFile = null;
    private CollectionTaxonomyFile m_collectionTaxonomyFile = null;

    // unused files
    private BiosampleDiseaseFile m_bioSampleDiseaseFile = null;
    private BiosampleFile m_bioSampleFile = null;
    private BiosampleFromSubjectFile m_bioSampleFromSubjectFile = null;
    private BiosampleGeneFile m_bioSampleGene = null;
    private BiosampleInCollectionFile m_bioSampleInCollectionFile = null;
    private BiosampleSubstanceFile m_bioSampleSubstanceFile = null;
    private CollectionPhenotypeFile m_collectionPhenotypeFile = null;
    private CollectionSubstanceFile m_collectionSubstanceFile = null;
    private FileDescribesBiosampleFile m_fileDescribesBiosampleFile = null;
    private FileDescribesSubjectFile m_fileDescribesSubjectFile = null;
    private FileInCollectionFile m_fileInCollectionFile = null;
    private SubjectDiseaseFile m_subjectDiseaseFile = null;
    private SubjectFile m_subjectFile = null;
    private SubjectInCollectionFile m_subjectInCollectionFile = null;
    private SubjectPhenotypeFile m_subPhenotypeFile = null;
    private SubjectRaceFile m_subjectRaceFile = null;
    private SubjectRoleTaxonomyFile m_subjectRoleTaxonomyFile = null;
    private SubjectSubstanceFile m_subjectSubstanceFile = null;

    public CFDEGenerator(DCC a_dcc, Project a_projectMaster, Project a_projectGlyGen,
            Namespace a_namespace)
    {
        this.m_dcc = a_dcc;
        this.m_projectMaster = a_projectMaster;
        this.m_projectGlyGen = a_projectGlyGen;
        this.m_namespace = a_namespace;
    }

    private void openFiles(String a_outputFolder) throws IOException
    {
        // core
        this.m_dccFile = new DCCFile(a_outputFolder, this.m_namespace.getId());
        this.m_projectFile = new ProjectFile(a_outputFolder, this.m_namespace.getId());
        this.m_projectInProjectFile = new ProjectInProjectFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_idNamespaceFile = new IdNamespaceFile(a_outputFolder);

        // glygen
        this.m_fileFile = new FileFile(a_outputFolder, this.m_namespace.getId());
        this.m_collectionFile = new CollectionFile(a_outputFolder, this.m_namespace.getId());
        this.m_fileDescribesCollectionFile = new FileDescribesCollectionFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionDefinedByProjectFile = new CollectionDefinedByProjectFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionAnatomyFile = new CollectionAnatomyFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionDiseaseFile = new CollectionDiseaseFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionCompoundFile = new CollectionCompoundFile(a_outputFolder,
                this.m_namespace.getId(), this.m_mappingFolder);
        this.m_collectionProteinFile = new CollectionProteinFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionGeneFile = new CollectionGeneFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionTaxonomyFile = new CollectionTaxonomyFile(a_outputFolder,
                this.m_namespace.getId());

        // empty
        this.m_bioSampleDiseaseFile = new BiosampleDiseaseFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_bioSampleFile = new BiosampleFile(a_outputFolder, this.m_namespace.getId());
        this.m_bioSampleFromSubjectFile = new BiosampleFromSubjectFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_bioSampleGene = new BiosampleGeneFile(a_outputFolder, this.m_namespace.getId());
        this.m_bioSampleInCollectionFile = new BiosampleInCollectionFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_bioSampleSubstanceFile = new BiosampleSubstanceFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionPhenotypeFile = new CollectionPhenotypeFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionSubstanceFile = new CollectionSubstanceFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_fileDescribesBiosampleFile = new FileDescribesBiosampleFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_fileDescribesSubjectFile = new FileDescribesSubjectFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_fileInCollectionFile = new FileInCollectionFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_subjectDiseaseFile = new SubjectDiseaseFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_subjectFile = new SubjectFile(a_outputFolder, this.m_namespace.getId());
        this.m_subjectInCollectionFile = new SubjectInCollectionFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_subPhenotypeFile = new SubjectPhenotypeFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_subjectRaceFile = new SubjectRaceFile(a_outputFolder, this.m_namespace.getId());
        this.m_subjectRoleTaxonomyFile = new SubjectRoleTaxonomyFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_subjectSubstanceFile = new SubjectSubstanceFile(a_outputFolder,
                this.m_namespace.getId());
    }

    private void closeFiles() throws IOException
    {
        // core
        this.m_dccFile.closeFile();
        this.m_projectFile.closeFile();
        this.m_projectInProjectFile.closeFile();
        this.m_idNamespaceFile.closeFile();

        // glygen
        this.m_fileFile.closeFile();
        this.m_collectionFile.closeFile();
        this.m_fileDescribesCollectionFile.closeFile();
        this.m_collectionDefinedByProjectFile.closeFile();
        this.m_collectionAnatomyFile.closeFile();
        this.m_collectionCompoundFile.closeFile();
        this.m_collectionDiseaseFile.closeFile();
        this.m_collectionGeneFile.closeFile();
        this.m_collectionProteinFile.closeFile();
        this.m_collectionTaxonomyFile.closeFile();

        // empty
        this.m_bioSampleDiseaseFile.closeFile();
        this.m_bioSampleFile.closeFile();
        this.m_bioSampleFromSubjectFile.closeFile();
        this.m_bioSampleGene.closeFile();
        this.m_bioSampleInCollectionFile.closeFile();
        this.m_bioSampleSubstanceFile.closeFile();
        this.m_collectionPhenotypeFile.closeFile();
        this.m_collectionSubstanceFile.closeFile();
        this.m_fileDescribesBiosampleFile.closeFile();
        this.m_fileDescribesSubjectFile.closeFile();
        this.m_fileInCollectionFile.closeFile();
        this.m_subjectDiseaseFile.closeFile();
        this.m_subjectFile.closeFile();
        this.m_subjectInCollectionFile.closeFile();
        this.m_subPhenotypeFile.closeFile();
        this.m_subjectRaceFile.closeFile();
        this.m_subjectRoleTaxonomyFile.closeFile();
        this.m_subjectSubstanceFile.closeFile();
    }

    public void createTSV(List<FileConfig> a_configFiles, String a_outputFolder,
            String a_mappingFolder) throws IOException
    {
        this.m_outputFolder = a_outputFolder;
        this.m_mappingFolder = a_mappingFolder;
        this.createSubFolders();
        this.openFiles(this.m_outputFolder + File.separator + CFDEGenerator.FOLDER_NAME_TSV);
        // DCC, ID Namespace, Project, Project in Project
        this.writeBasics();
        // process files
        for (FileConfig t_fileConfig : a_configFiles)
        {
            this.processFile(t_fileConfig);
        }
        this.closeFiles();
    }

    private void writeBasics()
    {
        // create the DCC entry
        this.m_dccFile.write(this.m_dcc, this.m_projectMaster);
        // namespace table
        this.m_idNamespaceFile.write(this.m_namespace);
        // create the root and glygen project
        this.m_projectFile.write(this.m_projectMaster);
        this.m_projectFile.write(this.m_projectGlyGen);
        // linking them
        this.m_projectInProjectFile.write(this.m_projectMaster, this.m_projectGlyGen);
    }

    private void createSubFolders() throws IOException
    {
        // download folder
        this.m_downloadFolder = this.m_outputFolder + File.separator
                + CFDEGenerator.FOLDER_NAME_DOWNLOAD;
        File t_file = new File(this.m_downloadFolder);
        if (!t_file.exists())
        {
            if (!t_file.mkdirs())
            {
                throw new IOException("Failed to create download folder: " + this.m_downloadFolder);
            }
        }
        // tsv folder
        t_file = new File(this.m_outputFolder + File.separator + CFDEGenerator.FOLDER_NAME_TSV);
        if (!t_file.exists())
        {
            if (!t_file.mkdirs())
            {
                throw new IOException("Failed to create TSV folder: " + this.m_outputFolder
                        + File.separator + FOLDER_NAME_TSV);
            }
        }
    }

    private void processFile(FileConfig a_fileConfig) throws IOException
    {
        // download the file
        Downloader t_downloader = new Downloader();
        // get the file name from the URL
        URL t_url = new URL(a_fileConfig.getFileUrl());
        String t_fileName = FilenameUtils.getName(t_url.getPath());
        String t_localFileName = this.createLocalFileName(t_fileName);
        String t_localFileNamePath = this.m_downloadFolder + File.separator + t_localFileName;
        t_downloader.downloadFile(a_fileConfig.getFileUrl(), t_localFileNamePath);
        CFDEFile t_cfdeFile = new CFDEFile();
        // general information from the config file
        t_cfdeFile.setAnalysisType(null);
        t_cfdeFile.setAssayType(null);
        t_cfdeFile.setCreationTime(a_fileConfig.getCreationTime());
        t_cfdeFile.setDataType(a_fileConfig.getDataType());
        t_cfdeFile.setFileFormat(a_fileConfig.getDataType());
        t_cfdeFile.setFilename(t_fileName);
        t_cfdeFile.setId(a_fileConfig.getLocalId());
        t_cfdeFile.setMimeType(a_fileConfig.getMimeType());
        t_cfdeFile.setPersistentId(a_fileConfig.getPersitentId());
        // file related information
        ChecksumUtil t_util = new ChecksumUtil();
        try
        {
            t_cfdeFile.setMd5(t_util.createMD5(t_localFileNamePath));
            t_cfdeFile.setFileSize(t_util.getFileSize(t_localFileNamePath));
            t_cfdeFile.setSha256(t_util.createSha256(t_localFileNamePath));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IOException(e.getMessage(), e);
        }
        // write the file entry
        this.m_fileFile.write(this.m_projectGlyGen, t_cfdeFile);
        // decide how to process the file
        if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_DATA))
        {
            this.processGlyGenProteinDataFile(t_localFileNamePath, a_fileConfig);
        }
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_GLYCAN_DATA))
        {
            this.processGlyGenGlycanDataFile(t_localFileNamePath, a_fileConfig);
        }
        else
        {
            throw new IOException(
                    "Unable to process files of type: " + a_fileConfig.getType().getKey());
        }
    }

    private String createLocalFileName(String a_fileName) throws MalformedURLException
    {
        SimpleDateFormat t_formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSS");
        Date t_currentTime = new Date(System.currentTimeMillis());
        String t_timeStamp = t_formatter.format(t_currentTime);
        return t_timeStamp + "-" + a_fileName;
    }

    private void processGlyGenProteinDataFile(String a_localFileNamePath, FileConfig a_fileConfig)
            throws IOException
    {
        // parse the file
        ProteinFileReader t_reader = new ProteinFileReader(CFDEGenerator.LINE_LIMIT);
        List<Protein> t_proteins = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_mappingFolder);
        for (Protein t_protein : t_proteins)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_protein.getUniprotAcc(), a_fileConfig,
                    "Information for protein ", this.m_projectGlyGen);
            // add the protein/gene to collection
            this.m_collectionProteinFile.write(t_collectionID, t_protein.getUniprotAcc());
            this.m_collectionGeneFile.write(t_collectionID, t_protein.getEnsemblAcc());
            // add the glycans to collection
            for (String t_compound : t_protein.getCompound())
            {
                this.m_collectionCompoundFile.write(t_collectionID, t_compound);
            }
            // add disease to collection
            for (String t_disease : t_protein.getDisease())
            {
                this.m_collectionDiseaseFile.write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_protein.getAnatomy())
            {
                this.m_collectionAnatomyFile.write(t_collectionID, t_anatomy);
            }
            // species
            this.m_collectionTaxonomyFile.write(t_collectionID, t_protein.getSpecies());
        }
    }

    private void processGlyGenGlycanDataFile(String a_localFileNamePath, FileConfig a_fileConfig)
            throws IOException
    {
        // parse the file
        GlycanFileReader t_reader = new GlycanFileReader(CFDEGenerator.LINE_LIMIT);
        List<Glycan> t_glycans = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_mappingFolder);
        for (Glycan t_glycan : t_glycans)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_glycan.getGlycanAcc(), a_fileConfig,
                    "Information for glycan ", this.m_projectGlyGen);
            // glycan
            this.m_collectionCompoundFile.write(t_collectionID, t_glycan.getGlycanAcc());
            // add the protein/gene to collection
            HashMap<String, Protein> t_proteins = t_glycan.getProteins();
            for (Protein t_protein : t_proteins.values())
            {
                this.m_collectionProteinFile.write(t_collectionID, t_protein.getUniprotAcc());
                this.m_collectionGeneFile.write(t_collectionID, t_protein.getEnsemblAcc());
            }
            // add disase to collection
            for (String t_disease : t_glycan.getDisease())
            {
                this.m_collectionDiseaseFile.write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_glycan.getAnatomy())
            {
                this.m_collectionAnatomyFile.write(t_collectionID, t_anatomy);
            }
            // species
            for (String t_species : t_glycan.getSpecies())
            {
                this.m_collectionTaxonomyFile.write(t_collectionID, t_species);
            }
        }
    }

    private String createCollection(String a_id, FileConfig a_fileConfig,
            String a_descriptionPrefix, Project a_project)
    {
        // make the collection
        String t_collectionID = "COL_" + a_id + "_" + a_fileConfig.getLocalId();
        this.m_collectionFile.write(t_collectionID,
                "Protein " + a_id + "(" + a_fileConfig.getLocalId() + ")", a_descriptionPrefix
                        + a_id + " in the context of file " + a_fileConfig.getLocalId());
        // associate collection with the file
        this.m_fileDescribesCollectionFile.write(t_collectionID, a_fileConfig.getLocalId());
        // associate with project
        this.m_collectionDefinedByProjectFile.write(t_collectionID, a_project.getId());
        return t_collectionID;
    }

}

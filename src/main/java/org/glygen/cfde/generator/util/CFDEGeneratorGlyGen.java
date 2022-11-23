package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.glygen.cfde.generator.csv.GlycanFileReader;
import org.glygen.cfde.generator.csv.ProteinFileReader;
import org.glygen.cfde.generator.csv.ProteinGlycanMixFileReader;
import org.glygen.cfde.generator.csv.ProteinNoGeneFileReader;
import org.glygen.cfde.generator.om.CFDEFile;
import org.glygen.cfde.generator.om.DataFileType;
import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Glycan;
import org.glygen.cfde.generator.om.Project;
import org.glygen.cfde.generator.om.Protein;

// https://data.glygen.org/ln2data/releases/data/current/reviewed/glycan_pubchem_status.csv

// https://github.com/nih-cfde/published-documentation/wiki/TableInfo:-file.tsv
public class CFDEGeneratorGlyGen
{
    private static final Integer LINE_LIMIT = Integer.MAX_VALUE;

    private boolean m_writeGeneLess = true;

    private TSVGenerator m_tsvGenerator = null;
    private HashSet<String> m_proteinIDs = new HashSet<>();
    private HashMap<String, String> m_glycanIDs = new HashMap<>();

    public CFDEGeneratorGlyGen(TSVGenerator a_tsvGenerator, boolean a_writeGeneLess)
    {
        super();
        this.m_tsvGenerator = a_tsvGenerator;
        this.m_writeGeneLess = a_writeGeneLess;
    }

    public void process(List<FileConfig> a_configFiles)
    {
        for (FileConfig t_fileConfig : a_configFiles)
        {
            this.m_tsvGenerator.getErrorFile().setCurrentFile(t_fileConfig.getLocalId());
            System.out.println("Processing " + t_fileConfig.getLocalId());
            try
            {
                this.processFile(t_fileConfig);
            }
            catch (Exception e)
            {
                this.m_tsvGenerator.getErrorFile().writeError(t_fileConfig.getLocalId(), "",
                        e.getMessage(), "Skipped file");
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
        String t_localFileNamePath = this.m_tsvGenerator.getDownloadFolder() + File.separator
                + t_localFileName;
        t_downloader.downloadFile(a_fileConfig.getFileUrl(), t_localFileNamePath);
        CFDEFile t_cfdeFile = new CFDEFile();
        // general information from the config file
        t_cfdeFile.setAnalysisType(null);
        t_cfdeFile.setAssayType(null);
        t_cfdeFile.setCreationTime(a_fileConfig.getCreationTime());
        t_cfdeFile.setDataType(a_fileConfig.getDataType());
        t_cfdeFile.setFileFormat(a_fileConfig.getFileFormat());
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
        this.m_tsvGenerator.getFileFile().write(this.m_tsvGenerator.getProjectGlyGen(), t_cfdeFile);
        // decide how to process the file
        if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_DATA))
        {
            List<Protein> t_proteins = this.processGlyGenProteinDataFile(t_localFileNamePath,
                    a_fileConfig);
            this.logProteins(t_proteins);
        }
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_NO_GENE_DATA))
        {
            List<Protein> t_proteins = this.processGlyGenProteinNoGeneDataFile(t_localFileNamePath,
                    a_fileConfig);
            this.logProteins(t_proteins);
        }
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_GLYCAN_DATA))
        {
            List<Glycan> t_glycans = this.processGlyGenGlycanDataFile(t_localFileNamePath,
                    a_fileConfig);
            this.logGlycans(t_glycans);
        }
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_GLYCAN_MIX_DATA))
        {
            this.processGlyGenProteinGlycanMixDataFile(t_localFileNamePath, a_fileConfig);
        }
        else
        {
            throw new IOException(
                    "Unable to process files of type: " + a_fileConfig.getType().getKey());
        }
    }

    private void processGlyGenProteinGlycanMixDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinGlycanMixFileReader t_reader = new ProteinGlycanMixFileReader(
                CFDEGeneratorGlyGen.LINE_LIMIT);
        t_reader.loadFile(a_localFileNamePath, a_fileConfig, this.m_tsvGenerator.getMappingFolder(),
                this.m_tsvGenerator.getErrorFile());
        List<Protein> t_proteinList = t_reader.getProteinList();
        for (Protein t_protein : t_proteinList)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_protein.getUniprotAcc(), a_fileConfig,
                    "Information for protein ", this.m_tsvGenerator.getProjectGlyGen());
            // add the protein/gene to collection
            this.m_tsvGenerator.getCollectionProteinFile().write(t_collectionID,
                    t_protein.getUniprotAcc());
            if (t_protein.getEnsemblAcc() != null)
            {
                if (!this.m_writeGeneLess)
                {
                    this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                            t_protein.getEnsemblAcc());
                }
            }
            // add the glycans to collection
            for (String t_compound : t_protein.getCompound())
            {
                this.m_tsvGenerator.getCollectionCompoundFile().write(t_collectionID, t_compound);
            }
            // add disease to collection
            for (String t_disease : t_protein.getDisease())
            {
                this.m_tsvGenerator.getCollectionDiseaseFile().write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_protein.getAnatomy())
            {
                this.m_tsvGenerator.getCollectionAnatomyFile().write(t_collectionID, t_anatomy);
            }
            // species
            this.m_tsvGenerator.getCollectionTaxonomyFile().write(t_collectionID,
                    t_protein.getSpecies());
        }
        this.logProteins(t_proteinList);
        List<Glycan> t_glycanList = t_reader.getGlycanList();
        for (Glycan t_glycan : t_glycanList)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_glycan.getGlycanAcc(), a_fileConfig,
                    "Information for glycan ", this.m_tsvGenerator.getProjectGlyGen());
            // glycan
            this.m_tsvGenerator.getCollectionCompoundFile().write(t_collectionID,
                    t_glycan.getGlycanAcc());
            // add the protein/gene to collection
            HashMap<String, Protein> t_proteins = t_glycan.getProteins();
            for (Protein t_protein : t_proteins.values())
            {
                this.m_tsvGenerator.getCollectionProteinFile().write(t_collectionID,
                        t_protein.getUniprotAcc());
                if (t_protein.getEnsemblAcc() != null)
                {
                    if (!this.m_writeGeneLess)
                    {
                        this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                                t_protein.getEnsemblAcc());
                    }
                }
            }
            // add disase to collection
            for (String t_disease : t_glycan.getDisease())
            {
                this.m_tsvGenerator.getCollectionDiseaseFile().write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_glycan.getAnatomy())
            {
                this.m_tsvGenerator.getCollectionAnatomyFile().write(t_collectionID, t_anatomy);
            }
            // species
            for (String t_species : t_glycan.getSpecies())
            {
                this.m_tsvGenerator.getCollectionTaxonomyFile().write(t_collectionID, t_species);
            }
        }
        this.logGlycans(t_glycanList);
    }

    private void logGlycans(List<Glycan> a_glycans)
    {
        for (Glycan t_glycan : a_glycans)
        {
            String t_id = this.m_tsvGenerator.getCollectionCompoundFile()
                    .getCFDEnamespace(t_glycan.getGlycanAcc());
            this.m_glycanIDs.put(t_glycan.getGlycanAcc(), t_id);
            if (t_glycan.getProteins() != null)
            {
                for (String t_proteinId : t_glycan.getProteins().keySet())
                {
                    this.m_proteinIDs.add(t_proteinId);
                }
            }
        }
    }

    private void logProteins(List<Protein> a_proteins)
    {
        for (Protein t_protein : a_proteins)
        {
            this.m_proteinIDs.add(t_protein.getUniprotAcc());
            if (t_protein.getCompound() != null)
            {
                for (String t_glyTouCanId : t_protein.getCompound())
                {
                    String t_id = this.m_tsvGenerator.getCollectionCompoundFile()
                            .getCFDEnamespace(t_glyTouCanId);
                    this.m_glycanIDs.put(t_glyTouCanId, t_id);
                }
            }
        }
    }

    private String createLocalFileName(String a_fileName) throws MalformedURLException
    {
        SimpleDateFormat t_formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSS");
        Date t_currentTime = new Date(System.currentTimeMillis());
        String t_timeStamp = t_formatter.format(t_currentTime);
        return t_timeStamp + "-" + a_fileName;
    }

    private List<Protein> processGlyGenProteinDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinFileReader t_reader = new ProteinFileReader(CFDEGeneratorGlyGen.LINE_LIMIT);
        List<Protein> t_proteins = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_tsvGenerator.getMappingFolder(), this.m_tsvGenerator.getErrorFile());
        for (Protein t_protein : t_proteins)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_protein.getUniprotAcc(), a_fileConfig,
                    "Information for protein ", this.m_tsvGenerator.getProjectGlyGen());
            // add the protein/gene to collection
            this.m_tsvGenerator.getCollectionProteinFile().write(t_collectionID,
                    t_protein.getUniprotAcc());
            if (t_protein.getEnsemblAcc() != null)
            {
                if (!this.m_writeGeneLess)
                {
                    this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                            t_protein.getEnsemblAcc());
                }
            }
            // add the glycans to collection
            for (String t_compound : t_protein.getCompound())
            {
                this.m_tsvGenerator.getCollectionCompoundFile().write(t_collectionID, t_compound);
            }
            // add disease to collection
            for (String t_disease : t_protein.getDisease())
            {
                this.m_tsvGenerator.getCollectionDiseaseFile().write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_protein.getAnatomy())
            {
                this.m_tsvGenerator.getCollectionAnatomyFile().write(t_collectionID, t_anatomy);
            }
            // species
            this.m_tsvGenerator.getCollectionTaxonomyFile().write(t_collectionID,
                    t_protein.getSpecies());
        }
        return t_proteins;
    }

    private List<Protein> processGlyGenProteinNoGeneDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinNoGeneFileReader t_reader = new ProteinNoGeneFileReader(
                CFDEGeneratorGlyGen.LINE_LIMIT);
        List<Protein> t_proteins = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_tsvGenerator.getMappingFolder(), this.m_tsvGenerator.getErrorFile());
        for (Protein t_protein : t_proteins)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_protein.getUniprotAcc(), a_fileConfig,
                    "Information for protein ", this.m_tsvGenerator.getProjectGlyGen());
            // add the protein/gene to collection
            this.m_tsvGenerator.getCollectionProteinFile().write(t_collectionID,
                    t_protein.getUniprotAcc());
            // add the glycans to collection
            for (String t_compound : t_protein.getCompound())
            {
                this.m_tsvGenerator.getCollectionCompoundFile().write(t_collectionID, t_compound);
            }
            // add disease to collection
            for (String t_disease : t_protein.getDisease())
            {
                this.m_tsvGenerator.getCollectionDiseaseFile().write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_protein.getAnatomy())
            {
                this.m_tsvGenerator.getCollectionAnatomyFile().write(t_collectionID, t_anatomy);
            }
            // species
            this.m_tsvGenerator.getCollectionTaxonomyFile().write(t_collectionID,
                    t_protein.getSpecies());
        }
        return t_proteins;
    }

    private List<Glycan> processGlyGenGlycanDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        GlycanFileReader t_reader = new GlycanFileReader(CFDEGeneratorGlyGen.LINE_LIMIT);
        List<Glycan> t_glycans = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_tsvGenerator.getMappingFolder(), this.m_tsvGenerator.getErrorFile());
        for (Glycan t_glycan : t_glycans)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_glycan.getGlycanAcc(), a_fileConfig,
                    "Information for glycan ", this.m_tsvGenerator.getProjectGlyGen());
            // glycan

            this.m_tsvGenerator.getCollectionCompoundFile().write(t_collectionID,
                    t_glycan.getGlycanAcc());
            // add the protein/gene to collection
            HashMap<String, Protein> t_proteins = t_glycan.getProteins();
            for (Protein t_protein : t_proteins.values())
            {
                this.m_tsvGenerator.getCollectionProteinFile().write(t_collectionID,
                        t_protein.getUniprotAcc());
                if (t_protein.getEnsemblAcc() != null)
                {
                    if (!this.m_writeGeneLess)
                    {
                        this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                                t_protein.getEnsemblAcc());
                    }
                }
            }
            // add disase to collection
            for (String t_disease : t_glycan.getDisease())
            {
                this.m_tsvGenerator.getCollectionDiseaseFile().write(t_collectionID, t_disease);
            }
            // add anatomy to collection
            for (String t_anatomy : t_glycan.getAnatomy())
            {
                this.m_tsvGenerator.getCollectionAnatomyFile().write(t_collectionID, t_anatomy);
            }
            // species
            for (String t_species : t_glycan.getSpecies())
            {
                this.m_tsvGenerator.getCollectionTaxonomyFile().write(t_collectionID, t_species);
            }
        }
        return t_glycans;
    }

    private String createCollection(String a_id, FileConfig a_fileConfig,
            String a_descriptionPrefix, Project a_project)
    {
        // make the collection
        String t_collectionID = "COL_" + a_id + "_" + a_fileConfig.getLocalId();
        this.m_tsvGenerator.getCollectionFile().write(t_collectionID,
                "Protein " + a_id + "(" + a_fileConfig.getLocalId() + ")", a_descriptionPrefix
                        + a_id + " in the context of file " + a_fileConfig.getLocalId());
        // associate collection with the file
        this.m_tsvGenerator.getFileDescribesCollectionFile().write(t_collectionID,
                a_fileConfig.getLocalId());
        // associate with project
        this.m_tsvGenerator.getCollectionDefinedByProjectFile().write(t_collectionID,
                a_project.getId());
        return t_collectionID;
    }

    public HashSet<String> getProteinIDs()
    {
        return this.m_proteinIDs;
    }

    public HashMap<String, String> getGlycanIDs()
    {
        return this.m_glycanIDs;
    }

}

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
import org.glygen.cfde.generator.csv.ProteinGlycanMixNoGeneFileReader;
import org.glygen.cfde.generator.csv.ProteinNoGeneFileReader;
import org.glygen.cfde.generator.csv.ProteinPhosphoFileReader;
import org.glygen.cfde.generator.csv.ProteinPhosphoNoGeneFileReader;
import org.glygen.cfde.generator.om.CFDEFile;
import org.glygen.cfde.generator.om.DataFileType;
import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Glycan;
import org.glygen.cfde.generator.om.Project;
import org.glygen.cfde.generator.om.Protein;
import org.glygen.cfde.generator.om.PtmType;
import org.glygen.cfde.generator.om.Site;

// https://data.glygen.org/ln2data/releases/data/current/reviewed/glycan_pubchem_status.csv

// https://github.com/nih-cfde/published-documentation/wiki/TableInfo:-file.tsv
public class CFDEGeneratorGlyGen
{
    private static final Integer LINE_LIMIT = Integer.MAX_VALUE;
    private static final String SITE_TYPE_DEFINED = "defined";
    private static final String SITE_TYPE_UNKNOWN = "unknown";
    private static final String SITE_TYPE_RANGE = "range";

    private boolean m_writeGeneLess = true;

    private TSVGenerator m_tsvGenerator = null;
    private HashSet<String> m_proteinIDs = new HashSet<>();
    private HashMap<String, String> m_glycanIDs = new HashMap<>();
    private HashMap<String, HashMap<Site, String>> m_ptmIds = new HashMap<String, HashMap<Site, String>>();

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
        t_cfdeFile.setAccessUrl(a_fileConfig.getAccessUrl());
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
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_GLYCAN_MIX_NO_GENE_DATA))
        {
            this.processGlyGenProteinGlycanMixNoGeneDataFile(t_localFileNamePath, a_fileConfig);
        }
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_PHOSPHO_DATA))
        {
            List<Protein> t_proteins = this.processGlyGenProteinPhophoDataFile(t_localFileNamePath,
                    a_fileConfig);
            this.logProteins(t_proteins);
        }
        else if (a_fileConfig.getType().equals(DataFileType.GLYGEN_PROTEIN_PHOSPHO_NO_GENE_DATA))
        {
            List<Protein> t_proteins = this
                    .processGlyGenProteinPhophoNoGeneDataFile(t_localFileNamePath, a_fileConfig);
            this.logProteins(t_proteins);
        }
        else
        {
            throw new IOException(
                    "Unable to process files of type: " + a_fileConfig.getType().getKey());
        }
    }

    private void processGlyGenProteinGlycanMixNoGeneDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinGlycanMixNoGeneFileReader t_reader = new ProteinGlycanMixNoGeneFileReader(
                CFDEGeneratorGlyGen.LINE_LIMIT);
        t_reader.loadFile(a_localFileNamePath, a_fileConfig, this.m_tsvGenerator.getMappingFolder(),
                this.m_tsvGenerator.getErrorFile());
        List<Protein> t_proteinList = t_reader.getProteinList();
        for (Protein t_protein : t_proteinList)
        {
            String t_collectionID = this.writeCommonProteinInformation(t_protein, a_fileConfig);
            // add the glycans to collection
            this.writeGlycosylation(t_protein, t_collectionID);
            this.writePTMs(t_protein, t_collectionID);
            if (t_protein.getEnsemblAcc() != null)
            {
                if (!this.m_writeGeneLess)
                {
                    this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                            t_protein.getEnsemblAcc());
                }
            }
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
            String t_collectionID = this.writeCommonProteinInformation(t_protein, a_fileConfig);
            // add the glycans to collection
            this.writeGlycosylation(t_protein, t_collectionID);
            this.writePTMs(t_protein, t_collectionID);
            if (t_protein.getEnsemblAcc() != null)
            {
                if (!this.m_writeGeneLess)
                {
                    this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                            t_protein.getEnsemblAcc());
                }
            }
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
            if (t_protein.getGlycans() != null)
            {
                for (String t_glyTouCanId : t_protein.getGlycans())
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
        t_reader.loadFile(a_localFileNamePath, a_fileConfig, this.m_tsvGenerator.getMappingFolder(),
                this.m_tsvGenerator.getErrorFile());
        List<Protein> t_proteins = t_reader.getProteinList();
        for (Protein t_protein : t_proteins)
        {
            String t_collectionID = this.writeCommonProteinInformation(t_protein, a_fileConfig);
            // add the glycans to collection
            this.writeGlycosylation(t_protein, t_collectionID);
            this.writePTMs(t_protein, t_collectionID);
            if (t_protein.getEnsemblAcc() != null)
            {
                if (!this.m_writeGeneLess)
                {
                    this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                            t_protein.getEnsemblAcc());
                }
            }
        }
        return t_proteins;
    }

    private List<Protein> processGlyGenProteinPhophoDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinPhosphoFileReader t_reader = new ProteinPhosphoFileReader(
                CFDEGeneratorGlyGen.LINE_LIMIT);
        t_reader.loadFile(a_localFileNamePath, a_fileConfig, this.m_tsvGenerator.getMappingFolder(),
                this.m_tsvGenerator.getErrorFile());
        List<Protein> t_proteins = t_reader.getProteinList();
        for (Protein t_protein : t_proteins)
        {
            String t_collectionID = this.writeCommonProteinInformation(t_protein, a_fileConfig);
            // add the glycans to collection
            this.writePTMs(t_protein, t_collectionID);
            if (t_protein.getEnsemblAcc() != null)
            {
                if (!this.m_writeGeneLess)
                {
                    this.m_tsvGenerator.getCollectionGeneFile().write(t_collectionID,
                            t_protein.getEnsemblAcc());
                }
            }
        }
        return t_proteins;
    }

    private List<Protein> processGlyGenProteinNoGeneDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinNoGeneFileReader t_reader = new ProteinNoGeneFileReader(
                CFDEGeneratorGlyGen.LINE_LIMIT);
        t_reader.loadFile(a_localFileNamePath, a_fileConfig, this.m_tsvGenerator.getMappingFolder(),
                this.m_tsvGenerator.getErrorFile());
        List<Protein> t_proteins = t_reader.getProteinList();
        for (Protein t_protein : t_proteins)
        {
            String t_collectionID = this.writeCommonProteinInformation(t_protein, a_fileConfig);
            // add the glycans to collection
            this.writeGlycosylation(t_protein, t_collectionID);
            this.writePTMs(t_protein, t_collectionID);
        }
        return t_proteins;
    }

    private List<Protein> processGlyGenProteinPhophoNoGeneDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinPhosphoNoGeneFileReader t_reader = new ProteinPhosphoNoGeneFileReader(
                CFDEGeneratorGlyGen.LINE_LIMIT);
        t_reader.loadFile(a_localFileNamePath, a_fileConfig, this.m_tsvGenerator.getMappingFolder(),
                this.m_tsvGenerator.getErrorFile());
        List<Protein> t_proteins = t_reader.getProteinList();
        for (Protein t_protein : t_proteins)
        {
            String t_collectionID = this.writeCommonProteinInformation(t_protein, a_fileConfig);
            // add the glycans to collection
            this.writeGlycosylation(t_protein, t_collectionID);
            this.writePTMs(t_protein, t_collectionID);
        }
        return t_proteins;
    }

    private void writePTMs(Protein a_protein, String a_collectionID)
    {
        HashMap<Site, String> t_proteinPtmIds = this.m_ptmIds.get(a_protein.getUniprotAcc());
        if (t_proteinPtmIds == null)
        {
            t_proteinPtmIds = new HashMap<Site, String>();
            this.m_ptmIds.put(a_protein.getUniprotAcc(), t_proteinPtmIds);
        }
        for (Site t_site : a_protein.getPtmSites())
        {
            String t_ptmId = t_proteinPtmIds.get(t_site);
            if (t_ptmId == null)
            {
                // PTM does not exist yet
                String t_siteType = this.getSiteType(t_site);
                if (t_siteType == "")
                {
                    this.m_tsvGenerator.getErrorFile().writeError("Unable to figure out site type",
                            "Collection ID " + a_collectionID + " protein "
                                    + a_protein.getUniprotAcc() + " site "
                                    + t_site.getPositionOne().toString());
                }
                String t_ptmType = this.getPtmType(t_site);
                if (t_ptmType == "")
                {
                    this.m_tsvGenerator.getErrorFile().writeError("Unable to figure out ptm type",
                            "Collection ID " + a_collectionID + " protein "
                                    + a_protein.getUniprotAcc() + " site "
                                    + t_site.getPositionOneAA().toString());
                }
                String t_ptmSubType = this.getPtmSubType(t_site, t_siteType);
                if (t_ptmSubType == "" && t_siteType.equals(CFDEGeneratorGlyGen.SITE_TYPE_DEFINED))
                {
                    this.m_tsvGenerator.getErrorFile().writeError(
                            "Unable to figure out ptm sub type",
                            "Collection ID " + a_collectionID + " protein "
                                    + a_protein.getUniprotAcc() + " site "
                                    + t_site.getPositionOneAA().toString());
                }
                String t_ptmUniqueString = this.createUniqueString(a_protein.getUniprotAcc(),
                        t_site.getPositionOne(), t_site.getPositionOneAA(), t_site.getPositionTwo(),
                        t_site.getPositionTwoAA(), t_siteType, t_ptmType, t_ptmSubType);
                if (t_siteType.equals(CFDEGeneratorGlyGen.SITE_TYPE_DEFINED))
                {
                    if (this.m_tsvGenerator.getPtmFile().write(t_ptmUniqueString,
                            a_protein.getUniprotAcc(), t_site.getPositionOne().toString(),
                            t_site.getPositionOneAA(), "", "", t_siteType, t_ptmType, t_ptmSubType,
                            this.m_tsvGenerator.getErrorFile()))
                    {
                        t_proteinPtmIds.put(t_site, t_ptmUniqueString);
                        t_ptmId = t_ptmUniqueString;
                    }
                }
                else if (t_siteType.equals(CFDEGeneratorGlyGen.SITE_TYPE_UNKNOWN))
                {
                    if (this.m_tsvGenerator.getPtmFile().write(t_ptmUniqueString,
                            a_protein.getUniprotAcc(), "-1", "X", "", "", t_siteType, t_ptmType,
                            t_ptmSubType, this.m_tsvGenerator.getErrorFile()))
                    {
                        t_proteinPtmIds.put(t_site, t_ptmUniqueString);
                        t_ptmId = t_ptmUniqueString;
                    }
                }
                else if (t_siteType.equals(CFDEGeneratorGlyGen.SITE_TYPE_RANGE))
                {
                    if (this.m_tsvGenerator.getPtmFile().write(t_ptmUniqueString,
                            a_protein.getUniprotAcc(), t_site.getPositionOne().toString(),
                            t_site.getPositionOneAA(), t_site.getPositionTwo().toString(),
                            t_site.getPositionTwoAA(), t_siteType, t_ptmType, t_ptmSubType,
                            this.m_tsvGenerator.getErrorFile()))
                    {
                        t_proteinPtmIds.put(t_site, t_ptmUniqueString);
                        t_ptmId = t_ptmUniqueString;
                    }
                }
            }
            if (t_ptmId != null)
            {
                this.m_tsvGenerator.getCollectionPtmFile().write(a_collectionID,
                        t_ptmId.toString());
            }
        }
    }

    private String createUniqueString(String a_uniprotAcc, Integer a_positionOne,
            String a_positionOneAA, Integer a_positionTwo, String a_positionTwoAA,
            String a_siteType, String a_ptmType, String a_ptmSubType)
    {
        StringBuffer t_compositeKey = new StringBuffer(a_uniprotAcc);
        t_compositeKey.append("_");
        if (a_positionOne != null)
        {
            t_compositeKey.append(a_positionOne);
        }
        if (a_positionOneAA != null)
        {
            t_compositeKey.append(a_positionOneAA);
        }
        t_compositeKey.append("_");
        if (a_positionTwo != null)
        {
            t_compositeKey.append(a_positionTwo);
        }
        if (a_positionTwoAA != null)
        {
            t_compositeKey.append(a_positionTwoAA);
        }
        t_compositeKey.append("_");
        if (a_siteType != null)
        {
            t_compositeKey.append(a_siteType);
        }
        t_compositeKey.append("_");
        if (a_ptmType != null)
        {
            t_compositeKey.append(a_ptmType);
        }
        t_compositeKey.append("_");
        if (a_ptmSubType != null)
        {
            t_compositeKey.append(a_ptmSubType);
        }
        return t_compositeKey.toString().toLowerCase();
    }

    private String getPtmSubType(Site a_site, String a_siteType)
    {
        if (a_siteType.equals(CFDEGeneratorGlyGen.SITE_TYPE_DEFINED))
        {
            if (a_site.getPtmType().equals(PtmType.GLYCOSYLATION))
            {
                if (a_site.getPositionOneAA().equals("Asn"))
                {
                    return "GO:0006487";
                }
                if (a_site.getPositionOneAA().equals("Ser"))
                {
                    return "GO:000649";
                }
                if (a_site.getPositionOneAA().equals("Thr"))
                {
                    return "GO:000649";
                }
                if (a_site.getPositionOneAA().equals("Tyr"))
                {
                    return "GO:000649";
                }
                if (a_site.getPositionOneAA().equals("Trp"))
                {
                    return "GO:0018103";
                }
                if (a_site.getPositionOneAA().equals("Cys"))
                {
                    return "GO:0018280";
                }
            }
            if (a_site.getPtmType().equals(PtmType.PHOSPHORYLATION))
            {
                if (a_site.getPositionOneAA().equals("Ser"))
                {
                    return "GO:0018105";
                }
                if (a_site.getPositionOneAA().equals("Thr"))
                {
                    return "GO:0018107";
                }
                if (a_site.getPositionOneAA().equals("Tyr"))
                {
                    return "GO:0018108";
                }
            }
        }
        return "";
    }

    private String getPtmType(Site a_site)
    {
        if (a_site.getPtmType().equals(PtmType.GLYCOSYLATION))
        {
            return "GO:0006486";
        }
        if (a_site.getPtmType().equals(PtmType.PHOSPHORYLATION))
        {
            return "GO:0006468";
        }
        return "";
    }

    private String getSiteType(Site a_site)
    {
        if (a_site.getPositionOne() == null)
        {
            return CFDEGeneratorGlyGen.SITE_TYPE_UNKNOWN;
        }
        if (a_site.getPositionTwo() == null)
        {
            return CFDEGeneratorGlyGen.SITE_TYPE_DEFINED;
        }
        if (a_site.getPositionOne().equals(a_site.getPositionTwo()))
        {
            return CFDEGeneratorGlyGen.SITE_TYPE_DEFINED;
        }
        return CFDEGeneratorGlyGen.SITE_TYPE_RANGE;
    }

    private void writeGlycosylation(Protein a_protein, String a_collectionID)
    {
        for (String t_glycan : a_protein.getGlycans())
        {
            // write compound file
            this.m_tsvGenerator.getCollectionCompoundFile().write(a_collectionID, t_glycan);
        }
    }

    private String writeCommonProteinInformation(Protein a_protein, FileConfig a_fileConfig)
    {
        // create collection and associate with file
        String t_collectionID = this.createCollection(a_protein.getUniprotAcc(), a_fileConfig,
                "Information for protein ", this.m_tsvGenerator.getProjectGlyGen());
        // add the protein/gene to collection
        this.m_tsvGenerator.getCollectionProteinFile().write(t_collectionID,
                a_protein.getUniprotAcc());
        // add disease to collection
        for (String t_disease : a_protein.getDisease())
        {
            this.m_tsvGenerator.getCollectionDiseaseFile().write(t_collectionID, t_disease);
        }
        // add anatomy to collection
        for (String t_anatomy : a_protein.getAnatomy())
        {
            this.m_tsvGenerator.getCollectionAnatomyFile().write(t_collectionID, t_anatomy);
        }
        // species
        this.m_tsvGenerator.getCollectionTaxonomyFile().write(t_collectionID,
                a_protein.getSpecies());
        return t_collectionID;
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

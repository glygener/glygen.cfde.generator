package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.glygen.cfde.generator.csv.CSVError;
import org.glygen.cfde.generator.csv.GlycanFileReader;
import org.glygen.cfde.generator.csv.GlycanFilter;
import org.glygen.cfde.generator.csv.ProteinFileReader;
import org.glygen.cfde.generator.csv.ProteinNoGeneFileReader;
import org.glygen.cfde.generator.json.ErrorResponse;
import org.glygen.cfde.generator.json.dataset.Block;
import org.glygen.cfde.generator.json.dataset.BlockLayout;
import org.glygen.cfde.generator.json.dataset.Dataset;
import org.glygen.cfde.generator.json.dataset.Image;
import org.glygen.cfde.generator.json.dataset.Layout;
import org.glygen.cfde.generator.json.dataset.PrintedSlide;
import org.glygen.cfde.generator.json.dataset.ProcessedData;
import org.glygen.cfde.generator.json.dataset.RawData;
import org.glygen.cfde.generator.json.dataset.Slide;
import org.glygen.cfde.generator.json.dataset.UploadedFile;
import org.glygen.cfde.generator.json.datasetlist.DatasetList;
import org.glygen.cfde.generator.json.datasetlist.DatasetSimple;
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
import org.glygen.cfde.generator.tsv.CollectionInCollectionFile;
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

import com.fasterxml.jackson.databind.ObjectMapper;

// https://data.glygen.org/ln2data/releases/data/current/reviewed/glycan_pubchem_status.csv

// https://github.com/nih-cfde/published-documentation/wiki/TableInfo:-file.tsv
public class CFDEGenerator
{
    private static final String ARRAY_API_BASE_URL = "https://glygen.ccrc.uga.edu/array/api/";
    private static final String FOLDER_NAME_DOWNLOAD = "download";
    private static final String FOLDER_NAME_TSV = "tsv";
    private static final Integer LINE_LIMIT = Integer.MAX_VALUE;
    private static final String ARRAY_ANALYSIS_TYPE = "OBI:0001985";
    private static final String ARRAY_ASSAY_TYPE = "OBI:0001985";
    private static final String ARRAY_DATA_TYPE_IMAGE = "data:1714";
    private static final String ARRAY_DATA_TYPE_RAW = "data:3110";
    private static final String ARRAY_DATA_TYPE_PROCESSED = "data:3111";

    private GlycanFilter m_glycanBlackList = new GlycanFilter();

    private DCC m_dcc = null;
    private Project m_projectMaster = null;
    private Project m_projectGlyGen = null;
    private Project m_projectArray = null;
    private Namespace m_namespace = null;

    private String m_outputFolder = null;
    private String m_mappingFolder = null;
    private String m_downloadFolder = null;

    // error reporting file
    private CSVError m_errorFile = null;

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
    private CollectionInCollectionFile m_collectionInCollection = null;

    private HashSet<String> m_proteinIDs = new HashSet<>();
    private HashMap<String, String> m_glycanIDs = new HashMap<>();

    private HashMap<String, List<String>> m_blockLayoutCache = new HashMap<>();

    public CFDEGenerator(DCC a_dcc, Project a_projectMaster, Project a_projectGlyGen,
            Project a_projectArray, Namespace a_namespace)
    {
        this.m_dcc = a_dcc;
        this.m_projectMaster = a_projectMaster;
        this.m_projectGlyGen = a_projectGlyGen;
        this.m_projectArray = a_projectArray;
        this.m_namespace = a_namespace;
    }

    /**
     * Create and open all TSV files
     *
     * Files are now present as member variables and ready to be filled. It is
     * important to close the files (closeFiles) otherwise parts of the
     * information will not be written.
     *
     * @param a_outputFolder
     *            Path to the tsv folder that will contain the files
     * @throws IOException
     *             Thrown if the file creation fails
     */
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
                this.m_namespace.getId(), this.m_errorFile);
        this.m_collectionDiseaseFile = new CollectionDiseaseFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionCompoundFile = new CollectionCompoundFile(a_outputFolder,
                this.m_namespace.getId(), this.m_mappingFolder);
        this.m_collectionProteinFile = new CollectionProteinFile(a_outputFolder,
                this.m_namespace.getId(), this.m_errorFile);
        this.m_collectionGeneFile = new CollectionGeneFile(a_outputFolder,
                this.m_namespace.getId());
        this.m_collectionTaxonomyFile = new CollectionTaxonomyFile(a_outputFolder,
                this.m_namespace.getId(), this.m_errorFile);

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
        this.m_collectionInCollection = new CollectionInCollectionFile(a_outputFolder,
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
        this.m_collectionInCollection.closeFile();
    }

    public void createTSV(List<FileConfig> a_configFiles, String a_outputFolder,
            String a_mappingFolder) throws IOException
    {
        this.m_outputFolder = a_outputFolder;
        this.m_mappingFolder = a_mappingFolder;
        // create the output folders
        this.createSubFolders();
        // open all files and create error output file
        this.m_errorFile = new CSVError(this.m_outputFolder);
        this.openFiles(this.m_outputFolder + File.separator + CFDEGenerator.FOLDER_NAME_TSV);
        // DCC, ID Namespace, Project, Project in Project
        this.writeBasics();
        // process GlyGen files
        // this.processGlyGen(a_configFiles);
        // process array files
        this.processArray();
        this.closeFiles();
        this.m_errorFile.closeFile();
    }

    private void processArray()
    {
        this.m_errorFile.setCurrentFile("ARRAY DATABASE");
        HashSet<String> t_arrayDatasets = this.getArrayDatasetIds();
        for (String t_datasetId : t_arrayDatasets)
        {
            System.out.println("Process array dataset:" + t_datasetId);
            this.processArrayDataset(t_datasetId);
            return;
        }
    }

    private void processArrayDataset(String a_datasetId)
    {
        Downloader t_downloader = new Downloader();
        try
        {
            String t_json = t_downloader.downloadArrayDataset(ARRAY_API_BASE_URL, a_datasetId);
            Dataset t_dataset = null;
            try
            {
                ObjectMapper t_mapper = new ObjectMapper();
                t_dataset = t_mapper.readValue(t_json, Dataset.class);
                Project t_datasetProject = this.writeArrayProject(a_datasetId, t_dataset);
                for (Slide t_slide : t_dataset.getSlides())
                {
                    HashMap<String, List<String>> t_glycansPerBlock = this
                            .getGlycansForSlide(a_datasetId, t_slide, t_downloader);

                    this.processSlideData(t_datasetProject, t_slide, t_glycansPerBlock);
                }
            }
            catch (Exception e)
            {
                String t_errorMessage = this.processArrayError(t_json);
                if (t_errorMessage == null)
                {
                    t_errorMessage = e.getMessage();
                }
                this.m_errorFile.writeError(a_datasetId, "Unable to parse dataset JSON",
                        t_errorMessage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.m_errorFile.writeError(a_datasetId, "Failed to process dataset", e.getMessage());
        }
    }

    private Project writeArrayProject(String a_datasetId, Dataset a_dataset)
    {
        Project t_project = new Project();
        // id
        t_project.setId(a_datasetId);
        // abbreviation
        t_project.setAbbr(a_datasetId);
        // persitent ID
        String t_value = a_dataset.getUri();
        if (t_value == null)
        {
            this.m_errorFile.writeWarning(a_datasetId, "There is no URI for the dataset", null);
        }
        t_project.setPersistent(t_value);
        // name
        t_value = a_dataset.getName();
        if (t_value == null)
        {
            this.m_errorFile.writeWarning(a_datasetId, "There is no name for the dataset", null);
            t_value = a_datasetId;
        }
        t_project.setName(t_value);
        // time
        t_value = a_dataset.getCreationDate();
        Date t_date = null;
        if (t_value != null)
        {
            DateFormat t_dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSZ");
            try
            {
                t_date = t_dateFormater.parse(t_value);
            }
            catch (ParseException e)
            {
                this.m_errorFile.writeWarning(a_datasetId,
                        "Invalid date format for creation time of dataset", t_value);
            }
        }
        t_project.setCreationTime(t_date);
        // description
        t_value = a_dataset.getDescription();
        t_project.setDescription(t_value);
        this.m_projectFile.write(t_project);
        this.m_projectInProjectFile.write(this.m_projectArray, t_project);
        return t_project;
    }

    private void processSlideData(Project a_dataset, Slide a_slide,
            HashMap<String, List<String>> a_glycansPerBlock)
    {
        // Get the list of glycans used in the experiment
        List<String> t_glycanList = this.findUsedGlycans(a_dataset, a_slide, a_glycansPerBlock);

        // for each Image in the list ... process and store data
        List<Image> t_imageList = a_slide.getImages();
        if (t_imageList == null || t_imageList.size() == 0)
        {
            this.m_errorFile.writeError(a_dataset.getId(), "No images found for slide",
                    "Slide:" + a_slide.getId());
        }
        else
        {
            String t_collectionId = this.writeCollectionInformation(a_dataset.getId(),
                    a_slide.getId(), t_glycanList);
            for (Image t_image : t_imageList)
            {
                this.writeImageInformation(t_image, a_dataset, t_collectionId);
            }
        }
    }

    private void writeImageInformation(Image a_image, Project a_dataset, String a_collectionId)
    {
        if (a_image.getFile() != null)
        {
            // write the image file and link the collection
            try
            {
                String t_extension = FilenameUtils.getExtension(a_image.getFile().getFilename());
                String t_fileFormat = null;
                String t_mimeType = null;
                if (t_extension.equalsIgnoreCase("jpg") || t_extension.equalsIgnoreCase("jpeg"))
                {
                    t_fileFormat = "format:3579";
                    t_mimeType = "image/jpeg";
                }
                else if (t_extension.equalsIgnoreCase("png"))
                {
                    t_fileFormat = "format:3603";
                    t_mimeType = "image/png";
                }
                else if (t_extension.equalsIgnoreCase("tif")
                        || t_extension.equalsIgnoreCase("tiff"))
                {
                    t_fileFormat = "format:3591";
                    t_mimeType = "image/tiff";
                }
                else
                {
                    this.m_errorFile.writeWarning(a_dataset.getId(),
                            "Unknwon file extension for image file. Assume xls instead",
                            "Found: " + t_extension);
                    t_fileFormat = "format:3591";
                    t_mimeType = "image/tiff";
                }
                this.processFile(a_dataset, a_image.getFile(), ARRAY_ANALYSIS_TYPE,
                        ARRAY_ASSAY_TYPE, ARRAY_DATA_TYPE_RAW, t_fileFormat, t_mimeType);
            }
            catch (Exception e)
            {
                this.m_errorFile.writeError(a_collectionId,
                        "Error when processing the raw data file of dataset " + a_dataset.getId(),
                        e.getMessage());
            }
        }
        // for all raw datasets
        List<RawData> t_rawDataList = a_image.getRawData();
        if (t_rawDataList == null || t_rawDataList.size() == 0)
        {
            this.m_errorFile.writeError(a_dataset.getId(), "Missing raw data for image",
                    a_image.getId());
        }
        else
        {
            for (RawData t_rawdata : t_rawDataList)
            {
                this.writeRawDataInformation(t_rawdata, a_dataset, a_collectionId);
            }
        }
    }

    private void writeRawDataInformation(RawData a_rawdata, Project a_dataset,
            String a_collectionId)
    {
        if (a_rawdata.getFile() != null)
        {
            // write the raw data file and link the collection
            try
            {
                String t_extension = FilenameUtils.getExtension(a_rawdata.getFile().getFilename());
                String t_fileFormat = null;
                String t_mimeType = null;
                if (t_extension.equalsIgnoreCase("xls"))
                {
                    t_fileFormat = "format:3468";
                    t_mimeType = "application/vnd.ms-excel";
                }
                else if (t_extension.equalsIgnoreCase("xlsx"))
                {
                    t_fileFormat = "format:3620";
                    t_mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                }
                else
                {
                    this.m_errorFile.writeWarning(a_dataset.getId(),
                            "Unknwon file extension for raw data file. Assume xls instead",
                            "Found: " + t_extension);
                    t_fileFormat = "format:3468";
                    t_mimeType = "application/vnd.ms-excel";
                }
                this.processFile(a_dataset, a_rawdata.getFile(), ARRAY_ANALYSIS_TYPE,
                        ARRAY_ASSAY_TYPE, ARRAY_DATA_TYPE_RAW, t_fileFormat, t_mimeType);
            }
            catch (Exception e)
            {
                this.m_errorFile.writeError(a_collectionId,
                        "Error when processing the raw data file of dataset " + a_dataset.getId(),
                        e.getMessage());
            }
        }
        // for all processed data files
        List<ProcessedData> t_processedDataList = a_rawdata.getProcessedData();
        if (t_processedDataList == null || t_processedDataList.size() == 0)
        {
            this.m_errorFile.writeError(a_dataset.getId(), "Missing processed data for raw data",
                    a_rawdata.getId());
        }
        else
        {
            for (ProcessedData t_proceesedData : t_processedDataList)
            {
                this.writeProcessedInformation(t_proceesedData, a_dataset, a_collectionId);
            }
        }
    }

    private void writeProcessedInformation(ProcessedData a_proceesedData, Project a_dataset,
            String a_collectionId)
    {
        if (a_proceesedData.getFile() == null)
        {
            this.m_errorFile.writeError(a_collectionId,
                    "Missing data file for processed data entry", a_proceesedData.getId());
        }
        else
        {
            // write processed data file and link the collection
            try
            {
                String t_extension = FilenameUtils
                        .getExtension(a_proceesedData.getFile().getFilename());
                String t_fileFormat = null;
                String t_mimeType = null;
                if (t_extension.equalsIgnoreCase("xls"))
                {
                    t_fileFormat = "format:3468";
                    t_mimeType = "application/vnd.ms-excel";
                }
                else if (t_extension.equalsIgnoreCase("xlsx"))
                {
                    t_fileFormat = "format:3620";
                    t_mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                }
                else
                {
                    this.m_errorFile.writeWarning(a_dataset.getId(),
                            "Unknwon file extension for processed data file. Assume xls instead",
                            "Found: " + t_extension);
                    t_fileFormat = "format:3468";
                    t_mimeType = "application/vnd.ms-excel";
                }
                this.processFile(a_dataset, a_proceesedData.getFile(), ARRAY_ANALYSIS_TYPE,
                        ARRAY_ASSAY_TYPE, ARRAY_DATA_TYPE_PROCESSED, t_fileFormat, t_mimeType);
            }
            catch (Exception e)
            {
                this.m_errorFile.writeError(a_collectionId,
                        "Error when processing the processed data file of dataset "
                                + a_dataset.getId(),
                        e.getMessage());
            }
        }
    }

    // https://glygen.ccrc.uga.edu/array/api/array/public/download?fileFolder=/uploads/AD9524196&fileIdentifier=1660671125153.xls&originalName=19-10_2_17104_v5.2_GenePix572_RESULTS.xls.xls
    private void processFile(Project a_project, UploadedFile a_file, String a_analysisType,
            String a_assayType, String a_dataType, String a_fileFormat, String a_mimeType)
            throws IOException
    {
        // download the file
        Downloader t_downloader = new Downloader();
        // get the file name from the URL
        URL t_url = new URL(a_url);
        String t_fileName = FilenameUtils.getName(t_url.getPath());
        String t_localFileName = this.createLocalFileName(t_fileName);
        String t_localFileNamePath = this.m_downloadFolder + File.separator + t_localFileName;
        t_downloader.downloadFile(a_url, t_localFileNamePath);
        CFDEFile t_cfdeFile = new CFDEFile();
        // general information from the config file
        t_cfdeFile.setAnalysisType(a_analysisType);
        t_cfdeFile.setAssayType(a_assayType);
        t_cfdeFile.setCreationTime(a_project.getCreationTime());
        t_cfdeFile.setDataType(a_dataType);
        t_cfdeFile.setFileFormat(a_fileFormat);
        t_cfdeFile.setFilename(t_fileName);
        t_cfdeFile.setId(a_id);
        t_cfdeFile.setMimeType(a_mimeType);
        t_cfdeFile.setPersistentId(a_persistId);
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
        this.m_fileFile.write(a_project, t_cfdeFile);
    }

    private String writeCollectionInformation(String a_dataSetId, String a_slideId,
            List<String> a_glycanList)
    {
        // write the collection first
        String t_collectionID = a_dataSetId + "-" + a_slideId;
        this.m_collectionFile.write(t_collectionID,
                "Dataset:" + a_dataSetId + "-Slide:" + a_slideId,
                "Collection for all glycans on slide " + a_slideId + " in dataset " + a_dataSetId);
        // add all glycans
        for (String t_glycan : a_glycanList)
        {
            if (!this.m_glycanBlackList.isIgnore(t_glycan))
            {
                this.m_collectionCompoundFile.write(t_collectionID, t_glycan);
            }
        }
        return t_collectionID;
    }

    private List<String> findUsedGlycans(Project a_dataset, Slide a_slide,
            HashMap<String, List<String>> a_glycansPerBlock)
    {
        List<String> t_usedBlocks = a_slide.getUsedBlocks();
        if (t_usedBlocks == null)
        {
            t_usedBlocks = new ArrayList<>();
        }
        HashSet<String> t_glycanSet = new HashSet<>();
        if (t_usedBlocks.size() == 0)
        {
            // all blocks are used
            for (String t_blockId : a_glycansPerBlock.keySet())
            {
                List<String> t_glycanList = a_glycansPerBlock.get(t_blockId);
                for (String t_glycan : t_glycanList)
                {
                    t_glycanSet.add(t_glycan);
                }
            }
        }
        else
        {
            for (String t_blockId : t_usedBlocks)
            {
                List<String> t_glycanList = a_glycansPerBlock.get(t_blockId);
                for (String t_glycan : t_glycanList)
                {
                    t_glycanSet.add(t_glycan);
                }
            }
        }
        List<String> t_glycanList = new ArrayList<>();
        for (String t_glycanId : t_glycanSet)
        {
            t_glycanList.add(t_glycanId);
        }
        return t_glycanList;
    }

    private HashMap<String, List<String>> getGlycansForSlide(String a_datasetId, Slide a_slide,
            Downloader a_downloader)
    {
        HashMap<String, List<String>> t_result = new HashMap<>();
        String t_slideId = a_slide.getId();
        if (a_slide.getId() == null)
        {
            t_slideId = "N/A";
            this.m_errorFile.writeError(a_datasetId, "Unable to find slide ID", "");
        }
        PrintedSlide t_slidePrinted = a_slide.getPrintedSlide();
        if (t_slidePrinted == null)
        {
            this.m_errorFile.writeError(a_datasetId, "Unable to find printed slide",
                    "Slide ID:" + t_slideId);
            return t_result;
        }
        Layout t_layout = t_slidePrinted.getLayout();
        if (t_layout == null)
        {
            this.m_errorFile.writeError(a_datasetId, "Unable to find slide layout",
                    "Slide ID:" + t_slideId);
            return t_result;
        }
        List<Block> t_blockList = t_layout.getBlocks();
        if (t_blockList == null || t_blockList.size() == 0)
        {
            this.m_errorFile.writeError(a_datasetId, "Unable to find blocks for slide layout",
                    "Slide ID:" + t_slideId);
            return t_result;
        }
        for (Block t_block : t_blockList)
        {
            String t_blockId = t_block.getId();
            if (t_blockId == null)
            {
                this.m_errorFile.writeError(a_datasetId, "Unable to find block ID",
                        "Slide ID:" + t_slideId);
            }
            else
            {
                BlockLayout t_blockLayout = t_block.getLayout();
                if (t_blockLayout == null)
                {
                    this.m_errorFile.writeError(a_datasetId,
                            "Unable to find block layout for block",
                            "Slide ID:" + t_slideId + "; Block ID: " + t_blockId);
                }
                else
                {
                    if (t_blockLayout.getId() == null)
                    {
                        this.m_errorFile.writeError(a_datasetId,
                                "Unable to find block layout ID for block",
                                "Slide ID:" + t_slideId + "; Block ID: " + t_blockId);
                    }
                    else
                    {
                        List<String> t_glycans = this.getGlycansForBlockLayout(a_datasetId,
                                t_blockLayout.getId(), a_downloader);
                        t_result.put(t_blockId, t_glycans);
                    }
                }
            }
        }
        return t_result;
    }

    private List<String> getGlycansForBlockLayout(String a_datasetId, String a_id,
            Downloader a_downloader)
    {
        List<String> t_glycans = this.m_blockLayoutCache.get(a_id);
        if (t_glycans == null)
        {
            try
            {
                String t_json = a_downloader.getGlycansPerBlockLayout(ARRAY_API_BASE_URL, a_id);
                try
                {
                    ObjectMapper t_mapper = new ObjectMapper();
                    String[] t_glyTouCan = t_mapper.readValue(t_json, String[].class);
                    t_glycans = Arrays.asList(t_glyTouCan);
                }
                catch (Exception e)
                {
                    String t_errorMessage = this.processArrayError(t_json);
                    if (t_errorMessage == null)
                    {
                        t_errorMessage = e.getMessage();
                    }
                    this.m_errorFile.writeError(a_datasetId,
                            "Unable to parse glycans for block layout JSON", t_errorMessage);
                }
            }
            catch (Exception e)
            {
                this.m_errorFile.writeError(a_datasetId,
                        "Unable to retrieve glycans for block layout JSON", e.getMessage());
            }
        }
        return t_glycans;
    }

    private String processArrayError(String a_json)
    {
        String t_result = null;
        try
        {
            ObjectMapper t_mapper = new ObjectMapper();
            ErrorResponse t_error = t_mapper.readValue(a_json, ErrorResponse.class);
            t_result = "Web service error response: error code (" + t_error.getErrorCode()
                    + ") ; status (" + t_error.getStatus() + ") ; status code ("
                    + t_error.getStatusCode() + ") ; message: " + t_error.getMessage();
        }
        catch (Exception e)
        {
        }
        return t_result;
    }

    private void processGlyGen(List<FileConfig> a_configFiles)
    {
        for (FileConfig t_fileConfig : a_configFiles)
        {
            this.m_errorFile.setCurrentFile(t_fileConfig.getLocalId());
            System.out.println("Processing " + t_fileConfig.getLocalId());
            try
            {
                this.processFile(t_fileConfig);
            }
            catch (Exception e)
            {
                this.m_errorFile.writeError(t_fileConfig.getLocalId(), "", e.getMessage(),
                        "Skipped file");
            }
        }
    }

    private HashSet<String> getArrayDatasetIds()
    {
        Integer t_limit = 5;
        Integer t_offset = 0;
        HashSet<String> t_result = new HashSet<>();
        Downloader t_downloader = new Downloader();
        try
        {
            while (true)
            {
                System.out.println("Retrieving array dataset list: offset (" + t_offset.toString()
                        + "); limit (" + t_limit.toString() + ")");
                String t_json = t_downloader.downloadDatasetList(ARRAY_API_BASE_URL, t_offset,
                        t_limit);
                try
                {
                    ObjectMapper t_mapper = new ObjectMapper();
                    DatasetList t_datasetList = t_mapper.readValue(t_json, DatasetList.class);
                    for (DatasetSimple t_dataset : t_datasetList.getDatasetList())
                    {
                        if (t_dataset.getId() == null)
                        {
                            this.m_errorFile.writeError("Dataset without ID found", "Offset: "
                                    + t_offset.toString() + " ; Limit: " + t_limit.toString());
                        }
                        else
                        {
                            t_result.add(t_dataset.getId());
                        }
                    }
                    t_offset += t_limit;
                    if (t_offset >= t_datasetList.getTotal())
                    {
                        return t_result;
                    }
                }
                catch (Exception e)
                {
                    ObjectMapper t_mapper = new ObjectMapper();
                    ErrorResponse t_error = t_mapper.readValue(t_json, ErrorResponse.class);
                    this.m_errorFile.writeError(
                            "Webservice call to retieve list of datasets failed --> STOPPED",
                            "Offset: " + t_offset.toString() + " ; Limit: " + t_limit.toString()
                                    + " ; error code (" + t_error.getErrorCode() + ") ; status ("
                                    + t_error.getStatus() + ") ; status code ("
                                    + t_error.getStatusCode() + ") ; message: "
                                    + t_error.getMessage());
                    return t_result;
                }
                // TODO
                return t_result;
            }
        }
        catch (Exception e)
        {
            System.out.println("Unable to retieve array dataset IDs: " + e.getMessage());
        }
        return t_result;
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
        this.m_projectFile.write(this.m_projectArray);
        // linking them
        this.m_projectInProjectFile.write(this.m_projectMaster, this.m_projectGlyGen);
        this.m_projectInProjectFile.write(this.m_projectMaster, this.m_projectArray);
    }

    /**
     * Create the output folders including the download folder and tsv folder if
     * they not already exist
     *
     * @throws IOException
     *             Thrown if folder creation fails
     */
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
        this.m_fileFile.write(this.m_projectGlyGen, t_cfdeFile);
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
        else
        {
            throw new IOException(
                    "Unable to process files of type: " + a_fileConfig.getType().getKey());
        }
    }

    private void logGlycans(List<Glycan> a_glycans)
    {
        for (Glycan t_glycan : a_glycans)
        {
            String t_id = this.m_collectionCompoundFile.getCFDEnamespace(t_glycan.getGlycanAcc());
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
                    String t_id = this.m_collectionCompoundFile.getCFDEnamespace(t_glyTouCanId);
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
        ProteinFileReader t_reader = new ProteinFileReader(CFDEGenerator.LINE_LIMIT);
        List<Protein> t_proteins = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_mappingFolder, this.m_errorFile);
        for (Protein t_protein : t_proteins)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_protein.getUniprotAcc(), a_fileConfig,
                    "Information for protein ", this.m_projectGlyGen);
            // add the protein/gene to collection
            this.m_collectionProteinFile.write(t_collectionID, t_protein.getUniprotAcc());
            if (t_protein.getEnsemblAcc() != null)
            {
                this.m_collectionGeneFile.write(t_collectionID, t_protein.getEnsemblAcc());
            }
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
        return t_proteins;
    }

    private List<Protein> processGlyGenProteinNoGeneDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        ProteinNoGeneFileReader t_reader = new ProteinNoGeneFileReader(CFDEGenerator.LINE_LIMIT);
        List<Protein> t_proteins = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_mappingFolder, this.m_errorFile);
        for (Protein t_protein : t_proteins)
        {
            // create collection and associate with file
            String t_collectionID = this.createCollection(t_protein.getUniprotAcc(), a_fileConfig,
                    "Information for protein ", this.m_projectGlyGen);
            // add the protein/gene to collection
            this.m_collectionProteinFile.write(t_collectionID, t_protein.getUniprotAcc());
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
        return t_proteins;
    }

    private List<Glycan> processGlyGenGlycanDataFile(String a_localFileNamePath,
            FileConfig a_fileConfig) throws IOException
    {
        // parse the file
        GlycanFileReader t_reader = new GlycanFileReader(CFDEGenerator.LINE_LIMIT);
        List<Glycan> t_glycans = t_reader.loadFile(a_localFileNamePath, a_fileConfig,
                this.m_mappingFolder, this.m_errorFile);
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
                if (t_protein.getEnsemblAcc() != null)
                {
                    this.m_collectionGeneFile.write(t_collectionID, t_protein.getEnsemblAcc());
                }
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
        return t_glycans;
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

    public HashSet<String> getProteinIDs()
    {
        return this.m_proteinIDs;
    }

    public HashMap<String, String> getGlycanIDs()
    {
        return this.m_glycanIDs;
    }

}

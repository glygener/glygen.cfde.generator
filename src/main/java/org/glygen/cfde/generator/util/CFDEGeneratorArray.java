package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.glygen.cfde.generator.csv.GlycanFilter;
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
import org.glygen.cfde.generator.om.Project;

import com.fasterxml.jackson.databind.ObjectMapper;

// https://data.glygen.org/ln2data/releases/data/current/reviewed/glycan_pubchem_status.csv

// https://github.com/nih-cfde/published-documentation/wiki/TableInfo:-file.tsv
public class CFDEGeneratorArray
{
    private static final String ARRAY_API_BASE_URL = "https://glygen.ccrc.uga.edu/ggarray/api/";
    private static final String ARRAY_ANALYSIS_TYPE = "OBI:0001985";
    private static final String ARRAY_ASSAY_TYPE = "OBI:0001985";
    private static final String ARRAY_DATA_TYPE_IMAGE = "data:1714";
    private static final String ARRAY_DATA_TYPE_RAW = "data:3110";
    private static final String ARRAY_DATA_TYPE_PROCESSED = "data:3111";

    private static final Integer DATASET_LIMIT = Integer.MAX_VALUE;
    
    private TSVGenerator m_tsvGenerator = null;

    private GlycanFilter m_glycanBlackList = new GlycanFilter();

    private HashMap<String, String> m_glycanIDs = new HashMap<>();

    private HashMap<String, List<String>> m_blockLayoutCache = new HashMap<>();

    public CFDEGeneratorArray(TSVGenerator a_tsvGenerator)
    {
        super();
        this.m_tsvGenerator = a_tsvGenerator;
    }

    public void process()
    {
        Integer t_counter = 0;
        this.m_tsvGenerator.getErrorFile().setCurrentFile("ARRAY DATABASE");
        HashSet<String> t_arrayDatasets = this.getArrayDatasetIds();
        for (String t_datasetId : t_arrayDatasets)
        {
            t_counter++;
            if ( t_counter <= DATASET_LIMIT)
            {
            	System.out
            	.println("Process array dataset(" + t_counter.toString() + "): " + t_datasetId);
            	this.processArrayDataset(t_datasetId);
            }
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
                this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                        "Unable to parse dataset JSON", t_errorMessage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.m_tsvGenerator.getErrorFile().writeError(a_datasetId, "Failed to process dataset",
                    e.getMessage());
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
            this.m_tsvGenerator.getErrorFile().writeWarning(a_datasetId,
                    "There is no URI for the dataset", null);
        }
        t_project.setPersistent(t_value);
        // name
        t_value = a_dataset.getName();
        if (t_value == null)
        {
            this.m_tsvGenerator.getErrorFile().writeWarning(a_datasetId,
                    "There is no name for the dataset", null);
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
                this.m_tsvGenerator.getErrorFile().writeWarning(a_datasetId,
                        "Invalid date format for creation time of dataset", t_value);
            }
        }
        t_project.setCreationTime(t_date);
        // description
        t_value = a_dataset.getDescription();
        t_project.setDescription(t_value);
        this.m_tsvGenerator.getProjectFile().write(t_project);
        this.m_tsvGenerator.getProjectInProjectFile().write(this.m_tsvGenerator.getProjectArray(),
                t_project);
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
            this.m_tsvGenerator.getErrorFile().writeError(a_dataset.getId(),
                    "No images found for slide", "Slide:" + a_slide.getId());
        }
        else
        {
            String t_collectionId = this.writeCollectionInformation(a_dataset.getId(),
                    a_slide.getId(), t_glycanList);
            this.m_tsvGenerator.getCollectionDefinedByProjectFile().write(t_collectionId,
                    a_dataset.getId());
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
                    this.m_tsvGenerator.getErrorFile().writeWarning(a_dataset.getId(),
                            "Unknwon file extension for image file. Assume tiff instead",
                            "Found: " + t_extension);
                    t_fileFormat = "format:3591";
                    t_mimeType = "image/tiff";
                }
                this.processFile(a_dataset, a_image.getFile(), ARRAY_ANALYSIS_TYPE,
                        ARRAY_ASSAY_TYPE, ARRAY_DATA_TYPE_IMAGE, t_fileFormat, t_mimeType,
                        a_collectionId);
            }
            catch (Exception e)
            {
                this.m_tsvGenerator.getErrorFile().writeError(a_collectionId,
                        "Error when processing the raw data file of dataset " + a_dataset.getId(),
                        e.getMessage());
            }
        }
        // for all raw datasets
        List<RawData> t_rawDataList = a_image.getRawData();
        if (t_rawDataList == null || t_rawDataList.size() == 0)
        {
            this.m_tsvGenerator.getErrorFile().writeError(a_dataset.getId(),
                    "Missing raw data for image", a_image.getId());
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
                else if (t_extension.equalsIgnoreCase("txt"))
                {
                    t_fileFormat = "format:2330";
                    t_mimeType = "  text/plain";
                }
                else
                {
                    this.m_tsvGenerator.getErrorFile().writeWarning(a_dataset.getId(),
                            "Unknwon file extension for raw data file. Assume xls instead",
                            "Found: " + t_extension);
                    t_fileFormat = "format:3468";
                    t_mimeType = "application/vnd.ms-excel";
                }
                this.processFile(a_dataset, a_rawdata.getFile(), ARRAY_ANALYSIS_TYPE,
                        ARRAY_ASSAY_TYPE, ARRAY_DATA_TYPE_RAW, t_fileFormat, t_mimeType,
                        a_collectionId);
            }
            catch (Exception e)
            {
                this.m_tsvGenerator.getErrorFile().writeError(a_collectionId,
                        "Error when processing the raw data file of dataset " + a_dataset.getId(),
                        e.getMessage());
            }
        }
        // for all processed data files
        List<ProcessedData> t_processedDataList = a_rawdata.getProcessedData();
        if (t_processedDataList == null || t_processedDataList.size() == 0)
        {
            this.m_tsvGenerator.getErrorFile().writeError(a_dataset.getId(),
                    "Missing processed data for raw data", a_rawdata.getId());
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
            this.m_tsvGenerator.getErrorFile().writeError(a_collectionId,
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
                    this.m_tsvGenerator.getErrorFile().writeWarning(a_dataset.getId(),
                            "Unknwon file extension for processed data file. Assume xls instead",
                            "Found: " + t_extension);
                    t_fileFormat = "format:3468";
                    t_mimeType = "application/vnd.ms-excel";
                }
                this.processFile(a_dataset, a_proceesedData.getFile(), ARRAY_ANALYSIS_TYPE,
                        ARRAY_ASSAY_TYPE, ARRAY_DATA_TYPE_PROCESSED, t_fileFormat, t_mimeType,
                        a_collectionId);
            }
            catch (Exception e)
            {
                this.m_tsvGenerator.getErrorFile().writeError(a_collectionId,
                        "Error when processing the processed data file of dataset "
                                + a_dataset.getId(),
                        e.getMessage());
            }
        }
    }

    // https://glygen.ccrc.uga.edu/array/api/array/public/download?fileFolder=/uploads/AD9524196&fileIdentifier=1660671125153.xls&originalName=19-10_2_17104_v5.2_GenePix572_RESULTS.xls.xls
    private void processFile(Project a_project, UploadedFile a_file, String a_analysisType,
            String a_assayType, String a_dataType, String a_fileFormat, String a_mimeType,
            String a_collectionID) throws IOException
    {
        // download the file
        Downloader t_downloader = new Downloader();
        // get the file name from the URL
        String t_fileName = a_project.getId() + "-" + a_file.getId();
        String t_localFileName = this.createLocalFileName(t_fileName);
        String t_localFileNamePath = this.m_tsvGenerator.getDownloadFolder() + File.separator
                + t_localFileName;
        t_downloader.downloadFile(
        		ARRAY_API_BASE_URL + "array/public/download?fileFolder="
                        + a_file.getFileFolder() + "&fileIdentifier=" + a_file.getId()
                        + "&originalName=1.xls",
                t_localFileNamePath);
        CFDEFile t_cfdeFile = new CFDEFile();
        // general information from the config file
        t_cfdeFile.setAnalysisType(a_analysisType);
        t_cfdeFile.setAssayType(a_assayType);
        t_cfdeFile.setCreationTime(a_project.getCreationTime());
        t_cfdeFile.setDataType(a_dataType);
        t_cfdeFile.setFileFormat(a_fileFormat);
        t_cfdeFile.setFilename(t_fileName);
        t_cfdeFile.setId(t_fileName);
        t_cfdeFile.setMimeType(a_mimeType);
        t_cfdeFile.setPersistentId("http://array.glygen.org/public/file/" + t_fileName);
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
        this.m_tsvGenerator.getFileFile().write(a_project, t_cfdeFile);
        this.m_tsvGenerator.getFileDescribesCollectionFile().write(a_collectionID, t_fileName);
    }

    private String writeCollectionInformation(String a_dataSetId, String a_slideId,
            List<String> a_glycanList)
    {
        // write the collection first
        String t_collectionID = a_dataSetId + "-" + a_slideId;
        this.m_tsvGenerator.getCollectionFile().write(t_collectionID,
                "Dataset:" + a_dataSetId + "-Slide:" + a_slideId,
                "Collection for all glycans on slide " + a_slideId + " in dataset " + a_dataSetId);
        // add all glycans
        for (String t_glycan : a_glycanList)
        {
            if (!this.m_glycanBlackList.isIgnore(t_glycan))
            {
                this.m_tsvGenerator.getCollectionCompoundFile().write(t_collectionID, t_glycan);
                this.logGlycans(t_glycan);
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
            this.m_tsvGenerator.getErrorFile().writeError(a_datasetId, "Unable to find slide ID",
                    "");
        }
        PrintedSlide t_slidePrinted = a_slide.getPrintedSlide();
        if (t_slidePrinted == null)
        {
            this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                    "Unable to find printed slide", "Slide ID:" + t_slideId);
            return t_result;
        }
        Layout t_layout = t_slidePrinted.getLayout();
        if (t_layout == null)
        {
            this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                    "Unable to find slide layout", "Slide ID:" + t_slideId);
            return t_result;
        }
        List<Block> t_blockList = t_layout.getBlocks();
        if (t_blockList == null || t_blockList.size() == 0)
        {
            this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                    "Unable to find blocks for slide layout", "Slide ID:" + t_slideId);
            return t_result;
        }
        for (Block t_block : t_blockList)
        {
            String t_blockId = t_block.getId();
            if (t_blockId == null)
            {
                this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                        "Unable to find block ID", "Slide ID:" + t_slideId);
            }
            else
            {
                BlockLayout t_blockLayout = t_block.getLayout();
                if (t_blockLayout == null)
                {
                    this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                            "Unable to find block layout for block",
                            "Slide ID:" + t_slideId + "; Block ID: " + t_blockId);
                }
                else
                {
                    if (t_blockLayout.getId() == null)
                    {
                        this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
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
                    this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
                            "Unable to parse glycans for block layout JSON", t_errorMessage);
                }
            }
            catch (Exception e)
            {
                this.m_tsvGenerator.getErrorFile().writeError(a_datasetId,
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
                            this.m_tsvGenerator.getErrorFile().writeError(
                                    "Dataset without ID found", "Offset: " + t_offset.toString()
                                            + " ; Limit: " + t_limit.toString());
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
                    this.m_tsvGenerator.getErrorFile().writeError(
                            "Webservice call to retieve list of datasets failed --> STOPPED",
                            "Offset: " + t_offset.toString() + " ; Limit: " + t_limit.toString()
                                    + " ; error code (" + t_error.getErrorCode() + ") ; status ("
                                    + t_error.getStatus() + ") ; status code ("
                                    + t_error.getStatusCode() + ") ; message: "
                                    + t_error.getMessage());
                    return t_result;
                }
                // TODO
                // return t_result;
            }
        }
        catch (Exception e)
        {
            System.out.println("Unable to retieve array dataset IDs: " + e.getMessage());
        }
        return t_result;
    }

    private void logGlycans(String a_glycans)
    {
        String t_id = this.m_tsvGenerator.getCollectionCompoundFile().getCFDEnamespace(a_glycans);
        this.m_glycanIDs.put(a_glycans, t_id);
    }

    private String createLocalFileName(String a_fileName) throws MalformedURLException
    {
        SimpleDateFormat t_formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSS");
        Date t_currentTime = new Date(System.currentTimeMillis());
        String t_timeStamp = t_formatter.format(t_currentTime);
        return t_timeStamp + "-" + a_fileName;
    }

    public HashMap<String, String> getGlycanIDs()
    {
        return this.m_glycanIDs;
    }

}

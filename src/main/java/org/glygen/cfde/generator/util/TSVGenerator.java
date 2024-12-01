package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.glygen.cfde.generator.csv.CSVError;
import org.glygen.cfde.generator.om.DCC;
import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Namespace;
import org.glygen.cfde.generator.om.Project;
import org.glygen.cfde.generator.tsv.BiosampleDiseaseFile;
import org.glygen.cfde.generator.tsv.BiosampleFile;
import org.glygen.cfde.generator.tsv.BiosampleFromSubjectFile;
import org.glygen.cfde.generator.tsv.BiosampleGeneFile;
import org.glygen.cfde.generator.tsv.BiosampleInCollectionFile;
import org.glygen.cfde.generator.tsv.BiosampleSubstanceFile;
import org.glygen.cfde.generator.tsv.CollectionAnatomyFile;
import org.glygen.cfde.generator.tsv.CollectionBiofluid;
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

// https://data.glygen.org/ln2data/releases/data/current/reviewed/glycan_pubchem_status.csv

// https://github.com/nih-cfde/published-documentation/wiki/TableInfo:-file.tsv
public class TSVGenerator
{
    private static final String FOLDER_NAME_DOWNLOAD = "download";
    private static final String FOLDER_NAME_TSV = "tsv";

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
    private CollectionBiofluid m_collectionBioFluidFile = null;
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

    public TSVGenerator(DCC a_dcc, Project a_projectMaster, Project a_projectGlyGen,
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
        this.m_collectionBioFluidFile = new CollectionBiofluid(a_outputFolder,
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

    public void closeFiles() throws IOException
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
        this.m_collectionBioFluidFile.closeFile();
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
        // error
        this.m_errorFile.closeFile();
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
        this.openFiles(this.m_outputFolder + File.separator + TSVGenerator.FOLDER_NAME_TSV);
        // DCC, ID Namespace, Project, Project in Project
        this.writeBasics();
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
                + TSVGenerator.FOLDER_NAME_DOWNLOAD;
        File t_file = new File(this.m_downloadFolder);
        if (!t_file.exists())
        {
            if (!t_file.mkdirs())
            {
                throw new IOException("Failed to create download folder: " + this.m_downloadFolder);
            }
        }
        // tsv folder
        t_file = new File(this.m_outputFolder + File.separator + TSVGenerator.FOLDER_NAME_TSV);
        if (!t_file.exists())
        {
            if (!t_file.mkdirs())
            {
                throw new IOException("Failed to create TSV folder: " + this.m_outputFolder
                        + File.separator + FOLDER_NAME_TSV);
            }
        }
    }

    public FileFile getFileFile()
    {
        return this.m_fileFile;
    }

    public CollectionFile getCollectionFile()
    {
        return this.m_collectionFile;
    }

    public FileDescribesCollectionFile getFileDescribesCollectionFile()
    {
        return this.m_fileDescribesCollectionFile;
    }

    public CollectionDefinedByProjectFile getCollectionDefinedByProjectFile()
    {
        return this.m_collectionDefinedByProjectFile;
    }

    public CollectionAnatomyFile getCollectionAnatomyFile()
    {
        return this.m_collectionAnatomyFile;
    }

    public CollectionDiseaseFile getCollectionDiseaseFile()
    {
        return this.m_collectionDiseaseFile;
    }

    public CollectionCompoundFile getCollectionCompoundFile()
    {
        return this.m_collectionCompoundFile;
    }

    public CollectionProteinFile getCollectionProteinFile()
    {
        return this.m_collectionProteinFile;
    }

    public CollectionGeneFile getCollectionGeneFile()
    {
        return this.m_collectionGeneFile;
    }

    public CollectionTaxonomyFile getCollectionTaxonomyFile()
    {
        return this.m_collectionTaxonomyFile;
    }

    public CSVError getErrorFile()
    {
        return this.m_errorFile;
    }

    public ProjectFile getProjectFile()
    {
        return this.m_projectFile;
    }

    public ProjectInProjectFile getProjectInProjectFile()
    {
        return this.m_projectInProjectFile;
    }

    public Project getProjectArray()
    {
        return this.m_projectArray;
    }

    public String getDownloadFolder()
    {
        return this.m_downloadFolder;
    }

    public Project getProjectGlyGen()
    {
        return this.m_projectGlyGen;
    }

    public String getMappingFolder()
    {
        return this.m_mappingFolder;
    }

}

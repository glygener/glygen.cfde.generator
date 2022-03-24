package org.glygen.cfde.generator.tsv;

import java.io.File;
import java.io.IOException;

import org.glygen.cfde.generator.om.CFDEFile;
import org.glygen.cfde.generator.om.Project;

public class FileFile extends TSVFile
{
    public FileFile(String a_folderPath, String a_namespace) throws IOException
    {
        super();
        this.m_header = new String[] { "id_namespace", "local_id", "project_id_namespace",
                "project_local_id", "persistent_id", "creation_time", "size_in_bytes",
                "uncompressed_size_in_bytes", "sha256", "md5", "filename", "file_format",
                "compression_format", "data_type", "assay_type", "analysis_type", "mime_type",
                "bundle_collection_id_namespace", "bundle_collection_local_id" };
        this.m_namespace = a_namespace;
        this.openFile(a_folderPath + File.separator + "file.tsv");
    }

    public void write(Project a_project, CFDEFile a_file)
    {
        String[] t_line = new String[19];
        t_line[0] = this.addString(this.m_namespace);
        t_line[1] = this.addString(a_file.getId());
        t_line[2] = this.addString(this.m_namespace);
        t_line[3] = this.addString(a_project.getId());
        t_line[4] = this.addString(a_file.getPersistentId());
        t_line[5] = this.addDate(a_file.getCreationTime());
        t_line[6] = this.addLong(a_file.getFileSize());
        t_line[7] = this.addLong(a_file.getFileSize());
        t_line[8] = this.addString(a_file.getSha256());
        t_line[9] = this.addString(a_file.getMd5());
        t_line[10] = this.addString(a_file.getFilename());
        t_line[11] = this.addString(a_file.getFileFormat());
        t_line[12] = this.addString("");
        t_line[13] = this.addString(a_file.getDataType());
        t_line[14] = this.addString(a_file.getAssayType());
        t_line[15] = this.addString(a_file.getAnalysisType());
        t_line[16] = this.addString(a_file.getMimeType());
        t_line[17] = this.addString("");
        t_line[18] = this.addString("");
        this.m_csvWriter.writeNext(t_line);
    }

}

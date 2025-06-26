package org.glygen.cfde.generator.om;

import java.util.Date;

public class FileConfig
{
    private DataFileType m_type = null;
    private String m_fileUrl = null;
    private String m_bcoUrl = null;
    private String m_localId = null;
    private String m_persitentId = null;
    private Date m_creationTime = null;
    private String m_fileFormat = null;
    private String m_dataType = null;
    private String m_mimeType = null;
    private String m_proteinColumn = null;
    private String m_geneColumn = null;
    private String m_glycanColumn = null;
    private String m_diseaseColumn = null;
    private String m_anatomyColumn = null;
    private String m_sitePosOneColumn = null;
    private String m_sitePosOneAAColumn = null;
    private String m_sitePosTwoColumn = null;
    private String m_sitePosTwoAAColumn = null;
    private String m_speciesColumn = null;
    private String m_accessUrl = null;

    public DataFileType getType()
    {
        return this.m_type;
    }

    public void setType(DataFileType a_type)
    {
        this.m_type = a_type;
    }

    public String getFileUrl()
    {
        return this.m_fileUrl;
    }

    public void setFileUrl(String a_fileUrl)
    {
        this.m_fileUrl = a_fileUrl;
    }

    public String getBcoUrl()
    {
        return this.m_bcoUrl;
    }

    public void setBcoUrl(String a_bcoUrl)
    {
        this.m_bcoUrl = a_bcoUrl;
    }

    public String getLocalId()
    {
        return this.m_localId;
    }

    public void setLocalId(String a_localId)
    {
        this.m_localId = a_localId;
    }

    public String getPersitentId()
    {
        return this.m_persitentId;
    }

    public void setPersitentId(String a_persitentId)
    {
        this.m_persitentId = a_persitentId;
    }

    public Date getCreationTime()
    {
        return this.m_creationTime;
    }

    public void setCreationTime(Date a_creationTime)
    {
        this.m_creationTime = a_creationTime;
    }

    public String getFileFormat()
    {
        return this.m_fileFormat;
    }

    public void setFileFormat(String a_fileFormat)
    {
        this.m_fileFormat = a_fileFormat;
    }

    public String getDataType()
    {
        return this.m_dataType;
    }

    public void setDataType(String a_dataType)
    {
        this.m_dataType = a_dataType;
    }

    public String getMimeType()
    {
        return this.m_mimeType;
    }

    public void setMimeType(String a_mimeType)
    {
        this.m_mimeType = a_mimeType;
    }

    public String getProteinColumn()
    {
        return this.m_proteinColumn;
    }

    public void setProteinColumn(String a_proteinColumn)
    {
        this.m_proteinColumn = a_proteinColumn;
    }

    public String getGeneColumn()
    {
        return this.m_geneColumn;
    }

    public void setGeneColumn(String a_geneColumn)
    {
        this.m_geneColumn = a_geneColumn;
    }

    public String getGlycanColumn()
    {
        return this.m_glycanColumn;
    }

    public void setGlycanColumn(String a_glycanColumn)
    {
        this.m_glycanColumn = a_glycanColumn;
    }

    public String getDiseaseColumn()
    {
        return this.m_diseaseColumn;
    }

    public void setDiseaseColumn(String a_diseaseColumn)
    {
        this.m_diseaseColumn = a_diseaseColumn;
    }

    public String getAnatomyColumn()
    {
        return this.m_anatomyColumn;
    }

    public void setAnatomyColumn(String a_anatomyColumn)
    {
        this.m_anatomyColumn = a_anatomyColumn;
    }

    public String getSpeciesColumn()
    {
        return this.m_speciesColumn;
    }

    public void setSpeciesColumn(String a_speciesColumn)
    {
        this.m_speciesColumn = a_speciesColumn;
    }

    public String getAccessUrl()
    {
        return this.m_accessUrl;
    }

    public void setAccessUrl(String a_accessUrl)
    {
        this.m_accessUrl = a_accessUrl;
    }

    public String getSitePosOneColumn()
    {
        return this.m_sitePosOneColumn;
    }

    public void setSitePosOneColumn(String a_sitePosOne)
    {
        this.m_sitePosOneColumn = a_sitePosOne;
    }

    public String getSitePosOneAAColumn()
    {
        return this.m_sitePosOneAAColumn;
    }

    public void setSitePosOneAAColumn(String a_sitePosOneAA)
    {
        this.m_sitePosOneAAColumn = a_sitePosOneAA;
    }

    public String getSitePosTwoAAColumn()
    {
        return this.m_sitePosTwoAAColumn;
    }

    public void setSitePosTwoAAColumn(String a_sitePosTwoAA)
    {
        this.m_sitePosTwoAAColumn = a_sitePosTwoAA;
    }

    public String getSitePosTwoColumn()
    {
        return this.m_sitePosTwoColumn;
    }

    public void setSitePosTwoColumn(String a_sitePosTwo)
    {
        this.m_sitePosTwoColumn = a_sitePosTwo;
    }
}

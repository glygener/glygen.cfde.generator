package org.glygen.cfde.generator.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.glygen.cfde.generator.om.DCC;
import org.glygen.cfde.generator.om.Project;

public class PropertiesProcessor
{
    private Properties m_properties = null;

    public PropertiesProcessor(Properties a_properties)
    {
        this.m_properties = a_properties;
    }

    public DCC getDCC() throws IOException
    {
        DCC t_dcc = new DCC();
        t_dcc.setId(this.getPropertyString("dcc.id", true));
        t_dcc.setName(this.getPropertyString("dcc.dcc_name", true));
        t_dcc.setAbbr(this.getPropertyString("dcc.dcc_abbreviation", true));
        t_dcc.setDescription(this.getPropertyString("dcc.dcc_description", true));
        t_dcc.setEmail(this.getPropertyString("dcc.contact_email", true));
        t_dcc.setContact(this.getPropertyString("dcc.contact_name", true));
        t_dcc.setUrl(this.getPropertyString("dcc.dcc_url", true));
        return t_dcc;
    }

    private String getPropertyString(String a_key, boolean a_mandetory) throws IOException
    {
        String t_value = this.m_properties.getProperty(a_key);
        if (a_mandetory)
        {
            if (t_value == null)
            {
                throw new IOException("Missing value for key: " + a_key);
            }
        }
        return t_value;
    }

    private Project getProject(String a_keyPart) throws IOException
    {
        Project t_project = new Project();
        t_project.setId(this.getPropertyString("project." + a_keyPart + ".local_id", true));
        t_project.setName(this.getPropertyString("project." + a_keyPart + ".name", true));
        t_project.setAbbr(this.getPropertyString("project." + a_keyPart + ".abbreviation", true));
        t_project.setDescription(
                this.getPropertyString("project." + a_keyPart + ".description", true));
        t_project.setPersistent(
                this.getPropertyString("project." + a_keyPart + ".persistent_id", true));
        t_project.setCreationTime(
                this.getPropertyDate("project." + a_keyPart + ".creation_time", true));
        return t_project;
    }

    private Date getPropertyDate(String a_key, boolean a_mandetory) throws IOException
    {
        String t_value = this.getPropertyString(a_key, a_mandetory);
        if (t_value != null)
        {
            SimpleDateFormat t_formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            try
            {
                Date t_date = t_formatter.parse(t_value);
                return t_date;
            }
            catch (Exception e)
            {
                throw new IOException("Date format for key " + a_key
                        + " does not follow the pattern MM/dd/yyyy.");
            }
        }
        return null;
    }

    public Project getProjectMaster() throws IOException
    {
        return this.getProject("master");
    }

    public Project getProjectGlyGen() throws IOException
    {
        return this.getProject("glygen");
    }

    public String getNamespace() throws IOException
    {
        return this.getPropertyString("id_namespace", true);
    }
}

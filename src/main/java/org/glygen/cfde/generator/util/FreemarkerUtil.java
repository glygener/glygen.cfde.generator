package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerUtil
{
    private Configuration m_config = null;

    public FreemarkerUtil(String a_templateFolder) throws IOException
    {
        this.m_config = new Configuration(Configuration.VERSION_2_3_29);
        this.m_config.setDirectoryForTemplateLoading(new File(a_templateFolder));
        this.m_config.setDefaultEncoding("UTF-8");
        this.m_config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.m_config.setLogTemplateExceptions(false);
        this.m_config.setWrapUncheckedExceptions(true);
        this.m_config.setFallbackOnNullLoopVariable(false);
    }

    public String render(Map<String, Object> a_inputObject, String a_templateFile)
            throws IOException, TemplateException
    {
        Template t_template = this.m_config.getTemplate(a_templateFile);
        StringWriter t_writer = new StringWriter();
        t_template.process(a_inputObject, t_writer);
        t_writer.close();
        return t_writer.toString();
    }
}

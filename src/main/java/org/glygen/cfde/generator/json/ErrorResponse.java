package org.glygen.cfde.generator.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse
{
    private String m_errorCode = null;
    private String m_message = null;
    private String m_status = null;
    private String m_statusCode = null;

    @JsonProperty("errorCode")
    public String getErrorCode()
    {
        return this.m_errorCode;
    }

    public void setErrorCode(String a_errorCode)
    {
        this.m_errorCode = a_errorCode;
    }

    @JsonProperty("message")
    public String getMessage()
    {
        return this.m_message;
    }

    public void setMessage(String a_message)
    {
        this.m_message = a_message;
    }

    @JsonProperty("status")
    public String getStatus()
    {
        return this.m_status;
    }

    public void setStatus(String a_status)
    {
        this.m_status = a_status;
    }

    @JsonProperty("statusCode")
    public String getStatusCode()
    {
        return this.m_statusCode;
    }

    public void setStatusCode(String a_statusCode)
    {
        this.m_statusCode = a_statusCode;
    }
}

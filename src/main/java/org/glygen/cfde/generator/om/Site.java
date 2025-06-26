package org.glygen.cfde.generator.om;

import java.util.Objects;

public class Site
{
    private Integer m_positionOne = null;
    private String m_positionOneAA = null;
    private Integer m_positionTwo = null;
    private String m_positionTwoAA = null;
    private PtmType m_ptmType = null;

    public Site(PtmType a_ptmType)
    {
        super();
        this.m_ptmType = a_ptmType;
    }

    public Site(Integer a_positionOne, String a_positionOneAA, Integer a_positionTwo,
            String a_positionTwoAA, PtmType a_ptmType)
    {
        super();
        this.m_positionOne = a_positionOne;
        this.m_positionOneAA = a_positionOneAA;
        this.m_positionTwo = a_positionTwo;
        this.m_positionTwoAA = a_positionTwoAA;
        this.m_ptmType = a_ptmType;
    }

    public Integer getPositionOne()
    {
        return this.m_positionOne;
    }

    public void setPositionOne(Integer a_positionOne)
    {
        this.m_positionOne = a_positionOne;
    }

    public String getPositionOneAA()
    {
        return this.m_positionOneAA;
    }

    public void setPositionOneAA(String a_positionOneAA)
    {
        this.m_positionOneAA = a_positionOneAA;
    }

    public Integer getPositionTwo()
    {
        return this.m_positionTwo;
    }

    public void setPositionTwo(Integer a_positionTwo)
    {
        this.m_positionTwo = a_positionTwo;
    }

    public String getPositionTwoAA()
    {
        return this.m_positionTwoAA;
    }

    public void setPositionTwoAA(String a_positionTwoAA)
    {
        this.m_positionTwoAA = a_positionTwoAA;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.m_positionOne, this.m_positionOneAA, this.m_positionTwo,
                this.m_positionTwoAA);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Site other = (Site) obj;
        return Objects.equals(this.m_positionOne, other.m_positionOne)
                && Objects.equals(this.m_positionOneAA, other.m_positionOneAA)
                && Objects.equals(this.m_positionTwo, other.m_positionTwo)
                && Objects.equals(this.m_positionTwoAA, other.m_positionTwoAA)
                && Objects.equals(this.m_ptmType, other.m_ptmType);
    }

    public PtmType getPtmType()
    {
        return this.m_ptmType;
    }

    public void setPtmType(PtmType a_ptmType)
    {
        this.m_ptmType = a_ptmType;
    }

}

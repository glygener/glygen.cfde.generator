package org.glygen.cfde.generator.csv;

import java.util.HashSet;

public class GlycanFilter
{
    private HashSet<String> m_ignore = new HashSet<>();

    public GlycanFilter()
    {
        this.m_ignore.add("G60070LT");
        this.m_ignore.add("G78166NF");
    }

    public boolean isIgnore(String a_id)
    {
        return this.m_ignore.contains(a_id);
    }
}

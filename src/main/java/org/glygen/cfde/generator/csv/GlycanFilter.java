package org.glygen.cfde.generator.csv;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class GlycanFilter
{
    private HashSet<String> m_ignore = new HashSet<>();

    public GlycanFilter()
    {
        try
        {
            List<String> t_allLines = Files.readAllLines(Paths.get("./data/glycan.ignore"));
            for (String t_line : t_allLines)
            {
                this.m_ignore.add(t_line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isIgnore(String a_id)
    {
        return this.m_ignore.contains(a_id);
    }

}

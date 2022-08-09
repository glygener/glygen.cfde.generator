package org.glygen.cfde.generator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.glygen.cfde.generator.json.MarkDownEntry;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MarkDownGenerator
{

    public void writeGlycanFile(String a_templateFile, HashMap<String, String> a_ids,
            String a_outputFile) throws IOException
    {
        List<MarkDownEntry> t_entries = new ArrayList<>();
        String t_template = Files.readString(Paths.get(a_templateFile));
        for (String t_glyTouCanId : a_ids.keySet())
        {
            String t_cfdeId = a_ids.get(t_glyTouCanId);
            String t_md = t_template.replaceAll("$GTCID", t_glyTouCanId);
            MarkDownEntry t_entry = new MarkDownEntry();
            t_entry.setId(t_cfdeId);
            t_entry.setMarkdown(t_md);
            t_entries.add(t_entry);
        }
        // create the JSON string
        ObjectMapper t_mapper = new ObjectMapper();
        t_mapper.writeValue(new File(a_outputFile), t_entries);
    }

}

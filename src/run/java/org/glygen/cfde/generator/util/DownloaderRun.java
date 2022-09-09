package org.glygen.cfde.generator.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.glygen.cfde.generator.json.glycanlist.Glycan;
import org.glygen.cfde.generator.json.glycanlist.GlycanList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DownloaderRun
{

    public static void main(String[] args) throws URISyntaxException, IOException
    {
        Downloader t_downloader = new Downloader();
        Integer t_offset = 0;
        Integer t_limit = 10;
        List<String> t_glyTouCans = new ArrayList<>();
        while (true)
        {
            System.out.println("Retrieving array dataset list: offset (" + t_offset.toString()
                    + "); limit (" + t_limit.toString() + ")");
            String t_json = t_downloader.downloadGlycanList(
                    "https://glygen.ccrc.uga.edu/array/api/", t_offset, t_limit);
            ObjectMapper t_mapper = new ObjectMapper();
            GlycanList t_glycanList = t_mapper.readValue(t_json, GlycanList.class);
            for (Glycan t_glycan : t_glycanList.getGlycanList())
            {
                if (t_glycan.getGlyTouCanId() != null)
                {
                    t_glyTouCans.add(t_glycan.getGlyTouCanId());
                }
            }
            t_offset += t_limit;
            if (t_offset >= t_glycanList.getTotal())
            {
                for (String t_string : t_glyTouCans)
                {
                    System.out.println(t_string);
                }
                return;
            }
        }

    }

}

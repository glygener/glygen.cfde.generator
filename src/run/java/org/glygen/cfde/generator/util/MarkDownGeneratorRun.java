package org.glygen.cfde.generator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.glygen.cfde.generator.json.glycan.Glycan;

import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.TemplateException;

public class MarkDownGeneratorRun
{

    public static void main(String[] args) throws IOException, TemplateException
    {
        Path filePath = Path.of("./doc/GlyGen_API/glycan.json");
        String t_json = Files.readString(filePath);

        ObjectMapper t_mapper = new ObjectMapper();
        Glycan t_glycanInfo = t_mapper.readValue(t_json, Glycan.class);

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("glyTouCanId", "G012345DB");
        // input.put("pubChemId", "10001");
        input.put("mass", t_glycanInfo.getMass());
        input.put("composition", "something");
        input.put("motifs", t_glycanInfo.getMotifs());
        input.put("organism", t_glycanInfo.getSpecies());

        FreemarkerUtil t_freemaker = new FreemarkerUtil("./data/templates/");
        String t_result = t_freemaker.render(input, "glycan.template.ftlh");

        // MarkDownGenerator t_generator = new MarkDownGenerator();
        //
        // HashMap<String, String> t_glycans = new HashMap<>();
        // t_glycans.put("G1", "G1");
        // t_glycans.put("G2", "123");
        // t_generator.writeGlycanFile("./data/mapping/glycan.template.md",
        // t_glycans, "./test.out");

        System.out.println(t_result);
    }

}

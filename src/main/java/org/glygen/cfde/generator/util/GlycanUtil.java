package org.glygen.cfde.generator.util;

import java.util.HashMap;

public class GlycanUtil
{
    private HashMap<String, String> m_validComponents = new HashMap<>();

    public GlycanUtil()
    {
        this.m_validComponents.put("hex", "Hex");
        this.m_validComponents.put("hexnac", "HexNAc");
        this.m_validComponents.put("hexn", "HexN");
        this.m_validComponents.put("dhex", "dHex");
        this.m_validComponents.put("neuac", "NeuAc");
        this.m_validComponents.put("neugc", "NeuGc");
        this.m_validComponents.put("hexa", "HexA");
        this.m_validComponents.put("p", "P");
        this.m_validComponents.put("s", "S");
        this.m_validComponents.put("other", "Other");
    }
}

package org.glygen.cfde.generator.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.glygen.cfde.generator.json.glycan.Component;

public class GlycanCompositionUtil
{
    private HashMap<String, String> m_validComponents = new HashMap<>();
    private StringBuffer m_compositionString;
    private HashMap<String, Component> m_usedComponents;

    public GlycanCompositionUtil()
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

    public String buildCompositionString(List<Component> a_component) throws IOException
    {
        this.m_usedComponents = new HashMap<>();
        this.m_compositionString = new StringBuffer();
        for (Component t_component : a_component)
        {
            String t_residue = t_component.getResidue();
            if (this.m_validComponents.get(t_residue) == null)
            {
                throw new IOException("Unknown composition component: " + t_residue);
            }
            this.m_usedComponents.put(t_residue, t_component);
        }
        this.addResidueToString("hex");
        this.addResidueToString("hexnac");
        this.addResidueToString("hexn");
        this.addResidueToString("dhex");
        this.addResidueToString("neuac");
        this.addResidueToString("neugc");
        this.addResidueToString("hexa");
        this.addResidueToString("p");
        this.addResidueToString("s");
        this.addResidueToString("other");

        String t_result = this.m_compositionString.toString();
        return t_result.trim();
    }

    public void addResidueToString(String a_resiude)
    {
        Component t_component = this.m_usedComponents.get(a_resiude);
        if (t_component != null)
        {
            this.m_compositionString.append(this.m_validComponents.get(a_resiude)
                    + t_component.getCount().toString() + " ");
        }
    }
}

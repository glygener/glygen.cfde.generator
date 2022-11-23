package org.glygen.cfde.generator;

import java.io.IOException;

public class AppRun
{
    public static void main(String[] args) throws IOException
    {
        String t_comandLine = "-c ./data/files.csv -o ./data/output/ -p ./data/glygen.properties -m ./data/mapping/ -g";
        String[] t_args = t_comandLine.split(" ");
        App.main(t_args);
    }
}

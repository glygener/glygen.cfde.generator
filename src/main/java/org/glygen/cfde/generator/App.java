package org.glygen.cfde.generator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glygen.cfde.generator.om.DCC;
import org.glygen.cfde.generator.om.FileConfig;
import org.glygen.cfde.generator.om.Namespace;
import org.glygen.cfde.generator.om.Project;
import org.glygen.cfde.generator.util.CFDEGeneratorArray;
import org.glygen.cfde.generator.util.CFDEGeneratorGlyGen;
import org.glygen.cfde.generator.util.ConfigFileParser;
import org.glygen.cfde.generator.util.PropertiesProcessor;
import org.glygen.cfde.generator.util.TSVGenerator;

public class App
{

    public static void main(String[] a_args)
    {
        // parse the command line arguments and store them
        Options t_options = App.buildComandLineOptions();
        AppArguments t_arguments = App.processCommandlineArguments(a_args, t_options);
        if (t_arguments == null)
        {
            // error messages and command line options have been printed already
            return;
        }
        // load the properties
        DCC t_dcc = null;
        Project t_projectMaster = null;
        Project t_projectGlyGen = null;
        Project t_projectArray = null;
        Namespace t_namespace = null;
        try
        {
            Properties t_properties = App.loadProperties(t_arguments.getPropertiesFile());
            try
            {
                PropertiesProcessor t_processor = new PropertiesProcessor(t_properties);
                t_dcc = t_processor.getDCC();
                t_projectMaster = t_processor.getProjectMaster();
                t_projectGlyGen = t_processor.getProjectGlyGen();
                t_projectArray = t_processor.getProjectArray();
                t_namespace = t_processor.getNamespace();
            }
            catch (Exception e)
            {
                System.out.println("Error in properties file. " + e.getMessage());
                return;
            }
        }
        catch (Exception e)
        {
            System.out.println("Unable to read properties file:" + e.getMessage());
            App.printComandParameter(t_options);
            return;
        }
        // load the config file
        List<FileConfig> t_fileConfigs = new ArrayList<>();
        try
        {
            ConfigFileParser t_parser = new ConfigFileParser();
            t_fileConfigs = t_parser.loadConfigFile(t_arguments.getConfigFile());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            App.printComandParameter(t_options);
            return;
        }
        try
        {
            // generate the TSV files in the output folder
            TSVGenerator t_generatorTSV = new TSVGenerator(t_dcc, t_projectMaster, t_projectGlyGen,
                    t_projectArray, t_namespace);
            t_generatorTSV.createTSV(t_fileConfigs, t_arguments.getOutputFolder(),
                    t_arguments.getMappingFolder());
            CFDEGeneratorGlyGen t_generatorGlyGen = new CFDEGeneratorGlyGen(t_generatorTSV);
            t_generatorGlyGen.process(t_fileConfigs);
            CFDEGeneratorArray t_generatorArray = new CFDEGeneratorArray(t_generatorTSV);
            t_generatorArray.process();
            t_generatorTSV.closeFiles();
            // log all proteins and glycans into files (used for downloading
            // their JSON files)
            App.logMolecules(t_arguments, t_generatorGlyGen, t_generatorArray);

        }
        catch (Exception e)
        {
            System.out.println("Failed ! " + e.getMessage());
            return;
        }
    }

    private static void logMolecules(AppArguments a_arguments,
            CFDEGeneratorGlyGen a_generatorGlyGen, CFDEGeneratorArray a_generatorArray)
            throws IOException
    {
        FileWriter t_writer = new FileWriter(
                a_arguments.getOutputFolder() + File.separator + "glycan.txt");
        HashMap<String, String> t_glycansGlyGen = a_generatorGlyGen.getGlycanIDs();
        for (String t_glyTouCan : t_glycansGlyGen.keySet())
        {
            String t_usedNamespaceId = t_glycansGlyGen.get(t_glyTouCan);
            if (t_glyTouCan.equals(t_usedNamespaceId))
            {
                t_writer.write(t_glyTouCan + "\n");
            }
            else
            {
                t_writer.write(t_glyTouCan + " " + t_usedNamespaceId + "\n");
            }
        }
        HashMap<String, String> t_glycansArray = a_generatorArray.getGlycanIDs();
        for (String t_glyTouCan : t_glycansArray.keySet())
        {
            if (t_glycansGlyGen.get(t_glyTouCan) == null)
            {
                String t_usedNamespaceId = t_glycansArray.get(t_glyTouCan);
                if (t_glyTouCan.equals(t_usedNamespaceId))
                {
                    t_writer.write(t_glyTouCan + "\n");
                }
                else
                {
                    t_writer.write(t_glyTouCan + " " + t_usedNamespaceId + "\n");
                }
            }
        }
        t_writer.close();
        t_writer = new FileWriter(a_arguments.getOutputFolder() + File.separator + "protein.txt");
        for (String t_protein : a_generatorGlyGen.getProteinIDs())
        {
            t_writer.write(t_protein + "\n");
        }
        t_writer.close();
    }

    /**
     * Load the properties file
     *
     * @param a_file
     *            Path to the properties file
     * @return Properties object with the values from the file
     * @throws Exception
     *             If the loading of the properties file fails
     */
    private static Properties loadProperties(String a_file) throws IOException
    {
        // open the file
        FileReader t_reader = new FileReader(a_file);
        // read properties
        Properties t_properties = new Properties();
        t_properties.load(t_reader);
        // close file
        t_reader.close();
        return t_properties;
    }

    /**
     * Process the command line options and create the AppArgument object.
     *
     * If the processing failed the error messages and command line options have
     * been printed.
     *
     * @param a_args
     *            Command line arguments given by the user
     * @param a_options
     *            Configuration object for the command line parameters
     * @return AppArguments object with the extracted command line options or
     *         NULL if parsing/validation failed. In this case error messages
     *         and valid command line options have been printed to System.out.
     */
    private static AppArguments processCommandlineArguments(String[] a_args, Options a_options)
    {
        // initialize the arguments from command line
        AppArguments t_arguments = null;
        try
        {
            t_arguments = App.parseArguments(a_args, a_options);
            if (t_arguments == null)
            {
                // failed, message was printed, time to go
                App.printComandParameter(a_options);
                return null;
            }
        }
        catch (ParseException e)
        {
            System.out.println("Invalid commandline arguments: " + e.getMessage());
            App.printComandParameter(a_options);
            return null;
        }
        catch (Exception e)
        {
            System.out.println(
                    "There was an error processing the command line arguments: " + e.getMessage());
            App.printComandParameter(a_options);
            return null;
        }
        return t_arguments;
    }

    /**
     * Parse the command line parameters or load the values from a properties
     * file. Values are validated as well.
     *
     * @param a_args
     *            Command line parameters handed down to the application.
     * @return Validated parameter object or null if loading/validation fails.
     *         In that case corresponding error message are printed to console.
     * @throws ParseException
     *             Thrown if the command line parsing fails
     */
    private static AppArguments parseArguments(String[] a_args, Options a_options)
            throws ParseException
    {
        // create the command line parser
        CommandLineParser t_parser = new DefaultParser();
        // parse the command line arguments
        CommandLine t_commandLine = t_parser.parse(a_options, a_args);
        AppArguments t_arguments = new AppArguments();
        // overwrite from arguments
        t_arguments.setConfigFile(t_commandLine.getOptionValue("c"));
        t_arguments.setOutputFolder(t_commandLine.getOptionValue("o"));
        t_arguments.setPropertiesFile(t_commandLine.getOptionValue("p"));
        t_arguments.setMappingFolder(t_commandLine.getOptionValue("m"));
        // check settings
        if (!App.checkArguments(t_arguments))
        {
            return null;
        }
        return t_arguments;
    }

    /**
     * Check the command line arguments.
     *
     * @param a_arguments
     *            Argument object filled with the parsed command line parameters
     * @return TRUE if the parameters are valid. FALSE if at least one parameter
     *         is incorrect. In that case a message is printed to System.out
     */
    private static boolean checkArguments(AppArguments a_arguments)
    {
        boolean t_valid = true;
        // config file
        if (a_arguments.getConfigFile() != null)
        {
            // config file must exist
            File t_file = new File(a_arguments.getConfigFile());
            if (t_file.exists())
            {
                if (t_file.isDirectory())
                {
                    System.out.println("Config file (-c) can not be a directory.");
                    t_valid = false;
                }
            }
            else
            {
                System.out.println("Config file (-c) does not exist.");
                t_valid = false;
            }
        }
        else
        {
            System.out.println("Config file (-c) is missing.");
            t_valid = false;
        }
        // properties file
        if (a_arguments.getConfigFile() != null)
        {
            // file must exist
            File t_file = new File(a_arguments.getPropertiesFile());
            if (t_file.exists())
            {
                if (t_file.isDirectory())
                {
                    System.out.println("Properties file (-p) can not be a directory.");
                    t_valid = false;
                }
            }
            else
            {
                System.out.println("Properties file (-p) does not exist.");
                t_valid = false;
            }
        }
        else
        {
            System.out.println("Properties file (-p) is missing.");
            t_valid = false;
        }
        // output folder
        if (a_arguments.getOutputFolder() != null)
        {
            File t_file = new File(a_arguments.getOutputFolder());
            if (!t_file.exists())
            {
                if (!t_file.mkdirs())
                {
                    System.out.println("Unable to create output folder.");
                    t_valid = false;
                }
            }
        }
        else
        {
            System.out.println("Output folder (-o) is missing.");
            t_valid = false;
        }
        // mapping folder
        if (a_arguments.getMappingFolder() != null)
        {
            File t_file = new File(a_arguments.getMappingFolder());
            if (t_file.exists())
            {
                if (!t_file.isDirectory())
                {
                    System.out.println("Mapping folder (-m) has to be a directory.");
                    t_valid = false;
                }
            }
            else
            {
                System.out.println("Mapping folder (-m) does not exist.");
                t_valid = false;
            }
        }
        return t_valid;
    }

    /**
     * Print out the command line parameter.
     */
    private static void printComandParameter(Options a_options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "<command> -c <configFile> -o <OutputFolder> -p <propertyFile> -m <mappingFolder>",
                a_options);
    }

    /**
     * Build the command line argument object that contains all options
     *
     * @return Object with the command line options
     */
    private static Options buildComandLineOptions()
    {
        // create the Options
        Options t_options = new Options();
        // configuration file
        Option t_option = new Option("c", "config", true,
                "CSV configuration file with the list of files to include");
        t_option.setArgs(1);
        t_option.setRequired(true);
        t_options.addOption(t_option);
        // output folder
        t_option = new Option("o", "output", true,
                "Output folder that will contain the TSV files.");
        t_option.setArgs(1);
        t_option.setRequired(true);
        t_options.addOption(t_option);
        // properties file
        t_option = new Option("p", "prop", true,
                "Properties files with DCC information and general information.");
        t_option.setArgs(1);
        t_option.setRequired(true);
        t_options.addOption(t_option);
        // mapping folder
        t_option = new Option("m", "mapping", true, "Folder with the mapping files.");
        t_option.setArgs(1);
        t_option.setRequired(false);
        t_options.addOption(t_option);

        return t_options;
    }
}

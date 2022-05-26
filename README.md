# GlyGen CFDE C2M2 data model generator

## Purpose
This program is a maven/java project with the aim to generate the files needed to upload the GlyGen data to the CFDE data portal. This files have to follow the [C2M2 specification](https://docs.nih-cfde.org/en/latest/c2m2/draft-C2M2_specification/). The Java program will download the GlyGen files according to the [File configuration](../../wiki/File-configuration) and generate the corresponding TSV files.

## Prerequisite
To prepare the execution of the Java program two files have to be generated or update:
* [File configuration](../../wiki/File-configuration) with the list of all files to be integrated into the generation process. The default **files.csv** is available in the repository in folder [data](https://github.com/glygener/glygen.cfde.generator/tree/main/data)
* [Property file](../../wiki/Property-file) containing the settings for the TSV metadata. The default **glygen.properties** is available in the repository in folder [data](https://github.com/glygener/glygen.cfde.generator/tree/main/data)

## Run the application
To run the program it is necessary to download and install:
* Java 1.8+; either [OpenJDK](https://openjdk.java.net) or [Oracle Java](https://www.java.com/download/ie_manual.jsp)
* [Apache Maven](https://maven.apache.org/download.cgi)

It is also necessary to checkout/clone or download the source code from the [repository](https://github.com/glygener/glygen.cfde.generator).

Before running the program the code needs to be compiled. Run the following commandline in the root folder of the source code (the **pom.xml** file is located there): 

`mvn compile`

To finally run the program from commandline use the following command from the root folder of the source code (the **pom.xml** file is located there):

`mvn exec:java -Dexec.mainClass=org.glygen.cfde.generator.App -Dexec.args="-c ./data/files.csv -o ./data/output/ -p ./data/glygen.properties -m ./data/mapping/"`

The values in double quote are the commandline arguments that need to be adjusted according to the local system:
* **-c** location of the file with the [File configuration](../../wiki/File-configuration)
* **-o** folder that will be used to generate the output (GlyGen downloads, TSV files, error report)
* **-p** location of the [Property file](../../wiki/Property-file)
* **-m** folder with the mapping files

The program runs successful it will generate a folder named **tsv** inside the output folder. This folder will contain all generated TSV files. Inside the output folder will also be a file **log.csv** which contains the error logging. Its important to check this file after the run to ensure now files or data rows where omitted.

## Post run
After the execution was successful it is important to check the error file. After this the missing dictionary tables have to be generated using the [presubmission prep tool](https://github.com/nih-cfde/published-documentation/wiki/submission-prep-script) from CFDE. Once this is successfully completed and data package can be submitted using the [CFDE submission tool](https://docs.nih-cfde.org/en/latest/cfde-submit/docs/).

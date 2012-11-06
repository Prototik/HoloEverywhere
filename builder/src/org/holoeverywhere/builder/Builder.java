
package org.holoeverywhere.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Builder {
    private static final HelpFormatter helpFormatter = new HelpFormatter();
    private static final Options options = new Options();
    private static final CommandLineParser parser = new PosixParser();
    static {
        Option option;
        option = new Option("s", "source", true, "Source file for build");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("o", "output", true, "Output file");
        option.setRequired(true);
        options.addOption(option);
    }

    public static void main(String[] args) {
        System.out.println("HoloEverywhere Builder v0.0.1");
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp();
            return;
        }
        String sourceName = cmd.getOptionValue('s');
        String outputName = cmd.getOptionValue('o');
        process(new File(sourceName), new File(outputName));
    }

    private static void printHelp() {
        helpFormatter.printHelp("java -jar builder.jar", options, true);
    }

    public static void process(File source, File output) {
        if (!validFile(source)) {
            System.err.println("Source " + source.getAbsolutePath() + " doesn't exists");
            printHelp();
            return;
        }
        Document document = Parser.parse(source);
        try {
            Processer.process(document, new FileOutputStream(output));
        } catch (FileNotFoundException e) {
            System.err.println("Error while processing: " + e.getMessage());
        }
    }

    private static boolean validFile(File file) {
        return file.exists() && file.isFile();
    }
}

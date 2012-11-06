
package org.holoeverywhere.translator;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Translator {
    private static final int DEFAULT_ANDROID_VERSION = 16; // Jelly Bean
    private static final HelpFormatter helpFormatter = new HelpFormatter();
    private static final Options options = new Options();
    private static final CommandLineParser parser = new PosixParser();
    static {
        Option option;
        option = new Option("s", "sdk", true, "Android SDK path");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "file", true, "Input file");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("o", "output", true, "Output folder");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("v", "version", true, "Version of Android platform for grab translate");
        option.setRequired(false);
        options.addOption(option);
    }

    public static void main(String[] args) {
        System.out.println("HoloEverywhere Translator v0.0.1");
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp();
            return;
        }
        String sdkName = cmd.getOptionValue('s');
        String inputName = cmd.getOptionValue('f');
        String outputName = cmd.getOptionValue('o');
        int version = cmd.hasOption('v') ? Integer.parseInt(cmd.getOptionValue('v'))
                : DEFAULT_ANDROID_VERSION;
        process(new File(sdkName), new File(inputName), new File(outputName), version);
    }

    private static void printHelp() {
        helpFormatter.printHelp("java -jar translator.jar", options, true);
    }

    public static void process(File sdk, File input, File output, int version) {
        if (!validDir(sdk)) {
            System.err.println("Android SDK path not valid: " + sdk.getAbsolutePath());
            printHelp();
            return;
        }
        File platform = new File(sdk, "platforms/android-" + version);
        if (!validDir(platform)) {
            System.err.println("Platform path for version " + version + " not found: "
                    + platform.getAbsolutePath());
            printHelp();
            return;
        }
        final Grabber grabber = Grabber.grabber(new File(platform, "data/res"));
        if (!validFile(input)) {
            System.err.println("Input file not found: " + input.getAbsolutePath());
            printHelp();
            return;
        }
        final Document document = Parser.parse(input);
        Processer.process(document, grabber, output);
    }

    private static boolean validDir(File file) {
        return file.exists() && file.isDirectory();
    }

    private static boolean validFile(File file) {
        return file.exists() && file.isFile();
    }

}

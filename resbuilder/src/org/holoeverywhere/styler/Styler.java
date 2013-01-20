
package org.holoeverywhere.styler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal styler
 * @phase initialize
 */
public class Styler extends AbstractMojo {
    public static final class Config {
        /**
         * @parameter
         */
        private String[] input;
        /**
         * @parameter
         */
        private File outputDir;
        /**
         * @parameter
         */
        private File includeDir;
    }

    /**
     * @parameter alias="styler"
     */
    private Config config;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (config == null || config.input == null || config.input.length == 0) {
            getLog().info("Not need to generate resources");
            return;
        }
        Parser.setSourcePath(config.includeDir);
        try {
            for (String file : config.input) {
                process(file);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("IOException", e);
        }
    }

    public void process(String sourceName) throws MojoExecutionException, MojoFailureException,
            IOException {
        File source = new File(config.includeDir, sourceName);
        if (!validFile(source)) {
            throw new MojoFailureException("Source " + source.getAbsolutePath() + " doesn't exists");
        }
        Document document = Parser.parse(source);
        if (document.output == null) {
            getLog().warn(source.getCanonicalPath() + " don't has output file, ignore");
            return;
        }
        File output = new File(config.outputDir, document.output);
        getLog().info("Compile " + source.getName() + " => " + output.getCanonicalPath());
        try {
            Processer.process(document, new FileOutputStream(output));
        } catch (FileNotFoundException e) {
            getLog().error("Error while processing " + source.getCanonicalPath(), e);
        }
    }

    private static boolean validFile(File file) {
        return file.exists() && file.isFile();
    }
}

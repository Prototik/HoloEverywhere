
package org.holoeverywhere.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal build
 */
public class Builder extends AbstractMojo {
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (input.length == 0) {
            getLog().info("Not need to generate resources");
            return;
        }
        Parser.setSourcePath(includeDir);
        try {
            getLog().info("");
            for (String file : input) {
                process(file);
                getLog().info("");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("IOException", e);
        }
    }

    public void process(String sourceName) throws MojoExecutionException, MojoFailureException,
            IOException {
        File source = new File(includeDir, sourceName);
        if (!validFile(source)) {
            throw new MojoFailureException("Source " + source.getAbsolutePath() + " doesn't exists");
        }
        Document document = Parser.parse(source);
        if (document.output == null) {
            getLog().warn(source.getCanonicalPath() + " don't has output file, ignore");
            return;
        }
        File output = new File(outputDir, document.output);
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

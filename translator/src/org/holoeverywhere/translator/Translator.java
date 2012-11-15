
package org.holoeverywhere.translator;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal build
 */
public class Translator extends AbstractMojo {
    /**
     * @parameter expression="${android.sdk.path}"
     */
    private File sdk;
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
    /**
     * @parameter default-value="16"
     */
    private int version;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (input.length == 0) {
            getLog().info("Not need to generate resources");
            return;
        }
        if (!validDir(sdk)) {
            getLog().error(
                    "Android SDK path not valid: " + (sdk == null ? "null" : sdk.getAbsolutePath()));
            return;
        }
        File platform = new File(sdk, "platforms/android-" + version);
        if (!validDir(platform)) {
            getLog().error("Platform path for version " + version + " not found: "
                    + platform.getAbsolutePath());
            return;
        }
        final Grabber grabber = Grabber.grabber(new File(platform, "data/res"));
        getLog().info("Res folder for compile: " + outputDir.getAbsolutePath());
        getLog().info("");
        for (String fileName : input) {
            File file = new File(includeDir, fileName);
            if (!validFile(file)) {
                getLog().error("Input file not found: " + file.getAbsolutePath());
                return;
            }
            final Document document = Parser.parse(file);
            getLog().info("Compile " + file.getName());
            Processer.process(document, grabber, outputDir);
            getLog().info("");
        }
    }

    private static boolean validDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    private static boolean validFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

}

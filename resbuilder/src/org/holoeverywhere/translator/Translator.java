
package org.holoeverywhere.translator;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal translator
 * @phase initialize
 */
public class Translator extends AbstractMojo {
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
     * @parameter alias="translator"
     */
    private Config config;
    /**
     * @parameter expression="${android.sdk.path}"
     * @required
     */
    private File sdk;
    /**
     * @parameter expression="${android.sdk.platform}" default-value="16"
     * @required
     */
    private int version;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (config == null || config.input == null || config.input.length == 0) {
            getLog().info("Not need to generate resources");
            return;
        }
        if (!validDir(sdk)) {
            throw new MojoFailureException("Android SDK path not valid: "
                    + (sdk == null ? "null" : sdk.getAbsolutePath()));
        }
        File platform = new File(sdk, "platforms/android-" + version);
        if (!validDir(platform)) {
            throw new MojoFailureException("Platform path for version " + version
                    + " not found: " + platform.getAbsolutePath());
        }
        final Grabber grabber = Grabber.grabber(new File(platform, "data/res"));
        getLog().info("Res folder for compile: " + config.outputDir.getAbsolutePath());
        getLog().info("");
        for (String fileName : config.input) {
            File file = new File(config.includeDir, fileName);
            if (!validFile(file)) {
                getLog().error("Input file not found: " + file.getAbsolutePath());
                return;
            }
            final Document document = Parser.parse(file);
            getLog().info("Compile " + file.getAbsolutePath());
            Processer.process(document, grabber, config.outputDir);
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

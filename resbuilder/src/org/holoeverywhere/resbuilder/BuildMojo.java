
package org.holoeverywhere.resbuilder;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.holoeverywhere.resbuilder.FileProcesser.FileProcesserException;

/**
 * @goal build
 * @phase initialize
 */
public class BuildMojo extends AbstractMojo {
    private static final FileFilter BUILD_ALL_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().length() > 5
                    && pathname.getName().endsWith(".json");
        }
    };

    /**
     * Path to android sdk
     * 
     * @parameter expression="${android.sdk.path}"
     * @required
     */
    public File androidSdkPath;

    /**
     * @parameter expression="${android.sdk.platform}" default-value="16"
     * @required
     */
    public int androidSdkVersion;

    /**
     * Build all files from all include dirs
     * 
     * @parameter expression="${holo.resbuilder.buildAll}" default-value="false"
     */
    public boolean buildAll;

    /**
     * Dirs for search of input files
     * 
     * @parameter expression="${holo.resbuilder.includeDirs}"
     */
    public File[] includeDirs;

    /**
     * Files for processing
     * 
     * @parameter expression="${holo.resbuilder.input}" alias="input"
     */
    public String[] inputFiles;

    /**
     * Default output dir, if input file don't specify it
     * 
     * @parameter expression="${holo.resbuilder.outputDir}"
     *            default-value="${basedir}/res"
     */
    public File outputDir;
    public FileProcesser processer;

    /**
     * If true - skip resource build
     * 
     * @parameter expression="${holo.resbuilder.skip}" default-value="false"
     */
    public boolean skip;

    /**
     * Be verbose
     * 
     * @parameter expression="${holo.resbuilder.verbose}" default-value="true"
     */
    public boolean verbose;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Flag 'Skip' is true");
            return;
        }
        if (buildAll) {
            if (includeDirs == null || includeDirs.length == 0) {
                getLog().warn(
                        "BuildAll: You want build all files from all include dirs, but you don't specify any include dir. Nothing to build. Skip.");
                return;
            }
            List<String> list = new ArrayList<String>();
            for (File dir : includeDirs) {
                File[] files = dir.listFiles(BUILD_ALL_FILTER);
                for (File file : files) {
                    getLog().info("BuildAll: add " + file.getAbsolutePath());
                    list.add(file.getAbsolutePath());
                }
            }
            if (inputFiles != null && inputFiles.length > 0) {
                list.addAll(0, Arrays.asList(inputFiles));
            }
            inputFiles = list.toArray(new String[list.size()]);
            if (inputFiles.length == 0) {
                getLog().info("BuildAll: nothing to build");
                return;
            }
        }
        if (inputFiles == null || inputFiles.length == 0) {
            getLog().info("Don't specify input files, skip");
            return;
        }
        if (!buildAll && includeDirs == null || includeDirs.length == 0) {
            getLog().warn("Include dirs don't specified");
        }
        if (verbose) {
            getLog().info("");
            getLog().info("Final configuration:");
            getLog().info(
                    " # androidSdkPath: " + androidSdkPath);
            getLog().info(" # androidSdkVersion: " + androidSdkVersion);
            if (includeDirs == null || includeDirs.length == 0) {
                getLog().info(" # includeDirs: empty");
            } else {
                getLog().info(" # includeDirs: [");
                for (File dir : includeDirs) {
                    getLog().info(" # # " + dir.getAbsolutePath());
                }
                getLog().info(" # ]");
            }
            if (inputFiles == null || inputFiles.length == 0) {
                getLog().info(" # input: empty");
            } else {
                getLog().info(" # input: [");
                for (String input : inputFiles) {
                    getLog().info(" # # " + input);
                }
                getLog().info(" # ]");
            }
            getLog().info(" # outputDir: " + outputDir);
        }
        try {
            FileProcesser.process(this);
        } catch (FileProcesserException e) {
            throw new MojoFailureException("Error in FileProcesser", e);
        }
    }
}

package org.holoeverywhere.plugin.extension.fakeandroid

import com.android.build.gradle.internal.LoggerWrapper
import com.android.build.gradle.internal.SdkHandler
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

public class FakeAndroidExtension {
    private final Project project

    public FakeAndroidExtension(Project project) {
        this.project = project
        androidSdkDir = new SdkHandler(project, new LoggerWrapper(project.logger)).getSdkFolder()
    }

    public int compileSdkVersion = 0
    public final File androidSdkDir

    public FileCollection api(int version) {
        final File api = new File(androidSdkDir, String.format('platforms/android-%d/android.jar', version))
        if (!api.exists()) {
            throw new IllegalStateException("Cannot resolve android api dependency for version ${version}")
        }
        return project.files(api)
    }

    public FileCollection compileApi() {
        if (compileSdkVersion <= 0) {
            throw new IllegalStateException("You should setup a compileSdkVersion property")
        }
        return api(compileSdkVersion)
    }

    public void compileSdkVersion(int compileSdkVersion) {
        this.compileSdkVersion = compileSdkVersion
    }
}

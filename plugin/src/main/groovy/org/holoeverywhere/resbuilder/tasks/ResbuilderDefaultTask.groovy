package org.holoeverywhere.resbuilder.tasks

import com.android.build.gradle.BasePlugin
import com.android.sdklib.IAndroidTarget
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet

abstract class ResbuilderDefaultTask extends DefaultTask {
    @Input
    def Set<ResbuilderSourceSet> source

    private def File resourcesDir

    File obtainResourcesDir() {
        if (resourcesDir == null) {
            try {
                def BasePlugin androidPlugin = project.plugins.find {
                    BasePlugin.class.isAssignableFrom(it.class)
                } as BasePlugin
                if (androidPlugin == null) {
                    throw new RuntimeException("Could not find android plugin/extension")
                }
                resourcesDir = new File(androidPlugin.loadedSdkParser.target.getPath(IAndroidTarget.RESOURCES))
            } catch (Exception e) {
                project.logger.error("Cannot retrieve resource directory", e)
            }
        }
        return resourcesDir
    }
}

package org.holoeverywhere.plugin.extension

import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSetFactory

class ResbuilderContainer implements Configurable<ResbuilderContainer> {
    ResbuilderContainer(Project project, Instantiator instantiator) {
        String resourcesDir = null;
        try {
            BaseExtension androidExtension = project.extensions.getByName('android') as BaseExtension
            Iterator<File> iterator = androidExtension.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).res.srcDirs.iterator()
            resourcesDir = iterator.hasNext() ? (project as ProjectInternal).fileResolver.resolveAsRelativePath(iterator.next()) : null
        } catch (Exception e) {
        }

        sourceSets = project.container(ResbuilderSourceSet, ResbuilderSourceSetFactory.fromProject(project, instantiator, resourcesDir))
        sourceSets.create(SourceSet.MAIN_SOURCE_SET_NAME)
    }

    final NamedDomainObjectContainer<ResbuilderSourceSet> sourceSets
    def boolean enable = true
    def boolean formatTask = true

    def sourceSets(Closure<?> closure) {
        sourceSets.configure closure
    }

    @Override
    ResbuilderContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this)
        return this
    }
}

package org.holoeverywhere.resbuilder.dsl

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSetOutput
import org.gradle.api.internal.tasks.TaskResolver
import org.gradle.api.tasks.SourceSetOutput
import org.gradle.internal.reflect.Instantiator

class ResbuilderSourceSetFactory implements NamedDomainObjectFactory<ResbuilderSourceSet> {
    static ResbuilderSourceSetFactory fromProject(Project project, Instantiator instantiator, String resourcesDir) {
        return new ResbuilderSourceSetFactory(instantiator, (ProjectInternal) project, resourcesDir ?: 'res')
    }

    class DefaultResbuilderSourceSet implements ResbuilderSourceSet {
        private final String name
        private final SourceDirectorySet resources
        private final SourceSetOutput output

        public DefaultResbuilderSourceSet(String name) {
            this.name = name

            this.resources = new DefaultSourceDirectorySet(name, fileResolver)
            this.resources.srcDir('resbuilder').filter.include('**/*.yml')

            this.output = new DefaultSourceSetOutput(name, fileResolver, taskResolver)
            this.output.resourcesDir = fileResolver.resolve(resourcesDir)
        }


        @Override
        String getName() {
            return name;
        }

        @Override
        SourceDirectorySet getResources() {
            return resources
        }

        @Override
        ResbuilderSourceSet resources(Closure<?> closure) {
            closure = closure.clone() as Closure<?>;
            closure.delegate = resources
            closure.call(resources)
            return this
        }

        @Override
        ResbuilderSourceSet resources(String pattern) {
            resources.include(pattern)
            return this
        }

        @Override
        SourceSetOutput getOutput() {
            return output
        }

        @Override
        String toString() {
            return name
        }
    }

    private final Instantiator instantiator
    private final FileResolver fileResolver
    private final TaskResolver taskResolver
    private final Project project
    private final String resourcesDir

    public ResbuilderSourceSetFactory(Instantiator instantiator, ProjectInternal project, String resourcesDir) {
        this.instantiator = instantiator
        this.fileResolver = project.fileResolver
        this.taskResolver = project.tasks
        this.project = project
        this.resourcesDir = resourcesDir
    }

    @Override
    ResbuilderSourceSet create(String name) {
        return new DefaultResbuilderSourceSet(name)
    }
}

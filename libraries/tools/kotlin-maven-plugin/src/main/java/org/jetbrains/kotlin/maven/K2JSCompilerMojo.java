/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.cli.common.arguments.K2JSCompilerArguments;
import org.jetbrains.jet.cli.js.K2JSCompiler;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.join;

/**
 * Converts Kotlin to JavaScript code
 *
 * @goal js
 * @phase compile
 * @requiresDependencyResolution compile
 * @noinspection UnusedDeclaration
 */
public class K2JSCompilerMojo extends KotlinCompileMojoBase<K2JSCompilerArguments> {
    /**
     * Project classpath.
     *
     * @parameter default-value="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    public List<String> classpath;

    /**
     * The output JS file name
     *
     * @required
     * @parameter default-value="${project.build.directory}/js/${project.artifactId}.js"
     */
    private String outputFile;

    /**
     * The output Kotlin JS file directory
     *
     * @required
     * @parameter default-value="${project.build.directory}/js"
     * @parameter expression="${outputKotlinJSFile}"
     */
    private String outputLibraryJSPath;

    /**
     * Whether to copy the runtime js-files from libraries to the output directory
     *
     * @parameter default-value="true"
     * @parameter expression="${copyLibraryJS}"
     */
    private Boolean copyLibraryJS;

    @Override
    protected void configureSpecificCompilerArguments(@NotNull K2JSCompilerArguments arguments) throws MojoExecutionException {
        arguments.outputFile = outputFile;
        arguments.outputLibraryJSPath = outputLibraryJSPath;
        arguments.noStdlib = true;

        List<String> jarPaths = new ArrayList<String>();
        for(int i=0; i<classpath.size(); i++) {
            String path = classpath.get(i);
            if (path.endsWith(".jar")) {
                jarPaths.add(path);
            }
        }
        LOG.info("libraryFiles: " + jarPaths);
        arguments.libraryFiles = jarPaths.toArray(new String[] {});

        if (copyLibraryJS != null) {
            arguments.copyLibraryJS = copyLibraryJS;
        }
    }

    @NotNull
    @Override
    protected K2JSCompilerArguments createCompilerArguments() {
        return new K2JSCompilerArguments();
    }

    @NotNull
    @Override
    protected K2JSCompiler createCompiler() {
        return new K2JSCompiler();
    }
}

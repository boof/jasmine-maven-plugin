package jasmine.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import jasmine.plugin.readers.DirectoryReader;
import jasmine.plugin.readers.HtmlReader;
import jasmine.plugin.readers.SuiteReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal jasmine
 *
 * @phase test
 */
public class JasmineMojo
    extends AbstractMojo
{

    /**
     * Directory of SpecRunner.html.
     * @parameter default-value="src/main/webapp"
     * @required
     */
    private File baseDirectory;

    public void execute()
        throws MojoExecutionException
    {
        Log log = getLog();
        new JasmineRunner(baseDirectory, log).run();
    }

}

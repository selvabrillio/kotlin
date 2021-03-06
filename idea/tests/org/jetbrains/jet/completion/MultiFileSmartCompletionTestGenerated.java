/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.completion;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.jet.JUnit3RunnerWithInners;
import org.jetbrains.jet.JetTestUtils;
import org.jetbrains.jet.test.InnerTestClasses;
import org.jetbrains.jet.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.jet.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/completion/smartMultiFile")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class MultiFileSmartCompletionTestGenerated extends AbstractMultiFileSmartCompletionTest {
    public void testAllFilesPresentInSmartMultiFile() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/completion/smartMultiFile"), Pattern.compile("^([^\\.]+)$"), false);
    }

    @TestMetadata("FunctionFromAnotherPackage")
    public void testFunctionFromAnotherPackage() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/smartMultiFile/FunctionFromAnotherPackage/");
        doTest(fileName);
    }

    @TestMetadata("JavaStaticMethodArgument")
    public void testJavaStaticMethodArgument() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/testData/completion/smartMultiFile/JavaStaticMethodArgument/");
        doTest(fileName);
    }
}

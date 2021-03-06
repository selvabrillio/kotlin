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

package org.jetbrains.jet.codegen.generated;

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
@TestMetadata("compiler/testData/codegen/boxWithJava")
@TestDataPath("$PROJECT_ROOT")
@InnerTestClasses({BlackBoxWithJavaCodegenTestGenerated.BuiltinStubMethods.class, BlackBoxWithJavaCodegenTestGenerated.PlatformStatic.class, BlackBoxWithJavaCodegenTestGenerated.Properties.class})
@RunWith(JUnit3RunnerWithInners.class)
public class BlackBoxWithJavaCodegenTestGenerated extends AbstractBlackBoxCodegenTest {
    public void testAllFilesPresentInBoxWithJava() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/codegen/boxWithJava"), Pattern.compile("^([^\\.]+)$"), true);
    }

    @TestMetadata("referenceToJavaFieldOfKotlinSubclass")
    public void testReferenceToJavaFieldOfKotlinSubclass() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/referenceToJavaFieldOfKotlinSubclass/");
        doTestWithJava(fileName);
    }

    @TestMetadata("trait")
    public void testTrait() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/trait/");
        doTestWithJava(fileName);
    }

    @TestMetadata("compiler/testData/codegen/boxWithJava/builtinStubMethods")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(JUnit3RunnerWithInners.class)
    public static class BuiltinStubMethods extends AbstractBlackBoxCodegenTest {
        public void testAllFilesPresentInBuiltinStubMethods() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/codegen/boxWithJava/builtinStubMethods"), Pattern.compile("^([^\\.]+)$"), true);
        }

        @TestMetadata("extendJavaCollections")
        public void testExtendJavaCollections() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/builtinStubMethods/extendJavaCollections/");
            doTestWithJava(fileName);
        }

        @TestMetadata("substitutedIterable")
        public void testSubstitutedIterable() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/builtinStubMethods/substitutedIterable/");
            doTestWithJava(fileName);
        }

        @TestMetadata("substitutedList")
        public void testSubstitutedList() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/builtinStubMethods/substitutedList/");
            doTestWithJava(fileName);
        }

    }

    @TestMetadata("compiler/testData/codegen/boxWithJava/platformStatic")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(JUnit3RunnerWithInners.class)
    public static class PlatformStatic extends AbstractBlackBoxCodegenTest {
        public void testAllFilesPresentInPlatformStatic() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/codegen/boxWithJava/platformStatic"), Pattern.compile("^([^\\.]+)$"), true);
        }

        @TestMetadata("annotations")
        public void testAnnotations() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/platformStatic/annotations/");
            doTestWithJava(fileName);
        }

        @TestMetadata("classObject")
        public void testClassObject() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/platformStatic/classObject/");
            doTestWithJava(fileName);
        }

        @TestMetadata("object")
        public void testObject() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/platformStatic/object/");
            doTestWithJava(fileName);
        }

    }

    @TestMetadata("compiler/testData/codegen/boxWithJava/properties")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Properties extends AbstractBlackBoxCodegenTest {
        public void testAllFilesPresentInProperties() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/codegen/boxWithJava/properties"), Pattern.compile("^([^\\.]+)$"), true);
        }

        @TestMetadata("classObjectProperties")
        public void testClassObjectProperties() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/codegen/boxWithJava/properties/classObjectProperties/");
            doTestWithJava(fileName);
        }

    }

}

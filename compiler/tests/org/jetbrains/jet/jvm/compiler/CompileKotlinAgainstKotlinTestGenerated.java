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

package org.jetbrains.jet.jvm.compiler;

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
@TestMetadata("compiler/testData/compileKotlinAgainstKotlin")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class CompileKotlinAgainstKotlinTestGenerated extends AbstractCompileKotlinAgainstKotlinTest {
    public void testAllFilesPresentInCompileKotlinAgainstKotlin() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/compileKotlinAgainstKotlin"), Pattern.compile("^(.+)\\.A.kt$"), true);
    }

    @TestMetadata("ClassInObject.A.kt")
    public void testClassInObject() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/ClassInObject.A.kt");
        doTest(fileName);
    }

    @TestMetadata("ClassObjectInEnum.A.kt")
    public void testClassObjectInEnum() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/ClassObjectInEnum.A.kt");
        doTest(fileName);
    }

    @TestMetadata("ClassObjectMember.A.kt")
    public void testClassObjectMember() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/ClassObjectMember.A.kt");
        doTest(fileName);
    }

    @TestMetadata("ConstructorVararg.A.kt")
    public void testConstructorVararg() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/ConstructorVararg.A.kt");
        doTest(fileName);
    }

    @TestMetadata("DefaultConstructor.A.kt")
    public void testDefaultConstructor() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/DefaultConstructor.A.kt");
        doTest(fileName);
    }

    @TestMetadata("DoublyNestedClass.A.kt")
    public void testDoublyNestedClass() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/DoublyNestedClass.A.kt");
        doTest(fileName);
    }

    @TestMetadata("Enum.A.kt")
    public void testEnum() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/Enum.A.kt");
        doTest(fileName);
    }

    @TestMetadata("ImportObject.A.kt")
    public void testImportObject() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/ImportObject.A.kt");
        doTest(fileName);
    }

    @TestMetadata("InlinedConstants.A.kt")
    public void testInlinedConstants() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/InlinedConstants.A.kt");
        doTest(fileName);
    }

    @TestMetadata("InnerClassConstructor.A.kt")
    public void testInnerClassConstructor() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/InnerClassConstructor.A.kt");
        doTest(fileName);
    }

    @TestMetadata("KotlinPropertyAsAnnotationParameter.A.kt")
    public void testKotlinPropertyAsAnnotationParameter() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/KotlinPropertyAsAnnotationParameter.A.kt");
        doTest(fileName);
    }

    @TestMetadata("NestedClass.A.kt")
    public void testNestedClass() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/NestedClass.A.kt");
        doTest(fileName);
    }

    @TestMetadata("NestedEnum.A.kt")
    public void testNestedEnum() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/NestedEnum.A.kt");
        doTest(fileName);
    }

    @TestMetadata("NestedObject.A.kt")
    public void testNestedObject() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/NestedObject.A.kt");
        doTest(fileName);
    }

    @TestMetadata("PlatformNames.A.kt")
    public void testPlatformNames() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/PlatformNames.A.kt");
        doTest(fileName);
    }

    @TestMetadata("PlatformTypes.A.kt")
    public void testPlatformTypes() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/PlatformTypes.A.kt");
        doTest(fileName);
    }

    @TestMetadata("PropertyReference.A.kt")
    public void testPropertyReference() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/PropertyReference.A.kt");
        doTest(fileName);
    }

    @TestMetadata("Simple.A.kt")
    public void testSimple() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/Simple.A.kt");
        doTest(fileName);
    }

    @TestMetadata("StarImportEnum.A.kt")
    public void testStarImportEnum() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("compiler/testData/compileKotlinAgainstKotlin/StarImportEnum.A.kt");
        doTest(fileName);
    }
}

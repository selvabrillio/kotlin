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

package org.jetbrains.jet.resolve.constraintSystem;

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
@TestMetadata("compiler/testData/constraintSystem")
@TestDataPath("$PROJECT_ROOT")
@InnerTestClasses({ConstraintSystemTestGenerated.CheckStatus.class, ConstraintSystemTestGenerated.ComputeValues.class, ConstraintSystemTestGenerated.IntegerValueTypes.class, ConstraintSystemTestGenerated.SeveralVariables.class, ConstraintSystemTestGenerated.Variance.class})
@RunWith(JUnit3RunnerWithInners.class)
public class ConstraintSystemTestGenerated extends AbstractConstraintSystemTest {
    public void testAllFilesPresentInConstraintSystem() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/constraintSystem"), Pattern.compile("^(.+)\\.bounds$"), true);
    }

    @TestMetadata("compiler/testData/constraintSystem/checkStatus")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class CheckStatus extends AbstractConstraintSystemTest {
        public void testAllFilesPresentInCheckStatus() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/constraintSystem/checkStatus"), Pattern.compile("^(.+)\\.bounds$"), true);
        }

        @TestMetadata("conflictingConstraints.bounds")
        public void testConflictingConstraints() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/checkStatus/conflictingConstraints.bounds");
            doTest(fileName);
        }

        @TestMetadata("successful.bounds")
        public void testSuccessful() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/checkStatus/successful.bounds");
            doTest(fileName);
        }

        @TestMetadata("typeConstructorMismatch.bounds")
        public void testTypeConstructorMismatch() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/checkStatus/typeConstructorMismatch.bounds");
            doTest(fileName);
        }

        @TestMetadata("unknownParameters.bounds")
        public void testUnknownParameters() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/checkStatus/unknownParameters.bounds");
            doTest(fileName);
        }

        @TestMetadata("violatedUpperBound.bounds")
        public void testViolatedUpperBound() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/checkStatus/violatedUpperBound.bounds");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/constraintSystem/computeValues")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ComputeValues extends AbstractConstraintSystemTest {
        public void testAllFilesPresentInComputeValues() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/constraintSystem/computeValues"), Pattern.compile("^(.+)\\.bounds$"), true);
        }

        @TestMetadata("contradiction.bounds")
        public void testContradiction() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/computeValues/contradiction.bounds");
            doTest(fileName);
        }

        @TestMetadata("subTypeOfUpperBounds.bounds")
        public void testSubTypeOfUpperBounds() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/computeValues/subTypeOfUpperBounds.bounds");
            doTest(fileName);
        }

        @TestMetadata("superTypeOfLowerBounds1.bounds")
        public void testSuperTypeOfLowerBounds1() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/computeValues/superTypeOfLowerBounds1.bounds");
            doTest(fileName);
        }

        @TestMetadata("superTypeOfLowerBounds2.bounds")
        public void testSuperTypeOfLowerBounds2() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/computeValues/superTypeOfLowerBounds2.bounds");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/constraintSystem/integerValueTypes")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class IntegerValueTypes extends AbstractConstraintSystemTest {
        public void testAllFilesPresentInIntegerValueTypes() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/constraintSystem/integerValueTypes"), Pattern.compile("^(.+)\\.bounds$"), true);
        }

        @TestMetadata("byteOverflow.bounds")
        public void testByteOverflow() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/byteOverflow.bounds");
            doTest(fileName);
        }

        @TestMetadata("defaultLong.bounds")
        public void testDefaultLong() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/defaultLong.bounds");
            doTest(fileName);
        }

        @TestMetadata("numberAndAny.bounds")
        public void testNumberAndAny() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/numberAndAny.bounds");
            doTest(fileName);
        }

        @TestMetadata("numberAndString.bounds")
        public void testNumberAndString() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/numberAndString.bounds");
            doTest(fileName);
        }

        @TestMetadata("severalNumbers.bounds")
        public void testSeveralNumbers() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/severalNumbers.bounds");
            doTest(fileName);
        }

        @TestMetadata("simpleByte.bounds")
        public void testSimpleByte() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/simpleByte.bounds");
            doTest(fileName);
        }

        @TestMetadata("simpleInt.bounds")
        public void testSimpleInt() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/simpleInt.bounds");
            doTest(fileName);
        }

        @TestMetadata("simpleShort.bounds")
        public void testSimpleShort() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/integerValueTypes/simpleShort.bounds");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/constraintSystem/severalVariables")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class SeveralVariables extends AbstractConstraintSystemTest {
        public void testAllFilesPresentInSeveralVariables() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/constraintSystem/severalVariables"), Pattern.compile("^(.+)\\.bounds$"), true);
        }

        @TestMetadata("simpleDependency.bounds")
        public void testSimpleDependency() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/severalVariables/simpleDependency.bounds");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/constraintSystem/variance")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Variance extends AbstractConstraintSystemTest {
        public void testAllFilesPresentInVariance() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/constraintSystem/variance"), Pattern.compile("^(.+)\\.bounds$"), true);
        }

        @TestMetadata("consumer.bounds")
        public void testConsumer() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/variance/consumer.bounds");
            doTest(fileName);
        }

        @TestMetadata("invariant.bounds")
        public void testInvariant() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/variance/invariant.bounds");
            doTest(fileName);
        }

        @TestMetadata("producer.bounds")
        public void testProducer() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/constraintSystem/variance/producer.bounds");
            doTest(fileName);
        }
    }
}

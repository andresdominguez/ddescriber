package com.karateca.ddescriber;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Andres Dominguez.
 */
public class JasmineFinderTest extends LightCodeInsightFixtureTestCase {

  private JasmineFinder jasmineFinder;

  @Override
  protected String getTestDataPath() {
    String testPath = PathManager.getJarPathForClass(JasmineFinderTest.class);
    File sourceRoot = new File(testPath, "../../..");
    return new File(sourceRoot, "testData").getPath();
  }

  public void setUp() throws Exception {
    super.setUp();
    PsiFile psiFile = myFixture.configureByFile("jasmineTestBefore.js");
    jasmineFinder = new JasmineFinder(getProject(), new DocumentImpl(psiFile.getText()), psiFile.getVirtualFile());
  }

  public void testFindUnitTestsAndSuites() throws Exception {
    // Given that you have a jasmine test file.
    // When you find all the matches.
    jasmineFinder.findAll();

    // Then ensure the unit tests and the suites were found.
    assertEquals(7, jasmineFinder.testFindResults.size());
  }
}

package com.karateca.ddescriber;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import junit.framework.Assert;

import java.io.File;

/**
 * @author Andres Dominguez.
 */
public class BaseTestCase extends LightCodeInsightFixtureTestCase {

  DocumentImpl document;
  JasmineFinder jasmineFinder;

  @Override
  protected String getTestDataPath() {
    String testPath = PathManager.getJarPathForClass(BaseTestCase.class);
    File sourceRoot = new File(testPath, "../../..");
    return new File(sourceRoot, "testData").getPath();
  }

  public void testDummyTest() {
    // Created this test to get rid of the warning.
    Assert.assertEquals(1, 1);
  }

  void prepareScenarioWithTestFile(String fileName) {
    PsiFile psiFile = myFixture.configureByFile(fileName);
    document = new DocumentImpl(psiFile.getText());
    jasmineFinder = new JasmineFinder(getProject(), document, psiFile.getVirtualFile());
  }

  protected JasmineFinder createJasmineFinder() {
    PsiFile psiFile = myFixture.configureByFile("jasmineTestBefore.js");
    return new JasmineFinder(getProject(), getDocument(), psiFile.getVirtualFile());
  }

  DocumentImpl getDocument() {
    PsiFile psiFile = myFixture.configureByFile("jasmineTestBefore.js");
    return new DocumentImpl(psiFile.getText());
  }
}

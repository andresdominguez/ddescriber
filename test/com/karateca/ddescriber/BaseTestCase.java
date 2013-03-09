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

  protected JasmineFinder createJasmineFinder() {
    PsiFile psiFile = myFixture.configureByFile("jasmineTestBefore.js");
    return new JasmineFinder(getProject(), new DocumentImpl(psiFile.getText()), psiFile.getVirtualFile());
  }
}

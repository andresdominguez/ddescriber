package com.karateca.ddescriber;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import junit.framework.Assert;

import java.io.File;

/**
 * @author Andres Dominguez.
 */
public class BaseTestCase extends LightCodeInsightFixtureTestCase {

  protected Document document;
  protected JasmineFinder jasmineFinder;
  protected VirtualFile virtualFile;
  protected PsiFile psiFile;

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

  protected void prepareScenarioWithTestFile(String fileName) {
    psiFile = myFixture.configureByFile(fileName);
    virtualFile = psiFile.getVirtualFile();
    document = ActionUtil.getDocument(virtualFile);
    jasmineFinder = new JasmineFinder(getProject(), document);
  }
}

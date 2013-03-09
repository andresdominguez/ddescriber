package com.karateca.ddescriber;

import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.psi.PsiFile;

/**
 * @author Andres Dominguez.
 */
public class JasmineFinderTest extends BaseTestCase {

  private JasmineFinder jasmineFinder;

  public void setUp() throws Exception {
    super.setUp();
    PsiFile psiFile = myFixture.configureByFile("jasmineTestBefore.js");
    jasmineFinder = new JasmineFinder(getProject(), new DocumentImpl(psiFile.getText()), psiFile.getVirtualFile());
  }

  public void testFindAll() throws Exception {
    // Given that you have a jasmine test file.
    // When you find all the matches.
    jasmineFinder.findAll();

    // Then ensure the unit tests and the suites were found.
    assertEquals(7, jasmineFinder.testFindResults.size());
  }


}

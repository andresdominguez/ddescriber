package com.karateca.ddescriber.toolWindow;

import com.karateca.ddescriber.BaseTestCase;

/**
 * @author Andres Dominguez.
 */
public class FileIteratorTest extends BaseTestCase {

  private FileIterator fileIterator;

  public void testProcessFile() throws Exception {
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    fileIterator = new FileIterator(getProject(), true);

    // When you process a file.
    fileIterator.processFile(virtualFile);

    // Then ensure a new Jasmine file was created.
    assertEquals(1, fileIterator.getJasmineFiles().size());
  }
}

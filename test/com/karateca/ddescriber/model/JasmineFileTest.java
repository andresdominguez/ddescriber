package com.karateca.ddescriber.model;

import com.karateca.ddescriber.BaseTestCase;

/**
 * @author Andres Dominguez.
 */
public class JasmineFileTest extends BaseTestCase {

  public void testBuildTreeNode() {
    // Given that you create a jasmine file.
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    JasmineFile jasmineFile = new JasmineFile(getProject(), virtualFile);

    // When you build the tree node.
    TreeNode node = jasmineFile.buildTreeNode();

    // Then ensure the tree node contains all the describe() and it() in the files.
    assertNotNull(node.getNodeValue());
  }
}

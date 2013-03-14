package com.karateca.ddescriber.model;

import com.karateca.ddescriber.BaseTestCase;

import java.io.IOException;

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
    assertEquals("top describe", node.getNodeValue().getTestText());
    assertEquals(3, node.getChildCount());
  }

  public void testUpdateNodeOnFileChange() throws IOException {
    // Given that you build a tree not from a file.
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    JasmineFile jasmineFile = new JasmineFile(getProject(), virtualFile);
    TreeNode treeNode = jasmineFile.buildTreeNode();

    // When the file changes.
    virtualFile.setBinaryContent(("describe('file changed', function () {\n" +
        "    it('should have changed', function () {\n" +
        "        \n" +
        "    });" +
        "\n});" +
        "\n").getBytes());

    // Then ensure the tree node was modified.
    assertEquals("file changed", treeNode.getNodeValue().getTestText());
    assertEquals(1, treeNode.getChildCount());
  }
}

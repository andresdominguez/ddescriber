package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.karateca.ddescriber.BaseTestCase;
import com.karateca.ddescriber.model.TreeNode;
import com.karateca.ddescriber.toolWindow.JasmineFile;

import java.io.IOException;

/**
 * @author Andres Dominguez.
 */
public class JasmineFileTest extends BaseTestCase {

  private TreeNode givenThatYouBuildTreeNodeFromJasmineFile() {
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    JasmineFile jasmineFile = new JasmineFile(getProject(), virtualFile);
    return jasmineFile.buildTreeNode();
  }

  public void testBuildTreeNode() {
    TreeNode node = givenThatYouBuildTreeNodeFromJasmineFile();

    // Then ensure the tree node contains all the describe() and it() in the files.
    assertEquals("top describe", node.getNodeValue().getTestText());
    assertEquals(3, node.getChildCount());
  }

  public void testUpdateNodeOnFileChange() throws IOException {
    TreeNode treeNode = givenThatYouBuildTreeNodeFromJasmineFile();

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

  public void testRemoveNodeWhenFileIsDeleted() {
    TreeNode treeNode = givenThatYouBuildTreeNodeFromJasmineFile();

    // And give that you add the node to a parent.
    final TreeNode parent = new TreeNode("parent");
    parent.add(new TreeNode("First child"));
    parent.add(treeNode);


    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        // When you delete the file.
        psiFile.delete();

        // Then ensure the node was removed from the parent.
        assertEquals(1, parent.getChildCount());
      }
    });
  }
}

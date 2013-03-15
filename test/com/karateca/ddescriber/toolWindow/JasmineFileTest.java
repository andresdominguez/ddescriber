package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.karateca.ddescriber.BaseTestCase;
import com.karateca.ddescriber.model.TreeNode;

import java.io.IOException;

/**
 * @author Andres Dominguez.
 */
public class JasmineFileTest extends BaseTestCase {

  private JasmineFile jasmineFile;

  private TreeNode buildRootNodeFromFile(String fileName) {
    prepareScenarioWithTestFile(fileName);
    jasmineFile = new JasmineFile(getProject(), virtualFile);
    return jasmineFile.buildTreeNode();
  }

  public void testBuildTreeNode() {
    TreeNode node = buildRootNodeFromFile("jasmineTestCaretTop.js");

    // Ensure the tree node contains all the describe() and it() in the file.
    assertEquals("top describe", node.getNodeValue().getTestText());
    assertEquals(3, node.getChildCount());

    TreeNode test1 = (TreeNode) node.getChildAt(0);
    TreeNode suite2 = (TreeNode) node.getChildAt(1);
    TreeNode suite3 = (TreeNode) node.getChildAt(2);
    TreeNode suite6 = (TreeNode) node.getChildAt(3);

    assertEquals("test1", test1.getNodeValue().getTestText());
    assertEquals("suite2", suite2.getNodeValue().getTestText());
    assertEquals("suite3", suite3.getNodeValue().getTestText());
    assertEquals("suite6", suite6.getNodeValue().getTestText());

    // suite2
    assertEquals(2, suite2.getChildCount());

    // suite3
    assertEquals(2, suite3.getChildCount());
    TreeNode test4 = (TreeNode) suite3.getChildAt(0);
    TreeNode suite4 = (TreeNode) suite3.getChildAt(1);
    assertEquals("test4", test4.getNodeValue().getTestText());
    assertEquals("suite4", suite4.getNodeValue().getTestText());

    // suite4
    assertEquals(2, suite4.getChildCount());

    // suite6
    assertEquals(1, suite6);
  }

  public void testBuildTreeDeepStructure() {
    // Test with file with many levels.
    TreeNode root = buildRootNodeFromFile("testWihManyLevels.js");

    // Ensure the hierarchy is correct.
    assertEquals("suite1", root.getNodeValue().getTestText());
    assertEquals(4, root.getChildCount());
  }

  public void testUpdateNodeOnFileChange() throws IOException {
    TreeNode treeNode = buildRootNodeFromFile("jasmineTestCaretTop.js");

    // When the file changes.
    virtualFile.setBinaryContent(("describe('file changed', function () {\n" +
        "    it('should have changed', function () {\n" +
        "        \n" +
        "    });" +
        "\n});" +
        "\n").getBytes());
    jasmineFile.updateTreeNode();


    // Then ensure the tree node was modified.
    assertEquals("file changed", treeNode.getNodeValue().getTestText());
    assertEquals(1, treeNode.getChildCount());
  }

  public void testRemoveNodeWhenFileIsDeleted() {
    TreeNode treeNode = buildRootNodeFromFile("jasmineTestCaretTop.js");

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

  public void testTreeCopy() throws Exception {
    buildRootNodeFromFile("jasmineTestCaretTop.js");

    // Given that you have a tree to copy and a destination.
    TreeNode destination = new TreeNode("destRoot");
    TreeNode source = new TreeNode("source");
    TreeNode parent1 = new TreeNode("parent 1");
    parent1.add(new TreeNode("foo"));
    parent1.add(new TreeNode("bar"));
    source.add(parent1);
    source.add(new TreeNode("Level 1, ch1"));

    // When you do the deep copy.
    jasmineFile.copyTree(source, destination);

    // Then ensure the destination has the same structure.
    assertEquals("source", destination.getUserObject());
    assertEquals(2, destination.getChildCount());
    TreeNode firstChild = (TreeNode) destination.getFirstChild();

    // Test first level.
    assertEquals("parent 1", firstChild.getUserObject());
    assertEquals("Level 1, ch1", ((TreeNode)destination.getLastChild()).getUserObject());

    // Test second level.
    assertEquals(2, firstChild.getChildCount());
    assertEquals("foo", ((TreeNode)firstChild.getFirstChild()).getUserObject());
    assertEquals("bar", ((TreeNode)firstChild.getLastChild()).getUserObject());
  }
}

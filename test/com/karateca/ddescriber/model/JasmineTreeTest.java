package com.karateca.ddescriber.model;

import com.karateca.ddescriber.BaseTestCase;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class JasmineTreeTest extends BaseTestCase {

  private JasmineTree jasmineTree;

  public void setUp() throws Exception {
    super.setUp();
    jasmineTree = new JasmineTree();
  }

  private JasmineFile getJasmineFile() {
    prepareScenarioWithTestFile("jasmineTestBefore.js");
    JasmineFileImpl jasmineFile = new JasmineFileImpl(getProject(), virtualFile);
    jasmineFile.buildTreeNodeSync();
    return jasmineFile;
  }

  private void expectRootNodeContainsDescribeWithName(String expectedName) {
    TreeNode rootNode = jasmineTree.getRootNode();
    TreeNode firstChild = (TreeNode) rootNode.getFirstChild();

    assertEquals(1, rootNode.getChildCount());
    assertEquals(expectedName, firstChild.getNodeValue().getTestText());
  }

  public void testShouldDeclareEmptyRoot() {
    assertEquals("Root node", jasmineTree.getRootNode().getUserObject());
  }

  public void testShouldHideRootNodeAfterAddingFiles() {
    // When you add files.
    jasmineTree.addFiles(new ArrayList<JasmineFile>());

    // Then ensure the root is not visible.
    assertFalse(jasmineTree.isRootVisible());
  }

  public void testShouldAddTestsToEmptyTree() {
    // Given a test file.
    JasmineFile jasmineFile = getJasmineFile();

    // When you add the jasmine file.
    jasmineTree.addFiles(Arrays.asList(jasmineFile));

    // Then ensure the tree has the new nodes.
    TreeNode rootNode = jasmineTree.getRootNode();
    assertEquals(1, rootNode.getChildCount());

    // Ensure the describe node was added.
    TreeNode describeNode = (TreeNode) rootNode.getFirstChild();
    assertEquals("top describe", describeNode.getNodeValue().getTestText());

    // Ensure the describe has children.
    assertEquals(3, describeNode.getChildCount());
  }

  public void testShouldAddJasmineFileWhenItHasResultsMarkedToRun() {
    // Given a describe with tests marked to run.
    JasmineFile jasmineFile = getJasmineFile();

    // When you update the jasmine file.
    jasmineTree.updateFile(jasmineFile);

    // Then ensure the file was added.
    expectRootNodeContainsDescribeWithName("top describe");
  }

  public void testShouldRemoveExistingTestFileWhenThereAreNoTestsMarked() {
    JasmineFile jasmineFile = getJasmineFile();

    // Given that you are showing a jasmine file.
    jasmineTree.updateFile(jasmineFile);
    assertEquals(1, jasmineTree.getRootNode().getChildCount());

    // When you clean the file and update.
    jasmineFile.cleanFile();
    jasmineFile.buildTreeNodeSync();
    jasmineTree.updateFile(jasmineFile);

    // Then ensure the node was removed.
    assertEquals(0, jasmineTree.getRootNode().getChildCount());
  }

  public void testShouldUpdateExistingTest() {
    JasmineFile jasmineFile = getJasmineFile();

    // Given that you are showing a jasmine file.
    jasmineTree.updateFile(jasmineFile);
    assertEquals(1, jasmineTree.getRootNode().getChildCount());

    // When you update the file.
    JasmineFile updatedFile = new JasmineFileImpl(getProject(), virtualFile);
    updatedFile.buildTreeNodeSync();
    jasmineTree.updateFile(updatedFile);

    // Then ensure the node was updated.
    expectRootNodeContainsDescribeWithName("top describe");
  }

  public void testShouldClearTree() {
    // Given a tree with a test.
    jasmineTree.addFiles(Arrays.asList(getJasmineFile()));

    // When you clear the tree.
    jasmineTree.clear();

    // Then ensure there are no nodes left.
    assertEquals(0, jasmineTree.getRootNode().getChildCount());
  }
}

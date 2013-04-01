package com.karateca.ddescriber.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class JasmineTreeTest {

  private JasmineTree jasmineTree;

  @Before
  public void setUp() throws Exception {
    jasmineTree = new JasmineTree();
  }

  @Test
  public void shouldDeclareEmptyRoot() {
    assertEquals("Root node", jasmineTree.getRootNode().getUserObject());
    assertFalse(jasmineTree.isRootVisible());
  }

  @Test
  public void shouldAddTestsToEmptyTree() {
    // Given a describe with two its.
    JasmineFile jasmineFile = createJasmineFile(false);

    // When you add the jasmine file.
    jasmineTree.addFile(jasmineFile);

    // Then ensure the tree has the new nodes.
    TreeNode rootNode = jasmineTree.getRootNode();
    assertEquals(1, rootNode.getChildCount());

    // Ensure the describe node was added.
    TreeNode describeNode = (TreeNode) rootNode.getFirstChild();
    assertEquals("d1", describeNode.getNodeValue().getTestText());

    // Ensure the describe has two children.
    assertEquals(2, describeNode.getChildCount());
  }

  private JasmineFile createJasmineFile(boolean hasTestsMarkedToRun) {
    TreeNode describe = createDescribe();
    JasmineFile jasmineFile = mock(JasmineFile.class);
    when(jasmineFile.hasTestsMarkedToRun()).thenReturn(hasTestsMarkedToRun);
    when(jasmineFile.getTreeNode()).thenReturn(describe);
    return jasmineFile;
  }

  private TreeNode createDescribe() {
    TestFindResult descFindResult = MockFindResult.buildDescribe("d1");

    TreeNode describeNode = new TreeNode(descFindResult);
    describeNode.add(buildIt("it1"));
    describeNode.add(buildIt("it2"));

    return describeNode;
  }

  @Test
  public void shouldAddJasmineFileWhenItHasResultsMarkedToRun() {
    // Given a describe with tests marked to run.
    JasmineFile jasmineFile = createJasmineFile(true);

    // When you update the jasmine file.
    jasmineTree.updateFile(jasmineFile);

    // Then ensure the file was added.
    TreeNode rootNode = jasmineTree.getRootNode();
    TreeNode firstChild = (TreeNode) rootNode.getFirstChild();

    assertEquals(1, rootNode.getChildCount());
    assertEquals("d1", firstChild.getNodeValue().getTestText());
  }

  @Test
  public void shouldNotAddJasmineFileWhenItHasNoTestsMarkedToRun() {
    // Given a test file without marked tests.
    JasmineFile jasmineFile = createJasmineFile(false);

    // When you update the file.
    jasmineTree.updateFile(jasmineFile);

    // Then ensure the file was not added.
    assertEquals(0, jasmineTree.getRootNode().getChildCount());
  }

  private TreeNode buildIt(String testText) {
    return new TreeNode(MockFindResult.buildIt(testText));
  }
}

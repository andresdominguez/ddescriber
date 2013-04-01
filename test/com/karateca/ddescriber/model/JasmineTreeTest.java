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
    TreeNode describe = buildDescribe("d1");
    describe.add(buildIt("it1"));
    describe.add(buildIt("it2"));

    JasmineFile jasmineFile = mock(JasmineFile.class);
    when(jasmineFile.getTreeNode()).thenReturn(describe);

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

  private TreeNode buildDescribe(String testText) {
    return new TreeNode(createFindResult(true, testText));
  }

  private TreeNode buildIt(String testText) {
    return new TreeNode(createFindResult(false, testText));
  }

  private TestFindResult createFindResult(boolean isDescribe, String testText) {
    TestFindResult findResult = mock(TestFindResult.class);

    when(findResult.isDescribe()).thenReturn(isDescribe);
    when(findResult.getTestText()).thenReturn(testText);

    return findResult;
  }
}

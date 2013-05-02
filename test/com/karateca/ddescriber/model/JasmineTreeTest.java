package com.karateca.ddescriber.model;

import com.intellij.psi.PsiFile;
import com.karateca.ddescriber.BaseTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class JasmineTreeTest extends BaseTestCase {

  private JasmineTree tree;
  private TreeNode rootNode;

  public void setUp() throws Exception {
    super.setUp();
    tree = new JasmineTree();
    rootNode = tree.getRootNode();
  }

  private JasmineFile getJasmineFile() {
    return getJasmineFiles("jasmineTestBefore.js").get(0);
  }

  private List<JasmineFile> getJasmineFiles(String... fileNames) {
    for (String fileName : fileNames) {
      myFixture.copyFileToProject(fileName);
    }

    List<JasmineFile> jasmineFiles = new ArrayList<JasmineFile>();

    for (PsiFile file : myFixture.configureByFiles(fileNames)) {
      JasmineFile jasmineFile = new JasmineFile(getProject(), file.getVirtualFile());
      jasmineFile.buildTreeNodeSync();

      jasmineFiles.add(jasmineFile);
    }

    return jasmineFiles;
  }

  public void testShouldDeclareEmptyRoot() {
    assertEquals("Root node", rootNode.getUserObject());
  }

  public void testShouldHideRootNodeAfterAddingFiles() {
    // When you add files.
    tree.addFiles(new ArrayList<JasmineFile>());

    // Then ensure the root is not visible.
    assertFalse(tree.isRootVisible());
  }

  public void testShouldAddTestsToEmptyTree() {
    // Given a test file.
    JasmineFile jasmineFile = getJasmineFile();

    // When you add the jasmine file.
    tree.addFiles(Arrays.asList(jasmineFile));

    // Then ensure the tree has the new nodes.
    assertEquals(1, rootNode.getChildCount());

    // Ensure the describe node was added.
    TreeNode describeNode = (TreeNode) rootNode.getFirstChild();
    assertEquals("top describe", describeNode.getNodeValue().getTestText());

    // Ensure the describe has children.
    assertEquals(3, describeNode.getChildCount());
  }

  public void testShouldClearTree() {
    // Given a tree with a test.
    tree.addFiles(Arrays.asList(getJasmineFile()));

    // When you clear the tree.
    tree.clear();

    // Then ensure there are no nodes left.
    assertEquals(0, rootNode.getChildCount());
  }

//  public void testShouldShowSelectedNodesOnly() {
//    // Given that you are showing files.
//    List<JasmineFile> files = getJasmineFiles("jasmineTestCaretTop.js", "jasmineTestBefore.js");
//    tree.addFiles(files);
//    assertEquals(2, rootNode.getChildCount());
//
//    // When you show the marked only.
//    tree.showSelectedNodesOnly();
//
//    // Then ensure only the marked tests are shown.
//    assertEquals(4, rootNode.getChildCount());
//  }
}

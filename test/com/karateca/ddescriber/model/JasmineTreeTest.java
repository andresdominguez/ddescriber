package com.karateca.ddescriber.model;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.BaseTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class JasmineTreeTest extends BaseTestCase {

  private JasmineTree tree;

  public void setUp() throws Exception {
    super.setUp();
    tree = new JasmineTree();
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
      JasmineFileImpl jasmineFile = new JasmineFileImpl(getProject(), file.getVirtualFile());
      jasmineFile.buildTreeNodeSync();

      jasmineFiles.add(jasmineFile);
    }

    return jasmineFiles;
  }

  private void expectRootNodeContainsDescribeWithName(String expectedName) {
    TreeNode rootNode = tree.getRootNode();
    TreeNode firstChild = (TreeNode) rootNode.getFirstChild();

    assertEquals(1, rootNode.getChildCount());
    assertEquals(expectedName, firstChild.getNodeValue().getTestText());
  }

  public void testShouldDeclareEmptyRoot() {
    assertEquals("Root node", tree.getRootNode().getUserObject());
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
    TreeNode rootNode = tree.getRootNode();
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
    tree.updateFile(jasmineFile);

    // Then ensure the file was added.
    expectRootNodeContainsDescribeWithName("top describe");
  }

  public void testShouldRemoveExistingTestFileWhenThereAreNoTestsMarked() {
    JasmineFile jasmineFile = getJasmineFile();

    // Given that you are showing a jasmine file.
    tree.updateFile(jasmineFile);
    assertEquals(1, tree.getRootNode().getChildCount());

    // When you clean the file and update.
    jasmineFile.cleanFile();
    jasmineFile.buildTreeNodeSync();
    tree.updateFile(jasmineFile);

    // Then ensure the node was removed.
    assertEquals(0, tree.getRootNode().getChildCount());
  }

  public void testShouldUpdateExistingTest() {
    JasmineFile jasmineFile = getJasmineFile();

    // Given that you are showing a jasmine file.
    tree.updateFile(jasmineFile);
    assertEquals(1, tree.getRootNode().getChildCount());

    // When you update the file.
    final Document doc = ActionUtil.getDocument(jasmineFile.getVirtualFile());
    ActionUtil.runWriteActionInsideCommand(getProject(), new Runnable() {
      @Override
      public void run() {
        doc.setText("ddescribe('changed', function(){})");
      }
    });
    jasmineFile.buildTreeNodeSync();
    tree.updateFile(jasmineFile);

    // Then ensure the node was updated.
    expectRootNodeContainsDescribeWithName("changed");
  }

  public void testShouldClearTree() {
    // Given a tree with a test.
    tree.addFiles(Arrays.asList(getJasmineFile()));

    // When you clear the tree.
    tree.clear();

    // Then ensure there are no nodes left.
    assertEquals(0, tree.getRootNode().getChildCount());
  }

  public void testShouldRefreshTree() {
    List<JasmineFile> files = getJasmineFiles("testWihManyLevels.js", "jasmineTestBefore.js");

    // Given that you are showing a file.
    tree.addFiles(Arrays.asList(files.get(0)));

    // When you update the tree.
    tree.updateFiles(files);

    // Then ensure the tree got updated.
    assertEquals(2, tree.getRootNode().getChildCount());
  }

  public void testShouldShowMarkedOnly() {
    TreeNode rootNode = tree.getRootNode();

    // Given that you are showing files.
    List<JasmineFile> files = getJasmineFiles("jasmineTestCaretTop.js", "jasmineTestBefore.js");
    tree.addFiles(files);
    assertEquals(2, rootNode.getChildCount());

    // When you show the marked only.
    tree.showMarkedOnly(true);

    // Then ensure only the marked tests are shown.
    assertEquals(4, rootNode.getChildCount());
  }
}

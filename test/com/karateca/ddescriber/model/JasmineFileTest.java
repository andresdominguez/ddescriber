package com.karateca.ddescriber.model;

import com.karateca.ddescriber.BaseTestCase;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

public class JasmineFileTest extends BaseTestCase {

  private JasmineFile jasmineFile;

  private TreeNode buildRootNodeFromFile(String fileName) {
    prepareScenarioWithTestFile(fileName);
    jasmineFile = new JasmineFile(getProject(), virtualFile);
    return jasmineFile.buildTreeNodeSync();
  }

  public void testBuildTreeNode() {
    TreeNode node = buildRootNodeFromFile("testWihManyLevels.js");

    // Ensure the root is the top node.
    assertTrue(node.isTopNode());

    // Ensure the tree node contains all the describe() and it() in the file.
    assertEquals("suite1", node.getNodeValue().getTestText());
    assertEquals(4, node.getChildCount());

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
    assertEquals(1, suite6.getChildCount());
  }

  public void testShouldReadFileWithTwoDescribes() {
    TreeNode root = buildRootNodeFromFile("doubleDescribe.js");

    // Ensure the virtual file is set in the root.
    assertEquals(virtualFile, root.getVirtualFile());

    // Ensure the root is marked as top.
    assertTrue(root.isTopNode());

    // Ensure it has two children.
    assertEquals(2, root.getChildCount());
  }

  public void testGetClosestTest() {
    buildRootNodeFromFile("testWihManyLevels.js");

    assertEquals("suite1", jasmineFile.getClosestTestFromCaret(0).getTestText());
  }

  public void testElementsMarkedForRun() {
    elementsMarkedForRun("jasmine1/jasmineTestCaretTop.js");
    elementsMarkedForRun("jasmine2/jasmineTestCaretTop.js");
  }

  private void elementsMarkedForRun(String fileName) {
    buildRootNodeFromFile(fileName);

    // Ensure there are tests marked to run.
    assertTrue(jasmineFile.hasTestsMarkedToRun());

    // Ensure the marked tests can be found.
    List<TestFindResult> list = jasmineFile.getElementsMarkedToRun();
    assertEquals(2, list.size());
    assertEquals("inner describe", list.get(0).getTestText());
    assertEquals("inner it 3", list.get(1).getTestText());
  }

  public void testBuildTreeDeepStructure() {
    // Test with file with many levels.
    TreeNode root = buildRootNodeFromFile("testWihManyLevels.js");

    // Ensure the hierarchy is correct.
    assertEquals("suite1", root.getNodeValue().getTestText());
    assertEquals(4, root.getChildCount());
  }

  public void testTreeCopy() throws Exception {
    copiesTree("jasmine1/jasmineTestCaretTop.js");
    copiesTree("jasmine2/jasmineTestCaretTop.js");
  }

  private void copiesTree(String fileName) {
    buildRootNodeFromFile(fileName);

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
    assertEquals("Level 1, ch1", ((TreeNode) destination.getLastChild()).getUserObject());

    // Test second level.
    assertEquals(2, firstChild.getChildCount());
    assertEquals("foo", ((TreeNode) firstChild.getFirstChild()).getUserObject());
    assertEquals("bar", ((TreeNode) firstChild.getLastChild()).getUserObject());
  }

  public void testSearchResultsListener() {
    // Given a jasmine file.
    prepareScenarioWithTestFile("jasmine1/jasmineTestCaretTop.js");
    jasmineFile = new JasmineFile(getProject(), virtualFile);
    final boolean[] buildDone = new boolean[1];

    // And given that you register for changes.
    jasmineFile.addResultsReadyListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        buildDone[0] = true;
      }
    });

    // When you process the files.
    jasmineFile.buildTreeNodeAsync();

    // Then ensure the change event was broadcasted.
    assertTrue(buildDone[0]);
    assertEquals("top describe", jasmineFile.getTreeNode().getNodeValue().getTestText());
  }

  public void testCleanFile() {
    // Given a jasmine file with a ddescribe() and an iit().
    shouldCleanFile("jasmine1/jasmineTestBefore.js");

    // Given a jasmine file with a fdescribe() and an fit().
    shouldCleanFile("jasmine2/jasmineTestBefore.js");
  }

  private void shouldCleanFile(String fileName) {
    // Given a jasmine file with a [fd]describe() and an [fi]it().
    prepareScenarioWithTestFile(fileName);
    jasmineFile = new JasmineFile(getProject(), virtualFile);
    jasmineFile.buildTreeNodeSync();

    // When you clean the file.
    jasmineFile.cleanFile();

    // Then ensure the dd -> d and the iit > it.
    myFixture.checkResultByFile("jasmineTestAfter.js");
  }

  public void testShouldCountTests() {
    shouldCountTests("jasmine1/jasmineTestBefore.js");
    shouldCountTests("jasmine2/jasmineTestBefore.js");
  }

  private void shouldCountTests(String fileName) {
    // Given a jasmine file with included and excluded tests.
    prepareScenarioWithTestFile(fileName);
    jasmineFile = new JasmineFile(getProject(), virtualFile);
    jasmineFile.buildTreeNodeSync();

    // When you count the tests.
    TestCounts testCounts = jasmineFile.getTestCounts();

    // Then ensure there are counts for all the items.
    assertEquals(2, testCounts.getIncludedCount());
    assertEquals(2, testCounts.getExcludedCount());
    assertEquals(6, testCounts.getTestCount());
  }
}

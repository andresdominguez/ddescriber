package com.karateca.ddescriber.toolWindow;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.JasmineDescriberNotifier;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TreeNode;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineToolWindowTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/";
  }

  private PsiFile[] givenThatYouHaveTestFiles() {
    return configureFiles("doubleDescribe.js", "jasmineTestBefore.js");
  }

  private PsiFile[] configureFiles(String... files) {
    for (String file : files) {
      myFixture.copyFileToProject(file);
    }
    return myFixture.configureByFiles(files);
  }

  public void testShouldFindTestFilesMarkedToRun() {
    givenThatYouHaveTestFiles();

    // Override the method that shows the panel.
    final List<JasmineFile> testsFound = new ArrayList<JasmineFile>();
    JasmineToolWindow jasmineToolWindow = new JasmineToolWindow() {
      @Override
      protected void showTestsInToolWindow(List<JasmineFile> jasmineFiles) {
        for (JasmineFile jasmineFile : jasmineFiles) {
          testsFound.add(jasmineFile);
        }
      }
    };

    // When you create the tool window content.
    jasmineToolWindow.createToolWindowContent(getProject(), new FakeToolWindow());

    // Then ensure one file was found.
    assertEquals(1, testsFound.size());
    assertEquals("jasmineTestBefore.js", testsFound.get(0).getVirtualFile().getName());
  }

  public void testShouldAddContent() {
    givenThatYouHaveTestFiles();

    JasmineToolWindow jasmineToolWindow = whenYouCreateTheToolWindowContent();

    // Then ensure the tree was created.
    TreeNode root = (TreeNode) jasmineToolWindow.tree.getModel().getRoot();

    assertEquals("All tests", root.getUserObject());
    assertEquals(1, root.getChildCount());
  }

  private JasmineToolWindow whenYouCreateTheToolWindowContent() {
    JasmineToolWindow jasmineToolWindow = new JasmineToolWindow();
    FakeToolWindow fakeToolWindow = new FakeToolWindow();
    jasmineToolWindow.createToolWindowContent(getProject(), fakeToolWindow);

    return jasmineToolWindow;
  }

//  public void testShouldAddNewTestNode() {
//    PsiFile[] psiFiles = givenThatYouHaveTestFiles();
//
//    whenYouCreateTheToolWindowContent();
//
//    JasmineFile jasmineFile = new JasmineFile(getProject(), psiFiles[0].getVirtualFile());
//    jasmineFile.buildTreeNodeSync();
//    TreeNode firstChild = (TreeNode) jasmineFile.getTreeNode().getFirstChild();
//    TestFindResult findResult = firstChild.getNodeValue();
//    ArrayList<TestFindResult> list = new ArrayList<TestFindResult>();
//    list.add(findResult);
//    ActionUtil.changeSelectedLineRunningCommand(getProject(), ActionUtil.getDocument(firstChild.getVirtualFile()), list);
//
//    JasmineDescriberNotifier.getInstance().testWasChanged(jasmineFile);
//  }
}

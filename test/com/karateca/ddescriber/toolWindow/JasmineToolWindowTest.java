package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.karateca.ddescriber.model.JasmineFile;

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

  @Override
  public void setUp() throws Exception {
    super.setUp();

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

  private void givenThatYouHaveTestFiles() {
    myFixture.copyFileToProject("doubleDescribe.js");
    myFixture.copyFileToProject("jasmineTestBefore.js");
    myFixture.configureByFiles("doubleDescribe.js", "jasmineTestBefore.js");
  }

  public void testShouldAddContent() {
    givenThatYouHaveTestFiles();

    // When you create the toll window content.
    JasmineToolWindow jasmineToolWindow = new JasmineToolWindow();
    FakeToolWindow fakeToolWindow = new FakeToolWindow();
    jasmineToolWindow.createToolWindowContent(getProject(), fakeToolWindow);

    // Then ensure the panel was created.
    assertNotNull(fakeToolWindow.getContent());
  }
}

package com.karateca.ddescriber.toolWindow;

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

  public void testShouldFindTestFilesMarkedToRun() {
    // Given that you have three files in the project.
    myFixture.copyFileToProject("doubleDescribe.js");
    myFixture.copyFileToProject("jasmineTestBefore.js");
    myFixture.configureByFiles("doubleDescribe.js", "jasmineTestBefore.js");

    // Override the method that shows the panel.
    final List<JasmineFile> testsFound = new ArrayList<JasmineFile>();
    JasmineToolWindow toolWindow = new JasmineToolWindow() {
      @Override
      protected void showTestsInToolWindow(List<JasmineFile> jasmineFiles) {
        for (JasmineFile jasmineFile : jasmineFiles) {
          testsFound.add(jasmineFile);
        }
      }
    };

    // When you create the tool window content.
    toolWindow.createToolWindowContent(getProject(), null);

    // Then ensure one file was found.
    assertEquals(1, testsFound.size());
    assertEquals("jasmineTestBefore.js", testsFound.get(0).getVirtualFile().getName());
  }
}

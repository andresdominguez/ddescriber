package com.karateca.ddescriber;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.model.TestFindResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class ActionUtil {
  /**
   * Run a write operation within a command.
   *
   * @param project The current project.
   * @param action  The action to run.
   */
  public static void runWriteActionInsideCommand(Project project, final Runnable action) {
    CommandProcessor.getInstance().executeCommand(project, new Runnable() {
      @Override
      public void run() {
        ApplicationManager.getApplication().runWriteAction(action);
      }
    }, "Add / Remove tests", null);
  }

  /**
   * Run a read operation.
   *
   * @param action The action to run.
   */
  public static void runReadAction(Runnable action) {
    ApplicationManager.getApplication().runReadAction(action);
  }

  public static Document getDocument(VirtualFile virtualFile) {
    FileDocumentManager instance = FileDocumentManager.getInstance();
    return instance.getDocument(virtualFile);
  }

  /**
   * Change the contents of the selected line. Wrap the call into command and
   * write actions to support undo.
   *
   * @param project         The current project.
   * @param document        The document to modify.
   * @param testFindResults The lines that have to change.
   */
  public static void changeSelectedTests(Project project, final Document document, final List<TestFindResult> testFindResults) {
    changeSelectedLineRunningCommand(project, document, testFindResults, false);
  }

  /**
   * Exclude the selected tests by adding an x.
   *
   * @param project         The current project.
   * @param document        The document to modify.
   * @param testFindResults The lines that have to change.
   */
  public static void excludeTests(Project project, final Document document, final List<TestFindResult> testFindResults) {
    changeSelectedLineRunningCommand(project, document, testFindResults, true);
  }

  /**
   * Change the contents of the selected line. Wrap the call into command and
   * write actions to support undo.
   *
   * @param project         The current project.
   * @param document        The document to modify.
   * @param testFindResults The lines that have to change.
   * @param exclude         True if you want to exclude the tests.
   */
  private static void changeSelectedLineRunningCommand(Project project, final Document document, final List<TestFindResult> testFindResults, final boolean exclude) {
    // Replace the elements from the bottom up.
    Collections.sort(testFindResults, new Comparator<TestFindResult>() {
      @Override
      public int compare(TestFindResult left, TestFindResult right) {
        return right.getEndOffset() - left.getStartOffset();
      }
    });

    runWriteActionInsideCommand(project, new Runnable() {
      @Override
      public void run() {
        for (TestFindResult testFindResult : testFindResults) {
          changeSelectedLine(document, testFindResult, exclude);
        }
      }
    });
  }

  /**
   * Perform the replace for the selected line. It will add or remove a
   * "d" from describe() and an "i" form it().
   *
   * @param document The document you want to change.
   * @param test     The line that has to change.
   * @param exclude  True if you want to exclude the test.
   */
  private static void changeSelectedLine(Document document, TestFindResult test, boolean exclude) {
    String newText = getReplaceStringValue(test, exclude);

    int start = test.getStartOffset();
    int end = test.getEndOffset();

    document.replaceString(start, end, newText);
  }

  private static String getReplaceStringValue(TestFindResult test, boolean exclude) {
    if (exclude) {
      return test.isDescribe() ? "xdescribe(" : "xit(";
    }

    switch (test.getTestState()) {
      case Excluded:
      case Included:
        return test.isDescribe() ? "describe(" : "it(";
      default:
        return test.isDescribe() ? "ddescribe(" : "iit(";
    }
  }
}

package com.karateca.ddescriber;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.model.TreeNode;

import java.lang.Runnable;
import java.util.List;
import java.util.Stack;

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

  public static TreeNode populateTree(List<TestFindResult> elements) {
    TestFindResult first = elements.get(0);
    TreeNode root = new TreeNode(first);

    if (elements.size() < 2) {
      return root;
    }

    Stack<TreeNode> stack = new Stack<TreeNode>();
    int currentIndentation = first.getIndentation();

    TreeNode parent = root;
    TreeNode last = root;

    for (TestFindResult element : elements.subList(1, elements.size())) {
      int ind = element.getIndentation();

      TreeNode newNode = new TreeNode(element);

      if (ind > currentIndentation) {
        stack.push(parent);
        parent = last;
      } else if (ind < currentIndentation) {
        parent = stack.pop();
      }
      last = newNode;
      parent.add(last);

      currentIndentation = ind;
    }

    return root;
  }

  public static Document getDocument(VirtualFile virtualFile) {
    FileDocumentManager instance = FileDocumentManager.getInstance();
    return instance.getDocument(virtualFile);
  }
}

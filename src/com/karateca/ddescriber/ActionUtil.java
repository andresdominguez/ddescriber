package com.karateca.ddescriber;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;

import javax.swing.tree.DefaultMutableTreeNode;
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

  public static DefaultMutableTreeNode populateTree(List<TestFindResult> elements) {
    TestFindResult first = elements.get(0);
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(first);

    if (elements.size() < 2) {
      return root;
    }

    Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
    int currentIndentation = first.getIndentation();

    DefaultMutableTreeNode parent = root;
    DefaultMutableTreeNode last = root;

    for (TestFindResult element : elements.subList(1, elements.size())) {
      int ind = element.getIndentation();

      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(element);

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
}

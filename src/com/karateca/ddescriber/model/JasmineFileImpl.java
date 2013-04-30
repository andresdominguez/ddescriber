package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EventDispatcher;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.JasmineFinder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

/**
 * @author Andres Dominguez.
 */
public class JasmineFileImpl implements JasmineFile {
  private final Project project;
  private final VirtualFile virtualFile;
  private TreeNode treeNode;

  private final EventDispatcher<ChangeListener> myEventDispatcher = EventDispatcher.create(ChangeListener.class);
  private Hierarchy hierarchy;

  public JasmineFileImpl(Project project, VirtualFile virtualFile) {
    this.project = project;
    this.virtualFile = virtualFile;
  }

  public void buildTreeNodeAsync() {
    ActionUtil.runReadAction(new Runnable() {
      @Override
      public void run() {
        treeNode = createRootNode();
        myEventDispatcher.getMulticaster().stateChanged(new ChangeEvent("LinesFound"));
      }
    });
  }

  public void updateTreeNode(final TreeNode destination) {
    ActionUtil.runReadAction(new Runnable() {
      @Override
      public void run() {
        TreeNode newRoot = createRootNode();
        copyTree(newRoot, destination);
      }
    });
  }

  public void copyTree(TreeNode source, TreeNode destination) {
    // Replace contents of node.
    destination.removeAllChildren();
    destination.setUserObject(source.getUserObject());
    destination.setVirtualFile(source.getVirtualFile());

    doDeepCopy(source, destination);
  }

  private void doDeepCopy(TreeNode source, TreeNode destination) {
    Enumeration sourceChildren = source.children();
    while (sourceChildren.hasMoreElements()) {
      TreeNode sourceChildNode = (TreeNode) sourceChildren.nextElement();

      TreeNode destinationNode = new TreeNode(sourceChildNode.getUserObject());
      destinationNode.setVirtualFile(sourceChildNode.getVirtualFile());

      destination.add(destinationNode);

      if (sourceChildNode.getChildCount() > 0) {
        doDeepCopy(sourceChildNode, destinationNode);
      }
    }
  }

  public TreeNode buildTreeNodeSync() {
    treeNode = createRootNode();
    return treeNode;
  }

  private TreeNode createRootNode() {
    Document document = ActionUtil.getDocument(virtualFile);

    JasmineFinder jasmineFinder = new JasmineFinder(project, document);
    jasmineFinder.findAll();
    List<FindResult> findResults = jasmineFinder.getFindResults();

    hierarchy = new Hierarchy(document, findResults);

    return populateTree(hierarchy.getAllUnitTests());
  }

  private TreeNode populateTree(List<TestFindResult> elements) {
    // TODO: what happens when the list is empty?

    // Use a dummy root when you have multiple describes at the top.
    TreeNode root = new TreeNode(virtualFile.getName(), virtualFile);
    root.setTopNode(true);
    TreeNode parent = root;
    TreeNode last = root;

    Stack<TreeNode> stack = new Stack<TreeNode>();
    int currentIndentation = -1;

    for (TestFindResult element : elements) {
      int ind = element.getIndentation();

      TreeNode newNode = new TreeNode(element, virtualFile);

      if (ind > currentIndentation) {
        stack.push(parent);
        parent = last;
      } else if (ind < currentIndentation) {
        do {
          // Find a parent that is not under the current level.
          parent = stack.pop();
        } while (!(parent.getUserObject() instanceof String) && parent.getNodeValue().getIndentation() >= ind);
      }
      last = newNode;
      parent.add(last);

      currentIndentation = ind;
    }

    // If there is only one describe in this file then make it the top node.
    if (root.getChildCount() == 1) {
      TreeNode firstChild = (TreeNode) root.getFirstChild();
      firstChild.setTopNode(true);
      firstChild.removeFromParent();
      return firstChild;
    }

    // There are multiple top-level describes in this file. Return the fake as the parent.
    return root;
  }

  /**
   * Register for change events.
   *
   * @param changeListener The listener to be added.
   */
  public void addResultsReadyListener(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }

  public boolean hasTestsMarkedToRun() {
    return this.hierarchy.getMarkedElements().size() > 0;
  }

  public VirtualFile getVirtualFile() {
    return virtualFile;
  }

  public TreeNode getTreeNode() {
    return treeNode;
  }

  public TestFindResult getClosestTestFromCaret(int caretOffset) {
    return hierarchy.getClosestTestFromCaret(caretOffset);
  }

  public List<TestFindResult> getElementsMarkedToRun() {
    return hierarchy.getMarkedElements();
  }

  /**
   * Clean the file and notify all the changes.
   */
  public void cleanFile() {
    Document document = ActionUtil.getDocument(getVirtualFile());
    ActionUtil.changeSelectedLineRunningCommand(project, document, hierarchy.getMarkedElements());
  }
}

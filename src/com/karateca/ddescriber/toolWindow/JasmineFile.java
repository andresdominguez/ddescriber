package com.karateca.ddescriber.toolWindow;

import com.intellij.find.FindResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EventDispatcher;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.JasmineFinder;
import com.karateca.ddescriber.model.TreeNode;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineFile {
  private final Project project;
  private VirtualFile virtualFile;
  private TreeNode treeNode;

  private final EventDispatcher<ChangeListener> myEventDispatcher = EventDispatcher.create(ChangeListener.class);

  public JasmineFile(Project project, VirtualFile virtualFile) {
    this.project = project;
    this.virtualFile = virtualFile;
  }

  public void updateTreeNode() {
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        TreeNode newRoot = createRootNode();
        copyTree(newRoot, treeNode);
      }
    });
  }

  public void copyTree(TreeNode source, TreeNode destination) {
    // Replace contents of node.
    destination.removeAllChildren();
    destination.setUserObject(source.getUserObject());

    doDeepCopy(source, destination);
  }

  private void doDeepCopy(TreeNode source, TreeNode destination) {
    Enumeration sourceChildren = source.children();
    while (sourceChildren.hasMoreElements()) {
      TreeNode sourceChildNode = (TreeNode) sourceChildren.nextElement();

      TreeNode destinationNode = new TreeNode(sourceChildNode.getUserObject());
      destination.add(destinationNode);

      if (sourceChildNode.getChildCount() > 0) {
        doDeepCopy(sourceChildNode, destinationNode);
      }
    }
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

  @Deprecated
  TreeNode buildTreeNodeSync() {
    treeNode = createRootNode();
    return treeNode;
  }

  private TreeNode createRootNode() {
    Document document = ActionUtil.getDocument(virtualFile);

    JasmineFinder jasmineFinder = new JasmineFinder(project, document);
    jasmineFinder.findAll();
    List<FindResult> findResults = jasmineFinder.getFindResults();

    Hierarchy hierarchy = new Hierarchy(document, findResults);

    return ActionUtil.populateTree(hierarchy.getAllUnitTests());
  }

  /**
   * Register for change events.
   *
   * @param changeListener The listener to be added.
   */
  public void addResultsReadyListener(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }


  public VirtualFile getVirtualFile() {
    return virtualFile;
  }

  public TreeNode getTreeNode() {
    return treeNode;
  }
}

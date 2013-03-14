package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.JasmineFinder;

import javax.swing.tree.MutableTreeNode;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Andres Dominguez.
 *         TODO: remove when file is deleted.
 */
public class JasmineFile {
  private final Project project;
  private final VirtualFile virtualFile;
  private TreeNode treeNode;

  public JasmineFile(Project project, VirtualFile virtualFile) {
    this.project = project;
    this.virtualFile = virtualFile;

    // Listen for changes in the file to update the tree node.
    // TODO: listen for delete.
    VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
      @Override
      public void contentsChanged(VirtualFileEvent event) {
        if (treeNode != null) {
          updateTreeNode();
        }
      }
    });
  }

  private void updateTreeNode() {
    TreeNode newRoot = createRootNode();

    // Replace contents of node.
    treeNode.removeAllChildren();
    treeNode.setUserObject(newRoot.getUserObject());

    // Replace children.
    Enumeration children = newRoot.children();
    while (children.hasMoreElements()) {
      treeNode.add((MutableTreeNode) children.nextElement());
    }
  }

  public TreeNode buildTreeNode() {
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
}

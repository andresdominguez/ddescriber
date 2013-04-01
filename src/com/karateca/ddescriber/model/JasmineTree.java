package com.karateca.ddescriber.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;

import java.util.Enumeration;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class JasmineTree extends Tree {

  private final TreeNode rootNode;

  public JasmineTree() {
    super(new TreeNode("Root node"));
    rootNode = (TreeNode) this.getModel().getRoot();
    expandRow(0);
    setRootVisible(false);
  }

  public void addFile(JasmineFile jasmineFile) {
    rootNode.add(jasmineFile.getTreeNode());
  }

  public TreeNode getRootNode() {
    return rootNode;
  }

  public void updateFile(JasmineFile jasmineFile) {
    TreeNode found = findNodeForJasmineFile(jasmineFile.getVirtualFile());

    if (found != null) {
      // The jasmine file is in the tree already. Update it or remove it.
      updateOrRemove(jasmineFile, found);
    } else if (jasmineFile.hasTestsMarkedToRun()) {
      rootNode.add(jasmineFile.getTreeNode());
    }
  }

  private void updateOrRemove(JasmineFile jasmineFile, TreeNode found) {
    if (jasmineFile.hasTestsMarkedToRun()) {
      jasmineFile.updateTreeNode(found);
    } else {
      rootNode.remove(found);
      found.removeFromParent();
    }
  }

  private TreeNode findNodeForJasmineFile(VirtualFile virtualFile) {
    Enumeration children = rootNode.children();
    while (children.hasMoreElements()) {
      TreeNode treeNode = (TreeNode) children.nextElement();
      if (treeNode.getVirtualFile() == virtualFile) {
        return treeNode;
      }
    }
    return null;
  }
}

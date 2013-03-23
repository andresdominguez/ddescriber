package com.karateca.ddescriber.model;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Andres Dominguez.
 */
public class TreeNode extends DefaultMutableTreeNode {

  private VirtualFile virtualFile;

  public TreeNode(Object object) {
    super(object);
  }

  public TestFindResult getNodeValue() {
    return (TestFindResult) getUserObject();
  }

  public VirtualFile getVirtualFile() {
    return virtualFile;
  }

  public void setVirtualFile(VirtualFile virtualFile) {
    this.virtualFile = virtualFile;
  }
}

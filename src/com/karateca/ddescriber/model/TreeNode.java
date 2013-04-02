package com.karateca.ddescriber.model;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Andres Dominguez.
 */
public class TreeNode extends DefaultMutableTreeNode {

  private VirtualFile virtualFile;
  private boolean topNode;
  private final boolean testNode;

  public TreeNode(Object userObject) {
    super(userObject);
    testNode = userObject instanceof TestFindResult;
  }

  public TreeNode(Object userObject, VirtualFile virtualFile) {
    this(userObject);
    this.virtualFile = virtualFile;
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

  public boolean isTopNode() {
    return topNode;
  }

  public void setTopNode(boolean topNode) {
    this.topNode = topNode;
  }

  public boolean isTestNode() {
    return testNode;
  }
}

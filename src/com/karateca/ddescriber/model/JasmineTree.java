package com.karateca.ddescriber.model;

import com.intellij.ui.treeStructure.Tree;

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
}

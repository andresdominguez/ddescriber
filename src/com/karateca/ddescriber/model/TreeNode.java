package com.karateca.ddescriber.model;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Andres Dominguez.
 */
public class TreeNode extends DefaultMutableTreeNode {

  public TreeNode(Object object) {
    super(object);
  }

  public TestFindResult getNodeValue() {
    return (TestFindResult) getUserObject();
  }
}

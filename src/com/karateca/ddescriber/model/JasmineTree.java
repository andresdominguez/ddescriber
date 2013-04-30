package com.karateca.ddescriber.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class JasmineTree extends Tree {

  private final TreeNode rootNode;
  private boolean showingMarkedTests;

  public JasmineTree() {
    super(new TreeNode("Root node"));
    rootNode = (TreeNode) this.getModel().getRoot();
  }

  public void addFiles(List<JasmineFile> jasmineFiles) {
    for (JasmineFile jasmineFile : jasmineFiles) {
      rootNode.add(jasmineFile.getTreeNode());
    }

    expandRow(0);
    setRootVisible(false);
  }

  public TreeNode getRootNode() {
    return rootNode;
  }

  public void updateFiles(List<JasmineFile> files) {
    clear();

    if (showingMarkedTests) {
      addMarkedTestsToRootNode(files);
    } else {
      addFiles(files);
    }

    updateTree(rootNode);
  }

  private void addMarkedTestsToRootNode(List<JasmineFile> files) {
    for (JasmineFile jasmineFile : files) {
      jasmineFile.buildTreeNodeSync();
      for (TestFindResult findResult : jasmineFile.getElementsMarkedToRun()) {
        rootNode.add(new TreeNode(findResult, jasmineFile.getVirtualFile()));
      }
    }
  }

  /**
   * Show the tests that have been marked to run only.
   */
  public void showSelectedNodesOnly() {
    showingMarkedTests = true;

    List<TreeNode> markedTests = new ArrayList<TreeNode>();
    collectSelectedNodes(rootNode, markedTests);

    rootNode.removeAllChildren();
    for (TreeNode node : markedTests) {
      // Make a copy.
      rootNode.add(new TreeNode(node));
    }
    updateTree(rootNode);
  }

  private void collectSelectedNodes(TreeNode node, List<TreeNode> markedTests) {
    if (node != rootNode && node.isTestNode() && node.getNodeValue().isMarkedForRun()) {
      markedTests.add(node);
    }

    if (node.getChildCount() > 0) {
      Enumeration children = node.children();
      while (children.hasMoreElements()) {
        collectSelectedNodes((TreeNode) children.nextElement(), markedTests);
      }
    }
  }

  public void showAllTests(List<JasmineFile> jasmineFiles) {
    showingMarkedTests = false;
    updateFiles(jasmineFiles);
  }

  /**
   * Remove all the nodes from the tree.
   */
  public void clear() {
    rootNode.removeAllChildren();
    updateTree(rootNode);
  }

  private void updateTree(TreeNode nodeForFile) {
    DefaultTreeModel model = (DefaultTreeModel) getModel();
    model.reload(nodeForFile);
  }
}

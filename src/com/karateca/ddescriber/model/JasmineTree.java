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

  public void updateFile(JasmineFile jasmineFile) {
    if (showingMarkedTests) {

      // Remove all the tests for the file.
      for (TreeNode treeNode : getTreeNodesForFile(jasmineFile)) {
        treeNode.removeFromParent();
      }

      // Add them.
      jasmineFile.buildTreeNodeSync();
      for (TestFindResult item : jasmineFile.getElementsMarkedToRun()) {
        rootNode.add(new TreeNode(item, jasmineFile.getVirtualFile()));
      }

      updateTree(rootNode);
      return;
    }

    TreeNode found = findNodeForJasmineFile(jasmineFile.getVirtualFile());

    if (found != null) {
      // The jasmine file is in the tree already. Update it or remove it.
      updateOrRemove(jasmineFile, found);
    } else {
      // This is a new test. Add it at the end of the tree.
      TreeNode newTestNode = new TreeNode("");
      jasmineFile.updateTreeNode(newTestNode);
      rootNode.add(newTestNode);
      updateTree(rootNode);
    }
  }

  private List<TreeNode> getTreeNodesForFile(JasmineFile jasmineFile) {
    VirtualFile virtualFile = jasmineFile.getVirtualFile();
    List<TreeNode> result = new ArrayList<TreeNode>();

    Enumeration children = rootNode.children();
    while (children.hasMoreElements()) {
      TreeNode node = (TreeNode) children.nextElement();
      if (node.getVirtualFile() == virtualFile) {
        result.add(node);
      }
    }

    return result;
  }

  private void updateTree(TreeNode nodeForFile) {
    DefaultTreeModel model = (DefaultTreeModel) getModel();
    model.reload(nodeForFile);
  }

  private void updateOrRemove(JasmineFile jasmineFile, TreeNode found) {
    jasmineFile.updateTreeNode(found);

    if (jasmineFile.hasTestsMarkedToRun()) {
      updateTree(found);
    } else {
      rootNode.remove(found);
      updateTree(rootNode);
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

  public void clear() {
    rootNode.removeAllChildren();
    updateTree(rootNode);
  }

  public void updateFiles(List<JasmineFile> files) {
    clear();
    addFiles(files);
    updateTree(rootNode);
  }

  public void showMarkedOnly(boolean showingMarkedTests) {
    if (showingMarkedTests) {
      this.showingMarkedTests = showingMarkedTests;
      showSelectedNodesOnly();
    }
  }

  private void showSelectedNodesOnly() {
    List<TreeNode> markedTests = new ArrayList<TreeNode>();
    collectSelectedNodes(rootNode, markedTests);

    rootNode.removeAllChildren();
    for (TreeNode node : markedTests) {
      rootNode.add(node);
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
}

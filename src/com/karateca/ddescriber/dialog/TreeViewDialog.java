package com.karateca.ddescriber.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.TestFindResult;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

/**
 * @author Andres Dominguez.
 */
public class TreeViewDialog extends Dialog {

  private Tree tree;

  public TreeViewDialog(@Nullable Project project, Hierarchy hierarchy, boolean showAll) {
    super(project, hierarchy, showAll);
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    List<TestFindResult> elements = hierarchy.getAllUnitTests();
    final TestFindResult closest = hierarchy.getClosest();

    DefaultMutableTreeNode root = populateTree(elements);

    tree = new Tree(root);
    tree.setVisibleRowCount(VISIBLE_ROW_COUNT);
    tree.setCellRenderer(new CustomTreeCellRenderer());

    // Add search.
    new TreeSpeedSearch(tree) {
      @Override
      protected boolean compare(String text, String pattern) {
        return super.compare(text.toLowerCase(), pattern.toLowerCase());
      }
    }.setComparator(new SpeedSearchComparator(false));

    selectClosestTest(root, closest);

    JBScrollPane scrollPane = new JBScrollPane(tree);

    return scrollPane;
  }

  private void selectClosestTest(DefaultMutableTreeNode root, final TestFindResult closest) {
    Enumeration enumeration = root.breadthFirstEnumeration();

    while (enumeration.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

      if (node.getUserObject() == closest) {
        TreePath treePath = new TreePath(node.getPath());
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);

        return;
      }
    }
  }

  private DefaultMutableTreeNode populateTree(List<TestFindResult> elements) {
    TestFindResult first = elements.get(0);
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(first);

    if (elements.size() < 2) {
      return root;
    }

    Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
    int currentIndentation = first.getIndentation();

    DefaultMutableTreeNode parent = root;
    DefaultMutableTreeNode last = root;

    for (TestFindResult element : elements.subList(1, elements.size())) {
      int ind = element.getIndentation();

      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(element);

      if (ind > currentIndentation) {
        stack.push(parent);
        parent = last;
      } else if (ind < currentIndentation) {
        parent = stack.pop();
      }
      last = newNode;
      parent.add(last);

      currentIndentation = ind;
    }

    return root;
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return tree;
  }

  @Override
  public List<TestFindResult> getSelectedValues() {
    List<TestFindResult> selected = new ArrayList<TestFindResult>();

    for (DefaultMutableTreeNode node : tree.getSelectedNodes(DefaultMutableTreeNode.class, null)) {
      selected.add((TestFindResult) node.getUserObject());
    }

    return selected;
  }
}

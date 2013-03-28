package com.karateca.ddescriber.toolWindow;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.karateca.ddescriber.model.TreeNode;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class AllTestsPanel extends JPanel {

  Tree tree;
  TreeNode root;
  final JPanel topButtonPanel;
  final JBScrollPane panelWithCurrentTests;
  final JPanel leftButtonPanel;

  public AllTestsPanel() {
    super(new BorderLayout());

    topButtonPanel = new JPanel();
    panelWithCurrentTests = buildPanelWithTests();
    leftButtonPanel = new JPanel();

    add(BorderLayout.NORTH, topButtonPanel);
    add(BorderLayout.CENTER, panelWithCurrentTests);
    add(BorderLayout.LINE_START, leftButtonPanel);
  }

  private JBScrollPane buildPanelWithTests() {
    root = new TreeNode("All tests");
    tree = new Tree(root);

    JBScrollPane scrollPane = new JBScrollPane(tree);

    // Expand the root an make it invisible.
    tree.expandRow(0);
    tree.setRootVisible(false);

    return scrollPane;
  }
}

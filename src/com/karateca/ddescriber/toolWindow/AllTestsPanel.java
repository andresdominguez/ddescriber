package com.karateca.ddescriber.toolWindow;

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
  final JPanel panelWithCurrentTests;
  final JPanel leftButtonPanel;

  public AllTestsPanel() {
    super(new BorderLayout());

    topButtonPanel = new JPanel();
    panelWithCurrentTests = new JPanel();
    leftButtonPanel = new JPanel();
  }
}

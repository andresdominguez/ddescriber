package com.karateca.ddescriber.toolWindow;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import com.intellij.ui.treeStructure.Tree;
import com.karateca.ddescriber.model.TreeNode;

import org.junit.Test;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class AllTestsPanelTest {

  @Test
  public void shouldCreateInternalPanels() {
    AllTestsPanel panel = new AllTestsPanel();

    // Ensure there are 3 panels inside.
    assertNotNull(panel.topButtonPanel);
    assertNotNull(panel.panelWithCurrentTests);
    assertNotNull(panel.leftButtonPanel);

    assertEquals(3, panel.getComponentCount());
  }

  @Test
  public void shouldCreateRootNodeAndTree() {
    AllTestsPanel panel = new AllTestsPanel();

    TreeNode root = panel.root;
    Tree tree = panel.tree;

    // Ensure the root is defined.
    assertEquals("All tests", root.getUserObject());

    // Ensure the root is invisible.
    assertFalse(tree.isRootVisible());
  }
}

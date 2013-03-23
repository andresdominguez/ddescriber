package com.karateca.ddescriber;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Andres Dominguez.
 */
public class JasmineTreeUtil {
  // Add a double click handler to a JTree.
  public static void addDoubleClickListener(final Tree tree, final VoidFunction<TreePath> handler) {
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        if (selRow != -1 && e.getClickCount() == 2) {
          handler.fun(tree.getPathForLocation(e.getX(), e.getY()));
        }
      }
    });
  }
}

package com.karateca.ddescriber.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.karateca.ddescriber.TestFindResult;
import com.karateca.ddescriber.model.TreeNode;
import com.karateca.ddescriber.model.JasmineFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class TreeViewDialog extends DialogWrapper {
  public static final int CLEAN_CURRENT_EXIT_CODE = 100;
  public static final int GO_TO_TEST_EXIT_CODE = 101;

  private static final int VISIBLE_ROW_COUNT = 13;
  private final int caretOffset;
  private Tree tree;
  private TestFindResult selectedTest;
  private JasmineFile jasmineFile;

  public TreeViewDialog(Project project, JasmineFile jasmineFile, int caretOffset) {
    super(project);
    this.jasmineFile = jasmineFile;
    this.caretOffset = caretOffset;
    init();
    setTitle("Select the Test or Suite to Add / Remove");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    final TestFindResult closest = jasmineFile.getClosestTestFromCaret(caretOffset);

    // Build the tree.
    TreeNode root = jasmineFile.getTreeNode();
    tree = new Tree(root);
    tree.setVisibleRowCount(VISIBLE_ROW_COUNT);
    tree.setCellRenderer(new CustomTreeCellRenderer());

    // Add search, make it case insensitive.
    new TreeSpeedSearch(tree) {
      @Override
      protected boolean compare(String text, String pattern) {
        return super.compare(text.toLowerCase(), pattern.toLowerCase());
      }
    }.setComparator(new SpeedSearchComparator(false));

    addKeyAndMouseEvents();

    JBScrollPane scrollPane = new JBScrollPane(tree);
    selectClosestTest(root, closest);

    return scrollPane;
  }

  private void addKeyAndMouseEvents() {
    // Perform the OK action on enter.
    tree.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
          doOKAction();
        }
      }
    });

    // Go to the test on double click.
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        if (selRow != -1 && e.getClickCount() == 2) {
          TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
          nodeWasDoubleClicked(selPath);
        }
      }
    });
  }

  private void nodeWasDoubleClicked(TreePath selPath) {
    DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selPath.getLastPathComponent();
    selectedTest = (TestFindResult) lastPathComponent.getUserObject();
    close(GO_TO_TEST_EXIT_CODE);
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

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return tree;
  }

  public List<TestFindResult> getSelectedValues() {
    List<TestFindResult> selected = new ArrayList<TestFindResult>();

    for (DefaultMutableTreeNode node : tree.getSelectedNodes(DefaultMutableTreeNode.class, null)) {
      selected.add((TestFindResult) node.getUserObject());
    }

    return selected;
  }

  @Override
  protected Action[] createLeftSideActions() {
    return new Action[]{
        new DialogWrapperExitAction("Clean file", CLEAN_CURRENT_EXIT_CODE)
    };
  }

  public TestFindResult getSelectedTest() {
    return selectedTest;
  }
}

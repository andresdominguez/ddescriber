package com.karateca.ddescriber.dialog;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import com.karateca.ddescriber.JasmineTreeUtil;
import com.karateca.ddescriber.VoidFunction;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TestCounts;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TestState;
import com.karateca.ddescriber.model.TreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class TreeViewDialog extends DialogWrapper {
  public static final int CLEAN_CURRENT_EXIT_CODE = 100;
  public static final int GO_TO_TEST_EXIT_CODE = 101;
  public static final int EXCLUDE_EXIT_CODE = 104;

  private static final int VISIBLE_ROW_COUNT = 17;
  private final int caretOffset;
  private Tree tree;
  private TestFindResult selectedTest;
  private final JasmineFile jasmineFile;
  private final PendingChanges pendingChanges;

  public TreeViewDialog(Project project, JasmineFile jasmineFile, int caretOffset) {
    super(project);
    this.jasmineFile = jasmineFile;
    this.caretOffset = caretOffset;
    pendingChanges = new PendingChanges();
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

    // Check if there are multiple describes in the file.
    if (root.getUserObject() instanceof String) {
      tree.setRootVisible(false);
    }

    TreeUtil.expandAll(tree);

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

    JPanel panel = new JPanel(new BorderLayout());

    panel.add(BorderLayout.CENTER, scrollPane);
    panel.add(BorderLayout.SOUTH, createPanelWithLabels());

    return panel;
  }

  private JPanel createPanelWithLabels() {
    JPanel panel = new JPanel(new BorderLayout());

    TestCounts testCounts = jasmineFile.getTestCounts();

    String values = String.format("Tests: %s, Excluded: %s, Included: %s", testCounts.getTestCount(),
        testCounts.getExcludedCount(), testCounts.getIncludedCount());
    panel.add(BorderLayout.CENTER, new JLabel(values));
    return panel;
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
    JasmineTreeUtil.addDoubleClickListener(tree, new VoidFunction<TreePath>() {
      @Override
      public void fun(TreePath treePath) {
        nodeWasDoubleClicked(treePath);
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

  @NotNull
  @Override
  protected Action[] createLeftSideActions() {
    return new Action[]{
        new DialogWrapperExitAction("Clean file", CLEAN_CURRENT_EXIT_CODE)
    };
  }

  @NotNull
  @Override
  protected Action[] createActions() {
    Action excludeAction = new MyAction("Exclude (Alt E)", TestState.Excluded);
    Action includeAction = new MyAction("Include (Alt I)", TestState.Included);

    ShortcutSet ALT_X = new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
    ShortcutSet ALT_I = new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));

    registerForEveryKeyboardShortcut(excludeAction, ALT_X);
    registerForEveryKeyboardShortcut(includeAction, ALT_I);

    return new Action[]{
        excludeAction,
        includeAction,
        getCancelAction(),
        getOKAction()
    };
  }

  // todo: fix this
  private void registerForEveryKeyboardShortcut(ActionListener action, @NotNull ShortcutSet shortcuts) {
    for (Shortcut shortcut : shortcuts.getShortcuts()) {
      if (shortcut instanceof KeyboardShortcut) {
        KeyboardShortcut ks = (KeyboardShortcut) shortcut;
        KeyStroke first = ks.getFirstKeyStroke();
        KeyStroke second = ks.getSecondKeyStroke();
        if (second == null) {
          getRootPane().registerKeyboardAction(action, first, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
      }
    }
  }

  public TestFindResult getSelectedTest() {
    return selectedTest;
  }

  class MyAction extends DialogWrapperAction {
    private final TestState changeState;

    MyAction(String name, TestState changeState) {
      super(name);
      this.changeState = changeState;
    }

    @Override
    protected void doAction(ActionEvent e) {
      for (TestFindResult testFindResult : getSelectedValues()) {
        pendingChanges.itemChanged(testFindResult, changeState);
      }
      tree.repaint();
    }
  }
}

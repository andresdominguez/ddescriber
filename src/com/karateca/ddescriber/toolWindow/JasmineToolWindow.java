package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiManager;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Function;
import com.intellij.util.ui.tree.TreeUtil;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.JasmineDescriberNotifier;
import com.karateca.ddescriber.JasmineTreeUtil;
import com.karateca.ddescriber.dialog.CustomTreeCellRenderer;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TreeNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineToolWindow implements ToolWindowFactory {

  private ToolWindow toolWindow;
  private Project project;
  private Tree tree;
  private JComponent panelWithCurrentTests;
  private TreeNode root;

  private final Icon refreshIcon = IconLoader.findIcon("/icons/refresh.png");
  private final Icon expandIcon = IconLoader.findIcon("/icons/expandall.png");
  private final Icon collapseIcon = IconLoader.findIcon("/icons/collapseall.png");

  @Override
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    this.toolWindow = toolWindow;
    this.project = project;
    findAllFilesContainingTests(new Function<List<JasmineFile>, Void>() {
      @Override
      public Void fun(List<JasmineFile> jasmineFiles) {
        showTestsInToolWindow(jasmineFiles);
        return null;
      }
    });

    listenForFileChanges();
  }

  private void listenForFileChanges() {
    JasmineDescriberNotifier.getInstance().addTestChangedLister(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        JasmineFile jasmineFile = (JasmineFile) changeEvent.getSource();

        // Find the test.
        TreeNode nodeForFile = findTestInCurrentTree(jasmineFile);
        if (nodeForFile == null) {
          // This is a new test. Add it to the end.
          TreeNode newTestNode = new TreeNode("");
          jasmineFile.updateTreeNode(newTestNode);
          root.add(newTestNode);
          updateTree(root);
          return;
        }

        // The test file is in the tree. Update or remove if there are no marked tests.
        jasmineFile.updateTreeNode(nodeForFile);
        if (jasmineFile.hasTestsMarkedToRun()) {
          updateTree(nodeForFile);
        } else {
          root.remove(nodeForFile);
          updateTree(root);
        }
      }
    });
  }

  /**
   * Update the tree starting from a specific node.
   *
   * @param nodeForFile The node you want to refresh.
   */
  private void updateTree(TreeNode nodeForFile) {
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    model.reload(nodeForFile);
  }

  /**
   * Find the node matching a jasmine file.
   *
   * @param jasmineFile The jasmine file to test the nodes.
   * @return The node in the tree matching the jasmine file; null if not found.
   */
  private TreeNode findTestInCurrentTree(JasmineFile jasmineFile) {
    VirtualFile virtualFile = jasmineFile.getVirtualFile();

    Enumeration children = root.children();
    while (children.hasMoreElements()) {
      TreeNode child = (TreeNode) children.nextElement();
      if (child.getNodeValue().getVirtualFile() == virtualFile) {
        return child;
      }
    }
    return null;
  }

  /**
   * Go through the project and find any files containing jasmine files with ddescribe() and iit().
   *
   * @param doneCallback Called once all search is done.
   */
  private void findAllFilesContainingTests(final Function<List<JasmineFile>, Void> doneCallback) {
    ActionUtil.runReadAction(new Runnable() {
      @Override
      public void run() {
        FileIterator fileIterator = new FileIterator(project, true);
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileIterator);
        List<JasmineFile> jasmineFiles = fileIterator.getJasmineFiles();
        doneCallback.fun(jasmineFiles);
      }
    });
  }

  private void showTestsInToolWindow(List<JasmineFile> jasmineFiles) {
    JPanel panel = new JPanel(new BorderLayout());
    panelWithCurrentTests = createCenterPanel(jasmineFiles);

    JPanel leftButtonPanel = createButtonPanel();
    JPanel topButtonPanel = createTopPanel();

    panel.add(BorderLayout.NORTH, topButtonPanel);
    panel.add(BorderLayout.CENTER, panelWithCurrentTests);
    panel.add(BorderLayout.LINE_START, leftButtonPanel);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, "Active tests", false);
    toolWindow.getContentManager().addContent(content);
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel();

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panel.add(createRefreshButton());
    panel.add(Box.createVerticalStrut(8));
    panel.add(createExpandAllButton());
    panel.add(Box.createVerticalStrut(8));
    panel.add(createCollapseAllButton());

    return panel;
  }

  private JPanel createTopPanel() {
    JPanel pane = new JPanel();

    pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
    pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    final JBCheckBox checkBox = new JBCheckBox("Filter marked", false);
    JButton cleanAllButton = new JButton("Clean all");

    pane.add(checkBox);
    pane.add(Box.createHorizontalStrut(8));
    pane.add(cleanAllButton);

    checkBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (checkBox.isSelected()) {
          ArrayList<TreeNode> markedTests = new ArrayList<TreeNode>();
          collectSelectedNodes(root, markedTests);

          root.removeAllChildren();
          for (TreeNode node : markedTests) {
            root.add(node);
          }
          updateTree(root);
        } else {

        }
      }
    });

    cleanAllButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        findAllFilesContainingTests(new Function<List<JasmineFile>, Void>() {
          @Override
          public Void fun(List<JasmineFile> jasmineFiles) {
            Collections.reverse(jasmineFiles);
            for (JasmineFile file : jasmineFiles) {
              Document document = ActionUtil.getDocument(file.getVirtualFile());
              List<TestFindResult> elements = file.getElementsMarkedToRun();
              Collections.reverse(elements);
              TestFindResult[] findResults = elements.toArray(new TestFindResult[elements.size()]);
              ActionUtil.changeSelectedLineRunningCommand(project, document, findResults);
            }

            root.removeAllChildren();
            updateTree(root);

            return null;
          }
        });
      }
    });

    return pane;
  }

  private void collectSelectedNodes(TreeNode node, List<TreeNode> markedTests) {
    if (node != root && node.getNodeValue().isMarkedForRun()) {
      markedTests.add(node);
    }

    if (node.getChildCount() > 0) {
      Enumeration children = node.children();
      while (children.hasMoreElements()) {
        collectSelectedNodes((TreeNode) children.nextElement(), markedTests);
      }
    }
  }

  private JButton createButton(Icon icon, String tooltip) {
    JButton refreshButton = new JButton(icon);
    refreshButton.setBorder(BorderFactory.createEmptyBorder());
    refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    refreshButton.setToolTipText(tooltip);
    return refreshButton;
  }

  private JButton createRefreshButton() {
    JButton refreshButton = createButton(refreshIcon, "Refresh");
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        findAllFilesContainingTests(new Function<List<JasmineFile>, Void>() {
          @Override
          public Void fun(List<JasmineFile> jasmineFiles) {
            // Update the whole tree.
            root.removeAllChildren();
            updateTree(root);
            // Broadcast every file;
            for (JasmineFile jasmineFile : jasmineFiles) {
              JasmineDescriberNotifier.getInstance().testWasChanged(jasmineFile);
            }
            return null;
          }
        });
      }
    });
    return refreshButton;
  }

  private JButton createExpandAllButton() {
    JButton button = createButton(expandIcon, "Expand all");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        TreeUtil.expandAll(tree);
      }
    });
    return button;
  }

  private JButton createCollapseAllButton() {
    JButton button = createButton(collapseIcon, "Collapse all");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        TreeUtil.collapseAll(tree, 0);
      }
    });
    return button;
  }

  private JComponent createCenterPanel(List<JasmineFile> jasmineFiles) {
    // The root node is hidden.
    root = new TreeNode("All tests");
    tree = new Tree(root);

    for (JasmineFile jasmineFile : jasmineFiles) {
      root.add(jasmineFile.buildTreeNodeSync());
    }

    tree.setCellRenderer(new CustomTreeCellRenderer(true));

    // Add search, make it case insensitive.
    new TreeSpeedSearch(tree) {
      @Override
      protected boolean compare(String text, String pattern) {
        return super.compare(text.toLowerCase(), pattern.toLowerCase());
      }
    }.setComparator(new SpeedSearchComparator(false));

    // Go to the selected test on double-click.
    JasmineTreeUtil.addDoubleClickListener(tree, new Function<TreePath, Void>() {
      @Override
      public Void fun(TreePath treePath) {
        // Get the test for the selected node.
        TreeNode lastPathComponent = (TreeNode) treePath.getLastPathComponent();
        goToTest((TestFindResult) lastPathComponent.getUserObject());
        return null;
      }
    });

    // Go to selected test on enter.
    tree.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
          TreeNode[] selectedNodes = tree.getSelectedNodes(TreeNode.class, null);
          if (selectedNodes.length != 0) {
            goToTest(selectedNodes[0].getNodeValue());
          }
        }
      }
    });

    JBScrollPane scrollPane = new JBScrollPane(tree);
    tree.expandRow(0);
    tree.setRootVisible(false);

    return scrollPane;
  }

  private void goToTest(TestFindResult selectedTest) {
    VirtualFile virtualFile = selectedTest.getVirtualFile();

    // Open the file containing the test for the node you just clicked.
    PsiManager psiManager = PsiManager.getInstance(project);
    psiManager.findFile(virtualFile).navigate(true);

    // Go to selected location.
    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    editor.getCaretModel().moveToOffset(selectedTest.getStartOffset());
    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }
}

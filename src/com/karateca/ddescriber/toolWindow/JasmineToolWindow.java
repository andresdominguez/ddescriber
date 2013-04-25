package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiManager;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.tree.TreeUtil;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.JasmineDescriberNotifier;
import com.karateca.ddescriber.JasmineTreeUtil;
import com.karateca.ddescriber.VoidFunction;
import com.karateca.ddescriber.dialog.CustomTreeCellRenderer;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.JasmineTree;
import com.karateca.ddescriber.model.TreeNode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Andres Dominguez.
 */
public class JasmineToolWindow implements ToolWindowFactory {

  private ToolWindow toolWindow;
  private Project project;
  protected JasmineTree tree;

  private final Icon refreshIcon = IconLoader.findIcon("/icons/refresh.png");
  private final Icon expandIcon = IconLoader.findIcon("/icons/expandall.png");
  private final Icon collapseIcon = IconLoader.findIcon("/icons/collapseall.png");
  private boolean filterCheckboxSelected;
  private List<JasmineFile> currentJasmineFiles;

  @Override
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    this.toolWindow = toolWindow;
    this.project = project;
    findAllFilesContainingTests(new VoidFunction<List<JasmineFile>>() {
      @Override
      public void fun(List<JasmineFile> jasmineFiles) {
        showTestsInToolWindow(jasmineFiles);
      }
    });

    listenForFileChanges();
  }

  private void listenForFileChanges() {
    JasmineDescriberNotifier.getInstance().addTestChangedLister(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        JasmineFile jasmineFile = (JasmineFile) changeEvent.getSource();
        tree.updateFile(jasmineFile);
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
   * Go through the project and find any files containing jasmine files with ddescribe() and iit().
   *
   * @param doneCallback Called once all search is done.
   */
  private void findAllFilesContainingTests(final VoidFunction<List<JasmineFile>> doneCallback) {
    ActionUtil.runReadAction(new Runnable() {
      @Override
      public void run() {
        FileIterator fileIterator = new FileIterator(project, true);
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileIterator);
        currentJasmineFiles = fileIterator.getJasmineFiles();
        doneCallback.fun(currentJasmineFiles);
      }
    });
  }

  protected void showTestsInToolWindow(List<JasmineFile> jasmineFiles) {
    JPanel panel = new JPanel(new BorderLayout());
    JComponent panelWithCurrentTests = createTreePanel(jasmineFiles);

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
        filterCheckboxSelected = checkBox.isSelected();
        if (filterCheckboxSelected) {
          tree.showSelectedNodesOnly();
        } else {
          tree.showAllTests(currentJasmineFiles);
        }
      }
    });

    cleanAllButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        findAllFilesContainingTests(new VoidFunction<List<JasmineFile>>() {
          @Override
          public void fun(List<JasmineFile> jasmineFiles) {
            for (JasmineFile jasmineFile : jasmineFiles) {
              jasmineFile.cleanFile();
            }

            tree.clear();
          }
        });
      }
    });

    return pane;
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
        findAllFilesContainingTests(new VoidFunction<List<JasmineFile>>() {
          @Override
          public void fun(List<JasmineFile> jasmineFiles) {
            tree.updateFiles(jasmineFiles);
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

  private JComponent createTreePanel(List<JasmineFile> jasmineFiles) {
    tree = createJasmineTree();

    JBScrollPane scrollPane = new JBScrollPane(tree);
    tree.addFiles(jasmineFiles);

    return scrollPane;
  }

  private JasmineTree createJasmineTree() {
    final JasmineTree jasmineTree = new JasmineTree();
    jasmineTree.setCellRenderer(new CustomTreeCellRenderer(true));

    // Add search, make it case insensitive.
    new TreeSpeedSearch(jasmineTree) {
      @Override
      protected boolean compare(String text, String pattern) {
        return super.compare(text.toLowerCase(), pattern.toLowerCase());
      }
    }.setComparator(new SpeedSearchComparator(false));

    // Go to the selected test on double-click.
    JasmineTreeUtil.addDoubleClickListener(jasmineTree, new VoidFunction<TreePath>() {
      @Override
      public void fun(TreePath treePath) {
        // Get the test for the selected node.
        goToTest((TreeNode) treePath.getLastPathComponent());
      }
    });

    // Go to selected test on enter.
    jasmineTree.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
          TreeNode[] selectedNodes = jasmineTree
              .getSelectedNodes(TreeNode.class, null);
          if (selectedNodes.length != 0) {
            goToTest(selectedNodes[0]);
          }
        }
      }
    });

    return jasmineTree;
  }

  private void goToTest(TreeNode treeNode) {
    // Open the file containing the test for the node you just clicked.
    PsiManager psiManager = PsiManager.getInstance(project);
    psiManager.findFile(treeNode.getVirtualFile()).navigate(true);

    // Go to selected location.
    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    editor.getCaretModel().moveToOffset(getCaretOffset(treeNode));
    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }

  private int getCaretOffset(TreeNode treeNode) {
    // Is this a file or a test?
    if (treeNode.getUserObject() instanceof String) {
      return 0;
    }
    return treeNode.getNodeValue().getStartOffset();
  }
}

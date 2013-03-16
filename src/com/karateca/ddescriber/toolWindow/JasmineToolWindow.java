package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Function;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.JasmineDescriberNotifier;
import com.karateca.ddescriber.dialog.CustomTreeCellRenderer;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TreeNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

  private void updateTree(TreeNode nodeForFile) {
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    model.reload(nodeForFile);
  }

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

    JPanel buttonPanel = createButtonPanel();

    panel.add(BorderLayout.CENTER, panelWithCurrentTests);
    panel.add(BorderLayout.LINE_START, buttonPanel);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, "Active tests", false);
    toolWindow.getContentManager().addContent(content);
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel();

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JButton refreshButton = new JButton(refreshIcon);
    refreshButton.setBorder(BorderFactory.createEmptyBorder());
    refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    refreshButton.setToolTipText("Refresh");
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        findAllFilesContainingTests(new Function<List<JasmineFile>, Void>() {
          @Override
          public Void fun(List<JasmineFile> jasmineFiles) {
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

    panel.add(refreshButton);

    return panel;
  }

  private JComponent createCenterPanel(List<JasmineFile> jasmineFiles) {
    // The root node is hidden.
    root = new TreeNode("All tests");
    tree = new Tree(root);

    for (JasmineFile jasmineFile : jasmineFiles) {
      root.add(jasmineFile.buildTreeNodeSync());
    }

    tree.setCellRenderer(new CustomTreeCellRenderer());

    // Add search, make it case insensitive.
    new TreeSpeedSearch(tree) {
      @Override
      protected boolean compare(String text, String pattern) {
        return super.compare(text.toLowerCase(), pattern.toLowerCase());
      }
    }.setComparator(new SpeedSearchComparator(false));

    JBScrollPane scrollPane = new JBScrollPane(tree);
    tree.expandRow(0);
    tree.setRootVisible(false);

    return scrollPane;
  }
}

package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.JasmineDescriberNotifier;
import com.karateca.ddescriber.dialog.CustomTreeCellRenderer;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TreeNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
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

  @Override
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    this.toolWindow = toolWindow;
    this.project = project;
    findAllFilesContainingTests();

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

  private void findAllFilesContainingTests() {
    ActionUtil.runReadAction(new Runnable() {
      @Override
      public void run() {
        FileIterator fileIterator = new FileIterator(project, true);
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileIterator);
        List<JasmineFile> jasmineFiles = fileIterator.getJasmineFiles();
        showTestsInToolWindow(jasmineFiles);
      }
    });
  }

  private void showTestsInToolWindow(List<JasmineFile> jasmineFiles) {
    JPanel panel = new JPanel(new BorderLayout());
    panelWithCurrentTests = createCenterPanel(jasmineFiles);

    panel.add(BorderLayout.CENTER, panelWithCurrentTests);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, "Active tests", false);
    toolWindow.getContentManager().addContent(content);
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

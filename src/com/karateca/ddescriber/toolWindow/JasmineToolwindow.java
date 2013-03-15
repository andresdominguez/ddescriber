package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.karateca.ddescriber.dialog.CustomTreeCellRenderer;
import com.karateca.ddescriber.model.JasmineFile;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineToolWindow implements ToolWindowFactory {

  private ToolWindow toolWindow;
  private Project project;
  private Tree tree;

  @Override
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    this.toolWindow = toolWindow;
    this.project = project;
    findAllFilesContainingTests();
  }

  private void findAllFilesContainingTests() {
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        FileIterator fileIterator = new FileIterator(project);
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileIterator);
        List<JasmineFile> jasmineFiles = fileIterator.getJasmineFiles();
        showTestsInToolWindow(jasmineFiles);
      }
    });
  }

  private void showTestsInToolWindow(List<JasmineFile> jasmineFiles) {

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gridBagConstraintsScrollPane = new GridBagConstraints();
    gridBagConstraintsScrollPane.gridx = 0;
    gridBagConstraintsScrollPane.gridy = 1;
    gridBagConstraintsScrollPane.gridwidth = 10;
    gridBagConstraintsScrollPane.gridheight = 10;
    gridBagConstraintsScrollPane.fill = GridBagConstraints.BOTH;
    gridBagConstraintsScrollPane.anchor = GridBagConstraints.CENTER;
    gridBagConstraintsScrollPane.weightx = 1;
    gridBagConstraintsScrollPane.weighty = 10;
    panel.add(createCenterPanel(jasmineFiles), gridBagConstraintsScrollPane);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, "", false);
    toolWindow.getContentManager().addContent(content);
  }

  private JComponent createCenterPanel(List<JasmineFile> jasmineFiles) {
    VirtualFileListener virtualFileListener = new VirtualFileListener();
    for (JasmineFile jasmineFile : jasmineFiles) {
      virtualFileListener.registerForChangeEvent(jasmineFile, new ChangeCallback() {
        @Override
        public void contentsChanged(final JasmineFile jasmineFile) {
          System.out.println("This file changed " + jasmineFile.getVirtualFile().getName());
          jasmineFile.updateTreeNode();

          // Force an update.
          DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
          model.reload(jasmineFile.getTreeNode());
        }
      });
    }

    // The root node is hidden.
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("All tests");
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

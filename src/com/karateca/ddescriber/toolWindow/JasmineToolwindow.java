package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineToolWindow implements ToolWindowFactory {

  private ToolWindow toolWindow;
  private Project project;

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
        FileIterator fileIterator = new FileIterator();
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileIterator);
        List<JasminFile> jasminFiles = fileIterator.getJasminFiles();
        showTestsInToolWindow(jasminFiles);
      }
    });
  }

  private void showTestsInToolWindow(List<JasminFile> jasminFiles) {
    JPanel panel = new JPanel(new GridBagLayout());
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(panel, "", false);
    toolWindow.getContentManager().addContent(content);
  }
}

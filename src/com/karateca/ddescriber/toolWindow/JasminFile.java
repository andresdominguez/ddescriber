package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.JasmineFinder;

/**
 * @author Andres Dominguez.
 */
class JasminFile {

  private final Project project;
  private final VirtualFile virtualFile;

  public JasminFile(Project project, VirtualFile virtualFile) {
    this.project = project;
    this.virtualFile = virtualFile;
  }

  public Hierarchy createHierarchy() {
    Document document = ActionUtil.getDocument(virtualFile);

    JasmineFinder jasmineFinder = new JasmineFinder(project, document);
    jasmineFinder.findAll();
    Hierarchy hierarchy = new Hierarchy(document, jasmineFinder.getFindResults(), 0);

    return hierarchy;
  }

  @Override
  public String toString() {
    return virtualFile.getName();
  }

  public VirtualFile getVirtualFile() {
    return virtualFile;
  }
}
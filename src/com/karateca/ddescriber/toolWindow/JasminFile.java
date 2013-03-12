package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.JasmineFinder;

/**
 * @author Andres Dominguez.
 */
public class JasminFile {

  private final VirtualFile virtualFile;
  private Hierarchy hierarchy;

  public JasminFile(VirtualFile virtualFile) {
    this.virtualFile = virtualFile;
  }

  public Hierarchy createHierarchy(Project project) {
    FileDocumentManager instance = FileDocumentManager.getInstance();
    Document document = instance.getDocument(virtualFile);

    JasmineFinder jasmineFinder = new JasmineFinder(project, document, virtualFile);
    jasmineFinder.findAll();
    hierarchy = new Hierarchy(document, jasmineFinder.getFindResults(), 0);

    return hierarchy;
  }

  @Override
  public String toString() {
    return virtualFile.getName();
  }


}

package com.karateca.ddescriber.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Andres Dominguez.
 */
public class JasmineFile {
  private final Project project;
  private final VirtualFile virtualFile;

  public JasmineFile(Project project, VirtualFile virtualFile) {
    this.project = project;
    this.virtualFile = virtualFile;
  }


  public DefaultMutableTreeNode buildTreeNode() {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }
}

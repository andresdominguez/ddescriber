package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Andres Dominguez.
 */
public class JasminFile {

  private final VirtualFile virtualFile;

  public JasminFile(VirtualFile virtualFile) {
    this.virtualFile = virtualFile;
  }

  @Override
  public String toString() {
    return virtualFile.getName();
  }
}

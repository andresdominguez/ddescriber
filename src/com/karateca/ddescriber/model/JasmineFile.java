package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.ActionUtil;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.JasmineFinder;

import java.util.List;

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

  public TreeNode buildTreeNode() {
    Document document = ActionUtil.getDocument(virtualFile);
    JasmineFinder jasmineFinder = new JasmineFinder(project, document);
    jasmineFinder.findAll();
    List<FindResult> findResults = jasmineFinder.getFindResults();
    Hierarchy hierarchy = new Hierarchy(document, findResults);
    return ActionUtil.populateTree(hierarchy.getAllUnitTests());
  }
}

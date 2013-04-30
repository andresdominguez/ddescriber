package com.karateca.ddescriber.model;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

import javax.swing.event.ChangeListener;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public interface JasmineFile {

  void buildTreeNodeAsync();

  void updateTreeNode(TreeNode destination);

  void copyTree(TreeNode source, TreeNode destination);

  TreeNode buildTreeNodeSync();

  void addResultsReadyListener(ChangeListener changeListener);

  boolean hasTestsMarkedToRun();

  VirtualFile getVirtualFile();

  TreeNode getTreeNode();

  TestFindResult getClosestTestFromCaret(int caretOffset);

  List<TestFindResult> getElementsMarkedToRun();

  void cleanFile();
}

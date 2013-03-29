package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.JasmineFileImpl;

import java.io.IOException;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andres Dominguez.
 */
public class FileIterator implements ContentIterator {

  private final List<JasmineFile> jasmineFiles;
  private final Project project;
  private final boolean skipCleanTestFiles;

  public FileIterator(Project project, boolean skipCleanTestFiles) {
    this.project = project;
    this.skipCleanTestFiles = skipCleanTestFiles;
    this.jasmineFiles = new ArrayList<JasmineFile>();
  }

  @Override
  public boolean processFile(VirtualFile fileOrDir) {
    try {
      readFile(fileOrDir);
    } catch (IOException e) {
      System.err.println("Error reading file " + fileOrDir);
      e.printStackTrace(System.err);
    }

    return true;
  }

  private void readFile(VirtualFile fileOrDir) throws IOException {
    if (fileOrDir.isDirectory() || !fileOrDir.getName().endsWith(".js")) {
      return;
    }

    String fileContents = getFileContents(fileOrDir);

    Pattern pattern = Pattern.compile("(\\s*d?)describe\\(");
    Matcher matcher = pattern.matcher(fileContents);
    boolean found = matcher.find();
    if (!found) {
      return;
    }

    JasmineFile jasmineFile = new JasmineFileImpl(project, fileOrDir);
    jasmineFile.buildTreeNodeSync();

    if (skipCleanTestFiles && jasmineFile.getElementsMarkedToRun().size() == 0) {
      return;
    }

    jasmineFiles.add(jasmineFile);
  }

  private String getFileContents(VirtualFile virtualFile) throws IOException {
    return new String(virtualFile.contentsToByteArray());
  }

  public List<JasmineFile> getJasmineFiles() {
    return jasmineFiles;
  }
}

package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VirtualFile;

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

  private final List<JasminFile> jasminFiles;

  public FileIterator() {
    this.jasminFiles = new ArrayList<JasminFile>();
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

    jasminFiles.add(new JasminFile(fileOrDir));
  }

  private String getFileContents(VirtualFile virtualFile) throws IOException {
    return new String(virtualFile.contentsToByteArray());
  }

  public List<JasminFile> getJasminFiles() {
    return jasminFiles;
  }
}

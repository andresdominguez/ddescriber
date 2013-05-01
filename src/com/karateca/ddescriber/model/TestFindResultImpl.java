package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

/**
 * @author Andres Dominguez.
 */
public class TestFindResultImpl implements TestFindResult {

  private static final String REMOVE_START_OF_LINE = "\\s*([xd]?describe\\(|[xi]?it\\()[\"\'](\\S+)";
  private static final String REMOVE_END_OF_LINE = "(\\S+)([\"\'])(\\s*[,+]\\s*.*$)";
  private final int indentation;
  private final boolean isDescribe;
  private final boolean markedForRun;
  private final boolean excluded;
  private final int endOffset;
  private final int startOffset;
  private final int lineNumber;
  private String testText;

  public TestFindResultImpl(Document document, FindResult findResult) {
    startOffset = findResult.getStartOffset();
    endOffset = findResult.getEndOffset();

    int lineNumber = document.getLineNumber(endOffset);
    int startOfLine = document.getLineStartOffset(lineNumber);
    int endOfLine = document.getLineEndOffset(lineNumber);
    this.lineNumber = lineNumber + 1;

    String lineText = document.getText(new TextRange(startOfLine, endOfLine));
    isDescribe = lineText.contains("describe(");
    markedForRun = lineText.contains("ddescribe(") || lineText.contains("iit(");
    excluded = lineText.contains("xdescribe(") || lineText.contains("xit(");

    // Leave the Test text.
    // TODO: improve this regular expression.
    testText = lineText.replaceAll(REMOVE_END_OF_LINE, "$1");
    testText = testText.replaceAll(REMOVE_START_OF_LINE, "$2");

    indentation = startOffset - startOfLine;
  }

  @Override
  public int getIndentation() {
    return indentation;
  }

  @Override
  public boolean isDescribe() {
    return isDescribe;
  }

  @Override
  public boolean isMarkedForRun() {
    return markedForRun;
  }

  @Override
  public int getEndOffset() {
    return endOffset;
  }

  @Override
  public int getStartOffset() {
    return startOffset;
  }

  @Override
  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String getTestText() {
    return testText;
  }

  @Override
  public boolean isExcluded() {
    return excluded;
  }

  @Override
  public String toString() {
//    return String.format("%s (line: %d)", testText.trim(), lineNumber);
    return testText.trim();
  }
}

package com.karateca.ddescriber;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

/**
 * @author Andres Dominguez.
 */
public class TestFindResult {

  final String lineText;
  private final int indentation;
  private final boolean isDescribe;
  private final boolean markedForRun;
  private final int endOffset;
  private final int startOffset;
  private final int lineNumber;

  public TestFindResult(Document document, FindResult findResult) {
    startOffset = findResult.getStartOffset();
    endOffset = findResult.getEndOffset();

    int lineNumber = document.getLineNumber(endOffset);
    int startOfLine = document.getLineStartOffset(lineNumber);
    int endOfLine = document.getLineEndOffset(lineNumber);

    this.lineNumber = lineNumber + 1;
    lineText = document.getText(new TextRange(startOfLine, endOfLine));
    indentation = startOffset - startOfLine;

    isDescribe = lineText.contains("describe(");
    markedForRun = lineText.contains("ddescribe(") || lineText.contains("iit(");
  }

  public int getIndentation() {
    return indentation;
  }

  public boolean isDescribe() {
    return isDescribe;
  }

  public boolean isMarkedForRun() {
    return markedForRun;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String toString() {
    return String.format("line: %5d: %s", lineNumber, lineText);
  }
}

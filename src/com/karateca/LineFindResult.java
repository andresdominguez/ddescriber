package com.karateca;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.util.TextRange;

/**
 * @author Andres Dominguez.
 */
public class LineFindResult {

  private final String lineText;
  private final int indentation;
  private final boolean isDescribe;
  private final boolean markedForRun;
  private final int endOffset;
  private final int startOffset;

  public LineFindResult(DocumentImpl document, FindResult findResult) {
    startOffset = findResult.getStartOffset();
    endOffset = findResult.getEndOffset();

    int lineNumber = document.getLineNumber(endOffset);
    int startOfLine = document.getLineStartOffset(lineNumber);
    int endOfLine = document.getLineEndOffset(lineNumber);

    lineText = document.getText(new TextRange(startOfLine, endOfLine));
    indentation = startOffset - startOfLine;

    isDescribe = lineText.contains("describe(");
    markedForRun = lineText.contains("ddescribe(") || lineText.contains("iit(");
  }

  public String getLineText() {
    return lineText;
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
}

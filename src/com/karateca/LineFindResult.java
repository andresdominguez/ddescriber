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

  public LineFindResult(DocumentImpl document, FindResult findResult) {
    int lineNumber = document.getLineNumber(findResult.getEndOffset());
    int startOfLine = document.getLineStartOffset(lineNumber);
    int endOfLine = document.getLineEndOffset(lineNumber);

    lineText = document.getText(new TextRange(startOfLine, endOfLine));
    indentation = findResult.getStartOffset() - startOfLine;

    isDescribe = lineText.contains("describe(");
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
}

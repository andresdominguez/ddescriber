package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

/**
 * @author Andres Dominguez.
 */
public class TestFindResult {

  private static final String REMOVE_START_OF_LINE = "\\s*([xd]?describe\\(|[xi]?it\\()[\"\'](\\S+)";
  private static final String REMOVE_END_OF_LINE = "(\\S+)([\"\'])(\\s*[,+]\\s*.*$)";
  private final int indentation;
  private final boolean isDescribe;
  private final int endOffset;
  private final int startOffset;
  private final int lineNumber;
  private String testText;
  private TestState testState;
  private TestState pendingChangeState;

  public TestFindResult(Document document, FindResult findResult) {
    startOffset = findResult.getStartOffset();
    endOffset = findResult.getEndOffset();

    int lineNumber = document.getLineNumber(endOffset);
    int startOfLine = document.getLineStartOffset(lineNumber);
    int endOfLine = document.getLineEndOffset(lineNumber);
    this.lineNumber = lineNumber + 1;

    String lineText = document.getText(new TextRange(startOfLine, endOfLine));
    isDescribe = lineText.contains("describe(");

    if (lineText.contains("ddescribe") || lineText.contains("iit")) {
      testState = TestState.Included;
    } else if (lineText.contains("xdescribe") || lineText.contains("xit")) {
      testState = TestState.Excluded;
    } else {
      testState = TestState.NotModified;
    }

    // Leave the Test text.
    // TODO: improve this regular expression.
    testText = lineText.replaceAll(REMOVE_END_OF_LINE, "$1");
    testText = testText.replaceAll(REMOVE_START_OF_LINE, "$2");

    indentation = startOffset - startOfLine;
  }

  public int getIndentation() {
    return indentation;
  }

  public boolean isDescribe() {
    return isDescribe;
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

  public String getTestText() {
    return testText;
  }

  public TestState getTestState() {
    return testState;
  }

  public void setTestState(TestState testState) {
    this.testState = testState;
  }

  public String toString() {
    return testText.trim();
  }

  public TestState getPendingChangeState() {
    return pendingChangeState;
  }

  public void setPendingChangeState(TestState pendingChangeState) {
    this.pendingChangeState = pendingChangeState;
  }
}

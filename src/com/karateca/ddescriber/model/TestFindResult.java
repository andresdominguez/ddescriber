package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andres Dominguez.
 */
public class TestFindResult {

  private static final String REMOVE_START_OF_LINE =
      "\\s*([xfd]?describe\\s*\\(|[xfi]?it\\s*\\()[\"\'](\\S+)";
  private static final String REMOVE_END_OF_LINE = "(\\S+)([\"\'])(\\s*[,+]\\s*.*$)";
  private final int indentation;
  private final boolean isDescribe;
  private final int endOffset;
  private final int startOffset;
  private final int lineNumber;
  private String testText;
  private TestState testState;
  private TestState pendingChangeState;
  public static final Pattern INDENTATION_PATTERN =
      Pattern.compile("xit|fit|iit|it|xdescribe|fdescribe|ddescribe|describe");

  public TestFindResult(Document document, FindResult findResult) {
    endOffset = findResult.getEndOffset();

    int lineNumber = document.getLineNumber(endOffset);
    int startOfLine = document.getLineStartOffset(lineNumber);
    int endOfLine = document.getLineEndOffset(lineNumber);
    this.lineNumber = lineNumber + 1;

    String lineText = document.getText(new TextRange(startOfLine, endOfLine));
    isDescribe = lineText.matches("\\s*[xdf]?describe.*");

    if (lineText.matches("\\s*([fd]describe|[fi]it).*")) {
      testState = TestState.Included;
    } else if (lineText.matches("\\s*(xdescribe|xit).*")) {
      testState = TestState.Excluded;
    } else {
      testState = TestState.NotModified;
    }

    // Leave the Test text.
    // TODO: improve this regular expression.
    testText = lineText.replaceAll(REMOVE_END_OF_LINE, "$1");
    testText = testText.replaceAll(REMOVE_START_OF_LINE, "$2");

    // Calculate the indentation.
    int indentation = 0;
    Matcher matcher = INDENTATION_PATTERN.matcher(lineText);
    if (matcher.find()) {
      indentation = matcher.start();
    }
    this.indentation = indentation;
    this.startOffset = startOfLine + indentation;
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

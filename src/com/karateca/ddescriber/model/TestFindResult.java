package com.karateca.ddescriber.model;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public interface TestFindResult {

  int getIndentation();

  boolean isDescribe();

  boolean isMarkedForRun();

  int getEndOffset();

  int getStartOffset();

  int getLineNumber();

  String getTestText();
}

package com.karateca.ddescriber;

import com.karateca.ddescriber.model.JasmineFile;

/**
 * @author Andres Dominguez.
 */
public class TestChangeEvent {
  private final JasmineFile jasmineFile;
  private ChangeType changeType;

  private TestChangeEvent(JasmineFile jasmineFile, ChangeType changeType) {
    this.jasmineFile = jasmineFile;
    this.changeType = changeType;
  }

  public static TestChangeEvent newAddEvent(JasmineFile jasmineFile) {
    return new TestChangeEvent(jasmineFile, ChangeType.testAdded);
  }

  public static TestChangeEvent newCleanEvent(JasmineFile jasmineFile) {
    return new TestChangeEvent(jasmineFile, ChangeType.testCleaned);
  }

  public JasmineFile getJasmineFile() {
    return jasmineFile;
  }

  public ChangeType getChangeType() {
    return changeType;
  }

  public enum ChangeType {
    testAdded,
    testCleaned
  }
}

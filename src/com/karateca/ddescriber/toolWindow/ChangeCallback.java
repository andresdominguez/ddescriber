package com.karateca.ddescriber.toolWindow;

import com.karateca.ddescriber.model.JasmineFile;

/**
 * @author Andres Dominguez.
 */
public interface ChangeCallback {
  void contentsChanged(JasmineFile jasmineFile);
}

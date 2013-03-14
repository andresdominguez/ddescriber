package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.karateca.ddescriber.BaseTestCase;

import java.io.IOException;

/**
 * @author Andres Dominguez.
 */
public class VirtualFileListenerTest extends BaseTestCase {
  public void testListenForChanges() throws Exception {
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    final boolean[] fileWasChanged = {false};

    VirtualFileListener virtualFileListener = new VirtualFileListener();

    virtualFileListener.registerForChangeEvent(virtualFile, new ChangeCallback() {
      @Override
      public void contentsChanged() {
        fileWasChanged[0] = true;
      }
    });

    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        try {
          virtualFile.setBinaryContent(("describe('file changed', function () {\n" +
              "    it('should have changed', function () {\n" +
              "        \n" +
              "    });" +
              "\n});" +
              "\n").getBytes());

          assertTrue(fileWasChanged[0]);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}

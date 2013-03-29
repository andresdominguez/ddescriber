package com.karateca.ddescriber;

import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.JasmineFileImpl;

import org.junit.Test;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Andres Dominguez.
 */
public class JasmineDescriberNotifierTest extends BaseTestCase {

  private JasmineDescriberNotifier instance;
  private JasmineFile jasmineFile;

  public void setUp() throws Exception {
    super.setUp();

    instance = JasmineDescriberNotifier.getInstance();
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    jasmineFile = new JasmineFileImpl(getProject(), virtualFile);
  }

  @Test
  public void testTestWasChanged() throws Exception {
    final JasmineFile[] changedFile = {null};

    instance.addTestChangedLister(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        changedFile[0] = (JasmineFile) changeEvent.getSource();
      }
    });

    instance.testWasChanged(jasmineFile);

    assertNotNull(changedFile[0]);
  }
}

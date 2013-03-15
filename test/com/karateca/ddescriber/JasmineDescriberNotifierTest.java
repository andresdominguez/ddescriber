package com.karateca.ddescriber;

import com.karateca.ddescriber.model.JasmineFile;
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
    jasmineFile = new JasmineFile(getProject(), virtualFile);
  }

  @Test
  public void testBroadcastWhenTestWasAdded() throws Exception {
    final TestChangeEvent.ChangeType[] changeType = new TestChangeEvent.ChangeType[1];

    instance.addTestChangedLister(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        TestChangeEvent evt = (TestChangeEvent) changeEvent.getSource();
        changeType[0] = evt.getChangeType();
      }
    });

    instance.testWasAdded(jasmineFile);

    assertEquals(TestChangeEvent.ChangeType.testAdded, changeType[0]);
  }

  @Test
  public void testBroadcastWhenTestWasCleaned() throws Exception {
    final TestChangeEvent.ChangeType[] changeType = new TestChangeEvent.ChangeType[1];

    instance.addTestChangedLister(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        TestChangeEvent evt = (TestChangeEvent) changeEvent.getSource();
        changeType[0] = evt.getChangeType();
      }
    });

    instance.testWasCleaned(jasmineFile);

    assertEquals(TestChangeEvent.ChangeType.testCleaned, changeType[0]);
  }
}

package com.karateca.ddescriber;

import com.intellij.util.EventDispatcher;
import com.karateca.ddescriber.model.JasmineFile;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Andres Dominguez.
 */
public class JasmineDescriberNotifier {

  private final EventDispatcher<ChangeListener> myEventDispatcher = EventDispatcher.create(ChangeListener.class);
  private static JasmineDescriberNotifier appInstance;

  private JasmineDescriberNotifier() {
  }

  public void a(){
    ChangeEvent event = new ChangeEvent("LinesFound");
    myEventDispatcher.getMulticaster().stateChanged(event);
  }

  public static JasmineDescriberNotifier getInstance() {
    if (appInstance == null) {
      appInstance = new JasmineDescriberNotifier();
    }

    return appInstance;
  }

  /**
   * Register for change events.
   *
   * @param changeListener The listener to be added.
   */
  public void addTestChangedLister(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }

  public void testWasAdded(JasmineFile jasmineFile) {
    ChangeEvent event = new ChangeEvent(TestChangeEvent.newAddEvent(jasmineFile));
    myEventDispatcher.getMulticaster().stateChanged(event);
  }

  public void testWasCleaned(JasmineFile jasmineFile) {
    ChangeEvent event = new ChangeEvent(TestChangeEvent.newCleanEvent(jasmineFile));
    myEventDispatcher.getMulticaster().stateChanged(event);
  }
}

package com.karateca.ddescriber.toolWindow;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class AllTestsPanelTest {

  @Test
  public void shouldCreateInternalPanels() {
    AllTestsPanel panel = new AllTestsPanel();

    Assert.assertNotNull(panel.topButtonPanel);
    Assert.assertNotNull(panel.panelWithCurrentTests);
    Assert.assertNotNull(panel.leftButtonPanel);
  }
}

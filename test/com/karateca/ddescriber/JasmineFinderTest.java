package com.karateca.ddescriber;

/**
 * @author Andres Dominguez.
 */
public class JasmineFinderTest extends BaseTestCase {

  public void testFindAll() throws Exception {
    // Given that you have a jasmine test file.
    JasmineFinder jasmineFinder = createJasmineFinder();

    // When you find all the matches.
    jasmineFinder.findAll();

    // Then ensure the unit tests and the suites were found.
    assertEquals(7, jasmineFinder.getFindResults().size());
  }
}

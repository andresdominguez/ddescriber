package com.karateca.ddescriber;

/**
 * Represents the jasmine syntax.
 */
public enum JasmineSyntax {
  Version1("ddescribe", "iit"),
  Version2("fdescribe", "fit");

  String includeDescribe;
  String includeIt;

  JasmineSyntax(String includeDescribe, String includeIt) {
    this.includeDescribe = includeDescribe;
    this.includeIt = includeIt;
  }
}

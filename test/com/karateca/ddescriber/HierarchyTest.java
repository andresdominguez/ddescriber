package com.karateca.ddescriber;

import com.intellij.find.FindResult;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andres Dominguez.
 */
public class HierarchyTest extends LightCodeInsightFixtureTestCase {

  private DocumentImpl doc;
  private String docString = "describe('top describe', function () {\n" +
          "    it('first it', function () {\n" +
          "        console.log('WWWW');\n" +
          "    });\n" +
          "\n" +
          "    it('second it', function () {\n" +
          "        console.log('WWWW');\n" +
          "    });\n" +
          "\n" +
          "    describe('second describe', function () {\n" +
          "        it('inner it 1', function () {\n" +
          "            var a;\n" +
          "        });\n" +
          "\n" +
          "        it('inner it 2', function () {\n" +
          "            var a;\n" +
          "        });\n" +
          "\n" +
          "        iit('inner it 3', function () {\n" +
          "            var a;\n" +
          "        });\n" +
          "    });\n" +
          "});\n";
  private List<FindResult> results;

  @Override
  protected String getTestDataPath() {
    String testPath = PathManager.getJarPathForClass(HierarchyTest.class);
    File sourceRoot = new File(testPath, "../../..");
    return new File(sourceRoot, "testData").getPath();
  }

  void configureTest() throws Exception {
    PsiFile psiFile = myFixture.configureByFile("jasmineTestBefore.js");
    String text = psiFile.getOriginalFile().getText();
    doc = new DocumentImpl(text);

    results = new ArrayList<FindResult>();
    results.add(getFindResult("describe\\('top"));
    results.add(getFindResult("it\\('first"));
    results.add(getFindResult("it\\('second"));
    results.add(getFindResult("describe\\('second"));
    results.add(getFindResult("it\\('inner it 1"));
    results.add(getFindResult("it\\('inner it 2"));
    results.add(getFindResult("it\\('inner it 3"));
  }

  FindResult getFindResult(String searchString) {
    Matcher matcher = Pattern.compile(searchString).matcher(docString);
    matcher.find();
    return new FindResult(matcher.start(), matcher.end()) {
      @Override
      public boolean isStringFound() {
        return true;
      }
    };
  }

  @Test
  public void testFindClosest() throws Exception {
    configureTest();

    // Given that the current caret position is after inner it 2.
    int caretPosition = myFixture.getCaretOffset();
    Hierarchy hierarchy = new Hierarchy(doc, results, caretPosition);

    // When you get the closest.
    TestFindResult closest = hierarchy.getClosest();

    // Then ensure the closest is inner it 2.
    int startOffset = results.get(5).getStartOffset();
    Assert.assertEquals(startOffset, closest.getStartOffset());
  }
}

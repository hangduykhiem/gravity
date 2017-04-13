package org.zalando.core.utils;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for {@link EqualUtils} utility class
 *
 * Created by jduran on 12/01/16.
 */
public class EqualUtilsTest {

  @Test
  public void testEqualsNull() {

    assertTrue(EqualUtils.areEqual(null, null));
  }

  @Test
  public void testEqualsBasicTypes() {

    // Compare basic integer
    assertTrue(EqualUtils.areEqual(1, 1));
    assertFalse(EqualUtils.areEqual(1, 2));
    // Compare String
    assertTrue(EqualUtils.areEqual("String", "String"));
    assertFalse(EqualUtils.areEqual("String", "anotherString"));
  }

  @Test
  public void testEqualsObjects() {

    // Compare custom class
    assertTrue(EqualUtils.areEqual(new TestClass("String"), new TestClass("String")));
    assertFalse(EqualUtils.areEqual(new TestClass("String"), new TestClass("AnotherString")));
  }

  private class TestClass {

    private final String textToCompare;

    public TestClass(String textToCompare) {

      this.textToCompare = textToCompare;
    }


    public String getTextToCompare() {
      return textToCompare;
    }

    @Override
    public boolean equals(Object other) {

      if (other instanceof TestClass) {
        TestClass otherTestClass = (TestClass) other;
        return EqualUtils.areEqual(getTextToCompare(), otherTestClass.getTextToCompare());
      }
      return false;
    }
  }

}

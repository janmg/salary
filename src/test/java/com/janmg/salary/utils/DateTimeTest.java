package com.janmg.salary.utils;

import static org.junit.Assert.fail;
import org.junit.Test;

public class DateTimeTest {

    @Test
    public void PositiveOnlyTestNegative() {
        DateTime dt = new DateTime();
        for (int i=-1440;i<=0;i++) {
            if (dt.positiveOnly(i) != 0) fail("Negative numbers should always be zero");
        }
    }

    @Test
    public void PositiveOnlyTestPositive() {
        DateTime dt = new DateTime();
        for (int i=0;i<=1440;i++) {
            if (dt.positiveOnly(i) != i) fail("Positive should always be themselves");
        }
    }
}

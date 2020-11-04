package org.wrkr.clb.test.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.wrkr.clb.services.util.exception.ApplicationException;

public class JsonStatusCodeMatcher extends TypeSafeMatcher<ApplicationException> {

    private final String expectedCode;
    private String foundCode;

    private JsonStatusCodeMatcher(String expectedCode) {
        this.expectedCode = expectedCode;
    }

    public static JsonStatusCodeMatcher hasCode(String code) {
        return new JsonStatusCodeMatcher(code);
    }

    @Override
    protected boolean matchesSafely(final ApplicationException exception) {
        foundCode = exception.getJsonStatusCode();
        return foundCode.equalsIgnoreCase(expectedCode);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(foundCode).appendText(" was not found instead of ").appendValue(expectedCode);
    }
}

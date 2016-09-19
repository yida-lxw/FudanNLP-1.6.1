package org.fnlp.app.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public abstract class FilteringTokenFilter extends TokenFilter {

    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    // no init needed, as ctor enforces setting value!
    private boolean enablePositionIncrements;

    public FilteringTokenFilter(boolean enablePositionIncrements, TokenStream input) {
        super(input);
        this.enablePositionIncrements = enablePositionIncrements;
    }

    /**
     * Override this method and return
     * if the current input token should be
     * returned by {@link #incrementToken}.
     */
    protected abstract boolean accept() throws IOException;

    @Override
    public final boolean incrementToken() throws IOException {
        if (enablePositionIncrements) {
            int skippedPositions = 0;
            while (input.incrementToken()) {
                if (accept()) {
                    if (skippedPositions != 0) {
                        posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
                    }
                    return true;
                }
                skippedPositions += posIncrAtt.getPositionIncrement();
            }
        } else {
            while (input.incrementToken()) {
                if (accept()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getEnablePositionIncrements() {
        return enablePositionIncrements;
    }

    public void setEnablePositionIncrements(boolean enable) {
        this.enablePositionIncrements = enable;
    }
}
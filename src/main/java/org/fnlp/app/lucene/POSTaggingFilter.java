package org.fnlp.app.lucene;

import edu.fudan.nlp.cn.Tags;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public final class POSTaggingFilter extends FilteringTokenFilter {
    private final POSAttribute posAtt = addAttribute(POSAttribute.class);


    public POSTaggingFilter(boolean enablePositionIncrements, TokenStream in) {
        super(enablePositionIncrements, in);
    }

    public POSTaggingFilter(TokenStream in) {
        super(true, in);
    }

    @Override
    public boolean accept() throws IOException {
        String pos = posAtt.getPartOfSpeech();
        return !Tags.isStopword(pos);
    }
}
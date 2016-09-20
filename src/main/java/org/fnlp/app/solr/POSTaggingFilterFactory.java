package org.fnlp.app.solr;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.fnlp.app.lucene.POSTaggingFilter;

import java.util.Map;

/**
 * Created by Lanxiaowei
 * POSTaggingFilter过滤器的工厂类
 */
public class POSTaggingFilterFactory extends TokenFilterFactory {
    /**是否启用位置增量*/
    private boolean enablePositionIncrements;

    protected POSTaggingFilterFactory(Map<String, String> args) {
        super(args);
        this.enablePositionIncrements = getBoolean(args, "enable_pos_increment",true);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new POSTaggingFilter(this.enablePositionIncrements,tokenStream);
    }
}

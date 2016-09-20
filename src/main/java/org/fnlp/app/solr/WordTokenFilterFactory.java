package org.fnlp.app.solr;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.fnlp.app.lucene.WordTokenFilter;

import java.util.Map;

/**
 * Created by Lanxiaowei
 * WordTokenFilter的工厂类
 */
public class WordTokenFilterFactory extends TokenFilterFactory {
    /**用户自定义停用字典文件或目录的加载路径*/
    private String stopwordPath;

    protected WordTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.stopwordPath = get(args, "stopwords");
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new WordTokenFilter(tokenStream,stopwordPath);
    }
}

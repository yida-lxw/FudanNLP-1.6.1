package org.fnlp.app.lucene.test;

import edu.fudan.nlp.cn.CNFactory;
import org.apache.lucene.analysis.Analyzer;
import org.fnlp.app.lucene.FudanNLPAnalyzer;

/**
 * Created by Lanxiaowei
 * FudanNLP分词器简单测试
 */
public class FNLPAnalyzerTest {
    public static void main(String[] args) throws Exception {
        String text = "嘿嘿，赶早不赶晚 刚睡一会 哈哈哈";
        CNFactory.getInstance("models", CNFactory.Models.SEG_TAG);
        Analyzer analyzer = new FudanNLPAnalyzer();
        AnalyzerUtils.displayTokens(analyzer,text);
    }
}

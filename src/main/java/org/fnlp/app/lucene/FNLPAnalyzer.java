package org.fnlp.app.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.util.exception.LoadModelException;
/**
 * 基于FudanNLP的Lucene分词器
 * 兼容Lucene5.x
 * @author xpqiu
 */
public final class FNLPAnalyzer extends Analyzer {
	/**是否使用词性过滤器*/
	private boolean usePOSFilter;

	/**
	 * 指定CNFactory路径
	 * @param path CNFactory路径
	 * @param usePOSFilter 使用词性作为停用词过滤
	 * @throws LoadModelException 
	 */
	public FNLPAnalyzer(String path,boolean usePOSFilter) throws LoadModelException {
		CNFactory.getInstance(path,Models.SEG_TAG);
		this.usePOSFilter = usePOSFilter;
	}
	/**
	 * 指定CNFactory路径
	 * @param path
	 * @throws LoadModelException 
	 */
	public FNLPAnalyzer(String path) throws LoadModelException {
		CNFactory.getInstance(path,Models.SEG_TAG);
	}

	/**
	 * 需要预先建立CNFactory
	 */
	public FNLPAnalyzer() {
		this.usePOSFilter = true;
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new SentenceTokenizer();
		TokenStream result = new WordTokenFilter(tokenizer);
		if(this.usePOSFilter) {
			result = new POSTaggingFilter(true,result);
		}
		return new TokenStreamComponents(tokenizer, result);
	}
}

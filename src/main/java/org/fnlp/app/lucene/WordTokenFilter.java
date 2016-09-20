package org.fnlp.app.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.fudan.nlp.corpus.StopWords;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.cn.tag.POSTagger;

public final class WordTokenFilter extends TokenFilter {

	private Iterator<String> tokenIter;
	private List<String> tokenBuffer;
	
	private Iterator<String> posIter;
	private List<String> posBuffer;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final POSAttribute posAtt = addAttribute(POSAttribute.class);
	
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	private int tokStart; // only used if the length changed before this filter
	private int tokEnd; // only used if the length changed before this filter
	private boolean hasIllegalOffsets; // only if the length changed before this filter
	private int idx=0;

	private CNFactory factory;

	/**停用词处理类*/
	private StopWords stopWords;

	public WordTokenFilter(TokenStream in) {
		super(in);
		this.factory = CNFactory.getInstance();
	}

	public WordTokenFilter(TokenStream in,String stopwordPath) {
		super(in);
		this.factory = CNFactory.getInstance();
		this.stopWords = new StopWords(stopwordPath);
	}

	@Override
	public boolean incrementToken() throws IOException {
		// WordTokenFilter must clear attributes, as it is creating new tokens.
		clearAttributes();
		if (tokenIter == null || !tokenIter.hasNext()) {
			// there are no remaining tokens from the current sentence... are there more sentences?
			if (input.incrementToken()) {
				tokStart = offsetAtt.startOffset();
				tokEnd = offsetAtt.endOffset();
				// if length by start + end offsets doesn't match the term text then assume
				// this is a synonym and don't adjust the offsets.
				hasIllegalOffsets = (tokStart + termAtt.length()) != tokEnd;
				// a new sentence is available: process it.
				String[] w = factory.seg(termAtt.toString());
				String[] p = factory.tag(w);
				tokenBuffer = Arrays.asList(w);
				if(null != p) {
					posBuffer = Arrays.asList(p);
					posIter = posBuffer.iterator();
				}
				tokenIter = tokenBuffer.iterator();

				idx = 0;
				/* 
				 * it should not be possible to have a sentence with 0 words, check just in case.
				 * returning EOS isn't the best either, but its the behavior of the original code.
				 */
				if (!tokenIter.hasNext()) {
					return false;
				}
			} else {
				return false; // no more sentences, end of stream!
			}
		} 

		// There are remaining tokens from the current sentence, return the next one. 
		String nextWord = tokenIter.next();
		//如果停用词字典不为空，才进行停用词过滤
		if(null != stopWords && null != stopWords.getsWord()
				&& stopWords.getsWord().size() > 0) {
			//如果是停用词，则过滤掉
			if(stopWords.isStopWord(nextWord)) {
				return false;
			}
		}

		termAtt.append(nextWord);
		if(null != posIter) {
			String pos = posIter.next();
			posAtt.setPartOfSpeech(pos);
		}
		int end = idx+nextWord.length();
		if (hasIllegalOffsets) {
			offsetAtt.setOffset(tokStart, tokEnd);
		} else {
			offsetAtt.setOffset(idx, end-1);
		}
		idx = end;
		typeAtt.setType("word");
		return true;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		tokenIter = null;
	}
}

package org.fnlp.app.lucene.test;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.util.exception.LoadModelException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.fnlp.app.lucene.FudanNLPAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

import static edu.fudan.nlp.cn.CNFactory.ambiguity;

public class Search {
	public static void main(String[] args) throws IOException, ParseException, LoadModelException {
		String indexPath = "C:/luceneDir/index/";
		System.out.println("Index directory '" + indexPath);
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		//需要先初始化 CNFactory
		CNFactory factory = CNFactory.getInstance("models",Models.SEG_TAG,true);
		Analyzer analyzer = new FudanNLPAnalyzer();

		DirectoryReader ireader = DirectoryReader.open(dir);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		QueryParser parser = new QueryParser("content", analyzer);
		Query query = parser.parse("content:嘿*");
		ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;

		// 遍历查询结果
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("content"));
			System.out.println(hits[i].score);
		}
		ireader.close();
		dir.close();
	}
}

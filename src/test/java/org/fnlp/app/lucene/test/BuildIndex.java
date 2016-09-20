package org.fnlp.app.lucene.test;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.util.exception.LoadModelException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.fnlp.app.lucene.FNLPAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

/**
 * 使用FudanNLP分词器创建索引的示例
 */
public class BuildIndex {
	public static void main(String[] args) throws IOException, LoadModelException {
		System.setProperty("file.encoding","UTF-8");
		String indexPath = "C:/luceneDir/index/";
		System.out.println("Indexing to directory '" + indexPath  + "'...");
		Date start = new Date();
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		//需要先初始化 CNFactory
		CNFactory.getInstance("models",Models.SEG_TAG);
		Analyzer analyzer = new FNLPAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = new IndexWriter(dir, iwc);

		String[] strs = new String[]{
				"终端的保修期为一年。",
				"凡在保修期内非人为损坏，均可免费保修。",
				"人为损坏的终端将视情况收取维修费用。",
				"嘿嘿，赶早不赶晚"
		};
		for(int i=0;i<strs.length;i++){
			Document doc = new Document();
			Field field = new TextField("content", strs[i] , Field.Store.YES);
			doc.add(field);
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("content",strs[i]), doc);
			}
		}
		writer.commit();
		writer.close();

		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	}
}

package org.com.lucene.main;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class IndexUtil {

	private String[] ids = {"1","2","3","4","5","6"};// 邮件ID
	// 邮箱地址记录
	private String[] emails = {"aa@itat.org","bb@itat.org","cc@cc.org","dd@sina.org","ee@qq.org","ff@itat.org"};
	// 邮件内容
	private String[] contents = {
			
			"welcome to visit the Lucene",
			"welcome to visit China",
			"welcome to beijing",
			"welcome to Lucene world",
			"you are good boy",
			"do you like swiming"
	};
	
	private int[] attachs = {2,3,1,4,5,5};// 附件
	private String[] names = {"zhangsan","lisi","your","joe","mike","jack"};
	
	private Directory directory = null;
	private String filePath = "F:/Lucence";
	public IndexUtil() {
		try {
			directory = FSDirectory.open(new File(filePath + "/index01"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 创建索引
	public void index() {
		
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			Document doc = null;
			for(int i =0; i < ids.length; i++) {
				doc = new Document();
				doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("email", emails[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", contents[i], Field.Store.NO, Field.Index.ANALYZED));
				doc.add(new Field("name", names[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				writer.addDocument(doc);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null)writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
	}
	
	// 查询
	public void query() {
		try {
			IndexReader reader = IndexReader.open(directory);
			System.out.println("numDocs:" + reader.numDocs());
			System.out.println("maxDocs:" + reader.maxDoc());
			reader.close();

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

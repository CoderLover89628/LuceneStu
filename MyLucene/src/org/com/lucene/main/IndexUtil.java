package org.com.lucene.main;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class IndexUtil {

	private String[] ids = { "1", "2", "3", "4", "5", "6" };// 邮件ID
	// 邮箱地址记录
	private String[] emails = { "aa@itat.org", "bb@itat.org", "cc@cc.org",
			"dd@sina.org", "ee@qq.org", "ff@itat.org" };
	// 邮件内容
	private String[] contents = {

	"welcome to visit the Lucene", "welcome to visit China",
			"welcome to beijing", "welcome to Lucene world",
			"you are good boy", "do you like swiming" };

	private int[] attachs = { 2, 3, 1, 4, 5, 5 };// 附件
	private String[] names = { "zhangsan", "lisi", "your", "joe", "mike",
			"jack" };

	private Directory directory = null;
	private String filePath = "E:/MyWorkSpace/Lucence";

	// 加权操作 将itat.org结尾的邮箱，权值设置高一点
	private Map<String, Float> scores = new HashMap<String, Float>();
	
	private Date[] dates = null;
	
	// 将reader设置成单例
	private static IndexReader reader = null;

	public IndexUtil() {
		try {
			getDate();
			scores.put("itat.org", 2.0f);
			directory = FSDirectory.open(new File(filePath + "/index01"));
			
			reader = IndexReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IndexSearcher getSearcher() {
			try {
				if(reader == null) {
				
					reader = IndexReader.open(directory);
				} else {
					IndexReader tr = IndexReader.openIfChanged(reader);//此处的reader为旧的reader
					if(tr != null) {
						reader.close();// 关掉旧的reader
						reader = tr;// 赋值新的reader
					}
				}
				return new IndexSearcher(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	private void getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dates = new Date[ids.length];
		try {
			dates[0] = sdf.parse("2012-07-30");
			dates[1] = sdf.parse("2013-08-30");
			dates[2] = sdf.parse("2014-09-30");
			dates[3] = sdf.parse("2015-10-30");
			dates[4] = sdf.parse("2016-11-30");
			dates[5] = sdf.parse("2017-12-30");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private String parseTimeToDate(String time) {
		
		long tt = Long.valueOf(time);
		Date date = new Date(tt);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return date == null?"":sdf.format(date);
		
	}

	// 创建索引
	public void index() {

		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.deleteAll();// 在添加索引前，先清空所有的索引
			Document doc = null;
			for (int i = 0; i < ids.length; i++) {
				doc = new Document();
				doc.add(new Field("id", ids[i], Field.Store.YES,
						Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("email", emails[i], Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", contents[i], Field.Store.NO,
						Field.Index.ANALYZED));
				doc.add(new Field("name", names[i], Field.Store.YES,
						Field.Index.NOT_ANALYZED_NO_NORMS));

				// 对数字和日期进行加索引，使用NumericField
				doc.add(new NumericField("attach", Field.Store.YES, true).setIntValue(attachs[i]));
				doc.add(new NumericField("date", Field.Store.YES, true).setLongValue(dates[i].getTime()));
				
				String et = emails[i].substring(emails[i].lastIndexOf("@") + 1);
				System.out.println(et);
				// 索引的时候加权
				if (scores.containsKey(et)) {
					doc.setBoost(scores.get(et));
				} else {
					doc.setBoost(0.5f);
				}
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
				if (writer != null)
					writer.close();
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
			System.out.println("deleteDocs:" + reader.numDeletedDocs());
			reader.close();

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 删除索引
	public void delete() {
		// 通过IndexWriter来删除
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			// 该处的参数可以是一个Query,也可以是一个Term,Term是一个精确的值
			// 此时删除的文档并不会被完全的删除，而是存储在一个【.del】文件中，可以理解为电脑中的回收站，
			// 所以可以被恢复
			writer.deleteDocuments(new Term("id", "4"));// 删除id = 4的文档
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// 恢复删除的文档
	public void undelete() {
		// 通过IndexReader来进行恢复
		try {
			IndexReader reader = IndexReader.open(directory, false);// 此处需要手动设置IndexReader的readonly为false
			reader.undeleteAll();
			reader.close();
		} catch (StaleReaderException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 强制删除已删除的文档
	public void forceDelete() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.forceMergeDeletes();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 合并
	public void merge() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			// 会将索引合并为2段，这两段中的被删除的数据会被清空
			// 特别注意：此处lucene在3.5之后不建议使用，因为会产生大量的开销，lucene会根据情况自动的处理的
			writer.forceMerge(2);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 索引的更新
	public void update() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			/**
			 * lucene的更新过程是先删除，然后再添加
			 */
			Document doc = new Document();
			doc.add(new Field("id", "1111", Field.Store.YES,
					Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("email", emails[1], Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("content", contents[1], Field.Store.NO,
					Field.Index.ANALYZED));
			doc.add(new Field("name", names[1], Field.Store.YES,
					Field.Index.NOT_ANALYZED_NO_NORMS));
			writer.updateDocument(new Term("id", "1"), doc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检索，没有使用reader单例情况
	 */
	public void search() {
		try {
			IndexReader reader = IndexReader.open(directory);
			// 根据IndexReader创建IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			TermQuery tq = new TermQuery(new Term("content","welcome"));
			TopDocs tds = searcher.search(tq, 10);
			for(ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("(" + sd.doc + ")" + doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id"));
			}
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 检索，使用reader单例情况
	 */
	public void search01() {
		try {
			IndexSearcher searcher = getSearcher();
			TermQuery tq = new TermQuery(new Term("content","welcome"));
			TopDocs tds = searcher.search(tq, 10);
			for(ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("(" + sd.doc + ")" + doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "------" + doc.get("attach") + ":" + parseTimeToDate(doc.get("date")));
			}
			searcher.close();//此处不能使用reader.close()方法，因为reader是单例，close后将关闭检索，抛出异常
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

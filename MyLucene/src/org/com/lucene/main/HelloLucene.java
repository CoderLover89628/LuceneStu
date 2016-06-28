package org.com.lucene.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 * lucene初学
 * 
 * @author JZ
 *
 */
public class HelloLucene {

	private String filePath = "F:/Lucence";
	
	/**
	 * 建立索引
	 * 
	 * 过程：
	 * 1.创建Directory两种方式：
	 *  					①创建在内存中：Directory directoryRAM = new RAMDirectory();
	 *                      ②创建在硬盘上：Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// 建立在硬盘上
	 * 2.创建IndexWriter:以此来写索引
	 * 
	 * 3.创建Document:相当于数据库中，表的一条记录
	 * 
	 * 4.为Document添加Field:Field相当于表中的一个字段
	 * 						对Field.Store.YES或NO的说明（存储域选项）：
	 * 						①设置为YES时，表示把这个域中的内容完全的存储到文件（lucene文件）中，方便进行文本的还原，即，查找时，直接从lucene文件中查询
	 * 						②设置为NO时，表示不把这个域中的内容存储到文件中，但是可以被索引
	 * 						对Field.Index的说明（索引选项）：
	 * 						①Index.ANALYZED:进行分词和索引，适应于标题、内容等
	 * 						②Index.NOT_ANALYZED:进行索引，但不进行分词，如果身份证号码，姓名，ID等，适用于精确搜索
	 * 						③Index.ANALYZED_NOT_NORMS:进行分词但是不存储norms信息，这个norms中包括了创建索引的时间和权值等信息
	 * 						④Index.NOT_ANALYZED_NOT_NORMS:既不分词也不进行norms信息存储
	 * 						⑤Index.NO不进行索引
	 * 
	 * 5.通过IndexWriter添加文档到索引中
	 * 
	 * 6.关闭indexWriter
	 * 
	 * 路径：
	 * 将F:/Lucenc目录下的文档建立索引
	 * 
	 * 注意点：
	 * 建立索引的路径，一定不能是文件存在的路径，即，下面的input的路径（文档存在的路径），
	 * 否则会出现[java.io.FileNotFoundException: F:\Lucence\input\index (拒绝访问。)]
	 * 原因：建立索引需要在文件下进行写的操作，遍历文件时会出错
	 * 
	 */
	public void index() {
		
		IndexWriter writer = null;
		try {
			
			// 创建Directory
			//Directory directoryRAM = new RAMDirectory();//建立在内存中
			Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// 建立在硬盘上,注意点：建立索引的路径一定不能是文件存在的路径
			// 即，下面的input的路径，否则会出现[java.io.FileNotFoundException: F:\Lucence\input\index (拒绝访问。)]
			// 原因：建立索引需要在文件下进行读的操作，遍历文件时会出错
			
			// 创建IndexWriter
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
			writer = new IndexWriter(directoryFSD, iwc);
			
			// 创建Document
			Document doc = null;
			
			// 为Document添加Field
			File file = new File(filePath + "/input");
			for(File f : file.listFiles()) {
				doc = new Document();
				doc.add(new Field("content", new FileReader(f)));
				doc.add(new Field("fileName", f.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path", f.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				
				// 通过IndexWriter添加文档到索引中
				writer.addDocument(doc);
			}
		} catch (CorruptIndexException e) {

			e.printStackTrace();
		} catch (LockObtainFailedException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if(writer != null)
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
		}
		
	}
	
	/**
	 *  创建搜索
	 *  
	 *  过程：
	 *  1.创建Directory:两种方式：
	 *  					①创建在内存中：Directory directoryRAM = new RAMDirectory();
	 *                      ②创建在硬盘上：Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// 建立在硬盘上
	 *  2.创建IndexReader
	 *  3.根据IndexReader创建IndexSearcher
	 *  4.根据QueryParser创建搜索的Query：两步：
	 *  								①创建QueryParser对象，确定搜索的内容：
	 *  									QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
	 *  								②创建Query对象，表示域为content中包含“mvn”的文档：Query query = queryParser.parse("mvn");
	 *  5.根据Searcher搜索并返回TopDocs的文档
	 *  6.根据TopDocs获取ScoreDoc对象
	 *  7.根据searcher和ScoreDoc对象获取具体的Document对象
	 *  8.根据Document对象获取所需要的值
	 *  9.关闭reader
	 */
	public void search() {
		
		try {
			// 创建Directory
			Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// 建立在硬盘上
			
			// 创建IndexReader
			IndexReader reader = IndexReader.open(directoryFSD);
			
			// 根据IndexReader创建IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			
			// 根据QueryParser创建搜索的Query
			QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
			Query query = queryParser.parse("int");
			
			// 根据Searcher搜索并返回TopDocs的文档
			TopDocs tds = searcher.search(query, 10);//只出现10条记录
			
			// 根据TopDocs获取ScoreDoc对象
			ScoreDoc[] sds = tds.scoreDocs;
			for(ScoreDoc sd : sds) {
				// 根据searcher和ScoreDoc对象获取具体的Document对象
				Document doc = searcher.doc(sd.doc);
				// 根据Document对象获取所需要的值
				System.out.println("文件名：" + doc.get("fileName") + "\n" + "路径：" + doc.get("path"));
			}
			
			// 关闭reader
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}
}

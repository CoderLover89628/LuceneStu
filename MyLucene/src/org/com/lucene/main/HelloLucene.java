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
 * lucene��ѧ
 * 
 * @author JZ
 *
 */
public class HelloLucene {

	private String filePath = "F:/Lucence";
	
	/**
	 * ��������
	 * 
	 * ���̣�
	 * 1.����Directory���ַ�ʽ��
	 *  					�ٴ������ڴ��У�Directory directoryRAM = new RAMDirectory();
	 *                      �ڴ�����Ӳ���ϣ�Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// ������Ӳ����
	 * 2.����IndexWriter
	 * 3.����Document:�൱�����ݿ��У����һ����¼
	 * 4.ΪDocument���Field:Field�൱�ڱ��е�һ���ֶ�
	 * 						��Field.Store.YES��NO��˵�����洢��ѡ���
	 * 						������ΪYESʱ����ʾ��������е�������ȫ�Ĵ洢���ļ���lucene�ļ����У���������ı��Ļ�ԭ����������ʱ��ֱ�Ӵ�lucene�ļ��в�ѯ
	 * 						������ΪNOʱ����ʾ����������е����ݴ洢���ļ��У����ǿ��Ա�����
	 * 						��Field.Index��˵��������ѡ���
	 * 						��Index.ANALYZED:���зִʺ���������Ӧ�ڱ��⡢���ݵ�
	 * 						��Index.NOT_ANALYZED:�����������������зִʣ�������֤���룬������ID�ȣ������ھ�ȷ����
	 * 						��Index.ANALYZED_NOT_NORMS:���зִʵ��ǲ��洢norms��Ϣ�����norms�а����˴���������ʱ���Ȩֵ����Ϣ
	 * 						��Index.NOT_ANALYZED_NOT_NORMS:�Ȳ��ִ�Ҳ������norms��Ϣ�洢
	 * 						��Index.NO����������
	 * 5.ͨ��IndexWriter����ĵ���������
	 * 6.�ر�indexWriter
	 * 
	 * ·����
	 * ��F:/LucencĿ¼�µ��ĵ���������
	 * 
	 * ע��㣺
	 * ����������·����һ���������ļ����ڵ�·�������������input��·�����ĵ����ڵ�·������
	 * ��������[java.io.FileNotFoundException: F:\Lucence\input\index (�ܾ����ʡ�)]
	 * ԭ�򣺽���������Ҫ���ļ��½���д�Ĳ����������ļ�ʱ�����
	 * 
	 */
	public void index() {
		
		IndexWriter writer = null;
		try {
			
			// ����Directory
			//Directory directoryRAM = new RAMDirectory();//�������ڴ���
			Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// ������Ӳ����,ע��㣺����������·��һ���������ļ����ڵ�·��
			// ���������input��·������������[java.io.FileNotFoundException: F:\Lucence\input\index (�ܾ����ʡ�)]
			// ԭ�򣺽���������Ҫ���ļ��½��ж��Ĳ����������ļ�ʱ�����
			
			// ����IndexWriter
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
			writer = new IndexWriter(directoryFSD, iwc);
			
			// ����Document
			Document doc = null;
			
			// ΪDocument���Field
			File file = new File(filePath + "/input");
			for(File f : file.listFiles()) {
				doc = new Document();
				doc.add(new Field("content", new FileReader(f)));
				doc.add(new Field("fileName", f.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path", f.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				
				// ͨ��IndexWriter����ĵ���������
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
	 *  ��������
	 *  
	 *  ���̣�
	 *  1.����Directory:���ַ�ʽ��
	 *  					�ٴ������ڴ��У�Directory directoryRAM = new RAMDirectory();
	 *                      �ڴ�����Ӳ���ϣ�Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// ������Ӳ����
	 *  2.����IndexReader
	 *  3.����IndexReader����IndexSearcher
	 *  4.����QueryParser����������Query��������
	 *  								�ٴ���QueryParser����ȷ�����������ݣ�
	 *  									QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
	 *  								�ڴ���Query���󣬱�ʾ��Ϊcontent�а�����mvn�����ĵ���Query query = queryParser.parse("mvn");
	 *  5.����Searcher����������TopDocs���ĵ�
	 *  6.����TopDocs��ȡScoreDoc����
	 *  7.����searcher��ScoreDoc�����ȡ�����Document����
	 *  8.����Document�����ȡ����Ҫ��ֵ
	 *  9.�ر�reader
	 */
	public void search() {
		
		try {
			// ����Directory
			Directory directoryFSD = FSDirectory.open(new File(filePath + "/index"));// ������Ӳ����
			
			// ����IndexReader
			IndexReader reader = IndexReader.open(directoryFSD);
			
			// ����IndexReader����IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			
			// ����QueryParser����������Query
			QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
			Query query = queryParser.parse("mvn");
			
			// ����Searcher����������TopDocs���ĵ�
			TopDocs tds = searcher.search(query, 10);//ֻ����10����¼
			
			// ����TopDocs��ȡScoreDoc����
			ScoreDoc[] sds = tds.scoreDocs;
			for(ScoreDoc sd : sds) {
				// ����searcher��ScoreDoc�����ȡ�����Document����
				Document doc = searcher.doc(sd.doc);
				// ����Document�����ȡ����Ҫ��ֵ
				System.out.println("�ļ�����" + doc.get("fileName") + "\n" + "·����" + doc.get("path"));
			}
			
			// �ر�reader
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}
}

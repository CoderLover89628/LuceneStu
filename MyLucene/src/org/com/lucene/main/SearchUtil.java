package org.com.lucene.main;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class SearchUtil {

    private Directory directory = null;
    private IndexReader reader = null;

    private String[] ids = {"1", "2", "3", "4", "5", "6"};// 邮件ID
    // 邮箱地址记录
    private String[] emails = {"aa@itat.org", "bb@itat.org", "cc@cc.org",
            "dd@sina.org", "ee@qq.org", "ff@itat.org"};
    // 邮件内容
    private String[] contents = {

            "welcome to visit the Lucene", "welcome to visit China",
            "welcome to beijing", "welcome to Lucene world",
            "you are good boy,welcome", "welcome,do you like swiming"};

    private int[] attachs = {2, 3, 1, 4, 5, 5};// 附件
    private String[] names = {"zhangsan", "lisi", "your", "joe", "mike",
            "jack"};

    // 加权操作 将itat.org结尾的邮箱，权值设置高一点
    private Map<String, Float> scores = new HashMap<String, Float>();

    private Date[] dates = null;

    public SearchUtil() {
        setDate();
        scores.put("itat.org", 2.0f);
        directory = new RAMDirectory();
        index();
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
                doc.add(new NumericField("attach", Field.Store.YES, true)
                        .setIntValue(attachs[i]));
                doc.add(new NumericField("date", Field.Store.YES, true)
                        .setLongValue(dates[i].getTime()));

                String et = emails[i].substring(emails[i].lastIndexOf("@") + 1);
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

    /**
     * 根据域和内容进行精确的查找
     *
     * @param field     查询的字段
     * @param name      要查询的内容
     * @param resultNum 返回的多少条结果
     */
    public void searchByTerm(String field, String name, int resultNum) {
        IndexSearcher searcher = getSearcher();
        Query query = new TermQuery(new Term(field, name));
        findQuery(resultNum, searcher, query);
    }

    /**
     * 范围查找
     *
     * @param field     域
     * @param start     开始点
     * @param end       结束点
     * @param resultNum 返回结果
     */
    public void searchByTermRange(String field, String start, String end,
                                  int resultNum) {
        IndexSearcher searcher = getSearcher();
        Query query = new TermRangeQuery(field, start, end, true, true);// 最后两个true代表是否包含最低和最高的值,本质是开闭区间问题
        findQuery(resultNum, searcher, query);
    }

    /**
     * 数字范围检索
     *
     * @param field     域
     * @param start     开始点
     * @param end       结束点
     * @param resultNum 返回结果数
     */
    public void searchByNumericRange(String field, int start, int end, int resultNum) {
        IndexSearcher searcher = getSearcher();
        Query query = NumericRangeQuery.newIntRange(field, start, end, true, true);// 最后两个true代表是否包含最低和最高的值,本质是开闭区间问题
        findQuery(resultNum, searcher, query);

    }

    /**
     * 前缀检索
     *
     * @param field     域
     * @param value     值
     * @param resultNum 返回结果数
     */
    public void searchByPrefix(String field, String value, int resultNum) {
        IndexSearcher searcher = getSearcher();
        Query query = new PrefixQuery(new Term(field, value));
        findQuery(resultNum, searcher, query);

    }

    /**
     * 通配符检索
     *
     * @param field     域
     * @param value     值
     * @param resultNum 返回结果数
     */
    public void searchByWildcard(String field, String value, int resultNum) {
        IndexSearcher searcher = getSearcher();
        Query query = new WildcardQuery(new Term(field, value));
        findQuery(resultNum, searcher, query);

    }

    /**
     * 多条件查询
     *
     * @param resultNum
     */
    public void searchByBoolean(int resultNum) {
        IndexSearcher searcher = getSearcher();
        BooleanQuery query = new BooleanQuery();
        // first condition:name必须是zhangsan
        // 其中，Occur.MUST 表示必须出现
        // Occur.SHOULD表示可以出现
        // Occur.MUST_NOT表示必须不出现
        query.add(new TermQuery(new Term("name", "zhangsan")), BooleanClause.Occur.MUST);

        // second condition
        query.add(new TermQuery(new Term("content", "welcome")), BooleanClause.Occur.MUST);
        findQuery(resultNum, searcher, query);
    }

    /**
     * 短语查询
     *
     * @param resultNum
     */
    public void searchByPhrase(int resultNum) {
        IndexSearcher searcher = getSearcher();
        PhraseQuery query = new PhraseQuery();
        query.setSlop(1);// 代表单词之间的间隔,此处代表单词之间间隔一个单词，即，"welcome XXX visit"，空格不算单词
        query.add(new Term("content", "welcome"));
        query.add(new Term("content", "visit"));
        findQuery(resultNum, searcher, query);
    }


    /**
     * 模糊查询
     *
     * @param resultNum
     */
    public void searchByFuzzy(int resultNum){
        IndexSearcher searcher = getSearcher();
        // 模糊查询，默认情况下只能匹配一个字符出错的情况
        Query query = new FuzzyQuery(new Term("name","make"));
        findQuery(resultNum, searcher, query);

    }

    /**
     * 基于QueryParse的查询
     *
     * @param query
     * @param resultNum
     */
    public void searchByQueryParse(Query query, int resultNum) {
        IndexSearcher searcher = getSearcher();
        findQuery(resultNum, searcher, query);
    }

    /**
     * 查询
     *
     * @param resultNum
     * @param searcher
     * @param query
     */
    private void findQuery(int resultNum, IndexSearcher searcher, Query query) {
        try {
            TopDocs tds = searcher.search(query, resultNum);
            System.out.println("一共查询了：" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println("(" + sd.doc + ")" + doc.get("name") + "["
                        + doc.get("email") + "]-->" + doc.get("id") + "------"
                        + doc.get("attach") + ":"
                        + parseTimeToDate(doc.get("date")));
            }

            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 再查询方式实现分页
     *
     * @param query
     * @param pageIndex
     * @param pageSize
     */
    public void searchPage(String query,int pageIndex,int pageSize) {
        try {
            Directory dir = FileIndexUtils.getDirectory();
            IndexSearcher searcher = getSearcher(dir);
            QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
            Query q = parser.parse(query);
            TopDocs tds = searcher.search(q, 500);
            ScoreDoc[] sds = tds.scoreDocs;
            int start = (pageIndex-1)*pageSize;
            int end = pageIndex*pageSize;
            for(int i=start;i<end;i++) {
                Document doc = searcher.doc(sds[i].doc);
                System.out.println(sds[i].doc+":"+doc.get("path")+"-->"+doc.get("filename"));
            }

            searcher.close();
        } catch (org.apache.lucene.queryParser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据页码和分页大小获取上一次的最后一个ScoreDoc
     */
    private ScoreDoc getLastScoreDoc(int pageIndex,int pageSize,Query query,IndexSearcher searcher) throws IOException {
        if(pageIndex==1)return null;//如果是第一页就返回空
        int num = pageSize*(pageIndex-1);//获取上一页的数量
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num-1];
    }

    /**
     * 3.5版本后出现的分页查询
     *
     * @param query
     * @param pageIndex
     * @param pageSize
     */
    public void searchPageByAfter(String query,int pageIndex,int pageSize) {
        try {
            Directory dir = FileIndexUtils.getDirectory();
            IndexSearcher searcher = getSearcher(dir);
            QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
            Query q = parser.parse(query);
            //先获取上一页的最后一个元素
            ScoreDoc lastSd = getLastScoreDoc(pageIndex, pageSize, q, searcher);
            //通过最后一个元素搜索下页的pageSize个元素
            TopDocs tds = searcher.searchAfter(lastSd,q, pageSize);
            for(ScoreDoc sd:tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(sd.doc+":"+doc.get("path")+"-->"+doc.get("filename"));
            }
            searcher.close();
        } catch (org.apache.lucene.queryParser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不分页，同分页进行对比
     * @param query
     */
    public void searchNoPage(String query) {
        try {
            Directory dir = FileIndexUtils.getDirectory();
            IndexSearcher searcher = getSearcher(dir);
            QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
            Query q = parser.parse(query);
            TopDocs tds = searcher.search(q, 20);
            ScoreDoc[] sds = tds.scoreDocs;
            for(int i=0;i<sds.length;i++) {
                Document doc = searcher.doc(sds[i].doc);
                System.out.println(sds[i].doc+":"+doc.get("path")+"-->"+doc.get("filename"));
            }

            searcher.close();
        } catch (org.apache.lucene.queryParser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexSearcher getSearcher(Directory directory) {
        try {
            checkAndGetReader(directory);
            return new IndexSearcher(reader);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkAndGetReader(Directory directory) throws IOException {
        if(reader==null) {
            reader = IndexReader.open(directory);
        } else {
            IndexReader tr = IndexReader.openIfChanged(reader);
            if(tr!=null) {
                reader.close();
                reader = tr;
            }
        }
    }

    /**
     * 获得searcher
     *
     * @return
     */
    private IndexSearcher getSearcher() {
        try {
            checkAndGetReader(directory);
            return new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转数字转换成具体的日期
     *
     * @param time
     * @return
     */
    private String parseTimeToDate(String time) {

        long tt = Long.valueOf(time);
        Date date = new Date(tt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return date == null ? "" : sdf.format(date);

    }

    /**
     * 初始化日期
     */
    private void setDate() {
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
}

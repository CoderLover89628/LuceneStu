package org.com.lucene.seniorsearch.search_util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangsheng1 on 2016/7/3.
 */
public class SearchUtil {
    private static IndexReader reader = null;

    static {
        try {
            reader = IndexReader.open(FileIndexUtils.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexSearcher getSearcher() {
        if (reader == null) {
            try {
                reader = IndexReader.open(FileIndexUtils.getDirectory());
                return new IndexSearcher(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                IndexReader newReader = IndexReader.openIfChanged(reader);
                if (newReader != null) {
                    // 如果新的reader不为空，说明有改变，那么需要将原来的reader关闭
                    reader.close();
                    reader = newReader;
                }
                return new IndexSearcher(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void search(String queryStr, Sort sort) {

        try {
            IndexSearcher indexSearcher = getSearcher();
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse(queryStr);
            TopDocs topDocs = null;
            if (sort != null) {
                topDocs = indexSearcher.search(query, 30, sort);
            } else {
                topDocs = indexSearcher.search(query, 30);
            }

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                System.out.println(scoreDoc.doc + ":(" + scoreDoc.score + ")[" + document.get("filename") +
                        "【" + document.get("path") + "】------>" + document.get("size") +
                        "-----" + parseTimeToDate(document.get("date") )+ "]");

            }

            indexSearcher.close();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        return date == null ? "" : sdf.format(date);

    }
}

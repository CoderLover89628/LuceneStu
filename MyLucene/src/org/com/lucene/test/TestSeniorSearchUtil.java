package org.com.lucene.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.com.lucene.seniorsearch.search_util.FileIndexUtils;
import org.com.lucene.seniorsearch.search_util.SearchUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestSeniorSearchUtil {

    private SearchUtil su;
    private FileIndexUtils fileIndexUtils;

    @Before
    public void init() {
        fileIndexUtils.index(true);
        su = new SearchUtil();
    }

    /**
     * 分页查询用
     */
    @Test
    public void testCopyFiles() {
        try {
            File file = new File("E:\\MyWorkSpace\\Lucence\\example");
            for(File f:file.listFiles()) {
                String destFileName = FilenameUtils.getFullPath(f.getAbsolutePath())+
                        FilenameUtils.getBaseName(f.getName())+".he";//后缀名可以随意变
                FileUtils.copyFile(f, new File(destFileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchByTerm() {
//        su.search("SELECT",null);//会显示评分
//        su.search("SELECT", Sort.INDEXORDER);// 以序号来进行排序,不会显示评分
//        su.search("SELECT", Sort.RELEVANCE);// 以默认的评分来排序，不会显示评分
        su.search("SELECT", new Sort(new SortField("size",SortField.INT)));// 通过文件的大小排序
        su.search("SELECT",new Sort(new SortField("date",SortField.LONG)));// 通过日期来拍

    }
}

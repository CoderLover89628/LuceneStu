package org.com.lucene.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.com.lucene.main.FileIndexUtils;
import org.com.lucene.main.SearchUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestSearchUtil {

    private SearchUtil su;

    @Before
    public void init() {
        su = new SearchUtil();
    }

    /**
     * 分页查询用
     */
    @Test
    public void testCopyFiles() {
        try {
            File file = new File("d:/lucene/example/");
            for(File f:file.listFiles()) {
                String destFileName = FilenameUtils.getFullPath(f.getAbsolutePath())+
                        FilenameUtils.getBaseName(f.getName())+".she";//后缀名可以随意变
                FileUtils.copyFile(f, new File(destFileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchByTerm() {
        su.searchByTerm("content", "welcome", 3);
    }

    @Test
    public void searchByTermRange() {
        su.searchByTermRange("id", "1", "3", 10);
        System.out.println("-------------------");
        su.searchByTermRange("name", "a", "s", 10);//查询从a开始到s结束的name
    }

    @Test
    public void searchByNumericRange() {
        su.searchByNumericRange("id", 0, 3, 10);
    }

    @Test
    public void searchByPrefix() {
        su.searchByPrefix("id", "3", 10);
        System.out.println("----------------");
        su.searchByPrefix("content", "wel", 6);
        System.out.println("----------------");
        su.searchByPrefix("name", "J", 5);//注意大小写，该处只能查询出以“J”开头的内容，以“j”开头的查询不出

    }

    @Test
    public void searchByWildcard() {
        su.searchByWildcard("email", "*@itat.org", 3); // * 代表任意长度的字符
        System.out.println("-----------------------");
        su.searchByWildcard("name", "J???", 3); // ? 代表一个任意字符，此处为查询一个以J开头，长度为3的一个值
    }

    @Test
    public void searchByBoolean() {
        su.searchByBoolean(2);
    }

    @Test
    public void searchByPhrase() {
        su.searchByPhrase(2);
    }

    @Test
    public void searchByFuzzy() {
        su.searchByFuzzy(2);
    }


    @Test
    public void searchByQueryParse() {
        //创建默认搜索域为content
        QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
        // ⑦用,开启第一个字符为通配符匹配，默认情况下是关闭的，因为效率不高
        parser.setAllowLeadingWildcard(true);
        Query query = null;
        try {
            // ①content中包含有welcome的
            query = parser.parse("welcome");
            // ②content中包含welcome或者visit的，如下，空格代表或者关系，即or
            query = parser.parse("welcome visit");
            // ③content中包含to 和visit的
            query = parser.parse("to AND visit");
            // ④改变搜索域为name，值为zhangsan的查询
            query = parser.parse("name:zhangsan");
            // ⑤名字以j开头的
            query = parser.parse("name:j*");
            // ⑥名字以j开头的,长度为4的字段值
            query = parser.parse("name:j???");
            // ⑦此处默认关闭首字符为*的查询，需要开启
            query = parser.parse("email:*@itat.org");
            // ⑧"-"代表name域不能有zhangsan的，"+"代表content域要有visit的内容
            query = parser.parse("- name:zhangsan + visit");// 此处如果换成“+ to”则查询结果为0，原因待查
            // ⑨匹配一个区间（闭区间），此处的TO必须是大写的，小写的会出错
            query = parser.parse("id:[1 TO 3]");// 三个值
            // ⑩匹配一个区间（开区间）
            query = parser.parse("id:{1 TO 3}");// 只有id = 2的值
            // 11.完全匹配welcome to visit
            query = parser.parse("\"welcome to visit\"");
            // 12.welcome和visit之间有一个单词间隔的
            query = parser.parse("\"welcome visit\"~1");
            // 13.模糊查询
            query = parser.parse("name:make~");
            // 14.关于数字的查询：如下查询不到，因为不支持，需要扩展parser
            query = parser.parse("attach:[1 TO 3]");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        su.searchByQueryParse(query, 10);
    }

    @Test
    public void indexFile() {
        FileIndexUtils.index(true);
    }

    @Test
    public void testSearchPage01() {
        su.searchPage("java", 2,20);
        System.out.println("-------------------------------");
		su.searchNoPage("java");
        System.out.println("-------------------------------");
        su.searchPageByAfter("java", 2,20);
    }

    @Test
    public void testSearchPage02() {
        su.searchPageByAfter("java", 3,20);
    }

}

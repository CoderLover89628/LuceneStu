package org.com.lucene.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.com.lucene.analysis.*;
import org.junit.Test;

import java.io.File;

/**
 * Created by zhangsheng1 on 2016/6/11.
 */
public class TestAnalysis {

    @Test
    public void test01() {
        Analyzer analyzer01 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer analyzer02 = new StopAnalyzer(Version.LUCENE_35);
        Analyzer analyzer03 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer analyzer04 = new WhitespaceAnalyzer(Version.LUCENE_35);
        String str = "this is my house, I am coming from beijing." +
                "My email is zs@gmail.com, My QQ is 123456789";
        AnalyzerUtils.displayToken(str, analyzer01);
        AnalyzerUtils.displayToken(str, analyzer02);
        AnalyzerUtils.displayToken(str, analyzer03);
        AnalyzerUtils.displayToken(str, analyzer04);

    }

    @Test
    public void test02() {
        Analyzer analyzer01 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer analyzer02 = new StopAnalyzer(Version.LUCENE_35);
        Analyzer analyzer03 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer analyzer04 = new WhitespaceAnalyzer(Version.LUCENE_35);
        Analyzer analyzer05 = new MyMMsegAnalyzer(new File("E:\\MyWorkSpace\\data"));
        String str = "我来自北京市朝阳区大屯路,上海";
        AnalyzerUtils.displayToken(str, analyzer01);
        AnalyzerUtils.displayToken(str, analyzer02);
        AnalyzerUtils.displayToken(str, analyzer03);
        AnalyzerUtils.displayToken(str, analyzer04);
        AnalyzerUtils.displayToken(str, analyzer05);

    }

    @Test
    public void test03() {
        Analyzer analyzer01 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer analyzer02 = new StopAnalyzer(Version.LUCENE_35);
        Analyzer analyzer03 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer analyzer04 = new WhitespaceAnalyzer(Version.LUCENE_35);
        String str = "how are you thank you";
        AnalyzerUtils.displayAllTokenInfo(str, analyzer01);
        System.out.println("----------------------------");
        System.out.println("----------------------------");
        AnalyzerUtils.displayAllTokenInfo(str, analyzer02);
        System.out.println("----------------------------");
        System.out.println("----------------------------");
        AnalyzerUtils.displayAllTokenInfo(str, analyzer03);
        System.out.println("----------------------------");
        System.out.println("----------------------------");
        AnalyzerUtils.displayAllTokenInfo(str, analyzer04);

    }

    @Test
    public void test04() {
        Analyzer analyzer01 = new MyStopAnalyser(new String[]{"I","you","hate"});
        Analyzer analyzer02 = new MyStopAnalyser();
        String str = "how are you thank you,I hate hot dog!";
        AnalyzerUtils.displayAllTokenInfo(str, analyzer01);
        System.out.println("============================");
        AnalyzerUtils.displayAllTokenInfo(str, analyzer02);
    }

    @Test
    public void test05() {
        Analyzer analyzer01 = new MySameAnalyzer(new SimpleSameWordContext());
        String str = "我来自北京市朝阳区";
        AnalyzerUtils.displayToken(str, analyzer01);

    }
}

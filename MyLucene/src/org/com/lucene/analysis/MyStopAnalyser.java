package org.com.lucene.analysis;

import org.apache.lucene.analysis.*;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Set;

/**
 * Created by zhangsheng1 on 2016/6/19.
 *
 * 自定义停止分词器
 *
 * 注意：自定义分词器必须定义成final类型的，否则会抛出如下异常
 * java.lang.AssertionError: Analyzer implementation classes or at least their tokenStream() and reusableTokenStream() implementations must be final
 * 分词器的实现类，tokenStream（），reusableTokenStream()需要定义成final类型的
 */
public  final class MyStopAnalyser extends Analyzer {

    private Set stops;

    // 扩张了自己的停用词
    public MyStopAnalyser(final String[] sws) {
        Set stopWordsBefore = StopAnalyzer.ENGLISH_STOP_WORDS_SET;// StopAnalyser中的过滤词
        System.out.println("StopAnalyser中的过滤词: " + stopWordsBefore);

        // 会自动将字符串数组转换为set
        stops = StopFilter.makeStopSet(Version.LUCENE_35, sws, true);
        stops.addAll(stopWordsBefore);// 加入原来的停用词
    }

    // 如果没有参数，则使用默认的停用过滤词
    public MyStopAnalyser() {
        this.stops = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        // 为该分词器设置过滤链和tokenizer
        return new StopFilter(Version.LUCENE_35,
                new LowerCaseFilter(Version.LUCENE_35,
                        new LetterTokenizer(Version.LUCENE_35, reader)), stops);
    }
}

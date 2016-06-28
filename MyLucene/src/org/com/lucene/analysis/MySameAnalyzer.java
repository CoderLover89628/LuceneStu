package org.com.lucene.analysis;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.File;
import java.io.Reader;

/**
 * Created by zhangsheng1 on 2016/6/19.
 *
 * 同义词分词器
 *
 * 设计思路：
 *
 *          tokenizer                      filter                     获取同义词
 * Reader---------------》MyMMsegAnalyzer--------->  MyMMSegTokenizer------------>sameWord
 */
public final class MySameAnalyzer extends Analyzer {

    private SameWordContext sameWordContext;

    public MySameAnalyzer(SameWordContext sameWordContext) {
        this.sameWordContext = sameWordContext;
    }

    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        Dictionary dic = Dictionary.getInstance(new File("E:\\MyWorkSpace\\data"));

        return new MySameTokenFilter(new MyMMSegTokenizer(new MaxWordSeg(dic),reader),sameWordContext);
    }
}

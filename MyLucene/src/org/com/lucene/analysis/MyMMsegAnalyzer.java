package org.com.lucene.analysis;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by zhangsheng1 on 2016/6/19.
 *
 * 中文分词器
 *
 */
public final class MyMMsegAnalyzer extends MMSegAnalyzer{

    public MyMMsegAnalyzer() {
    }

    public MyMMsegAnalyzer(String s) {
        super(s);
    }

    public MyMMsegAnalyzer(Dictionary dictionary) {
        super(dictionary);
    }

    public MyMMsegAnalyzer(File file) {
        super(file);
    }

    @Override
    public TokenStream reusableTokenStream(String s, Reader reader) throws IOException {
        MyMMSegTokenizer var3 = (MyMMSegTokenizer)this.getPreviousTokenStream();
        if(var3 == null) {
            var3 = new MyMMSegTokenizer(this.newSeg(), reader);
            this.setPreviousTokenStream(var3);
        } else {
            var3.reset(reader);
        }

        return var3;
    }


    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        MyMMSegTokenizer var3 = new MyMMSegTokenizer(this.newSeg(), reader);
        return var3;
    }
}

package org.com.lucene.analysis;

import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.analysis.MMSegTokenizer;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by zhangsheng1 on 2016/6/19.
 * <p>
 * The {@code TokenStream}-API in Lucene is based on the decorator pattern.
 * Therefore all non-abstract subclasses must be final or
 * have at least a final implementation of {@link #incrementToken}!
 * This is checked when Java assertions are enabled.
 *
 * 意思是说所有的非抽象子类必须是 final 的或者至少有一个 final 修饰的 incrementToken() 覆写方法
 *
 * 但是MMSegTokenizer并没有提供，所以需要写一个final类继承MMSegTokenizer
 */
public final class MyMMSegTokenizer extends MMSegTokenizer {

    public MyMMSegTokenizer(Seg seg, Reader reader) {
        super(seg, reader);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        return super.incrementToken();
    }
}

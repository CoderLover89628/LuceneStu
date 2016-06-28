package org.com.lucene.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by zhangsheng1 on 2016/6/11.
 */
public class AnalyzerUtils {

    public static void displayToken(String str, Analyzer analyzer) {
        try {
            // 将获得的字符串转换成tokenStream，该流中包含着一个一个的token
            TokenStream tokenStream = analyzer.tokenStream("content",new StringReader(str));
            // 创建一个属性，保存相应的词汇，然后将该属性添加到流中。相当于创建了一个碗，用来接流中一个个token属性
            CharTermAttribute cta = tokenStream.addAttribute(CharTermAttribute.class);
            // 通过incrementToken来一个一个的进行访问
            while (tokenStream.incrementToken()){
                System.out.print("[" + cta + "]");
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void displayAllTokenInfo(String str,Analyzer analyzer) {
        try {
            // 将获得的字符串转换成tokenStream，该流中包含着一个一个的token
            TokenStream tokenStream = analyzer.tokenStream("content",new StringReader(str));
            // 创建一个属性，存放位置信息，然后将该属性添加到流中。
            PositionIncrementAttribute pia = tokenStream.addAttribute(PositionIncrementAttribute.class);
            // 创建一个属性，保存相应的词汇，然后将该属性添加到流中。相当于创建了一个碗，用来接流中一个个token属性
            CharTermAttribute cta = tokenStream.addAttribute(CharTermAttribute.class);
            // 创建一个属性，存放位置偏移信息，然后将该属性添加到流中。
            OffsetAttribute oa = tokenStream.addAttribute(OffsetAttribute.class);
            // 创建一个属性，存放类型信息，然后将该属性添加到流中。
            TypeAttribute ta = tokenStream.addAttribute(TypeAttribute.class);
            // 通过incrementToken来一个一个的进行访问
            while (tokenStream.incrementToken()){
                System.out.print(pia.getPositionIncrement() + ":");
                System.out.print(cta + "[" + oa.startOffset() + "-" + oa.endOffset() + "]-->" + ta.type());
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package org.com.lucene.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by zhangsheng1 on 2016/6/19.
 */
public final class MySameTokenFilter extends TokenFilter {

    private CharTermAttribute cta = null;

    private PositionIncrementAttribute pia = null;

    private AttributeSource.State current;

    private Stack<String> sames = null;

    private SameWordContext sameWordContext;

    public MySameTokenFilter(TokenStream input,SameWordContext sameWordContext) {
        super(input);
        cta = this.addAttribute(CharTermAttribute.class);
        pia = this.addAttribute(PositionIncrementAttribute.class);
        sames = new Stack<>();
        this.sameWordContext = sameWordContext;
    }

    @Override
    public boolean incrementToken() throws IOException {

        while (sames.size() > 0) {
            // 将元素出栈，并且获得这个同义词
            String str = sames.pop();
            // 还原状态
            restoreState(current);
            cta.setEmpty();
            cta.append(str);

            // 设置位置为0
            pia.setPositionIncrement(0);
            return true;
        }

        if (!input.incrementToken()) return false;// 如果检索的输入没有了输入内容，则返回false；否则继续执行

//        if (cta.toString().equals("北京")) {
//            cta.setEmpty();// 清除原来的分词【北京】
//            cta.append("帝都");// 置换新的分词【帝都】，如果上面的setEmpty()方法没有，则直接拼接【北京帝都】
//        }

        // 如果有同义词
        if (addSameWords(cta.toString())) {
            // 保存当前的状态
            current = captureState();
        }
        return true;
    }

    /**
     * 获取同义词
     *
     * @param name
     * @return
     */
    private boolean addSameWords(String name) {

        String[] sws = sameWordContext.getSameWords(name);

        // 如果有同义词，就把其加入到栈中
        if (sws != null) {
            for (String s : sws) {
                sames.push(s);
            }
            return true;
        }
        return false;

    }
}

package org.com.lucene.analysis;

/**
 * Created by zhangsheng1 on 2016/6/26.
 * 为了实现同义词的良好扩张
 * 专门定义一个接口用来实现同义词
 */
public interface SameWordContext {

    // 根据传入单词获取同义词
    public String[] getSameWords(String word);
}

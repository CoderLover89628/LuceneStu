package org.com.lucene.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangsheng1 on 2016/6/26.
 */
public class SimpleSameWordContext implements SameWordContext {

    Map<String, String[]>  maps = new HashMap<String, String[]>();

    public SimpleSameWordContext() {
        maps.put("我",new String[]{"咱","俺"});
        maps.put("北京",new String[]{"帝都","北平"});
        maps.put("中国",new String[]{"天朝","大陆"});
    }

    @Override
    public String[] getSameWords(String word) {
        return maps.get(word);
    }
}

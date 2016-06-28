分词流程：

例如，有如下一句内容：
“Hello Lucy！ what a good day ,this is an apple”

该段内容处理流程如下：
首先通过读入流进行读取，然后转化成tokenStream，tokenStream包含两个属性
Tokenier:负责将相应的一组数据转换为一个个的语汇单元
TokenFilter：对Tokenier进行包装，（装饰器模式）

过程如下：
reader--------》Tokenier----------->TokenFilter1--------->TokenFilter2....n-------tokenStream

常用的Tokenier有如下：
KeyWordTokenier：关键字分词器，不进行分词，传入什么就是什么
StandardTokenier：标准分词器，有比较智能的处理，例如aa@itag.org会作为一个分词
charTokenier:字符分词器
whiteSpaceTokenier：空格分词器
letterTokenier：标点符号分词


常用的Filter：
StopFilter：对一些词进行停用
lowercaseFilter:对单词进行小写化处理
standardFilter：对标准分词器进行一些控制



      how are you thank you

      上述一段内容在存储或者读取的时候，要进行如下的记录
      CharAttributeTerm:保存相应词汇
      OffsetTerm:保存各个词汇之间的偏移量
      PositionIncrTerm:保存词与词之间的位置增量

中文分词：

paoding:庖丁解牛分词器，停用
mmseg:使用，使用的是搜狗的词库



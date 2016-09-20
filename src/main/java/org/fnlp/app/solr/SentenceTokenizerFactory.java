package org.fnlp.app.solr;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.conf.Configuration;
import edu.fudan.nlp.conf.DefaultConfiguration;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.fnlp.app.lucene.SentenceTokenizer;

import java.util.List;
import java.util.Map;

/**
 * Created by Lanxiaowei
 * SentenceTokenizer的工厂类
 */
public class SentenceTokenizerFactory extends TokenizerFactory {
    /**模型文件所在目录的加载路径*/
    private String modePath;
    /**模型文件的类型，可选值有：seg,tag,seg_tag,ner,parser,all*/
    private String mode;
    /**用户自定义扩展字典文件或目录的加载路径*/
    private String userDicPath;

    /**是否启用对用户自定义扩展字典里的词语进行模糊处理*/
    private boolean ambiguity;

    /**用户自定义扩展字典文件或目录的加载路径[批量指定多个]*/
    private List<String> userDicPaths;

    /**分词器配置对象*/
    private static Configuration config;

    static {
        //加载分词器的核心配置文件：FudanNLP.xml
        config = DefaultConfiguration.getInstance();
    }

    protected SentenceTokenizerFactory(Map<String, String> args) {
        super(args);
        this.modePath = get(args,"modePath");
        //若用户未指定此参数，则从核心配置文件：FudanNLP.xml中获取
        if(null == this.modePath || "".equals(this.modePath)) {
            if(null != config) {
                this.modePath = config.modePath();
            }
        }
        //若用户未指定此参数，则从核心配置文件：FudanNLP.xml中获取
        // 若配置文件中也未指定，那么设置默认值为seg
        this.mode = get(args,"mode");
        if(null == this.mode || "".equals(this.mode)) {
            if(null != config) {
                this.mode = config.model();
                if(null == this.mode || "".equals(this.mode)) {
                    this.mode = "seg";
                }
            }
        }
        this.userDicPath = get(args,"userDicPath");
        //若用户未指定此参数，则从核心配置文件：FudanNLP.xml中获取
        if(null == this.userDicPath || "".equals(this.userDicPath)) {
            if(null != config) {
                this.userDicPaths = config.userDicPath();
            }
        }

        Object obj = get(args,"ambiguity");
        //若用户未指定此参数，则从核心配置文件：FudanNLP.xml中获取
        if(null == obj || "".equals(obj.toString())) {
            if(null != config) {
                this.ambiguity = config.ambiguity();
            }
        }
    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        //初始化模型文件并加载用户自定义扩展字典文件
        if(null != this.userDicPaths && this.userDicPaths.size() > 0) {
            CNFactory.getInstance(this.modePath,this.mode,userDicPaths,this.ambiguity);
        } else {
            CNFactory.getInstance(this.modePath,this.mode,userDicPath,this.ambiguity);
        }
        return new SentenceTokenizer(factory);
    }
}

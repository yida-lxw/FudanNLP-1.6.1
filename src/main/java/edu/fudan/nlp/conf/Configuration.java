package edu.fudan.nlp.conf;

import java.util.List;

/**
 * Created by Lanxiaowei
 * FudanNLP分词器配置管理接口
 */
public interface Configuration {
    /**
     * 返回模型文件的目录路径
     * @return
     */
    public String modePath();

    /**
     * 返回模型文件类型
     * @return
     */
    public String model();

    /**
     * 是否启用POSFilter
     * @return
     */
    public boolean usePOSFilter();

    /**
     * 是否启用对用户自定义扩展字典里的词语进行模糊处理
     * @return
     */
    public boolean ambiguity();

    /**
     * 返回停用词字典文件的加载路径
     * @return
     */
    public List<String> stopwordPath();

    /**
     * 返回用户自定义扩展字典文件的加载路径
     * @return
     */
    public List<String> userDicPath();
}

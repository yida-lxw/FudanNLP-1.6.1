package edu.fudan.nlp.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Lanxiaowei
 * FudanNLP分词器配置管理接口默认实现
 */
public class DefaultConfiguration implements Configuration {
    /**分词器配置文件的名称*/
    private static final String CONFIG_FILE_NAME = "FudanNLP.xml";
    /**用户自定义扩展字典文件路径*/
    private static final String USER_DIC = "user_dic";
    /**停用词字典文件路径*/
    private static final String STOP_WORD_DIC = "stop_word_dic";
    /**模型文件的类型*/
    private static final String MODE_TYPE = "mode";
    /**模型文件所在目录的路径*/
    private static final String MODE_PATH = "mode_path";
    /**是否启用POSFilter*/
    private static final String USE_POS_FILTER = "use_pos_filter";

    private Properties props;

    private DefaultConfiguration() {
        init();
    }

    private static class SingletonHolder {
        private static final Configuration INSTANCE = new DefaultConfiguration();
    }

    public static final Configuration getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 返回模型文件的目录路径
     *
     * @return
     */
    @Override
    public String modePath() {
        return props.getProperty(MODE_PATH);
    }

    /**
     * 返回模型文件类型
     *
     * @return
     */
    @Override
    public String model() {
        return props.getProperty(MODE_TYPE);
    }

    /**
     * 是否启用POSFilter
     * @return
     */
    public boolean usePOSFilter() {
        Object obj = props.get(USE_POS_FILTER);
        if(null == obj) {
            return false;
        }
        return Boolean.valueOf(obj.toString());
    }

    /**
     * 返回停用词字典文件的加载路径
     *
     * @return
     */
    @Override
    public List<String> stopwordPath() {
        return readDicPaths(STOP_WORD_DIC);
    }

    /**
     * 返回用户自定义扩展字典文件的加载路径
     *
     * @return
     */
    @Override
    public List<String> userDicPath() {
        return readDicPaths(USER_DIC);
    }

    /**
     * 初始化FudanNLP.xml
     */
    private void init() {
        if(null != props){
            return;
        }
        props = new Properties();
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        if(input != null){
            try {
                props.loadFromXML(input);
            } catch (InvalidPropertiesFormatException e) {
                throw new RuntimeException("FudanNLP.xml not valid",e);
            } catch (IOException e) {
                throw new RuntimeException("Load FudanNLP.xml occur IO exception.",e);
            }
        }
    }

    private List<String> readDicPaths(String propertyKey) {
        List<String> extDictFiles = new ArrayList<String>(10);
        String extDictCfg = props.getProperty(propertyKey);
        if(extDictCfg != null) {
            //剔除路径中的所有空格
            extDictCfg = extDictCfg.replace(" ","");
            //使用分号;分割多个字典文件或目录路径
            String[] filePaths = extDictCfg.split(";");
            if(filePaths != null){
                for(String filePath : filePaths){
                    if(null != filePath && !"".equals(filePath.trim())){
                        extDictFiles.add(filePath.trim());
                    }
                }
            }
        }
        return extDictFiles;
    }
}

package edu.fudan.nlp.cn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.cn.tag.NERTagger;
import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.nlp.parser.dep.JointParser;
import edu.fudan.nlp.parser.dep.TreeCache;
import edu.fudan.util.exception.LoadModelException;

/**
 * 统一的自然语言处理入口
 * 自然语言处理对象有此函数产生， 确保整个系统只有这一个对象，避免重复构造。
 *
 * @author xpqiu
 */
public class CNFactory {
    public static CWSTagger seg;
    public static POSTagger pos;
    public static NERTagger ner;
    public static JointParser parser;
    public static TreeCache treeCache;

    public static String segModel = "/seg.m";
    public static String posModel = "/pos.m";
    public static String parseModel = "/dep.m";
    public static Dictionary dict = new Dictionary(true);

    public static CNFactory factory = new CNFactory();
    public static ChineseTrans ct = new ChineseTrans();
    private static boolean isEnFilter = true;

    /**
     * 设置自定义词典
     *
     * @param path
     * @throws IOException
     * @throws LoadModelException
     */
    public static void loadDict(String... path) throws LoadModelException {
        for (String file : path) {
            dict.addFile(file);
        }
        setDict();
    }

    public static void loadDict(List<String> paths) throws LoadModelException {
        for (String file : paths) {
            dict.addFile(file);
        }
        setDict();
    }

    /**
     * @param al 字典 ArrayList<String[]>
     *           每一个元素为一个单元String[].
     *           String[] 第一个元素为单词，后面为对应的词性
     * @throws LoadModelException
     */
    public static void addDict(ArrayList<String[]> al) {
        dict.add(al);
        setDict();
    }

    /**
     * 往字典里添加新词，不带词性
     * @param strs
     */
    public static void addDict(Collection<String> strs) {
        dict.addSegDict(strs);
        setDict();
    }

    /**
     * 增加词典
     *
     * @param words
     * @param pos
     */
    public static void addDict(Collection<String> words, String pos) {
        for (String w : words) {
            dict.add(w, pos);
        }
        setDict();
    }

    /**
     * 更新词典
     */
    public static void setDict() {
        if (dict == null || dict.size() == 0)
            return;
        if (pos != null) {
            pos.setDictionary(dict, true);
        } else if (seg != null) {
            seg.setDictionary(dict);
        }
    }

    /**
     * 字典持久化到硬盘
     * @param path
     */
    public static void saveDict(String path) {
        if (dict == null || dict.size() == 0) {
            return;
        }
        dict.save(path);
    }

    public enum Models {
        SEG {
            String getValue() {
                return MODEL_SEG;
            }
        },
        TAG {
            String getValue() {
                return MODEL_TAG;
            }
        },
        SEG_TAG {
            String getValue() {
                return MODEL_SEG_TAG;
            }
        },
        NER {
            String getValue() {
                return MODEL_NER;
            }
        },
        PARSER {
            String getValue() {
                return MODEL_PARSER;
            }
        },
        ALL {
            String getValue() {
                return MODEL_ALL;
            }
        };

        abstract String getValue();

        public static final String MODEL_SEG = "seg";
        public static final String MODEL_TAG = "tag";
        public static final String MODEL_SEG_TAG = "seg_tag";
        public static final String MODEL_NER = "ner";
        public static final String MODEL_PARSER = "parser";
        public static final String MODEL_ALL = "all";
    }

    /**
     * 初始化
     *
     * @return
     */
    public static CNFactory getInstance() {
        return factory;
    }

    public static CNFactory getInstance(String modelPath) throws LoadModelException {
        return getInstance(modelPath, Models.SEG, null);
    }

    public static CNFactory getInstance(String modelPath,Models model) throws LoadModelException {
        return getInstance(modelPath, model, null);
    }

    public static CNFactory getInstance(String modelPath,String model) throws LoadModelException {
        return getInstance(modelPath, model,null);
    }

    /**
     * 初始化
     *
     * @param modelPath  模型文件所在目录
     * @param model 载入模型类型
     * @return
     * @throws LoadModelException
     */
    public static CNFactory getInstance(String modelPath, String model,String userDicPath) throws LoadModelException {
        if (modelPath.endsWith("/")) {
            modelPath = modelPath.substring(0, modelPath.length() - 1);
        }
        if (null == model || "".equals(model)) {
            loadSeg(modelPath);
        } else if (Models.SEG.getValue().equals(model)) {
            loadSeg(modelPath);
        } else if (Models.TAG.getValue().equals(model)) {
            loadTag(modelPath);
        } else if (Models.SEG_TAG.getValue().equals(model)) {
            loadSeg(modelPath);
            loadTag(modelPath);
        } else if (Models.NER.getValue().equals(model)) {
            loadNER(modelPath);
        } else if (Models.PARSER.getValue().equals(model)) {
            loadParser(modelPath);
        } else if (Models.ALL.getValue().equals(model)) {
            loadSeg(modelPath);
            loadTag(modelPath);
            loadNER(modelPath);
            loadParser(modelPath);
        } else {
            loadSeg(modelPath);
        }
        loadDict(userDicPath);
        return factory;
    }

    /**
     * 初始化
     *
     * @param modelPath  模型文件所在目录
     * @param model 载入模型类型
     * @return
     * @throws LoadModelException
     */
    public static CNFactory getInstance(String modelPath, Models model,String userDicPath) throws LoadModelException {
        if (modelPath.endsWith("/")) {
            modelPath = modelPath.substring(0, modelPath.length() - 1);
        }
        if (null == model) {
            loadSeg(modelPath);
        } else if (Models.SEG.equals(model)) {
            loadSeg(modelPath);
        } else if (Models.TAG.equals(model)) {
            loadTag(modelPath);
        } else if (Models.SEG_TAG.equals(model)) {
            loadSeg(modelPath);
            loadTag(modelPath);
        } else if (Models.NER.equals(model)) {
            loadNER(modelPath);
        } else if (Models.PARSER.equals(model)) {
            loadParser(modelPath);
        } else if (Models.ALL.equals(model)) {
            loadSeg(modelPath);
            loadTag(modelPath);
            loadNER(modelPath);
            loadParser(modelPath);
        } else {
            loadSeg(modelPath);
        }
        //加载用户自定义字典文件
        loadDict(userDicPath);
        return factory;
    }

    /**
     * 读入句法模型
     *
     * @param path 模型所在目录
     * @throws LoadModelException
     */
    public static void loadParser(String path) throws LoadModelException {
        if (parser == null) {
            String file = path + parseModel;
            parser = new JointParser(file);
        }
    }

    /**
     * 读入实体名识别模型
     *
     * @param path 模型所在目录，结尾不带"/"。
     * @throws LoadModelException
     */
    public static void loadNER(String path) throws LoadModelException {
        if (ner == null && pos != null) {
            ner = new NERTagger(pos);
        }
    }

    /**
     * 读入词性标注模型
     *
     * @param path 模型所在目录，结尾不带"/"。
     * @throws LoadModelException
     */
    public static void loadTag(String path) throws LoadModelException {
        if (pos == null) {
            String file = path + posModel;
            if (seg == null) {
                pos = new POSTagger(file);
            } else {
                pos = new POSTagger(seg, file);
            }
        }
    }

    /**
     * 读入分词模型
     *
     * @param path 模型所在目录，结尾不带"/"。
     * @throws LoadModelException
     */
    public static void loadSeg(String path) throws LoadModelException {
        if (seg == null) {
            String file = path + segModel;
            seg = new CWSTagger(file);
            seg.setEnFilter(isEnFilter);
        }
    }

    /**
     * 分词
     *
     * @param input 字符串
     * @return 词数组
     */
    public String[] seg(String input) {
        if (seg == null) {
            return null;
        }
        return seg.tag2Array(input);
    }

    /**
     * 词性标注
     *
     * @param input 词数组
     * @return 词性数组
     */
    public String[] tag(String[] input) {
        if (pos == null)
            return null;
        return pos.tagSeged(input);
    }

    /**
     * 词性标注
     *
     * @param input 字符串
     * @return 词+词性数组
     */
    public String[][] tag(String input) {
        if (pos == null || seg == null)
            return null;
        return pos.tag2Array(input);
    }

    /**
     * 词性标注
     *
     * @param input 字符串
     * @return 词/词性 的连续字符串
     */
    public String tag2String(String input) {
        if (pos == null || seg == null)
            return null;
        return pos.tag(input);
    }

    /**
     * 句法分析
     *
     * @param words 词数组
     * @param pos   词性数组
     * @return 依赖数组
     */
    public int[] parse(String[] words, String[] pos) {
        if (parser == null)
            return null;
        return parser.parse(words, pos);
    }

    /**
     * 句法分析
     *
     * @param tagg 词数组+词性数组
     * @return
     */
    public DependencyTree parse2T(String[][] tagg) {
        if (tagg == null)
            return null;
        return parse2T(tagg[0], tagg[1]);
    }

    /**
     * 句法分析
     *
     * @param words 词数组
     * @param pos   词性数组
     * @return 句法树
     */
    public DependencyTree parse2T(String[] words, String[] pos) {
        if (parser == null)
            return null;
        if (treeCache != null) {
            DependencyTree tree = treeCache.get(words, pos);
            if (tree != null)
                return tree;
        }

        if (words == null || pos == null || words.length == 0 || pos.length == 0 || words.length != pos.length)
            return null;
        return parser.parse2T(words, pos);
    }

    /**
     * 句法树
     *
     * @param sent 字符串
     * @return 句法树
     */
    public DependencyTree parse2T(String sent) {
        if (pos == null || seg == null || parser == null || sent == null)
            return null;
        String[][] wc = tag(sent);
        if (wc == null)
            return null;

        return parse2T(wc[0], wc[1]);
    }

    /**
     * 实体名识别
     *
     * @param s
     * @return 实体名+类型
     */
    public static HashMap<String, String> ner(String s) {
        if (ner == null)
            return null;
        return ner.tag(s);
    }

    public static void setEnFilter(boolean b) {
        isEnFilter = b;

    }

    public static void readDepCache(String file) throws IOException {
        treeCache = new TreeCache();
        treeCache.read(file);
    }


}
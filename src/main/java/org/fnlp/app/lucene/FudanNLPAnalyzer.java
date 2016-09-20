package org.fnlp.app.lucene;

import edu.fudan.nlp.conf.Configuration;
import edu.fudan.nlp.conf.DefaultConfiguration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.util.exception.LoadModelException;
/**
 * 基于FudanNLP的Lucene分词器
 * 兼容Lucene5.x
 * @author xpqiu
 */
public final class FudanNLPAnalyzer extends Analyzer {
	/**是否使用词性过滤器*/
	private boolean usePOSFilter;
	/**停用词字典文件的加载路径*/
	private String stopwordPath;

	/**分词器配置对象*/
	private static Configuration config;

	/**是否启用位置增量*/
	private boolean enablePositionIncrements;
	/**是否启用对用户自定义扩展字典里的词语进行模糊处理*/
	private boolean ambiguity;

	static {
		config = DefaultConfiguration.getInstance();
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath      模型文件目录,可以是硬盘绝对路径，也可以是classpath下相对路径
	 * @param mode          模型类型，可选值有：seg,tag,seg_tag,ner,parser,all
	 * @param userDicPath   用户自定义扩展字典文件或目录路径
	 * @param stopwordPath  用户扩展停用词字典文件路径
	 * @param usePOSFilter  是否启用词性过滤器
	 * @param ambiguity     是否启用对用户自定义扩展字典里的词语进行模糊处理
     */
	public FudanNLPAnalyzer(String modePath, String mode, String userDicPath,
							String stopwordPath, boolean usePOSFilter,boolean ambiguity) {
		this.stopwordPath = stopwordPath;
		this.usePOSFilter = usePOSFilter;
		this.enablePositionIncrements = true;
		//初始化模型文件并加载用户自定义扩展字典文件
		CNFactory.getInstance(modePath,mode,userDicPath,ambiguity);
	}

	public FudanNLPAnalyzer(String modePath, String mode, String userDicPath,
							String stopwordPath, boolean usePOSFilter) {
		if(null != config) {
			this.ambiguity = config.ambiguity();
		}
		this.stopwordPath = stopwordPath;
		this.usePOSFilter = usePOSFilter;
		this.enablePositionIncrements = true;
		//初始化模型文件并加载用户自定义扩展字典文件
		CNFactory.getInstance(modePath,mode,userDicPath,this.ambiguity);
	}


	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath      模型文件目录,可以是硬盘绝对路径，也可以是classpath下相对路径
	 * @param mode          模型类型，可选值有：seg,tag,seg_tag,ner,parser,all
	 * @param userDicPath   用户自定义扩展字典文件或目录路径
	 * @param stopwordPath  用户扩展停用词字典文件路径
	 */
	public FudanNLPAnalyzer(String modePath, String mode, String userDicPath,
							String stopwordPath) {
		this.stopwordPath = stopwordPath;
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			this.ambiguity = config.ambiguity();
		} else {
			this.usePOSFilter = true;
		}
		this.enablePositionIncrements = true;
		//初始化模型文件并加载用户自定义扩展字典文件
		CNFactory.getInstance(modePath,mode,userDicPath,this.ambiguity);
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath      模型文件目录,可以是硬盘绝对路径，也可以是classpath下相对路径
	 * @param mode          模型类型，可选值有：seg,tag,seg_tag,ner,parser,all
	 * @param userDicPath   用户自定义扩展字典文件或目录路径
	 * @param ambiguity     是否启用对用户自定义扩展字典里的词语进行模糊处理
	 */
	public FudanNLPAnalyzer(String modePath, String mode, String userDicPath,boolean ambiguity) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
		} else {
			this.usePOSFilter = true;
		}
		this.enablePositionIncrements = true;
		//初始化模型文件并加载用户自定义扩展字典文件
		CNFactory.getInstance(modePath,mode,userDicPath,ambiguity);
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath      模型文件目录,可以是硬盘绝对路径，也可以是classpath下相对路径
	 * @param mode          模型类型，可选值有：seg,tag,seg_tag,ner,parser,all
	 * @param userDicPath   用户自定义扩展字典文件或目录路径
	 */
	public FudanNLPAnalyzer(String modePath, String mode, String userDicPath) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			this.ambiguity = config.ambiguity();
		} else {
			this.usePOSFilter = true;
		}
		this.enablePositionIncrements = true;
		//初始化模型文件并加载用户自定义扩展字典文件
		CNFactory.getInstance(modePath,mode,userDicPath,this.ambiguity);
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath      模型文件目录,可以是硬盘绝对路径，也可以是classpath下相对路径
	 * @param mode          模型类型，可选值有：seg,tag,seg_tag,ner,parser,all
	 * @param ambiguity     是否启用对用户自定义扩展字典里的词语进行模糊处理
	 */
	public FudanNLPAnalyzer(String modePath, String mode,boolean ambiguity) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,mode,config.userDicPath(),ambiguity);
		} else {
			this.usePOSFilter = true;
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,mode,ambiguity);
		}
		this.enablePositionIncrements = true;
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath      模型文件目录,可以是硬盘绝对路径，也可以是classpath下相对路径
	 * @param mode          模型类型，可选值有：seg,tag,seg_tag,ner,parser,all
	 */
	public FudanNLPAnalyzer(String modePath, String mode) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			this.ambiguity = config.ambiguity();
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,mode,config.userDicPath(),this.ambiguity);
		} else {
			this.usePOSFilter = true;
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,this.ambiguity);
		}
		this.enablePositionIncrements = true;
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath  模型文件目录,可以是硬盘绝对路径，
	 *                  也可以是classpath下相对路径
	 * @param ambiguity     是否启用对用户自定义扩展字典里的词语进行模糊处理
	 */
	public FudanNLPAnalyzer(String modePath,boolean ambiguity) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,config.model(),config.userDicPath(),ambiguity);
		} else {
			this.usePOSFilter = true;
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,"",ambiguity);
		}
		this.enablePositionIncrements = true;
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param modePath  模型文件目录,可以是硬盘绝对路径，
	 *                  也可以是classpath下相对路径
	 */
	public FudanNLPAnalyzer(String modePath) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			this.ambiguity = config.ambiguity();
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,config.model(),config.userDicPath(),this.ambiguity);
		} else {
			this.usePOSFilter = true;
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(modePath,"",this.ambiguity);
		}
		this.enablePositionIncrements = true;
	}

	/**
	 * FNLPAnalyzer构造函数
	 * @param ambiguity     是否启用对用户自定义扩展字典里的词语进行模糊处理
	 */
	public FudanNLPAnalyzer(boolean ambiguity) {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(config.modePath(),config.model(),config.userDicPath(),ambiguity);
		} else {
			this.usePOSFilter = true;
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(null,"",ambiguity);
		}
		this.enablePositionIncrements = true;
	}

	public FudanNLPAnalyzer() {
		if(null != config) {
			this.usePOSFilter = config.usePOSFilter();
			this.ambiguity = config.ambiguity();
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(config.modePath(),config.model(),config.userDicPath(),this.ambiguity);
		} else {
			this.usePOSFilter = true;
			//初始化模型文件并加载用户自定义扩展字典文件
			CNFactory.getInstance(null,"",this.ambiguity);
		}
		this.enablePositionIncrements = true;
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new SentenceTokenizer();
		TokenStream result = null;
		if(null != this.stopwordPath && !"".equals(this.stopwordPath)) {
			result = new WordTokenFilter(tokenizer,this.stopwordPath);
		} else {
			result = new WordTokenFilter(tokenizer);
		}
		if(this.usePOSFilter) {
			result = new POSTaggingFilter(this.enablePositionIncrements,result);
		}
		return new TokenStreamComponents(tokenizer, result);
	}

	/**
	 * 用于设置启用或禁用索引的位置增量
	 * @param enablePositionIncrements
     */
	public void setEnablePositionIncrements(boolean enablePositionIncrements) {
		this.enablePositionIncrements = enablePositionIncrements;
	}
	/*private Models toModel(String mode) {
		if(null == mode || "".equals(mode)) {
			return Models.SEG;
		}
		if(mode.equalsIgnoreCase(Models.MODEL_SEG)) {
			return Models.SEG;
		}
		if(mode.equalsIgnoreCase(Models.MODEL_TAG)) {
			return Models.TAG;
		}
		if(mode.equalsIgnoreCase(Models.MODEL_SEG_TAG)) {
			return Models.SEG_TAG;
		}
		if(mode.equalsIgnoreCase(Models.MODEL_NER)) {
			return Models.NER;
		}
		if(mode.equalsIgnoreCase(Models.MODEL_PARSER)) {
			return Models.PARSER;
		}
		if(mode.equalsIgnoreCase(Models.MODEL_ALL)) {
			return Models.ALL;
		}
		return Models.SEG;
	}*/
}

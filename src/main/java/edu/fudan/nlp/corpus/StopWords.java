package edu.fudan.nlp.corpus;


import edu.fudan.util.FileUtils;

import java.io.*;
import java.lang.String;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

import static com.sun.xml.internal.bind.v2.util.ClassLoaderRetriever.getClassLoader;

/**
 * 本类主要功能是过滤停用词
 * @author ltian
 *
 */
public class StopWords {
	private TreeSet<String> sWord = new TreeSet<String>();
	private Pattern noise = Pattern.compile(".*["+CharSets.allRegexPunc+"\\d]+.*");
	/**停用词字典文件的加载路径*/
	private String dicPath;
	/**是否自动加载文件更新*/
	private boolean autoDetect;
	private HashMap<String, Long> lastModTime = new HashMap<String, Long>();

	public StopWords(){}

	public StopWords(String dicPath_,boolean autoDetect){
		this.dicPath = dicPath_;
		this.autoDetect = autoDetect;
		if(!this.autoDetect) {
			read(dicPath);
		}
		// 定期监视文件改动
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				read(dicPath);
			}
		}, new Date(System.currentTimeMillis() + 10000), 24*60*60*1000);
	}

	/**
	 * 构造函数
	 * @param dicPath
	 *        stopword所在地址
	 */

	public StopWords(String dicPath) {		
		this.dicPath = dicPath;
		this.autoDetect = false;
		read(dicPath);		
	}

	/**
	 * 读取stopword
	 * @param dicPath
	 *       stopword所在地址
	 * @throws FileNotFoundException
	 */

	public void read(String dicPath) {
		//File path = new File(dicPath);
		File path = FileUtils.makeFile(dicPath);
		if(null == path) {
			return;
		}
		String filePath = FileUtils.normalizePath(path.getPath());
		if(null == filePath) {
			return;
		}
		if(path.isDirectory()){
			String[] subdir = path.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".txt");
				}
			});

			for(int i=0;i < subdir.length; i++) {
				read(filePath + subdir[i]);
			}
			return;
		}
		Long newTime = path.lastModified();
		Long lastTime = lastModTime.get(dicPath);
		if(lastTime ==null || !lastTime.equals(newTime)){
			BufferedReader in = null;

			InputStream is = null;
			try {
				is = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("File[" + filePath + "] not found.",e);
			}
			try{
				InputStreamReader read = new InputStreamReader(new FileInputStream(path), "UTF-8");
				in = new BufferedReader(read);
				String s;
				while ((s = in.readLine()) != null){ 
					s = s.trim();
					//忽略注释
					if(s.length() > 1 && s.startsWith("#")) {
						continue;
					}
					//忽略空白行
					if (s.matches("^$")) {
						continue;
					}
					sWord.add(s);
				}
			} catch (Exception e) {
				System.err.println("停用词文件路径错误");
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 删除stopword
	 * 将string字符串转换为List类型，并返回
	 * @param words
	 *       要进行处理的字符串 
	 * @return
	 *       删除stopword后的List类型
	 */

	public List<String> phraseDel(String[] words){
		List<String> list = new ArrayList<String>(); 
		String s;
		int length= words.length;
		for(int i = 0; i < length; i++){
			s = words[i];
			if(!isStopWord(s)) {
				list.add(s);
			}
		}
		return list;
	}
	
	public boolean isStopWord(String word) {
		//单字和词语长度大于4就认为是停用词？
		/*if (word.length() == 1 || word.length()>4) {
			return true;
		}*/
		if(word == null || "".equals(word)) {
			return true;
		}
		if (noise.matcher(word).matches()) {
			return true;
		}
		if(null == sWord || sWord.size() <= 0) {
			return false;
		}
		if (sWord.contains(word)) {
			return true;
		}
		return false;
	}

	public String getDicPath() {
		return dicPath;
	}

	public boolean isAutoDetect() {
		return autoDetect;
	}

	public TreeSet<String> getsWord() {
		return sWord;
	}
}

package edu.fudan.ml.types;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.fudan.util.FileUtils;
import edu.fudan.util.MultiValueMap;
import edu.fudan.util.MyCollection;
import edu.fudan.util.exception.LoadModelException;

public class Dictionary {
	/**用户添加新词的最大长度，超过限制的词语不会被添加到字典中*/
	private static final int MAX_LEN = 10;
	/**用户添加新词的最小长度，超过限制的词语不会被添加到字典中*/
	private static final int MIN_LEN = 2;
	/**
	 * 词典，词和相应的词性
	 */
	private MultiValueMap<String,String> dp;

	private TreeMap<String, int[]> index = new TreeMap<String, int[]>();
	private int indexLen = 2;
	private boolean ambiguity;

	private int maxLen;
	private int minlen;

	public Dictionary() {
		this(null,false,MAX_LEN,MIN_LEN);
	}

	public Dictionary(boolean ambiguity) {
		this(null,ambiguity,MAX_LEN,MIN_LEN);
	}

	public Dictionary(String path) {
		this(path,false,MAX_LEN,MIN_LEN);
	}

	/**
	 * 
	 * @param path
	 * @param ambiguity 使用模糊处理
	 * @throws IOException
	 */
	public Dictionary(String path, boolean ambiguity,
	    int maxLen,int minLen) {
		this.maxLen = maxLen;
		this.minlen = minLen;
		dp = new MultiValueMap<String, String>();
		this.setAmbiguity(ambiguity);
		ArrayList<String[]> al = loadDict(path);
		add(al); 
		createIndex();
	}


	/**
	 * 加入不带词性的词典
	 * @param al 词的数组
	 */
	public void addSegDict(Collection<String> al) {
		for(String s: al){
			addDict(s);
		}
		//createIndex();
	}

	/**
	 * 
	 * @param word 词
	 * @param poses 词性数组
	 */
	public void add(String word, String... poses) {		
		addDict(word,poses);
		indexLen = MIN_LEN;
	}

	/**
	 * 
	 * @param al 词典 ArrayList<String[]>
	 * 						每一个元素为一个单元String[].
	 * 						String[] 第一个元素为单词，后面为对应的词性
	 * @return 
	 */
	public void add(ArrayList<String[]> al) {
		if(null == al || al.size() <= 0) {
			return;
		}
		for(String[] pos: al) {
			addDict(pos[0], Arrays.copyOfRange(pos, 1, pos.length));
		}
		indexLen = MIN_LEN;
		//createIndex();
	}
	/**
	 * 在目前词典中增加新的词典信息
	 * @param path
	 */
	public void addFile(String path) {
		ArrayList<String[]> al = loadDict(path);
		add(al);
		indexLen = MIN_LEN;
		createIndex();
	}


	/**
	 * 通过词典文件建立词典
	 * @param path
	 * @return 
	 * @throws FileNotFoundException
	 */
	private ArrayList<String[]> loadDict(String path) {
		File file = FileUtils.makeFile(path);
		if(null == file) {
			return null;
		}
		String filePath = FileUtils.normalizePath(file.getPath());
		if(null == filePath) {
			return null;
		}
		file = new File(filePath);
		if(null == file || !file.exists() || !file.canRead()){
			return null;
		}
		ArrayList<String[]> results = new ArrayList<String[]>();
		//如果是字典目录
		if(file.isDirectory()) {
			//获取当前目录下的所有子文件或子目录
			String[] subDir = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".txt") ||
							name.toLowerCase().endsWith(".dic");
				}
			});

			//递归读取每个子文件或子目录
			for(int i = 0;i < subDir.length; i++) {
				ArrayList<String[]> words = loadDict(filePath + subDir[i]);
				if(null == words || words.size() <= 0) {
					continue;
				}
				results.addAll(words);
			}
		}
		//如果是字典文件
		else {
			Scanner scanner = null;
			try {
				scanner = new Scanner(new FileInputStream(file), "utf-8");
			} catch (FileNotFoundException e) {
				return null;
			}
			while(scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				//忽略空白行
				if (line.matches("^$")) {
					continue;
				}
				if(line.length() > 0) {
					//忽略注释
					if(line.startsWith("#") && line.length() > 1) {
						continue;
					}
					String[] words = line.split("\\s");
					results.add(words);
				}
			}
			scanner.close();
		}
		return results;
	}
	/**
	 * 增加词典信息
	 * @param word
	 * @param poses
	 */
	private void addDict(String word, String... poses){
		if(word.length() > this.maxLen) {
			this.maxLen = word.length();
		} else if(word.length() < this.minlen) {
			this.minlen = word.length();
		}
		if(poses == null || poses.length==0) {
			if(!dp.containsKey(word)) {
				dp.put(word, null);
			}
			return;
		}

		for(int j = 0; j < poses.length; j++) {
			dp.put(word, poses[j]);
		}
	}

	/**
	 * 往字典中添加新词，带词性
	 * @param word
	 * @param poses
     */
	public void addWord(String word, String... poses) {
		addDict(word,poses);
		indexLen = MIN_LEN;
	}

	/**
	 * 建立词的索引
	 */
	private void createIndex() {
		indexLen = this.minlen;
		TreeMap<String, TreeSet<Integer>> indexT = new TreeMap<String, TreeSet<Integer>>();
		for(String s: dp.keySet()) {
			if(s.length() < indexLen)
				continue;
			String temp = s.substring(0, indexLen);
			if(indexT.containsKey(temp) == false) {
				TreeSet<Integer> set = new TreeSet<Integer>();
				set.add(s.length());
				indexT.put(temp, set);
			} else {
				indexT.get(temp).add(s.length());
			}
		}
		for(Entry<String, TreeSet<Integer>> entry: indexT.entrySet()) {
			String key = entry.getKey();
			TreeSet<Integer> set = entry.getValue();
			int[] ia = new int[set.size()];
			int i = set.size();
			for(Integer integer: set) {
				ia[--i] = integer;
			}
			index.put(key, ia);
		}
	}

	public int getMaxLen() {
		return this.maxLen;
	}

	public int getMinLen() {
		return this.minlen;
	}

	public boolean contains(String s) {
		return dp.containsKey(s);
	}

	public int[] getIndex(String s) {
		return index.get(s);
	}

	public TreeSet<String> getPOS(String s) {
		return dp.getSet(s);
	}

	public int getDictSize() {
		return dp.size();
	}

	public int getIndexLen() {
		return indexLen;
	}

	public boolean isAmbiguity() {
		return ambiguity;
	}

	private void setAmbiguity(boolean ambiguity) {
		this.ambiguity = ambiguity;
	}

	public Set<String> getDict() {
		return dp.keySet();
	}
	public MultiValueMap<String, String> getPOSDict() {
		return dp;
	}

	public TreeMap<String, int[]> getIndex() {
		return index;
	}
	public int size(){
		return dp.size();
	}

	public void save(String path) {
		MyCollection.writeMultiValueMap(dp, path);
		
	}
}

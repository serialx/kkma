/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.util;


import java.util.HashSet;


/**
 * <pre>
 * String값들을 HashSet으로 저장하여 가지고 있는데,
 * 해당 String 값들이 있는지를 확인하는 함수
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class StringSet
	extends HashSet<String>
{
	int	maxLen	= 0;


	public StringSet(String[] words)
	{
		super();
		addAll(words);
	}


	public boolean contains(char ch)
	{
		return super.contains(ch + "");
	}


	/**
	 * <pre>
	 * 해당 배열에 들어 있는 어휘를 셋에 저장한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 7
	 * @param fileName
	 * @param showLog
	 */
	public void addAll(String[] words)
	{
		if( words == null ) return;
		int len = -1;
		String temp = null;
		for( int i = 0, stop = words.length; i < stop; i++ ) {
			temp = words[i];
			len = temp.length();
			add(temp);
			if( len > maxLen ) maxLen = len;
		}
	}
}

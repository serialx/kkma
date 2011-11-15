/**
 * <pre>
 * </pre>
 * @author	Dongjoo
 * @since	2009. 12. 11
 */
package org.snu.ids.ha.dic;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 12. 11
 */
public class SpacingPDDictionary
{
	private static final float						DEFAULT_PROB	= (float) Math.log(0.5);
	private static final Hashtable<String, float[]>	PROB_HASH		= new Hashtable<String, float[]>();
	static {
		load(Dictionary.DIC_ROOT + "prob/lnpr_bi_syllable.dic");
	}


	/**
	 * <pre>
	 * 사전 파일로부터 음절 Bigram에 대한 확률 값을 읽어들인다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 11
	 * @param fileName
	 */
	public static final void load(String fileName)
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		String line = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				String[] arr = line.split("\t");
				float[] lnProb = new float[] { Float.parseFloat(arr[2]), Float.parseFloat(arr[3]) };
				PROB_HASH.put(getKey(arr[0], arr[1]), lnProb);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(line);
			System.err.println("Unable to load probability dictionary!!");
		} finally {
			timer.stop();
			System.out.println(PROB_HASH.size() + " values are loaded. (Loading time( " + timer.getInterval() + " secs)");
		}
	}


	/**
	 * <pre>
	 * 음절 Bigram에 대한 확률값을 반환한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 11
	 * @param ch1
	 * @param ch2
	 * @param hasSpace
	 * @return
	 */
	public static final float getProb(char ch1, char ch2, boolean hasSpace)
	{
		float[] probs = PROB_HASH.get(getKey(ch1, ch2));
		if( probs != null ) {
			if( hasSpace ) return probs[0];
			return probs[1];
		}
		return DEFAULT_PROB;
	}


	/**
	 * <pre>
	 * 해당 문자열에 대한 띄어쓰기 확률 값을 반환한다.
	 * 주어진 문자열은 한글 문자열이나 공백으로 이루어졌다고 가정한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 11
	 * @param str
	 * @return
	 */
	public static float getProb(String str)
	{
		if( !Util.valid(str) ) return Float.MIN_VALUE;
		float prob = 0;
		str = str.trim().replaceAll("[ \t]+", " ");

		for( int i = 0, len = str.length() - 1; i < len; i++ ) {
			boolean hasSpace = false;
			char ch1 = str.charAt(i);
			char ch2 = str.charAt(i + 1);
			if( ch2 == ' ' ) {
				ch2 = str.charAt(i + 1);
				i++;
				hasSpace = true;
			}

			float fTemp = getProb(ch1, ch2, hasSpace);
			prob += fTemp;
		}

		return prob;
	}


	/**
	 * <pre>
	 * 음절 Bigram에 대한 키를 생성한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 11
	 * @param syllable1
	 * @param syllable2
	 * @return
	 */
	private static String getKey(String syllable1, String syllable2)
	{
		return syllable1 + syllable2;
	}


	/**
	 * <pre>
	 * 음절 Bigram에 대한 키를 생성한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 11
	 * @param syllable1
	 * @param syllable2
	 * @return
	 */
	private static String getKey(char syllable1, char syllable2)
	{
		return syllable1 + "" + syllable2;
	}
}

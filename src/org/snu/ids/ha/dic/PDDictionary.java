/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2009. 10. 12
 */
package org.snu.ids.ha.dic;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * </pre>
 * @author 	therocks
 * @since	2009. 10. 12
 */
final public class PDDictionary
{
	private static final Hashtable<String, Float>	LN_PROB_HASH	= new Hashtable<String, Float>(800000);
	static {
		loadDic("UNI", Dictionary.DIC_ROOT + "prob/lnpr_uni.dic");
		loadDic("UNI", Dictionary.DIC_ROOT + "prob/lnpr_tag_uni.dic");
		loadDic("INTRA", Dictionary.DIC_ROOT + "prob/lnpr_bi_intra.dic");
		loadDic("INTER", Dictionary.DIC_ROOT + "prob/lnpr_bi_inter.dic");
	}


	/**
	 * 확률값을 저장하는 사전 데이터 로딩
	 * @author	therocks
	 * @since	2009. 10. 16
	 * @param fileName
	 */
	static final protected void loadDic(String prefix, String fileName)
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		float lnProb = 0;
		long prevTag = 0, tag = 0;
		String line = null, key = null, str = null;
		String[] arr = null;

		boolean isUni = prefix != null && prefix.equals("UNI");

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();

				arr = line.split("\t");

				if( isUni ) {
					// Pr(str,tag)
					if( arr.length == 3 ) {
						str = arr[0];
						tag = POSTag.getTagNum(arr[1]);
						lnProb = Float.parseFloat(arr[2]);
						key = getKey(prefix, 0l, str, tag);
					}
					// Pr(tag)
					else if( arr.length == 2 ) {
						tag = POSTag.getTagNum(arr[0]);
						lnProb = Float.parseFloat(arr[1]);
						key = getKey(prefix, 0l, null, tag);
					}
				} else {
					// Pr(prevTag|str,tag)
					if( arr.length == 4 ) {
						prevTag = POSTag.getTagNum(arr[0]);
						str = arr[1];
						tag = POSTag.getTagNum(arr[2]);
						lnProb = Float.parseFloat(arr[3]);
						key = getKey(prefix, prevTag, str, tag);
					}
					// Pr(prevTag|tag)
					else if( arr.length == 3 ) {
						prevTag = POSTag.getTagNum(arr[0]);
						tag = POSTag.getTagNum(arr[1]);
						lnProb = Float.parseFloat(arr[2]);
						key = getKey(prefix, prevTag, null, tag);
					}
				}

				LN_PROB_HASH.put(key, lnProb);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(line);
			System.err.println("Unable to load probability dictionary!!");
		} finally {
			timer.stop();
			System.out.println(LN_PROB_HASH.size() + " values are loaded. (Loading time( " + timer.getInterval() + " secs)");
		}
	}


	private static final float	DEFAULT_LN_PROB	= -17;
	private static final float	MIN_UNI_PROB	= -14;


	/**
	 * <pre>
	 * 형태소의 출현 확률 Pr(str,tag)을 반환한다.
	 * str이 null로 주어지면, 범주의 출현 확률 Pr(tag)을 반환한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 19
	 * @param str
	 * @param tag
	 * @return
	 */
	public static float getProbUni(String str, long tag)
	{
		String key = getKey("UNI", 0l, str, getPrTag(tag));
		Float fv = LN_PROB_HASH.get(key);
		if( fv == null ) return MIN_UNI_PROB;
		return fv.floatValue();
	}


	/**
	 * <pre>
	 * 어절 내에서의 형태소 분석 후보 간 인접 확률을 반환한다.
	 * ln(Pr(prevTag|str,tag)) 값을 사전에서 조회하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 16
	 * @param prevTag	이전 형태소 범주 태그
	 * @param str	형태소
	 * @param tag	범주 태그
	 * @return 어절 내에서의 형태소 분석 후보 간 인접 확률
	 */
	public static float getProbIntraBi(long prevTag, String str, long tag)
	{
		return getProb("INTRA", prevTag, str, tag);
	}


	/**
	 * <pre>
	 * 어절 간의 형태소 분석 후보 간 인접 확률을 반환한다.
	 * ln(Pr(prevTag|str,tag)) 값을 사전에서 조회하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 16
	 * @param prevTag	이전 형태소 범주 태그
	 * @param str	형태소
	 * @param tag	범주 태그
	 * @return 어절 간의 형태소 분석 후보 간 인접 확률
	 */
	public static float getProbInterBi(long prevTag, String str, long tag)
	{
		return getProb("INTER", prevTag, str, tag);
	}


	/**
	 * ln(Pr(prevTag|exp,tag)) 값을 사전에서 조회하여 반환한다.
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param prefix	확률값 구분자
	 * 					'INTER'	어절간 근접 확률
	 * 					'INTRA'	어절내 근접 확률
	 * 					'UNI'	형태소 출현 확률
	 * @param prevTag	이전 형태소 범주 태그
	 * @param str	형태소
	 * @param tag	범주 태그
	 * @return
	 */
	private static float getProb(String prefix, long prevTag, String str, long tag)
	{
		String key = null;
		try {
			long prTag = getPrTag(prevTag), prevPrag = getPrTag(tag);
			key = getKey(prefix, prTag, str, prevPrag);
			Float fv = LN_PROB_HASH.get(key);
			if( fv == null ) {
				key = getKey(prefix, prTag, null, prevPrag);
				fv = LN_PROB_HASH.get(key);
			}
			if( fv != null ) return fv.floatValue();
		} catch (Exception e) {
			System.err.println(key);
			e.printStackTrace();
		}
		return DEFAULT_LN_PROB;
	}


	/**
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param prefix	확률값 구분자
	 * 					'INTER'	어절간 근접 확률
	 * 					'INTRA'	어절내 근접 확률
	 * 					'UNI'	형태소 출현 확률 
	 * @param prevTag	이전 형태소 범주 태그
	 * @param str	형태소
	 * @param tag	형태소 범주 태그
	 * @return
	 */
	private static final String getKey(String prefix, long prevTag, String str, long tag)
	{
		return prefix + ":" + prevTag + ":" + str + ":" + tag;
	}


	/**
	 * 확률 값을 구할 수 있는 수준의 태그를 반환.
	 * @author	therocks
	 * @since	2009. 10. 14
	 * @param tag
	 * @return 확률값이 학습된 태그
	 */
	public static long getPrTag(long tag)
	{
		if( ((POSTag.NNA | POSTag.UN) & tag) > 0 ) {
			return POSTag.NNA;
		} else if( ((POSTag.NNM | POSTag.NNB) & tag) > 0 ) {
			return POSTag.NNB;
		} else if( (POSTag.VX & tag) > 0 ) {
			return POSTag.VX;
		} else if( ((POSTag.MD) & tag) > 0 ) {
			return POSTag.MD;
		} else if( (POSTag.EP & tag) > 0 ) {
			return POSTag.EP;
		} else if( (POSTag.EF & tag) > 0 ) {
			return POSTag.EF;
		} else if( (POSTag.EC & tag) > 0 ) {
			return POSTag.EC;
		}
		return tag;
	}


	public static void main(String[] args)
	{
		System.out.println(getProbInterBi(POSTag.JKS, "크", POSTag.VA) - getProbUni(null, POSTag.JKS));
		System.out.println(getProbInterBi(POSTag.JKC, "크", POSTag.VA) - getProbUni(null, POSTag.JKC));
	}
}

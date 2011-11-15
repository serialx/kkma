/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2009. 10. 13
 */
package org.snu.ids.ha.constants;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.snu.ids.ha.util.Hangul;


/**
 * <pre>
 * 형태소간 결합 조건을 인코딩하는 클래스
 * long 즉 64bit 값에 조건들을 OR로 지정하도록 한다.
 * </pre>
 * @author 	therocks
 * @since	2009. 10. 13
 */
public class Condition
	extends Hangul
{
	public static final String[] COND_ARR = { 
			"ㅣ겹", 		// 01 ㅣ겹모음 'ㅑ,ㅕ,ㅛ,ㅠ,ㅒ,ㅖ'
			"모음", 		// 02 마지막 음절이 종성을 가지지 않음
			"자음", 		// 03 마지막 음절이 종성을 가짐
			"양성", 		// 04 마지막 음절이 양성 모음
			"음성", 		// 05 마지막 음절이 음성 모음
			"사오", 		// 07 선어말 어미 -사오-
			"사옵", 		// 08 선어말 어미 -사옵-
			"시오", 		// 09 선어말 어미 -시오-
			"오", 		// 10 선어말 어미 -오-
			"으라", 		// 11 선어말 어미 -으라-
			"으리", 		// 12 선어말 어미 -으리-
			"으시", 		// 13 선어말 어미 -으시-
			"아", 		// 14 어말어미 '아'로 종결됨
			"었", 		// 15 선어말 어미 '었'이 부착됨
			"겠", 		// 16 선어말 어미 '겠'이 부착됨
			"려", 		// 17 려로 끝나서 다음에 '하+어미'형태로 준말
			"ㄴ",		// 18
			"ㄹ",		// 19
			"ㅁ",		// 20
			"ㅂ",		// 21
			"-ㄹ", 		// 22 자음 'ㄹ'이 탈락함
			"-ㅎ", 		// 23 자음 'ㅎ'이 탈락함
			"-ㅅ", 		// 24 자음 'ㅅ'이 탈락함
			"하", 		// 25 '하'로 끝나는 용언
			"가다", 		// 26 '가'로 끝나는 동사
			"오다", 		// 27 '오'로 끝나는 동사
			"ENG", 		// 28 영문을 소리나는 대로 읽은 말
			"체언", 		// 29 체언으로 사용됨
			"관형어", 	// 30 관형어로 사용됨
			"부사어", 	// 31 부사어로 사용됨
			"서술어", 	// 32 서술어로 사용됨
			"EC", 		// 33 보조적 연결 어미로 사용됨.	~고 있~, ~지 않~ 과 같이 특정하게 나오는 패턴을 위해서 보조적 연결어미 설정
			"F", 		// 34 보조사 '는'과 같이 어절의 끝에 오는 형태소를 위해 사용
			"생략" 		// 35 바다(이)다와 같이 (이)가 가운데 생략된 것을 표현하기 위한 기분석 후보를 표현, 생략된 것을 위한 기분석 후보는 띄어쓰기 될 수 없음.
		};

	public static final Hashtable<String, Long>	COND_HASH		= new Hashtable<String, Long>();
	public static final Hashtable<Long, String>	COND_NUM_HASH	= new Hashtable<Long, String>();
	static {
		long conditionNum = 0;
		// 일반 조건 생성
		for( int i = 0, stop = COND_ARR.length; i < stop; i++ ) {
			conditionNum = 1l << i;
			COND_HASH.put(COND_ARR[i], new Long(conditionNum));
			COND_NUM_HASH.put(new Long(conditionNum), COND_ARR[i]);
		}
	}


	/**
	 * <pre>
	 * i 번째 조건을 인코딩하는 long값을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param i
	 * @return
	 */
	private static final long getCondNum(int i)
	{
		return (1l << i);
	}


	/**
	 * <pre>
	 * 주어진 조건 정보에 대한 대한 long number를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param cond
	 * @return
	 */
	public static long getCondNum(String cond)
	{
		try {
			return COND_HASH.get(cond).longValue();
		} catch (Exception e) {
			System.err.println("[" + cond + "] 정의되지 않은 조건입니다.");
		}
		return 0l;
	}


	/**
	 * <pre>
	 * 조건을 가지는 문자열을 받아들여서 이에 해당하는 인코딩된 값을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param conds
	 * @return
	 */
	public static long getCondNum(String[] conds)
	{
		long l = 0;
		for( int i = 0, size = (conds == null ? 0 : conds.length); i < size; i++ ) {
			l |= getCondNum(conds[i]);
		}
		return l;
	}


	/**
	 * <pre>
	 * condNum에 대한 조건 문자를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condNum
	 * @return
	 */
	public static String getCond(long condNum)
	{
		return condNum == 0 ? null : COND_NUM_HASH.get(new Long(condNum));
	}


	/**
	 * <pre>
	 * 인코딩된 조건값이 나타내는 조건 목록을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param encCondNum
	 * @return
	 */
	public static List<String> getCondList(long encCondNum)
	{
		List<String> ret = new ArrayList<String>();
		for( int i = 0, stop = COND_ARR.length; i < stop; i++ ) {
			if( (encCondNum & getCondNum(i)) > 0 ) ret.add(COND_ARR[i]);
		}
		return ret;
	}


	/**
	 * <pre>
	 * 인코딩된 조건값을 문자열로 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param encCondNum
	 * @return
	 */
	public static String getCondStr(long encCondNum)
	{
		StringBuffer sb = new StringBuffer();
		List<String> condList = getCondList(encCondNum);
		for( int i = 0, size = condList.size(); i < size; i++ ) {
			if( i > 0 ) sb.append(",");
			sb.append(condList.get(i));
		}
		return sb.length() == 0 ? null : sb.toString();
	}


	/**
	 * <pre>
	 * 가능한 모든 조건을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 13
	 * @return
	 */
	public static final List<String> getCondList()
	{
		List<String> condList = new ArrayList<String>();
		List<Long> condNumList = new ArrayList<Long>(COND_NUM_HASH.keySet());
		Collections.sort(condNumList);
		for( int i = 0, size = condNumList.size(); i < size; i++ ) {
			condList.add(COND_NUM_HASH.get(condNumList.get(i)));
		}
		return condList;
	}


	/**
	 * <pre>
	 * 조건을 만족하는지 여부를 확인하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 13
	 * @param havingCond
	 * @param checkingCond
	 * @return
	 */
	public static final boolean checkAnd(long havingCond, long checkingCond)
	{
		return (havingCond & checkingCond) == checkingCond;
	}


	/**
	 * <pre>
	 * 해당 조건을 만족하는 것이 하나라도 있는지의 여부를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param havingCond
	 * @param checkingCond
	 * @return
	 */
	public static final boolean checkOr(long havingCond, long checkingCond)
	{
		return (havingCond & checkingCond) > 0;
	}


	/**
	 * 조건 값들을 저장해두고, 사용하도록 한다.
	 */
	// 음운 조건
	public static final long	YI_DB			= getCondNum("ㅣ겹");
	public static final long	MOEUM			= getCondNum("모음");
	public static final long	JAEUM			= getCondNum("자음");
	public static final long	YANGSEONG		= getCondNum("양성");
	public static final long	EUMSEONG		= getCondNum("음성");
	// 어미 조건
	public static final long	SAO				= getCondNum("사오");
	public static final long	SAOP			= getCondNum("사옵");
	public static final long	SIO				= getCondNum("시오");
	public static final long	OH				= getCondNum("오");
	public static final long	ERA				= getCondNum("으라");
	public static final long	ERI				= getCondNum("으리");
	public static final long	ESI				= getCondNum("으시");
	public static final long	AH				= getCondNum("아");
	public static final long	EUT				= getCondNum("었");
	public static final long	GET				= getCondNum("겠");
	public static final long	LYEO			= getCondNum("려");
	
	// 모음으로 끝나는 어간에 'ㄴ', 'ㅁ', 'ㄹ'을 합쳐서  
	public static final long	NIEUN			= getCondNum("ㄴ");
	public static final long	MIEUM			= getCondNum("ㅁ");
	public static final long	LIEUL			= getCondNum("ㄹ");
	// 어간에 ㅂ이 추가된 경우
	public static final long	BIEUB			= getCondNum("ㅂ");
	
	// 자음 탈락 조건
	public static final long	MINUS_LIEUL		= getCondNum("-ㄹ");
	public static final long	MINUS_HIEUT		= getCondNum("-ㅎ");
	public static final long	MINUS_SIOT		= getCondNum("-ㅅ");
	
	// 용언 조건
	public static final long	HA				= getCondNum("하");
	public static final long	GADA			= getCondNum("가다");
	public static final long	ODA				= getCondNum("오다");
	public static final long	ENG				= getCondNum("ENG");
	
	// 문장 성분 조건
	public static final long	N				= getCondNum("체언");
	public static final long	D				= getCondNum("관형어");
	public static final long	A				= getCondNum("부사어");
	public static final long	V				= getCondNum("서술어");
	
	// 보조적 연결어미
	public static final long	EC				= getCondNum("EC");
	
	public static final long	F				= getCondNum("F");
	public static final long	SHORTEN			= getCondNum("생략");

	public static final long	MINUS_JA_SET	= MINUS_LIEUL | MINUS_HIEUT | MINUS_SIOT;
	public static final long	SET_FOR_UN		= JAEUM | MOEUM | YANGSEONG | EUMSEONG;
}

/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2009. 10. 06
 */
package org.snu.ids.ha.constants;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 알파벳의 의미
 * N: Noun 명사
 * V: Verb 동사 (용언)
 * M: 수식
 * A: Adjective 형용사, Adverb 관형사 
 * P: Pronoun
 * X: 보조
 * S: Support 보조
 * D: 관형사 Prenoun 
 * J: Josa
 * K: Kyeok 격
 * C: Connection 연결, 지정
 * F: Final 종료
 * E: Eomi 어미
 * T: Transform 변형
 * O: Others 기타
 * </pre>
 * @author 	therocks
 * @since	2009. 10. 06
 */
public class POSTag
{
	/**
	 * 총 가능한 태그의 수가 63개인데, BOW, EOS까지 정의되었으므로 더이상의 태그는 정의 불가능함.
	 * 첫번째 필드는 복합어 여부를 확인하기 위한 flag를 위해서 남겨 두어야 함.
	 * @since	2009. 10. 15
	 * @author	therocks
	 */
	private static final String[] TAG_ARR = {
			// 체언
			"NNG", 	// 01 일반 명사
			"NNP", 	// 02 고유 명사
			"NNB", 	// 03 의존 명사
			"NNM", 	// 04 단위 명사
			"NR", 	// 05 수사
			"NP", 	// 06 대명사
			// 용언
			"VV", 	// 07 동사
			"VA", 	// 08 형용사
			"VXV", 	// 09 보조 동사
			"VXA", 	// 10 보조 형용사
			"VCP", 	// 11 긍정 지정사
			"VCN", 	// 12 부정 지정사
			// 관형사
			"MDN", 	// 13 수 관형사
			"MDT", 	// 14 기타 관형사
			// 부사
			"MAG", 	// 15 일반 부사
			"MAC", 	// 16 접속 부사
			// 감탄사
			"IC", 	// 17 감탄사
			// 조사
			"JKS", 	// 18 주격 조사
			"JKC", 	// 19 보격 조사
			"JKG", 	// 20 관형격 조사
			"JKO", 	// 21 목적격 조사
			"JKM", 	// 22 부사격 조사
			"JKI", 	// 23 호격 조사
			"JKQ", 	// 24 인용격 조사
			"JX", 	// 25 보조사
			"JC", 	// 26 접속 조사
			// 어미
			"EPH", 	// 27 존칭 선어말 어미
			"EPT", 	// 28 시제 선어말 어미
			"EPP", 	// 29 공손 선어말 어미
			"EFN", 	// 30 기본 종결 어미
			"EFQ", 	// 31 의문 종결 어미
			"EFO", 	// 32 명령 종결 어미
			"EFA", 	// 33 청유 종결 어미
			"EFI", 	// 34 감탄 종결 어미
			"EFR", 	// 35 존칭 종결 어미
			"ECE", 	// 36 대등 연결 어미
			"ECD", 	// 37 의존 연결 어미
			"ECS", 	// 38 보조 연결 어미
			"ETN", 	// 39 명사형 전성 어미
			"ETD", 	// 40 관형형 전성 어미
			// 접두사
			"XPN", 	// 41 체언 접두사
			"XPV", 	// 42 용언 접두사
			// 접미사
			"XSN", 	// 43 명사 파생접미사
			"XSV", 	// 44 동사 파생접미사
			"XSA", 	// 45 형용사 파생접미사
			"XSM", 	// 46 부사 파생접미사
			"XSO", 	// 47 기타 접미사
			"XR", 	// 48 어근
			// 기호
			"SY", 	// 49 기호 일반
			"SF", 	// 50 마침표물음표,느낌표
			"SP", 	// 51 쉼표,가운뎃점,콜론,빗금
			"SS", 	// 52 따옴표,괄호표,줄표
			"SE", 	// 53 줄임표
			"SO", 	// 54 붙임표(물결,숨김,빠짐)
			"SW", 	// 55 기타기호 (논리수학기호,화폐기호)
			// 분석 불능
			"UN", 	// 56 명사추정범주
			"UV", 	// 57 용언추정범주
			"UE", 	// 58 분석불능범주
			// 한글 이외
			"OL", 	// 59 외국어
			"OH", 	// 60 한자
			"ON", 	// 61 숫자
			"BOS", 	// 62 문장의 시작
			"EMO", 	// 63 그림말 (Emoticon)
		};

	private static final Hashtable<String, Long>	TAG_HASH			= new Hashtable<String, Long>();
	private static final Hashtable<Long, String>	TAG_NUM_HASH		= new Hashtable<Long, String>();
	static {
		long hgFuncNum = 0;
		for( int i = 0, stop = TAG_ARR.length; i < stop; i++ ) {
			hgFuncNum = 1l << i;
			TAG_HASH.put(TAG_ARR[i], new Long(hgFuncNum));
			TAG_NUM_HASH.put(new Long(hgFuncNum), TAG_ARR[i]);
		}
	}


	/**
	 * <pre>
	 * Array에 있는 i번째 HgFuncNum을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 18
	 * @param i
	 * @return
	 */
	private static final long getTagNum(int i)
	{
		return 1l << i;
	}


	/**
	 * <pre>
	 * Tag에 대한 long number를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param tag
	 * @return
	 */
	public static long getTagNum(String tag)
	{
		if( tag == null ) return 0l;
		if( tag.indexOf(',') > -1 ) return getTagNum(tag.split(","));
		long l = 0l;
		try {
			l = TAG_HASH.get(tag).longValue();
		} catch (Exception e) {
			System.err.println("[" + tag + "] 정의되지 않은 태그입니다.");
		}
		return l;
	}
	
	
	public static final long	NNG	= getTagNum("NNG");
	public static final long	NNP	= getTagNum("NNP");
	public static final long	NNB	= getTagNum("NNB");
	public static final long	NNM	= getTagNum("NNM");
	public static final long	NR	= getTagNum("NR");
	public static final long	NP	= getTagNum("NP");
	public static final long	VV	= getTagNum("VV");
	public static final long	VA	= getTagNum("VA");
	public static final long	VXV	= getTagNum("VXV");
	public static final long	VXA	= getTagNum("VXA");
	public static final long	VCP	= getTagNum("VCP");
	public static final long	VCN	= getTagNum("VCN");
	public static final long	MDT	= getTagNum("MDT");
	public static final long	MDN	= getTagNum("MDN");
	public static final long	MAG	= getTagNum("MAG");
	public static final long	MAC	= getTagNum("MAC");
	public static final long	IC	= getTagNum("IC");
	public static final long	JKS	= getTagNum("JKS");
	public static final long	JKC	= getTagNum("JKC");
	public static final long	JKG	= getTagNum("JKG");
	public static final long	JKO	= getTagNum("JKO");
	public static final long	JKM	= getTagNum("JKM");
	public static final long	JKI	= getTagNum("JKI");
	public static final long	JKQ	= getTagNum("JKQ");
	public static final long	JX	= getTagNum("JX");
	public static final long	JC	= getTagNum("JC");
	public static final long	EPH	= getTagNum("EPH");
	public static final long	EPT	= getTagNum("EPT");
	public static final long	EPP	= getTagNum("EPP");
	public static final long	EFN	= getTagNum("EFN");
	public static final long	EFQ	= getTagNum("EFQ");
	public static final long	EFO	= getTagNum("EFO");
	public static final long	EFA	= getTagNum("EFA");
	public static final long	EFI	= getTagNum("EFI");
	public static final long	EFR	= getTagNum("EFR");
	public static final long	ECE	= getTagNum("ECE");
	public static final long	ECD	= getTagNum("ECD");
	public static final long	ECS	= getTagNum("ECS");
	public static final long	ETN	= getTagNum("ETN");
	public static final long	ETD	= getTagNum("ETD");
	public static final long	XPN	= getTagNum("XPN");
	public static final long	XPV	= getTagNum("XPV");
	public static final long	XSN	= getTagNum("XSN");
	public static final long	XSV	= getTagNum("XSV");
	public static final long	XSA	= getTagNum("XSA");
	public static final long	XSM	= getTagNum("XSM");
	public static final long	XSO	= getTagNum("XSO");
	public static final long	XR	= getTagNum("XR");
	public static final long	SF	= getTagNum("SF");
	public static final long	SP	= getTagNum("SP");
	public static final long	SS	= getTagNum("SS");
	public static final long	SE	= getTagNum("SE");
	public static final long	SO	= getTagNum("SO");
	public static final long	SW	= getTagNum("SW");
	public static final long	UN	= getTagNum("UN");
	public static final long	UV	= getTagNum("UV");
	public static final long	UE	= getTagNum("UE");
	public static final long	OL	= getTagNum("OL");
	public static final long	OH	= getTagNum("OH");
	public static final long	ON	= getTagNum("ON");
	public static final long	BOS	= getTagNum("BOS");
	public static final long	EMO	= getTagNum("EMO");

	// 보통 명사 + 고유명사
	public static final long	NNA	= NNG | NNP;
	
	// 명사 (명사 + 수사 + 명사 추정 미등록어)	
	public static final long	NN	= NNA | NNB | NNM | NR | UN | ON;
	// 체언
	public static final long	N	= NP | NN;
	// 보조 용언
	public static final long	VX	= VXV | VXA;
	// 서술격 조사 '이다'를 제외한 용언
	public static final long	VP	= VV | VA | VX | VCN;
	// 지정사
	public static final long	VC	= VCN | VCP;
	// 용언
	public static final long	V	= VP | VCP;
	// 관형사
	public static final long	MD	= MDN | MDT;
	// 부사
	public static final long	MA	= MAG | MAC;
	// 수식언
	public static final long	M	= MD | MA;
	// 격조사
	public static final long	JK	= JKS | JKC | JKG | JKO | JKM | JKI | JKQ;
	// 조사
	public static final long	J	= JK | JX | JC;
	// 선어말 어미
	public static final long	EP	= EPH | EPT | EPP;
	// 어간 + 선어말 어미
	// 종결형 어말 어미
	public static final long	EF	= EFN | EFQ | EFO | EFA | EFI | EFR;
	// 연결형  어말 어미
	public static final long	EC	= ECE | ECD | ECS;
	// 전성형 어말 어미
	public static final long	ET	= ETN | ETD;
	// 어말 어미
	public static final long	EM	= EF | EC | ET;
	// 어미
	public static final long	E	= EP | EM;
	// 접두사
	public static final long	XP	= XPN | XPV;
	// 접미사
	public static final long	XS	= XSN | XSV | XSA | XSM | XSO;
	// 기호
	public static final long	S	= SF | SP | SS | SE | SO | SW;

	static {
		TAG_HASH.put("E", E);
		TAG_HASH.put("EC", EC);
		TAG_HASH.put("EF", EF);
		TAG_HASH.put("EM", EM);
		TAG_HASH.put("EP", EP);
		TAG_HASH.put("ET", ET);
		TAG_HASH.put("J", J);
		TAG_HASH.put("JK", JK);
		TAG_HASH.put("M", M);
		TAG_HASH.put("MA", MA);
		TAG_HASH.put("MD", MD);
		TAG_HASH.put("N", N);
		TAG_HASH.put("NN", NN);
		TAG_HASH.put("NNA", NNA);
		TAG_HASH.put("S", S);
		TAG_HASH.put("V", V);
		TAG_HASH.put("VC", VC);
		TAG_HASH.put("VP", VP);
		TAG_HASH.put("VX", VX);
		TAG_HASH.put("XP", XP);
		TAG_HASH.put("XS", XS);
		TAG_NUM_HASH.put(E, "E");
		TAG_NUM_HASH.put(EC, "EC");
		TAG_NUM_HASH.put(EF, "EF");
		TAG_NUM_HASH.put(EM, "EM");
		TAG_NUM_HASH.put(EP, "EP");
		TAG_NUM_HASH.put(ET, "ET");
		TAG_NUM_HASH.put(J, "J");
		TAG_NUM_HASH.put(JK, "JK");
		TAG_NUM_HASH.put(M, "M");
		TAG_NUM_HASH.put(MA, "MA");
		TAG_NUM_HASH.put(MD, "MD");
		TAG_NUM_HASH.put(N, "N");
		TAG_NUM_HASH.put(NN, "NN");
		TAG_NUM_HASH.put(NNA, "NNA");
		TAG_NUM_HASH.put(S, "S");
		TAG_NUM_HASH.put(V, "V");
		TAG_NUM_HASH.put(VX, "VX");
		TAG_NUM_HASH.put(VP, "VP");
		TAG_NUM_HASH.put(XP, "XP");
		TAG_NUM_HASH.put(XS, "XS");
	}

		
	/**
	 * <pre>
	 * tags에 해당하는 품사 정보들을 인코딩된 값으로 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param tagArr
	 * @return
	 */
	public static long getTagNum(String[] tagArr)
	{
		long l = 0;
		for( int i = 0, stop = (tagArr == null ? 0 : tagArr.length); i < stop; i++ ) {
			l |= getTagNum(tagArr[i]);
		}
		return l;
	}


	/**
	 * <pre>
	 * tagNum에 대한 tag문자를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param tagNum
	 * @return
	 */
	public static String getTag(long tagNum)
	{
		if( tagNum == 0l ) return null;
		String tag = TAG_NUM_HASH.get(tagNum);
		if( tag == null ) tag = getTagStr(tagNum);
		return tag;
	}


	/**
	 * <pre>
	 * encoding된 값이 나타내는 class를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param encTagNum
	 * @return
	 */
	public static List<String> getTagList(long encTagNum)
	{
		List<String> ret = new ArrayList<String>();
		for( int i = 0, stop = TAG_ARR.length; i < stop; i++ ) {
			if( (encTagNum & getTagNum(i)) > 0 )
				ret.add(TAG_ARR[i]);
		}
		return ret;
	}


	/**
	 * <pre>
	 * encTagNum 가 저장하고 있는 tag정보들을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param encTagNum
	 * @return
	 */
	public static String getTagStr(long encTagNum)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = TAG_ARR.length; i < stop; i++ ) {
			if( (encTagNum & getTagNum(i)) > 0 ) {
				if( sb.length() > 0 ) sb.append(",");
				sb.append(TAG_ARR[i]);
			}
		}
		return sb.length() == 0 ? null : sb.toString();
	}


	static final long		MASK_ALL		= 0xffffffffffffffffl;
	static final String[]	ZIP_TAG_ARR		= {
		"N",	// 체언
		"NN",	// 명사 상응
		"NNA",	// 보통명사, 고유명사
		"V",	// 용언
		"VP",	// '이다' 제외 용언
		"VC",	// 지정사, '이다', '아니다'
		"VX",	// 보조 용언
		"M",	// 수식언
		"MD",	// 관형사
		"MA",	// 부사
		"J",	// 조사
		"JK",	// 격조사
		"E",	// 어미
		"EM",	// 어말 어미
		"EP",	// 선어말 어미
		"EF",	// 종결형 어말 어미
		"EC",	// 연결형 어말 어미
		"ET",	// 전성형 어말 어미
		"XS",	// 접미사
		"S"		// 기호
	};
	static final int		ZIP_TAG_ARR_LEN	= ZIP_TAG_ARR.length;


	/**
	 * <pre>
	 * 압축된 형태의 간결한 조건 정보 반환
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 06
	 * @param encTagNum
	 * @return
	 */
	public static String getZipTagStr(long encTagNum)
	{
		StringBuffer sb = new StringBuffer();

		int zipTagCnt = 0;
		long zipTagEnc = 0;
		for( int i = 0; i < ZIP_TAG_ARR_LEN; i++ ) {
			zipTagEnc = getTagNum(ZIP_TAG_ARR[i]);
			if( (encTagNum & zipTagEnc) == zipTagEnc ) {
				if( zipTagCnt > 0 ) sb.append(",");
				sb.append(ZIP_TAG_ARR[i]);
				zipTagCnt++;
				encTagNum &= (MASK_ALL ^ zipTagEnc);
			}
		}

		String temp = getTagStr(encTagNum);

		if( Util.valid(temp) ) {
			if( zipTagCnt > 0 ) sb.append(",");
			sb.append(temp);
		}
		temp = sb.toString();
		return Util.valid(temp) ? temp : null;
	}


	public static final long	COMPOSED	= 0x8000000000000000l;
	public static final long	MASK_TAG	= 0xffffffffffffffffl ^ 0x8000000000000000l;


	/**
	 * <pre>
	 * 태그와 복합어 여부를 인코딩 하여 봔한한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 06
	 * @param hgTag
	 * @param compType
	 * @return
	 */
	public static final long encode(String hgTag, String compType)
	{
		long enc = getTagNum(hgTag);
		if( Util.valid(compType) && compType.equals("C") ) enc |= COMPOSED;
		return enc;
	}


	/**
	 * <pre>
	 * 태그와 복합어 여부를 디코드 하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 06
	 * @param hgEnc
	 * @return
	 */
	public static final String[] decode(long hgEnc)
	{
		String[] ret = new String[2];
		ret[0] = getTag(hgEnc & MASK_TAG);
		ret[1] = (hgEnc & COMPOSED) == COMPOSED ? "C" : "S";
		return ret;
	}


	/**
	 * <pre>
	 * 대상 태그인지 확인하는 함수
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 20
	 * @param tag 확인할 태그
	 * @param tagsEnc 태그 집합을 가지고 있는
	 * @return
	 */
	public static boolean isTagOf(String tag, long tagsEnc)
	{
		return isTagOf(getTagNum(tag), tagsEnc);
	}


	public static boolean isTagOf(long tagNum, long tagsEnc)
	{
		return Long.bitCount(tagNum & tagsEnc) > 0;
	}
}
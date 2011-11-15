/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.constants.Symbol;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 형태소 정보를 가지는 Class
 * 형태소, + 부가 class 정보 가짐
 * string : 형태소 정보
 * composed : 복합어 여부
 * tag : 형태소의 구분 품사, 어미, 어근,
 * infoEnc 에 encoding한 상태로 저장하도록 수정함
 * [composed]
 * 추가 색인어를 추출할 필요가 있는지를 구분하는 어휘
 * 사전에 있는 어휘에서 추가로 분해하여 반환한다.
 * 사랑방손님 -> 사랑방손님, 사랑방,손님
 * 둥글게둥글게 -> 둥글게둥글게, 둥글게
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class Morpheme
	extends Token
{
	/**
	 * <pre>
	 * 복합명사 여부와 품사 정보를 인코딩하여 저장한다.
	 * </pre>
	 * @since	2009. 10. 11
	 * @author	therocks
	 */
	protected long		infoEnc			= 0;
	/**
	 * <pre>
	 * 복합명사에 대한 분석 결과를 저장한다.
	 * </pre>
	 * @since	2009. 10. 11
	 * @author	therocks
	 */
	ArrayList<String>	compNounList	= null;


	/**
	 * <pre>
	 * copy에 사용하기 위한 constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 */
	protected Morpheme()
	{
		super();
	}


	/**
	 * <pre>
	 * default constructor
	 * 미등록어에 대한 기본적인 분석 결과를 생성한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 */
	public Morpheme(String string, int index)
	{
		this.index = index;
		this.string = string;
		this.charSet = CharSetType.HANGUL;
		infoEnc = POSTag.UN;
	}


	public Morpheme(String string, long tagNum)
	{
		this.string = string;
		this.charSet = CharSetType.HANGUL;
		infoEnc = tagNum;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string	형태소
	 * @param tag	태그
	 * @param compType	복합여부
	 */
	public Morpheme(String string, String tag, String compType)
	{
		this.string = string;
		this.charSet = CharSetType.HANGUL;
		infoEnc = POSTag.getTagNum(tag);
		setComposed(compType);
	}


	/**
	 * <pre>
	 * Token정보를 받아들여서 형태소 정보를 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param token
	 */
	public Morpheme(Token token)
	{
		this.index = token.index;
		this.string = token.string;
		this.charSet = token.charSet;

		// 한글은 분석 불능 명사로 처리
		if( token.isCharSetOf(CharSetType.HANGUL) ) {
			infoEnc = POSTag.UN;
		}
		// 숫자는 수사로 설정해줌
		else if( token.isCharSetOf(CharSetType.NUMBER) ) {
			infoEnc = POSTag.NR;
		}
		// 영문은 단순히 명사로 설정해줌
		else if( token.isCharSetOf(CharSetType.ENGLISH) || token.isCharSetOf(CharSetType.COMBINED) ) {
			infoEnc = POSTag.UN;
		}
		// 이모티콘은 이모티콘으로 설정해줌
		else if( token.isCharSetOf(CharSetType.EMOTICON) ) {
			infoEnc = POSTag.EMO;
		}
		// 이외
		else {
			infoEnc = Symbol.getSymbolTag(token.string);
		}
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mp
	 */
	public Morpheme(Morpheme mp)
	{
		this.index = mp.index;
		this.string = mp.string;
		this.charSet = mp.charSet;
		infoEnc = mp.infoEnc;
	}


	/**
	 * @return Returns the tag.
	 */
	public String getTag()
	{
		return POSTag.getTag(getTagNum());
	}


	/**
	 * <pre>
	 * 문자로 주어진 태그를 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 14
	 * @param tag
	 */
	public void setTag(String tag)
	{
		setTag(POSTag.getTagNum(tag));
	}


	/**
	 * <pre>
	 * 해당 정보로 설정된 태그 값을 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 14
	 * @param tagNum
	 */
	public void setTag(long tagNum)
	{
		infoEnc = (infoEnc & POSTag.COMPOSED) | (POSTag.MASK_TAG & tagNum);
	}


	/**
	 * <pre>
	 * 품사 번호를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @return
	 */
	public long getTagNum()
	{
		return infoEnc & POSTag.MASK_TAG;
	}


	/**
	 * @return Returns the composed.
	 */
	public boolean isComposed()
	{
		return infoEnc < 0;
	}


	/**
	 * <pre>
	 * 복합어인지 여부에 대한 코드를 반환
	 * C: 복합어
	 * S: 단일어
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 06
	 * @return
	 */
	public String getComposed()
	{
		return isComposed() ? "C" : "S";
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @param composed
	 */
	public void setComposed(boolean composed)
	{
		if( composed ) {
			infoEnc |= POSTag.COMPOSED;
		} else {
			infoEnc &= POSTag.MASK_TAG;
		}
	}


	/**
	 * @param compType The composed to set.
	 */
	public void setComposed(String compType)
	{
		setComposed(Util.valid(compType) && compType.equals("C"));
	}


	/**
	 * <pre>
	 * 해당 태그인지 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @param tagNum
	 * @return
	 */
	public boolean isTag(long tagNum)
	{
		return getTagNum() == tagNum;
	}


	/**
	 * <pre>
	 * 형태소가 가진 정보가 주어진 조건을 충족하는지 확인한다.
	 * OR 형태로 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @param tagNum
	 * @return
	 */
	public boolean isTagOf(long tagNum)
	{
		return ((infoEnc & POSTag.MASK_TAG) & tagNum) > 0;
	}


	/**
	 * <pre>
	 * 두 형태소가 새로운 단어를 만들어 낼 수 있는 경우에 합쳐줌
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param mp
	 */
	public void append(Morpheme mp)
	{
		// 부사 파생 접미사 처리
		if( mp.isTag(POSTag.XSM) ) {
			setTag(POSTag.MAG);
		}
		// '요'가 부착된 경우는 현재 태그를 유지함.
		else if( !mp.isTag(POSTag.EFR) ) {
			setTag(mp.getTagNum());
		}
		// '요'가 의존적, 보조적 연결어미 '아', '어'에 붙을 때에는 종결형으로 바꾸어줌.  
		else if( mp.isTag(POSTag.EFR) && isTagOf(POSTag.EC) // 
				&& (string.equals("아") || string.equals("어") || string.equals("구") || string.equals("고")) ) 
		{
			setTag(POSTag.EFN);
		}
		this.string += mp.string;
		setComposed(false);
	}


	/**
	 * <pre>
	 * 복사본을 반환
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 7
	 * @return
	 */
	public Morpheme copy()
	{
		Morpheme copy = new Morpheme();
		copy.string = this.string;
		copy.charSet = this.charSet;
		copy.index = this.index;
		copy.infoEnc = this.infoEnc;
		return copy;
	}


	/**
	 * <pre>
	 * 형태소 정보를 생성해서 저장한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param source
	 */
	static Morpheme create(String source)
	{
		Morpheme ret = null;
		if( source.startsWith("/") ) {
			ret = new Morpheme("/", "SY", null);
		} else {
			String[] arr = source.split("/");
			ret = new Morpheme(arr[0], arr[1], arr.length > 2 ? arr[2] : null);
		}
		return ret;
	}


	/**
	 * <pre>
	 * 형태소 정보를 문자열로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(index + "/" + string + "/" + getTag() + (isComposed() ? "/C" : ""));
		return sb.toString();
	}


	/**
	 * <pre>
	 * 검색 결과를 단순하게 출력하기 위해서 필요한 함수
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 04
	 * @return
	 */
	public String getSmplStr()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(string + "/" + getTag());
		return sb.toString();
	}


	/**
	 * <pre>
	 * 검색 결과를 단순하게 출력하기 위해서 필요한 함수
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 04
	 * @return
	 */
	public String getSmplStr2()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(string + "/" + getTag() + (isComposed() ? "/C" : ""));
		return sb.toString();
	}


	/**
	 * <pre>
	 * 형태소 정보를 인코딩된 형태로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	String getEncStr()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(string + "/" + infoEnc);
		return sb.toString();
	}
}

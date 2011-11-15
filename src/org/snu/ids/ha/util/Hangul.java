/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 5. 7
 */
package org.snu.ids.ha.util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 5. 7
 */
public class Hangul
{
	/**
	 * <pre>
	 * 양성 모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_POSITIVE_SET	= new StringSet(new String[] { "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅗ", "ㅛ", "ㅘ" });

	/**
	 * <pre>
	 * 음성 모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_NEGATIVE_SET	= new StringSet(new String[] { "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅜ", "ㅠ", "ㅝ",
			"ㅞ", "ㅟ", "ㅚ", "ㅙ"						});

	/**
	 * <pre>
	 * 중성 모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_NEUTRIAL_SET	= new StringSet(new String[] { "ㅡ", "ㅣ", "ㅢ" });

	/**
	 * <pre>
	 * 겹모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_DOUBLE_SET	= new StringSet(new String[] { "ㅘ", "ㅝ", "ㅞ", "ㅟ", "ㅚ", "ㅙ" });

	public char						cho				= 0;
	public char						jung			= 0;
	public char						jong			= 0;


	public String toString()
	{
		return "(" + cho + "," + jung + "," + jong + ")";
	}


	/**
	 * <pre>
	 * endsWith를 구현하기 위해서 추가한 함수
	 * 분해된 각 자음, 모음을 붙여서 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @return
	 */
	private String get()
	{
		String ret = (cho == 0 ? "" : cho + "");
		ret += (jung == 0 ? "" : jung + "");

		switch (jong) {
			case 0:
				ret += "";
				break;
			case 'ㄳ':
				ret += "ㄱㅅ";
				break;
			case 'ㄵ':
				ret += "ㄴㅋ";
				break;
			case 'ㄶ':
				ret += "ㄴㅎ";
				break;
			case 'ㄺ':
				ret += "ㄹㄱ";
				break;
			case 'ㄻ':
				ret += "ㄹㅁ";
				break;
			case 'ㄼ':
				ret += "ㄹㅂ";
				break;
			case 'ㄽ':
				ret += "ㄹㅅ";
				break;
			case 'ㄾ':
				ret += "ㄹㅌ";
				break;
			case 'ㄿ':
				ret += "ㄹㅍ";
				break;
			case 'ㅀ':
				ret += "ㄹㅎ";
				break;
			case 'ㅄ':
				ret += "ㅂㅅ";
				break;
			default:
				ret += jong;
				break;
		}
		return ret;
	}


	public boolean hasCho()
	{
		return cho != 0;
	}


	public boolean hasJung()
	{
		return jung != 0;
	}


	public boolean hasJong()
	{
		return jong != 0;
	}


	protected static final char getCho(int idx)
	{
		char ret = 0;
		switch (idx) {
			case 0:
				ret = 'ㄱ';
				break;
			case 1:
				ret = 'ㄲ';
				break;
			case 2:
				ret = 'ㄴ';
				break;
			case 3:
				ret = 'ㄷ';
				break;
			case 4:
				ret = 'ㄸ';
				break;
			case 5:
				ret = 'ㄹ';
				break;
			case 6:
				ret = 'ㅁ';
				break;
			case 7:
				ret = 'ㅂ';
				break;
			case 8:
				ret = 'ㅃ';
				break;
			case 9:
				ret = 'ㅅ';
				break;
			case 10:
				ret = 'ㅆ';
				break;
			case 11:
				ret = 'ㅇ';
				break;
			case 12:
				ret = 'ㅈ';
				break;
			case 13:
				ret = 'ㅉ';
				break;
			case 14:
				ret = 'ㅊ';
				break;
			case 15:
				ret = 'ㅋ';
				break;
			case 16:
				ret = 'ㅌ';
				break;
			case 17:
				ret = 'ㅍ';
				break;
			case 18:
				ret = 'ㅎ';
				break;
		}
		return ret;
	}


	/**
	 * <pre>
	 * 초성에 대한 index를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param ch
	 * @return
	 */
	protected static final int getChoIdx(char ch)
	{
		int ret = -1;
		switch (ch) {
			case 'ㄱ':
				ret = 0;
				break;
			case 'ㄲ':
				ret = 1;
				break;
			case 'ㄴ':
				ret = 2;
				break;
			case 'ㄷ':
				ret = 3;
				break;
			case 'ㄸ':
				ret = 4;
				break;
			case 'ㄹ':
				ret = 5;
				break;
			case 'ㅁ':
				ret = 6;
				break;
			case 'ㅂ':
				ret = 7;
				break;
			case 'ㅃ':
				ret = 8;
				break;
			case 'ㅅ':
				ret = 9;
				break;
			case 'ㅆ':
				ret = 10;
				break;
			case 'ㅇ':
				ret = 11;
				break;
			case 'ㅈ':
				ret = 12;
				break;
			case 'ㅉ':
				ret = 13;
				break;
			case 'ㅊ':
				ret = 14;
				break;
			case 'ㅋ':
				ret = 15;
				break;
			case 'ㅌ':
				ret = 16;
				break;
			case 'ㅍ':
				ret = 17;
				break;
			case 'ㅎ':
				ret = 18;
				break;
		}
		return ret;
	}


	protected static final char getJung(int idx)
	{
		char ret = 0;
		switch (idx) {
			case 0:
				ret = 'ㅏ';
				break;
			case 1:
				ret = 'ㅐ';
				break;
			case 2:
				ret = 'ㅑ';
				break;
			case 3:
				ret = 'ㅒ';
				break;
			case 4:
				ret = 'ㅓ';
				break;
			case 5:
				ret = 'ㅔ';
				break;
			case 6:
				ret = 'ㅕ';
				break;
			case 7:
				ret = 'ㅖ';
				break;
			case 8:
				ret = 'ㅗ';
				break;
			case 9:
				ret = 'ㅘ';
				break;
			case 10:
				ret = 'ㅙ';
				break;
			case 11:
				ret = 'ㅚ';
				break;
			case 12:
				ret = 'ㅛ';
				break;
			case 13:
				ret = 'ㅜ';
				break;
			case 14:
				ret = 'ㅝ';
				break;
			case 15:
				ret = 'ㅞ';
				break;
			case 16:
				ret = 'ㅟ';
				break;
			case 17:
				ret = 'ㅠ';
				break;
			case 18:
				ret = 'ㅡ';
				break;
			case 19:
				ret = 'ㅢ';
				break;
			case 20:
				ret = 'ㅣ';
				break;
		}
		return ret;
	}


	/**
	 * <pre>
	 * 중성에 대한 index를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param ch
	 * @return
	 */
	protected static final int getJungIdx(char ch)
	{
		int ret = -1;
		switch (ch) {
			case 'ㅏ':
				ret = 0;
				break;
			case 'ㅐ':
				ret = 1;
				break;
			case 'ㅑ':
				ret = 2;
				break;
			case 'ㅒ':
				ret = 3;
				break;
			case 'ㅓ':
				ret = 4;
				break;
			case 'ㅔ':
				ret = 5;
				break;
			case 'ㅕ':
				ret = 6;
				break;
			case 'ㅖ':
				ret = 7;
				break;
			case 'ㅗ':
				ret = 8;
				break;
			case 'ㅘ':
				ret = 9;
				break;
			case 'ㅙ':
				ret = 10;
				break;
			case 'ㅚ':
				ret = 11;
				break;
			case 'ㅛ':
				ret = 12;
				break;
			case 'ㅜ':
				ret = 13;
				break;
			case 'ㅝ':
				ret = 14;
				break;
			case 'ㅞ':
				ret = 15;
				break;
			case 'ㅟ':
				ret = 16;
				break;
			case 'ㅠ':
				ret = 17;
				break;
			case 'ㅡ':
				ret = 18;
				break;
			case 'ㅢ':
				ret = 19;
				break;
			case 'ㅣ':
				ret = 20;
				break;
		}

		return ret;
	}


	protected static final char getJong(int idx)
	{
		char ret = 0;
		switch (idx) {
			case 0:
				ret = 0;
				break;
			case 1:
				ret = 'ㄱ';
				break;
			case 2:
				ret = 'ㄲ';
				break;
			case 3:
				ret = 'ㄳ';
				break;
			case 4:
				ret = 'ㄴ';
				break;
			case 5:
				ret = 'ㄵ';
				break;
			case 6:
				ret = 'ㄶ';
				break;
			case 7:
				ret = 'ㄷ';
				break;
			case 8:
				ret = 'ㄹ';
				break;
			case 9:
				ret = 'ㄺ';
				break;
			case 10:
				ret = 'ㄻ';
				break;
			case 11:
				ret = 'ㄼ';
				break;
			case 12:
				ret = 'ㄽ';
				break;
			case 13:
				ret = 'ㄾ';
				break;
			case 14:
				ret = 'ㄿ';
				break;
			case 15:
				ret = 'ㅀ';
				break;
			case 16:
				ret = 'ㅁ';
				break;
			case 17:
				ret = 'ㅂ';
				break;
			case 18:
				ret = 'ㅄ';
				break;
			case 19:
				ret = 'ㅅ';
				break;
			case 20:
				ret = 'ㅆ';
				break;
			case 21:
				ret = 'ㅇ';
				break;
			case 22:
				ret = 'ㅈ';
				break;
			case 23:
				ret = 'ㅊ';
				break;
			case 24:
				ret = 'ㅋ';
				break;
			case 25:
				ret = 'ㅌ';
				break;
			case 26:
				ret = 'ㅍ';
				break;
			case 27:
				ret = 'ㅎ';
				break;
		}
		return ret;
	}


	/**
	 * <pre>
	 * 종성에 대한 index를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param ch
	 * @return
	 */
	protected static final int getJongIdx(char ch)
	{
		int ret = -1;
		switch (ch) {
			case 0:
				ret = 0;
				break;
			case ' ':
				ret = 0;
				break;
			case 'ㄱ':
				ret = 1;
				break;
			case 'ㄲ':
				ret = 2;
				break;
			case 'ㄳ':
				ret = 3;
				break;
			case 'ㄴ':
				ret = 4;
				break;
			case 'ㄵ':
				ret = 5;
				break;
			case 'ㄶ':
				ret = 6;
				break;
			case 'ㄷ':
				ret = 7;
				break;
			case 'ㄹ':
				ret = 8;
				break;
			case 'ㄺ':
				ret = 9;
				break;
			case 'ㄻ':
				ret = 10;
				break;
			case 'ㄼ':
				ret = 11;
				break;
			case 'ㄽ':
				ret = 12;
				break;
			case 'ㄾ':
				ret = 13;
				break;
			case 'ㄿ':
				ret = 14;
				break;
			case 'ㅀ':
				ret = 15;
				break;
			case 'ㅁ':
				ret = 16;
				break;
			case 'ㅂ':
				ret = 17;
				break;
			case 'ㅄ':
				ret = 18;
				break;
			case 'ㅅ':
				ret = 19;
				break;
			case 'ㅆ':
				ret = 20;
				break;
			case 'ㅇ':
				ret = 21;
				break;
			case 'ㅈ':
				ret = 22;
				break;
			case 'ㅊ':
				ret = 23;
				break;
			case 'ㅋ':
				ret = 24;
				break;
			case 'ㅌ':
				ret = 25;
				break;
			case 'ㅍ':
				ret = 26;
				break;
			case 'ㅎ':
				ret = 27;
				break;
		}
		return ret;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author Pilho Kim [phkim@cluecom.co.kr]
	 * @since	2001. 04. 20
	 * @param ch
	 * @return
	 */
	public static Hangul split(char ch)
	{
		Hangul hangul = new Hangul();
		int x = (ch & 0xFFFF), y = 0, z = 0;
		if( x >= 0xAC00 && x <= 0xD7A3 ) {
			y = x - 0xAC00;
			z = y % (21 * 28);
			hangul.cho = getCho(y / (21 * 28));
			hangul.jung = getJung(z / 28);
			hangul.jong = getJong(z % 28);
		} else if( x >= 0x3131 && x <= 0x3163 ) {
			if( getChoIdx(ch) > -1 ) {
				hangul.cho = ch;
			} else if( getJungIdx(ch) > -1 ) {
				hangul.jung = ch;
			} else if( getJongIdx(ch) > -1 ) {
				hangul.jong = ch;
			}
		} else {
			hangul.cho = ch;
		}
		return hangul;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author Pilho Kim [phkim@cluecom.co.kr]
	 * @since	2001. 04. 20
	 * @param string
	 * @return
	 */
	public static String split(String string)
	{
		if( string == null ) return null;

		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = string.length(); i < stop; i++ ) {
			sb.append(split(string.charAt(i)));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 초성 중성 종성을 읽어들여서 한글자로 합친다.
	 * </pre>
	 * @author Pilho Kim [phkim@cluecom.co.kr]
	 * @since	2001. 04. 20
	 * @param cho	초성
	 * @param jung	중성
	 * @param jong	종성
	 * @return
	 */
	public static char combine(char cho, char jung, char jong)
	{
		return (char) (getChoIdx(cho) * 21 * 28 + getJungIdx(jung) * 28 + getJongIdx(jong) + 0xAC00);
	}


	/**
	 * <pre>
	 * 붙임말이 자음만으로 시작하는 말을 앞자의 종성에 붙여서 두 문자열을 합쳐준다.
	 * 만드 + ㄴ => 만든
	 * 만드 + ㄹ거야 => 만들거야
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 24
	 * @param head
	 * @param tail
	 * @return
	 */
	public static String append(String head, String tail)
	{
		String ret = null;

		Hangul headTail = split(head.charAt(head.length() - 1));
		Hangul tailHead = split(tail.charAt(0));

		if( tailHead.hasJung() || headTail.hasJong() ) {
			ret = head + tail;
		} else {
			String headHead = head.substring(0, head.length() - 1);
			String tailTail = tail.substring(1);
			ret = headHead + combine(headTail.cho, headTail.jung, tailHead.cho) + tailTail;
		}
		return ret;
	}


	/**
	 * <pre>
	 * 종성을 가지고 있는지를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 3. 23
	 * @param ch
	 * @return
	 */
	public static boolean hasJong(char ch)
	{
		return split(ch).hasJong();
	}


	/**
	 * <pre>
	 * 종성을 가지고 있는지를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 3. 23
	 * @param string
	 * @return
	 */
	public static boolean hasJong(String string)
	{
		if( !Util.valid(string) ) return false;
		return hasJong(string.charAt(string.length() - 1));
	}


	/**
	 * <pre>
	 * 각 글자별로 한글을 분해하고, 분해된 자음 모음을 독립적 글자로 붙여주고 각 글자단위를 :로 끊어서 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @param string
	 * @return
	 */
	public static String split2(String string)
	{
		if( string == null ) return null;
		String ret = "";
		for( int i = 0, stop = string.length(); i < stop; i++ ) {
			ret += split(string.charAt(i)).get() + ":";
		}
		return ret;
	}


	/**
	 * <pre>
	 * 해당 String이 pattern으로 끝나는지 확인하는데,
	 * ㄴ ㄹ ㅂ ㅁ 등의 자음이나 모음등을 포함해서 확인함 포함해서 확인함
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @param string
	 * @param pattern
	 * @return
	 */
	public static boolean endsWith(String string, String pattern)
	{
		if( !Util.valid(string) || !Util.valid(pattern) ) return false;
		int slen = string.length(), plen = pattern.length();
		if( slen < plen ) return false;
		char sch = 0, pch = 0;
		for( int i = 0; i < plen; i++ ) {
			sch = string.charAt(slen - i - 1);
			pch = pattern.charAt(plen - i - 1);
			if( pch != sch ) {
				if( i == plen - 1 ) return endsWith2(sch, pch);
				return false;
			}
		}
		return true;
	}


	/**
	 * <pre>
	 * 해당 char로 끝나는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 13
	 * @param sch
	 * @param pch
	 * @return
	 */
	public static boolean endsWith(char sch, char pch)
	{
		if( sch == pch ) return true;
		return endsWith2(sch, pch);
	}


	/**
	 * <pre>
	 * endsWith(char sch, char pch) 와 같은 목적이지만, 두 문자열이 같다는 것을 확인하지 않는다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 13
	 * @param sch
	 * @param pch
	 * @return
	 */
	private static boolean endsWith2(char sch, char pch)
	{
		String stemp = split(sch).get(), ptemp = split(pch).get();
		return stemp.endsWith(ptemp);
	}


	/**
	 * <pre>
	 * 주어진 pattern으로 끝나는지 확인하고, 주어진 pattern으로 끝나는 경우
	 * string에서 pattern부분을 제거해준다.
	 * '입니다' 에서 'ㅂ니다' 를 제거하면 '이' 가 반환된다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @param string
	 * @param pattern
	 * @return
	 */
	public static String removeEnd(String string, String pattern)
	{
		// validity check
		if( !Util.valid(string) || !Util.valid(pattern) ) return string;
		int slen = string.length(), plen = pattern.length();
		if( slen < plen ) return string;
		if( string.endsWith(pattern) ) return string.substring(0, slen - plen);

		// 첫글자 외에는 나머지는 전체가 다 맞아야 한다.
		if( !pattern.substring(1).equals(string.substring(slen - plen + 1)) ) return string;

		// 한글자가 안되고, 자음 , 모음 + 자음일 때를 처리해줌
		String stemp = split(string.charAt(slen - plen)).get();
		String ptemp = split(pattern.charAt(0)).get();
		if( !stemp.endsWith(ptemp) ) return string;
		String temp = stemp.substring(0, stemp.length() - ptemp.length());
		char[] ch = { 0, 0, 0 };
		for( int i = 0, stop = temp.length(); i < stop; i++ ) {
			ch[i] = temp.charAt(i);
		}
		String ret = slen > plen ? string.substring(0, slen - plen) : "";
		char rch = combine(ch[0], ch[1], ch[2]);
		if( rch == 0 ) return ret;
		return ret += combine(ch[0], ch[1], ch[2]);
	}


	/**
	 * <pre>
	 * len에 해당하는 길이를 가진 어미를 반환한다.
	 * 어미는 'ㅂ니다'와 같이 마지막 종성을 같이 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 9
	 * @param string
	 * @param len
	 * @return
	 */
	public static String extractExtraEomi(final String string, int len)
	{
		int strlen = string.length();
		if( !Util.valid(string) || strlen < len ) return null;
		Hangul hg = split(string.charAt(strlen - len));
		if( !hg.hasJong() ) return null;
		String temp = hg.get();
		return temp.charAt(temp.length() - 1) + string.substring(strlen - len + 1);
	}
}
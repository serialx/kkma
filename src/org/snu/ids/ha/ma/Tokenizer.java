/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 4. 30
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snu.ids.ha.util.Util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 4. 30
 */
public class Tokenizer
{
	public static final TokenPattern[]	PREDEFINED_TOKEN_PATTERN	= {
		// model name pattern
		new TokenPattern("[a-zA-Z0-9]+[-][a-zA-Z0-9]+",CharSetType.COMBINED), 		
		// ㅋㅋㅋ, ㅠㅠㅠㅠㅠ, ㅜㅜㅜㅜㅜ와 같은 단순 문자 반복 이모티콘
		new TokenPattern("(ㅋ|ㅠ|ㅜ|ㅎ){2,}",CharSetType.EMOTICON), 					
		new TokenPattern("(\\^){3,}",CharSetType.EMOTICON), 					
		// 수치 
		new TokenPattern("[-]?[0-9]+([,][0-9]{3})*([.][0-9]+)?",CharSetType.NUMBER),
		// 웃는 이모티콘
		// (^_^'), ^_^', d^_^b, d-_-b, (^_^), ^_^, (^-^), ^-^, (^ ^), ^^, (^.^), ^.^v, ^.^V, ^.^, (^o^), ^o^, (^3^), ^3^, ^_^", ^_^;, ^^;, ^^a
		new TokenPattern("[(][\\^]([.]|_|[-]|o|0|O|3|~|[ ])?[\\^][']?[)]", CharSetType.EMOTICON), 
		new TokenPattern("[d][\\^]([.]|_|[-]|o|0|O|3|~|[ ])?[\\^][b]", CharSetType.EMOTICON), 
		new TokenPattern("[\\^]([.]|_|[-]|o|0|O|3|~|[ ])?[\\^]([;]+|['\"avVㅗ])?", CharSetType.EMOTICON),
		// 우는 이모티콘
		new TokenPattern("[(];_;[)]", CharSetType.EMOTICON),
		new TokenPattern("[(]T[_.~oO\\^]?T[)]", CharSetType.EMOTICON),
		new TokenPattern("ㅜ[_.]?ㅜ", CharSetType.EMOTICON),
		new TokenPattern("ㅡ[_.]?ㅜ", CharSetType.EMOTICON),
		new TokenPattern("ㅜ[_.]?ㅡ", CharSetType.EMOTICON),
		new TokenPattern("ㅠ[_.]?ㅠ", CharSetType.EMOTICON),
		new TokenPattern("ㅡ[_.]?ㅠ", CharSetType.EMOTICON),
		new TokenPattern("ㅠ[_.]?ㅡ", CharSetType.EMOTICON),
		new TokenPattern("ㅠ[_.]?ㅜ", CharSetType.EMOTICON),
		new TokenPattern("ㅜ[_.]?ㅠ", CharSetType.EMOTICON),
		// 인상 찡그린 이모티콘
		// (-.-), -_-;, -_-a, -_-ㅗ, --ㅗ, -.-, -_-, (-.-)zzZ, -_-zzZ
		new TokenPattern("[(][-](_|[.])?[-]([;]+|[aㅗ])?[)](zzZ)?", CharSetType.EMOTICON),
		new TokenPattern("[-](_|[.])?[-]([;]+|[aㅗ]|(zzZ))?", CharSetType.EMOTICON),
		new TokenPattern("[ㅡ](_|[.])?[ㅡ]([;]+|[aㅗ]|(zzZ))?", CharSetType.EMOTICON),
		// (>_<), >_<, (>.<), >.<, (>_>), >_>, (¬_¬)
		new TokenPattern("[(][>]([.]|_)?[<][)]", CharSetType.EMOTICON),
		new TokenPattern("[>]([.]|_)?[<]", CharSetType.EMOTICON),
		new TokenPattern("[(][>]([.]|_)?[>][)]", CharSetType.EMOTICON),
		new TokenPattern("[>]([.]|_)?[>]", CharSetType.EMOTICON),
		new TokenPattern("[(][¬]([.]|_)?[¬][)]", CharSetType.EMOTICON),
		new TokenPattern("[¬]([.]|_)?[¬]", CharSetType.EMOTICON),
		// 윙크 이모티콘
		// (`_^), `_^, (^_~), ^_~, ~.^, ^.~
		new TokenPattern("[(]'(_|[.])\\^[)]", CharSetType.EMOTICON),
		new TokenPattern("'(_|[.])\\^", CharSetType.EMOTICON),
		new TokenPattern("\\^(_|[.])[~]", CharSetType.EMOTICON),
		new TokenPattern("[~](_|[.])\\^", CharSetType.EMOTICON),
		// 띠옹띠옹
		// (._.), (,_,), (X_X), 0.o, O_o
		new TokenPattern("[(][.][_][.][)]", CharSetType.EMOTICON),
		new TokenPattern("[(]['][_]['][)]", CharSetType.EMOTICON),
		new TokenPattern("[(][,][_][,][)]", CharSetType.EMOTICON),
		new TokenPattern("[(][X][_][X][)]", CharSetType.EMOTICON),
		new TokenPattern("[O][_.][o]", CharSetType.EMOTICON),
		new TokenPattern("[o][_.][O]", CharSetType.EMOTICON),
		// 절
		new TokenPattern("m[(]_ _[)]m", CharSetType.EMOTICON)
	};
	
	public static List<Token> tokenize(String string)
	{
		if( !Util.valid(string) ) return null;
		ArrayList<Token> tkList = new ArrayList<Token>();
		
		StringBuffer sb = new StringBuffer(string);
		
		for(int i=0, ptnlen = PREDEFINED_TOKEN_PATTERN.length; i < ptnlen; i++) {
			TokenPattern tkptn = PREDEFINED_TOKEN_PATTERN[i];
			tkList.addAll(find(sb, tkptn));
		}
		
		int strlen = string.length();
		boolean[] chkPrednfdPtn = checkFound(strlen, tkList);
		
		
		char ch;
		String temp = "";
		CharSetType presentToken = CharSetType.ETC, lastToken = CharSetType.ETC;
		int tokenIndex = 0;
		
		for( int i = 0; i < strlen; i++ ) {
			ch = sb.charAt(i);
			lastToken = presentToken;
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
			
			// 이모티콘 확인
			if( chkPrednfdPtn[i] ) {
				presentToken = CharSetType.EMOTICON;
			}
			// 이모티콘 아닌 경우 확인
			else if( ub == Character.UnicodeBlock.HANGUL_SYLLABLES
					|| ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO )
			{
				presentToken = CharSetType.HANGUL;
				// 2009-10-16 한글 자모도 인식할 수 있도록 수정
//				Hangul hg = Hangul.split(ch);
//				if( !hg.hasCho() || !hg.hasJung() ) {
//					presentToken = CharSetType.SYMBOL;
//				} else {
//					presentToken = CharSetType.HANGUL;
//				}
			} else if( ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS )
			{
				presentToken = CharSetType.HANMUN;
			} else if( (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') ) {
				presentToken = CharSetType.ENGLISH;
			} else if( ch >= '0' && ch <= '9' ) {
				presentToken = CharSetType.NUMBER;
			} else if( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' ) {
				presentToken = CharSetType.SPACE;
			} else if( ub == Character.UnicodeBlock.LETTERLIKE_SYMBOLS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.BASIC_LATIN )
			{
				presentToken = CharSetType.SYMBOL;
			} else {
				presentToken = CharSetType.ETC;
			}

			if( i != 0 
					&& (lastToken != presentToken
							|| (presentToken == CharSetType.ETC && !(temp.length() > 0 && temp.charAt(temp.length() - 1) == ch))) )
			{
				// 이미 추출된 패턴은 따로 추출함.
				if( lastToken != CharSetType.EMOTICON ) tkList.add(new Token(temp, lastToken, tokenIndex));
				
				tokenIndex = i;
				temp = "";
			}
			temp = temp + ch;

		}//end for i

		if( Util.valid(temp) ) tkList.add(new Token(temp, presentToken, tokenIndex));
		
		Collections.sort(tkList);
		
		return tkList;
	}


	/**
	 * <pre>
	 * 특정 패턴을 토큰으로 생성하여 반환한다.
	 * 토큰이 찾아진 곳은 공백으로 바꾸어서 반환한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 22
	 * @param sb	패턴을 찾을 대상 문자열
	 * @param tkptn	찾을 패턴
	 * @return
	 */
	private static List<Token> find(StringBuffer sb, TokenPattern tkptn)
	{
		if( tkptn == null ) return null;
		ArrayList<Token> tkList = new ArrayList<Token>();

		Matcher matcher = tkptn.pattern.matcher(sb);
		while( matcher.find() ) {
			tkList.add(new Token(sb.substring(matcher.start(), matcher.end()), tkptn.charSetType,  matcher.start()));
			for( int i = matcher.start(); i < matcher.end(); i++ ) {
				sb.setCharAt(i, ' ');
			}
		}
		return tkList;
	}


	/**
	 * <pre>
	 * 문자열에서 토큰이 찾아진 위치를 이미 찾아졌다고 설정.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 22
	 * @param strlen 주어진 문자열의 길이.
	 * @param tkList 이미 찾아진 토큰 목록
	 * @return
	 */
	private static boolean[] checkFound(int strlen, List<Token> tkList)
	{
		boolean[] bFound = new boolean[strlen];
		for( int i = 0; i < strlen; i++ )
			bFound[i] = false;

		for( int i = 0, size = tkList == null ? 0 : tkList.size(); i < size; i++ ) {
			Token tk = tkList.get(i);
			for( int j = 0, jsize = tk.string.length(); j < jsize; j++ ) {
				bFound[tk.index + j] = true;
			}
		}
		return bFound;
	}
}

class TokenPattern
{
	Pattern		pattern		= null;
	CharSetType	charSetType	= null;


	TokenPattern(String strPattern, CharSetType charSetType)
	{
		pattern = Pattern.compile(strPattern);
		this.charSetType = charSetType;
	}
}

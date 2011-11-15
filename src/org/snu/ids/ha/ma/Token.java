package org.snu.ids.ha.ma;


import java.io.Serializable;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 4. 30
 */
public class Token
	implements Serializable, Comparable<Token>
{
	protected String		string	= null;				// 토큰 문자열
	protected CharSetType	charSet	= CharSetType.ETC;	// 토큰의 조합 유형
	protected int			index	= 0;				// 문자열에서의 토큰의 시작 지점


	/**
	 * <pre>
	 * default constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 14
	 */
	protected Token()
	{
		super();
	}


	/**
	 *
	 * @param string
	 * @param tokenType
	 */
	protected Token(String string, CharSetType tokenType)
	{
		this(string, tokenType, 0);
	}


	/**
	 *
	 * @param string
	 * @param charSet
	 * @param index
	 */
	public Token(String string, CharSetType charSet, int index)
	{
		setString(string);
		setCharSet(charSet);
		setIndex(index);
	}


	/**
	 * <pre>
	 * 동일한 정보를 가진 Token을 생성하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 14
	 * @return
	 */
	public Object clone()
	{
		return new Token(this);
	}


	/**
	 * <pre>
	 * 문자열이 주어진 string과 같은지 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 15
	 * @param string
	 * @return
	 */
	public boolean equals(String string)
	{
		return this.string != null && string != null && this.string.equals(string);
	}


	/**
	 * <pre>
	 * copy 본을 만들어낸다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 15
	 * @param token
	 */
	public Token(Token token)
	{
		this(token.string, token.charSet, token.index);
	}


	/**
	 * @return Returns the charSet.
	 */
	public CharSetType getCharSet()
	{
		return charSet;
	}


	/**
	 * <pre>
	 * Char Set Name을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 3
	 * @return
	 */
	public String getCharSetName()
	{
		return getCharSet(charSet);
	}


	/**
	 * @return Returns the index.
	 */
	public int getIndex()
	{
		return index;
	}


	/**
	 * @return Returns the string.
	 */
	public String getString()
	{
		return string;
	}


	/**
	 * @param charSet The charSet to set.
	 */
	public void setCharSet(CharSetType charSet)
	{
		this.charSet = charSet;
	}


	/**
	 * @param index The index to set.
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}


	/**
	 * @param string The string to set.
	 */
	public void setString(String string)
	{
		this.string = string;
	}


	/**
	 * <pre>
	 * 주어진 token 타입인지를 확인하는 함수
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 3
	 * @param charSet
	 * @return
	 */
	public boolean isCharSetOf(final CharSetType charSet)
	{
		return this.charSet == charSet;
	}


	/**
	 * <pre>
	 * debugging 할 때 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 14
	 * @return
	 */
	public String toString()
	{
		return "(" + index + "," + string + "," + getCharSet(charSet) + ")";
	}


	/**
	 * <pre>
	 * char set name을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 3
	 * @param tokenType
	 * @return
	 */
	public static String getCharSet(CharSetType tokenType)
	{
		if( CharSetType.SPACE == tokenType ) {
			return "Space";
		} else if( CharSetType.HANGUL == tokenType ) {
			return "Hangul";
		} else if( CharSetType.ENGLISH == tokenType ) {
			return "English";
		} else if( CharSetType.ETC == tokenType ) {
			return "Etc";
		} else if( CharSetType.NUMBER == tokenType ) {
			return "Number";
		} else if( CharSetType.HANMUN == tokenType ) {
			return "Hanmun";
		} else if( CharSetType.SYMBOL == tokenType ) {
			return "Symbol";
		} else if( CharSetType.EMOTICON == tokenType ) {
			return "Emoticon";
		} else if( CharSetType.COMBINED == tokenType ) {
			return "Combined";
		}
		return "Undefined";
	}


	public Token copy()
	{
		Token copy = new Token();
		copy.string = string;
		copy.charSet = charSet;
		copy.index = index;
		return copy;
	}


	/**
	 * <pre>
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 16
	 * @param arg0
	 * @return
	 */
	@Override
	public int compareTo(Token tk)
	{
		return index - tk.index;
	}
}

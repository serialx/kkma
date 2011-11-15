package org.snu.ids.ha.util;


/**
 * <pre>
 * 사전 처리 시 필요한 Utility
 * </pre>
 * @author 	therocks
 * @since	2006. 11. 27
 */
public class Util
{
	public static final String	LINE_SEPARATOR	= System.getProperty("line.separator");


	/**
	 * <br>
	 * 입력된 String의 공백이나 null 상태를 확인<br>
	 * <br><b>Date : </b><DL><DD>2004-07-19</DL><br>
	 * @author therocks
	 * @param str check할 string
	 * @return null이거나 값이 없으면 false, 이외의 경우 true
	 */
	public static boolean valid(String str)
	{
		if( str == null || str.trim().equals("") ) return false;
		return true;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @param str
	 * @return
	 */
	public static int getTabCnt(String str)
	{
		int cnt = 0;
		char ch;
		for( int i = 0; i < str.length(); i++ ) {
			ch = str.charAt(i);
			if( ch == ' ' || ch == '\t' ) cnt++;
		}
		return cnt;
	}


	/**
	 * <pre>
	 * 특정 크기만큼의 tab을 반환
	 * </pre>
	 * @param cnt
	 * @return
	 */
	private static String getTab(int cnt)
	{
		String tab = "";
		for( int i = 0; i < cnt; i++ )
			tab += "\t";
		return tab;
	}


	/**
	 * <pre>
	 * 일정 크기의 너비를 가지는 문자열을 만들어준다.
	 * tab size와 너비를 넘겨주면 해당 너비만큼 tab이 추가되도록 해서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @param string
	 * @param tabSize
	 * @param width
	 * @return
	 */
	public static String getTabbedString(String string, int tabSize, int width)
	{
		int cnt = (string == null ? 0 : string.getBytes().length);
		String ret = string + getTab((width - cnt) / tabSize);
		if( cnt % tabSize != 0 ) ret += "\t";
		return ret;
	}


	/**
	 * <pre>
	 * XML특수 문자를 교채한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 04
	 * @param src
	 * @return
	 */
	public final static String rplcXMLSpclChar(String src)
	{
		return src.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\\\"", "&quot;").replaceAll("'", "&apos;");
	}
}

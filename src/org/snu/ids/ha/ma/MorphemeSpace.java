/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 25
 */
package org.snu.ids.ha.ma;


import java.util.StringTokenizer;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.POSTag;


/**
 * <pre>
 * 띄어 쓰기를 해주기 위한 class
 * MCandidate가 앞뒤 조건들을 저장하듯이, space는 앞뒤 조건들을 설정할 수 있도록 함
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 25
 */
public class MorphemeSpace
	extends Morpheme
{
	long	atlEnc	= 0;
	long	hclEnc	= 0;
	long	cclEnc	= 0;
	long	bclEnc	= 0;
	long	eclEnc	= 0;
	long	pclEnc	= 0;


	protected MorphemeSpace()
	{
		super(" ", 0);
		charSet = CharSetType.SPACE;
		infoEnc = POSTag.SW;
	}


	/**
	 * <pre>
	 * 띄어쓰기된 것을 정의할 수 있도록 함
	 * 앞뒤 연결될 때의 부가 정보들을 정의하여 가지고 있을 수 있도록 하고,
	 * 이를 지정하도록 지원하는 생성자
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param source
	 */
	MorphemeSpace(String source)
	{
		this();
		String[] arr = source.split("/");
		if( arr.length > 1 ) {
			StringTokenizer st = new StringTokenizer(arr[1], "*" + MCandidate.DLMT_ATL + MCandidate.DLMT_HCL + MCandidate.DLMT_CCL + MCandidate.DLMT_ECL + MCandidate.DLMT_PCL, true);
			String token = null;
			while( st.hasMoreTokens() ) {
				token = st.nextToken();
				// 접속 가능한 품사 정보
				if( token.equals(MCandidate.DLMT_ATL) ) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.atlEnc = POSTag.getTagNum(token.split(","));
				}
				// 현재 후보가 가진 접속 조건
				else if( token.equals(MCandidate.DLMT_HCL) ) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.hclEnc = Condition.getCondNum(token.split(","));
				}
				// 접속할 때 확인해야 하는 조건
				else if( token.equals(MCandidate.DLMT_CCL) ) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.cclEnc = Condition.getCondNum(token.split(","));
				}
				// 접속할 때 배제해야 하는 조건
				else if( token.equals(MCandidate.DLMT_ECL) ) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.eclEnc = Condition.getCondNum(token.split(","));
				}
				// 뛰어스기 포함해서 이전에 나올 수 있는 품사
				else if( token.equals(MCandidate.DLMT_PCL) ) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.pclEnc = Condition.getCondNum(token.split(","));
				}
			}
		}
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param atlEnc	
	 * @param hclEnc
	 * @param bclEnc
	 * @param cclEnc
	 * @param eclEnc
	 * @param pclEnc
	 */
	MorphemeSpace(long atlEnc, long hclEnc, long bclEnc, long cclEnc, long eclEnc, long pclEnc)
	{
		this();
		charSet = CharSetType.SPACE;
		infoEnc = POSTag.SW;
		this.atlEnc = atlEnc;
		this.hclEnc = hclEnc;
		this.bclEnc = bclEnc;
		this.cclEnc = cclEnc;
		this.eclEnc = eclEnc;
		this.pclEnc = pclEnc;
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
		return " ";
	}


	public String getToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(string + "/");

		// 접속 가능한 품사 정보
		String temp = POSTag.getTagStr(atlEnc);
		if( temp != null ) sb.append(MCandidate.DLMT_ATL + "(" + temp + ")");

		// 현재 후보가 가진 접속 조건
		temp = Condition.getCondStr(hclEnc);
		if( temp != null ) sb.append(MCandidate.DLMT_HCL + "(" + temp + ")");

		// 접속할 때 확인해야 하는 조건
		temp = Condition.getCondStr(cclEnc);
		if( temp != null ) sb.append(MCandidate.DLMT_CCL + "(" + temp + ")");
		
		// 접속할 때 배제해야 하는 조건
		temp = Condition.getCondStr(eclEnc);
		if( temp != null ) sb.append(MCandidate.DLMT_ECL + "(" + temp + ")");

		// 뛰어스기 포함해서 이전에 나올 수 있는 품사
		temp = Condition.getCondStr(pclEnc);
		if( temp != null ) sb.append(MCandidate.DLMT_PCL + "(" + temp + ")");

		return sb.toString();
	}


	/**
	 * <pre>
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @return
	 */
	public Morpheme copy()
	{
		return new MorphemeSpace(atlEnc, hclEnc, bclEnc, cclEnc, eclEnc, pclEnc);
	}
}
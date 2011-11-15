/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;


/**
 * <pre>
 * 형태소 정보를 저장하는 Class
 * List의 간단 버젼
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class MorphemeList
	extends ArrayList<Morpheme>
{
	Morpheme	firstMorp	= null; // 분석열의 첫번째 형태소
	Morpheme	lastMorp	= null; // 분석열의 마지막 형태소


	/**
	 * <pre>
	 * deault constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	public MorphemeList()
	{
		super();
	}


	/**
	 * <pre>
	 * 형태소 정보 저장
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mp
	 */
	public boolean add(Morpheme mp)
	{
		if( firstMorp == null ) firstMorp = mp;
		lastMorp = mp;
		return super.add(mp);
	}


	/**
	 * <pre>
	 * 주어진 위치에 해당하는 형태소와 다음 형태소를 결합하여 하나의 형태소로 만든다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param idx
	 */
	public void mergeAt(int idx)
	{
		Morpheme mp1 = get(idx);
		Morpheme mp2 = remove(idx + 1);
		mp1.append(mp2);
		if( mp2 == lastMorp ) lastMorp = mp1;
	}


	/**
	 * <pre>
	 * 형태소 분석 결과의 동일성을 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param ml
	 * @return
	 */
	public boolean equals(MorphemeList ml)
	{
		return getEncStr().equals(ml.getEncStr());
	}


	/**
	 * <pre>
	 * 분석된 형태소 목록을 출력한다.
	 * 형태소+형태소+형태소 와 같은 형태로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 분석된 형태소 목록을 출력한다.
	 * 형태소+형태소+형태소 와 같은 형태로 출력한다.
	 * 형태소의 인덱스 정보를 제외한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @return
	 */
	public String getSmplStr2()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i).getSmplStr2());
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 분석된 형태소 목록을 출력한다.
	 * encoding된 형태로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	String getEncStr()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i).getEncStr());
		}
		return sb.toString();
	}


	/**
	 * @return the firstMorp
	 */
	public Morpheme getFirstMorp()
	{
		return firstMorp;
	}


	/**
	 * @return the lastMorp
	 */
	public Morpheme getLastMorp()
	{
		return lastMorp;
	}


	public int getStartIndex()
	{
		return firstMorp.index;
	}
}
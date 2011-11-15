/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 26
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;


/**
 * <pre>
 * 한문장을 이루는 어절들의 리스트를 가진다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 26
 */
public class Sentence
	extends ArrayList<Eojeol>
{

	public Sentence()
	{
		super();
	}


	public boolean add(Eojeol e)
	{
		Eojeol ej = e.removeIncorrectlyCombinedEojeol();
		if( ej != null ) {
			super.add(ej);
		}
		return super.add(e);
	}


	/**
	 * <pre>
	 * 문장의 띄어쓰기가 복원된 것을 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @return
	 */
	public String getSentence()
	{
		StringBuffer sb = new StringBuffer();
		Eojeol eojeol = null;
		String temp = null;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			eojeol = get(i);
			temp = eojeol.exp;
			if( i > 0 ) sb.append(" ");
			sb.append(temp);
		}
		return sb.toString();
	}
}

/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 26
 */
package org.snu.ids.ha.ma;


import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 26
 */
public class Eojeol
	extends MorphemeList
{
	String	exp	= null;


	private Eojeol()
	{
		super();
	}


	public Eojeol(MCandidate mc)
	{
		exp = mc.getExp();
		this.addAll(mc);
		this.firstMorp = mc.firstMorp;
		this.lastMorp = mc.lastMorp;
	}


	public Eojeol(MExpression me)
	{
		this(me.get(0));
	}


	/**
	 * <pre>
	 * 어절의 원래 모양을 스트링으로 리턴한다.
	 * </pre>
	 * @author myung
	 * @since  2007. 6. 27
	 * @return 어절의 표층형
	 */
	public String getExp()
	{
		return exp;
	}
	
	
	/**
	 * <pre>
	 * 보조사의 경우 이전 조사의 격을 그대로 유지하기 위해서 사용
	 * 보조사만으로 연결된 경우는 보조사임을 유지함.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 11. 01
	 * @param tag
	 * @return 마지막 tag가 주어진 tag를 만족하는지 여부
	 */
	public boolean isLastTagOf(long tag)
	{
		if( lastMorp.isTag(POSTag.JX) ) {
			for( int i = size() - 1; i > 0; i-- ) {
				Morpheme mp = get(i);
				if( mp.isTag(POSTag.JX) ) continue;
				if( mp.isTagOf(POSTag.J | POSTag.EM) ) return mp.isTagOf(tag);
				return lastMorp.isTagOf(tag);
			}
		} else if( lastMorp.isTagOf(POSTag.S) ) {
			for( int i = size() - 1; i > 0; i-- ) {
				Morpheme mp = get(i);
				if( mp.isTag(POSTag.S) ) continue;
				return mp.isTagOf(tag);
			}
		}
		return lastMorp.isTagOf(tag);
	}


	/**
	 * <pre>
	 * 해당 태그를 가진 형태소가 있는지 확인한다.
	 * VCP와의 의존 관계를 위한 함수.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 14
	 * @param tag
	 * @return 어절이 주어진 태그를 가지고 있는지 여부 
	 */
	public boolean containsTagOf(long tag)
	{
		for( int i = 0, size = size(); i < size; i++ ) {
			Morpheme mp = get(i);
			if( mp.isTagOf(tag) ) return true;
		}
		return false;
	}


	/**
	 * <pre>
	 * 문장의 종결을 나타내는 어절인지 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @return 문장의 종결 여부
	 */
	public boolean isEnding()
	{
		return lastMorp.isTagOf(POSTag.EF);
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @return
	 */
	Eojeol removeIncorrectlyCombinedEojeol()
	{
		if( size() < 2 ) return null;
		Morpheme mp1 = get(0);
		Morpheme mp2 = get(1);

		// 부사 + 용언의 잘못 처리, 관형사 + 체언의 잘 못 처리 
		if( mp1.isTagOf(POSTag.MA) && mp2.isTagOf(POSTag.V) || mp1.isTagOf(POSTag.MD) && mp2.isTagOf(POSTag.N) ) {
			Eojeol ej = new Eojeol();
			ej.exp = mp1.string;
			ej.add(mp1);
			exp = exp.substring(ej.exp.length());
			remove(mp1);
			return ej;
		}

		// 보조용언의 띄어쓰기 틀림 처리
		for( int i = 1; i < size(); i++ ) {
			mp1 = get(i - 1);
			mp2 = get(i);

			if( mp1.isTag(POSTag.ECS) && mp2.isTagOf(POSTag.VP) ) {
				Eojeol ej = new Eojeol();

				int idx = 0;
				for( int j = 0; j < i; j++ ) {
					if( j == 0 ) idx = get(0).index;
					ej.add(get(0));
					remove(0);
				}
				ej.exp = exp.substring(0, mp2.index - idx);

				exp = exp.substring(mp2.index - idx);
				return ej;
			}
		}
		return null;
	}


	public String toString()
	{
		return Util.getTabbedString(getExp(), 4, 16) + "=> [" + super.toString() + "]";
	}
}

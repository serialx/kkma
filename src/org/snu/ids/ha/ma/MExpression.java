/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class MExpression
	extends ArrayList<MCandidate>
	implements Comparable<MExpression>
{
	/**
	 * <pre>
	 * 형태소의 표층형
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	String	exp				= null;
	float	lnprOfSpacing	= 0;


	/**
	 * <pre>
	 * default constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param exp
	 */
	MExpression(String exp)
	{
		super();
		this.exp = exp;
	}


	/**
	 * <pre>
	 * default constructor
	 * 하나의 기분석 형태소 분석 결과를 저장한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param mc
	 */
	public MExpression(String exp, MCandidate mc)
		throws Exception
	{
		this(exp);
		add(mc);
	}


	/**
	 * <pre>
	 * 기분석 후보 하나를 가진 표현형 생성
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @param mc
	 * @throws Exception
	 */
	MExpression(MCandidate mc)
		throws Exception
	{
		this(mc.getExp());
		add(mc);
	}


	/**
	 * <pre>
	 * 표층형에 대한 기분석 형태소 분석 결과를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mc
	 * @param doMerge
	 */
	public boolean add(MCandidate mc)
	{
		if( mc != null && !contains(mc) ) {
//			MCandidate mc2 = null;
//			for( int i = 0, size = size(); i < size; i++ ) {
//				mc2 = get(i);
//				if( mc2.merge(mc) ) return true;
//			}
			return super.add(mc);
		}
		return false;
	}
	
	
	/**
	 * <pre>
	 * merge되었던 것을 분해하기 위함.
	 * merge작업 없이 추가하는 함수
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param mc
	 * @return
	 */
	public boolean add2(MCandidate mc)
	{
		if( mc != null && !contains(mc) ) return super.add(mc);
		return false;
	}


	/**
	 * @return Returns the exp.
	 */
	public String getExp()
	{
		return exp;
	}


	/**
	 * <pre>
	 * 각 후보들의 분석 결과에 대한 index(offset)정보를 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param index	시작 offset
	 */
	void setIndex(int index)
	{
		for(int i=0, size = size(); i < size; i++) {
			get(i).setIndex(index);
		}
	}


	/**
	 * <pre>
	 * 표층형 사전 정보를 문자열로 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(exp + Util.LINE_SEPARATOR);
		
		sb.append("\tscore\t");
		sb.append("\trdl");
		sb.append("\tcdl");
		sb.append("\tspc");
		sb.append("\tpcl");
		sb.append("\txs");
		sb.append("\tsiz");
		sb.append("\tbest score");
		sb.append("\tprob" + Util.LINE_SEPARATOR);
		
		for( int i = 0, stop = size(); i < stop; i++ ) {
			sb.append("\t{" + get(i) + "};" + Util.LINE_SEPARATOR);
		}
		return sb.toString();
	}


	public String toSmplStr()
	{
		StringBuffer sb = new StringBuffer(exp + Util.LINE_SEPARATOR);
		for( int i = 0, stop = size(); i < stop; i++ ) {
			sb.append("\t{" + get(i).toSimpleStr() + "};" + Util.LINE_SEPARATOR);
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * mCandidate의 encoding된 문자열을 읽어들인다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 11
	 * @return
	 */
	String getEncStr()
	{
		StringBuffer sb = new StringBuffer(exp + ":");
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append(";");
			sb.append(get(i).getEncStr());
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 덧붙는 mExp에 대해서 실재 가능한 형태소 분석 결과를 생성하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param meToAppend
	 * @return
	 */
	MExpression derive(MExpression meToAppend)
	{
		MExpression ret = new MExpression(this.exp + meToAppend.exp);
		MCandidate mcThis = null, mcToAppend = null;
		int jStop = meToAppend.size();
		for( int i = 0, iStop = size(); i < iStop; i++ ) {
			mcThis = get(i);
			for( int j = 0; j < jStop; j++ ) {
				mcToAppend = meToAppend.get(j);
				ret.add(mcThis.derive(mcToAppend));
			}
		}
		ret.prune();
		return ret;
	}


	private static final int PRUNE_SIZE = 12;


	/**
	 * <pre>
	 * 우선 순위가 떨어지는 것들은 후보 분석 결과에서 제외해버림
	 * 성능 문제로~
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	void prune()
	{
		int size = size();
		if( size < 2 ) return;

		int maxDicLen = -1, maxSpaceCnt = 0;
		int expLen = exp.length(), tempDicLen = 0;

		// 최적 후보 확인
		sort();
		MCandidate mcBest = get(0);

		// 완전히 종결되지 않으면 완전히 종결된 어휘를 남겨둔다.
		boolean uncomplete = mcBest.candDicLen > 0 || expLen > mcBest.getDicLenWithCand();

		// 최적후보의 실 사전어 길이 확인
		maxDicLen = mcBest.realDicLen;
		maxSpaceCnt = mcBest.numOfSpace;
		if( maxDicLen == expLen ) {
			// 마지막이 한글자 조사일 때에는 띄어쓰기에 의한 prunning을 할 때 띄어쓰기 하나를 더 허용해준다.
			// 다음 한글자로 인해서 더 적합한 결과가 나올 수 있는데 이것이 잘리는 것을 방지하기 위함
			if( mcBest.lastMorp.isTagOf(POSTag.J)
					&& mcBest.lastMorp.string.length() == 1 )
			{
				maxSpaceCnt++;
			}
		}
		// 종결되지 않은 것이 있으면 prunning하지 않는다.
		else if( !uncomplete && size < PRUNE_SIZE ) {
			return;
		}

		// 이외 후보 확인
		int pruneIdx = 1;
		for( int stop = size(); pruneIdx < stop; ) {
			MCandidate mcToPrune = get(pruneIdx);

			// 사전어 길이 확인
			tempDicLen = mcToPrune.getDicLenWithCand();

			// 결과가 너무 많이 나오면 강제로 prunning해버린다.
			// 완결되지 않았으면
			if( uncomplete && mcToPrune.getDicLenOnlyCand() == 0 && pruneIdx < PRUNE_SIZE ) {
				pruneIdx++;
				continue;
			}

			// 사전어 길이가 짧으면 삭제
			if( tempDicLen < maxDicLen ) break;

			// 띄어쓰기가 많은 것은 제외
			if( (maxSpaceCnt > 0 || stop > PRUNE_SIZE) && mcToPrune.numOfSpace > maxSpaceCnt ) break;

			pruneIdx++;
		}

		// 순위를 벗어난 것들을 삭제해줌
		for( int i = pruneIdx, stop = size(); i < stop; i++ ) {
			// 2008-03-18: 완전히 사전어로 이루어지지 않은 경우 마지막 미등록어를 남겨두어 추후 이상한 결과를 만들어 내는 것을 방지
			if( uncomplete && i == stop - 1 && get(pruneIdx).realDicLen == 0 ) break;
			remove(pruneIdx);
		}
	}


	/**
	 * <pre>
	 * 앞의 어절의 후보 결과들과 성립되는 것이 없으면 삭제한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @param mePrev
	 * @throws Exception
	 */
	void pruneWithPrev(MExpression mePrev)
		throws Exception
	{
		if( mePrev == null ) return;
		int thisMESize = this.size(), preMESize = mePrev.size();
		if( preMESize == 0 ) return;
		for( int i = 0; i < thisMESize; i++ ) {
			MCandidate mcThis = this.get(i);
			mcThis.numOfApndblMC = 0;
			for( int j = 0; j < preMESize; j++ ) {
				MCandidate preMC = mePrev.get(j);
				
				// 띄어쓰기 가능한지 확인, 띄어쓰기 오류인지 확인
				if( preMC.isApndblWithSpace(mcThis) || preMC.isApndbl(mcThis) ) {
					mcThis.numOfApndblMC++;
					break;
				}
			}
			if( mcThis.numOfApndblMC == 0 ) {
				this.remove(i);
				i--;
				thisMESize--;
			}
		}
	}


	/**
	 * <pre>
	 * 앞의 기분석 후보들 중에 가능한 결과만 남겨둔다.
	 * prune을 강하게 하면 속도는 빨라지지만, 분석 오류가 발생할 수 있다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param nextME
	 */
	void pruneWithNext(MExpression nextME)
		throws Exception
	{
		int thisMESize = this.size(), nextMESize = nextME.size();
		if( nextMESize == 0 ) return;
		for( int i = 0; i < thisMESize; i++ ) {
			MCandidate thisMC = get(i);
			thisMC.numOfApndblMC = 0;
			for( int j = 0; j < nextMESize; j++ ) {
				MCandidate nextMC = nextME.get(j);

				// 띄어쓰기 포함 결합 가능 확인
				if( thisMC.isApndblWithSpace(nextMC) ) {
					thisMC.numOfApndblMC++;
					break;
				}
			}
			if( thisMC.numOfApndblMC == 0 && this.size() > 1 ) {
				remove(i);
				i--;
				thisMESize--;
			}
		}
	}



	/**
	 * <pre>
	 * 앞쪽 문자열을 headStr로 하고, 뒷쪽 문자열을 tailStr로 하는 분리된 기분석 후보를 생성하여 반환한다.
	 * 앞, 뒤를 자르는 위치는 divideIdx번째 띄어쓰기로 한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param headStr
	 * @param headIndex	head 부분의 token index시작
	 * @param tailStr
	 * @param tailIndex	tail 부분의 token index시작
	 * @return
	 */
	MExpression[] divideHeadTailAt(String headStr, int headIndex, String tailStr, int tailIndex)
		throws Exception
	{
		MExpression[] ret = new MExpression[2];
		MExpression headME = ret[0] = new MExpression(headStr);
		MExpression tailME = ret[1] = new MExpression(tailStr);

		for( int j = 0, stop = size(); j < stop; j++ ) {
			MCandidate[] mcHeadTail = get(j).divideHeadTailAt(headStr, headIndex, tailStr, tailIndex);
			if( mcHeadTail != null && mcHeadTail[0].getExp().equals(headStr) ) {
				headME.add(mcHeadTail[0]);
				tailME.add(mcHeadTail[1]);
			} else {
				headME.add(new MCandidate(headStr, headIndex));
				tailME.add(new MCandidate(tailStr, tailIndex));
			}
		}
		return ret;
	}


	/**
	 * <pre>
	 * 후보 분석 결과들을 합쳐준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mExp
	 */
	void merge(MExpression mExp)
	{
		for( int i = 0, stop = mExp.size(); i < stop; i++ ) {
			add(mExp.get(i));
		}
		prune();
	}


	/**
	 * <pre>
	 * 띄어쓰기를 기준으로 각 표현형을 분해해서 반환해준다.
	 * 띄어쓰기가 제대로 수행되지 않을 때에는 표현형을 기준으로 그대로 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	List<MExpression> split()
		throws Exception
	{
		if( size() == 0 ) return null;
		ArrayList<MExpression> ret = new ArrayList<MExpression>();

		// 기준 설정
		MCandidate mc = this.get(0);
		List<MCandidate> splitedMCList = mc.split();
		int splitedMCSize = splitedMCList.size();
		for( int i = 0; i < splitedMCSize; i++ ) {
			ret.add(new MExpression(splitedMCList.get(i)));
		}

		int size = size();

		// 이후 동일한 띄어쓰기 보이는 후보 저장
		if( size > 1 ) {
			String preExpWithSpace = mc.geExpStrWithSpace();
			for( int i = 1; i < size; i++ ) {
				mc = get(i);
				// 동일한 띄어쓰기인지 확인
				String curExpWithSpace = mc.geExpStrWithSpace();
				
				if( !preExpWithSpace.equals(curExpWithSpace) ) continue;
				
				// 쪼개서 저장
				splitedMCList = mc.split();
				for( int j = 0; j < splitedMCSize; j++ ) {
					ret.get(j).add(splitedMCList.get(j));
				}
			}
		}

		return ret;
	}


	/**
	 * <pre>
	 * 띄어쓰기가
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 25
	 * @return
	 */
	boolean isOneEojeol()
	{
		return size() > 0 && get(0).numOfSpace == 0;
	}


	/**
	 * <pre>
	 * 현재 후보를 점수에 따라서 정렬한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 */
	void sort()
	{
		Collections.sort(this);
	}


	/**
	 * <pre>
	 * 할거 다 하고 먼가 더 필요하면 rule을 적용하여 처리한다.
	 * 동일한 우선 순위이면 첫번째 형태소가 긴것을 우선시 한다.
	 * 보통 분석시에 앞이 길게 분석되는 경우가 많으므로!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	void sortBfrSpacing()
	{
		if( this.size() < 2 ) return;
		final int expLen = exp.length();
		Collections.sort(this, new Comparator<MCandidate>()
		{
			public int compare(final MCandidate mc1, final MCandidate mc2)
			{
				int ret = 0;

				// 미등록어에 따른 처리
				if( mc1.realDicLen < expLen && mc2.realDicLen < expLen && Math.abs(mc1.realDicLen - mc2.realDicLen) < 3 ) {
					ret = mc1.numOfSpace - mc2.numOfSpace;
				}

				// 기본 정렬
				if( ret == 0 ) ret = mc1.compareTo(mc2);

				// Occams Razor
				if( ret == 0 ) ret = mc1.size() - mc2.size();

				// 짧은 끝말 우선 원칙
				if( ret == 0 && mc1.firstMorp != mc1.lastMorp && mc2.firstMorp != mc2.lastMorp ) {
					ret = mc1.lastMorp.string.length() - mc2.lastMorp.string.length();
					if( ret == 0 ) {
						ret = mc2.firstMorp.string.length() - mc1.firstMorp.string.length();
					}
				}
				
				return ret;
			}
		});
	}
	
	
	/**
	 * <pre>
	 * 최적 후보 점수를 이용해서 정렬해준다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 20
	 */
	void sortByScoreNProb()
	{
		Collections.sort(this, new Comparator<MCandidate>()
		{

			@Override
			public int compare(MCandidate arg0, MCandidate arg1)
			{
				float prob1 = arg0.lnprOfBestMC, prob2 = arg1.lnprOfBestMC;
				int score1 = arg0.scoreOfBestMC, score2 = arg1.scoreOfBestMC;
				if( prob1 > prob2 + 10 ) {
					return -1;
				} else if( prob1 + 10 < prob2 ) {
					return 1;
				} else if( score1 > score2 ) {
					return -1;
				} else if( score1 < score2 ) {
					return 1;
				} else {
					if( prob1 > prob2 ) {
						return -1;
					} else if( prob2 > prob1 ) {
						return 1;
					}
				}
				return 0;
			}
		});
	}


	/**
	 * <pre>
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 28
	 * @param comp
	 * @return
	 */
	public int compareTo(MExpression comp)
	{
		return this.exp.compareTo(comp.exp);
	}


	/**
	 * <pre>
	 * 공통된 띄어쓰기를 하는 head를 반환한다.
	 * 최초로 발견되는 head를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @return
	 */
	String getCommonHead()
	{
		sort();
		MCandidate mc = get(0);
		int spaceCnt = mc.numOfSpace;
		if( spaceCnt < 1 ) return null;

		int size = size();
		for( int i = 0; i < spaceCnt; i++ ) {
			String tempCommonHead = null, minCommonHead = null;
			for( int j = 0; j < size; j++ ) {
				tempCommonHead = get(j).getExp(i);
				if( j == 0 ) {
					minCommonHead = tempCommonHead;
				} else if( !minCommonHead.equals(tempCommonHead) ) {
					minCommonHead = null;
					break;
				}
			}
			// 공통 문자열을 반환
			if( minCommonHead != null && minCommonHead.length() > 1 ) {
				// 바로 직전이나 다음자가 미등록어이면 더 분석 되도록 둠
				if( mc.isUNBfrOrAftrIthSpace(i) ) {
					minCommonHead = null;
					continue;
				}

				// 공통 head로 반환
				return minCommonHead;
			}
		}
		return null;
	}


	/**
	 * <pre>
	 * 띄어쓰기 오류가 없는 완전한 문장인지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @return
	 */
	boolean isComplete()
		throws Exception
	{
		return size() > 0 && get(0).isComplete();
	}


	/**
	 * <pre>
	 * 숫자를 분석한 후보인지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	boolean isOneEojeolCheckable()
	{
		if( size() == 1 ) {
			MCandidate mc = get(0);
			Morpheme mp = mc.firstMorp;
			if( mc.size() == 1
					&& (mp.isCharSetOf(CharSetType.NUMBER)
							|| mp.isCharSetOf(CharSetType.ENGLISH)
							|| mp.isCharSetOf(CharSetType.COMBINED) ) )
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * 사전 정보에서 겹치는걸 방지하기 위함
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @return
	 */
	public MExpression copy()
	{
		MExpression copy = new MExpression(this.exp);
		for( int i = 0, stop = size(); i < stop; i++ ) {
			copy.add(get(i).copy());
		}
		return copy;
	}
	
	
	/**
	 * <pre>
	 * 최적 후보 하나만 남기고 모두 삭제한다.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 01
	 */
	public void leaveJustBest()
	{
		for( int i = size() - 1; i > 0; i-- ) {
			remove(i);
		}
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param mePrev
	 */
	public void setBestPrevMC(MExpression mePrev)
	{
		for( int i = 0, size = size(); i < size; i++ ) {
			MCandidate mcCur = get(i);

			if( mePrev == null ) {
				mcCur.setBestPrevMC(null);
			} else {
				for( int j = 0, jSize = mePrev.size(); j < jSize; j++ ) {
					MCandidate mcPrev = mePrev.get(j);
					mcCur.setBestPrevMC(mcPrev);
				}
			}
		}
	}


	/**
	 * <pre>
	 * 한글 이외의 것인지 확인한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @return
	 */
	public boolean isNotHangul()
	{
		return size() == 1 && get(0).isNotHangul();
	}


	public MCandidate getBest()
	{
		return get(0);
	}


	/**
	 * @return the lnprOfSpacing
	 */
	public float getLnprOfSpacing()
	{
		return lnprOfSpacing;
	}


	/**
	 * @param lnprOfSpacing the lnprOfSpacing to set
	 */
	public void setLnprOfSpacing(float lnprOfSpacing)
	{
		this.lnprOfSpacing = lnprOfSpacing;
	}

}

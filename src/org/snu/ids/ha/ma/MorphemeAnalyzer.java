/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.dic.Dictionary;
import org.snu.ids.ha.dic.SpacingPDDictionary;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 한글, 숫자, 영문자 등을 포함한 문자열에 대한 형태소 분석을 수행한다.
 * 동적 프로그래밍 기법을 활용해서 한다.
 * 띄어쓰기 단위로 수행하지 않고, token단위로 동적 프로그래밍을 하여,
 * 띄어쓰기에 대한 내성을 가지도록 한다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class MorphemeAnalyzer
{
	protected Dictionary dic	= null;


	/**
	 * <pre>
	 * default constructor
	 * 사전 객체를 얻어온다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	public MorphemeAnalyzer()
	{
		dic = Dictionary.getInstance();
	}


	/**
	 * <pre>
	 * 형태소 분석을 수행한다.
	 * 수행 결과에는 후보 분석 결과가 List에 담겨서 반환된다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @return
	 * @throws Exception 
	 */
	public List<MExpression> analyze(String string)
		throws Exception
	{
		if( !Util.valid(string) ) return null;
		string = string.trim();

		// 결과 객체
		ArrayList<MExpression> ret = new ArrayList<MExpression>();

		// 문자 셋 단위로 쪼개서어 토큰 리스트로 만든다.
		List<Token> tokenList = Tokenizer.tokenize(string);

		// 한글 토큰에 대해서 형태소 분석을 수행하고, 특수기호, 수치 등에 대한 형태소 지정 과정을 수행해준다.
		MExpression mePrev = null, meCur = null;
		for( int i = 0, stop = tokenList.size(); i < stop; i++ ) {
			Token token = tokenList.get(i);
			if( token.isCharSetOf(CharSetType.SPACE) ) continue;
			List<MExpression> meList = analyze(mePrev, token);
			
			// 이전 결과를 확인하면서 적합하지 않은 결과는 없앤다.
			for( int j = 0, jStop = meList.size(); j < jStop; j++ ) {
				meCur = meList.get(j);
				if( mePrev != null ) mePrev.pruneWithNext(meCur);
				ret.add(meCur);
				mePrev = meCur;
			}
		}
		
		return ret;
	}
	
	

	/**
	 * <pre>
	 * 해당 token에 대한 가능한 형태소 분석 결과를 반환한다.
	 * 긴 문장에 대해서는 여러개의 MExpression을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param preMe
	 * @param token
	 * @return
	 */
	private List<MExpression> analyze(MExpression mePrev, Token token)
		throws Exception
	{
		List<MExpression> ret = new ArrayList<MExpression>();
		// 한글 이외의 분석 결과
		if( !token.isCharSetOf(CharSetType.HANGUL) ) {
			ret.add(new MExpression(token.string, new MCandidate(token)));
			return ret;
		}
		

		// 한글에 대한 토큰 분석 결과
		String string = token.string;
		int strlen = string.length();
		MExpression meHeadTemp = null, meTailTemp = null, meNew = null;

		MExpression[] meArr = new MExpression[strlen];
		String substr = null, tail = null;
		int tailCutPos = 1;
		
		// 해당 분석 문자열의 주어진 string에서의 offset정보로 저장하기 위한 변수
		int firstOffset = token.index;
		for( ; tailCutPos <= strlen; tailCutPos++ ) {
			// 현재 분석 문자열 설정
			substr = string.substring(0, tailCutPos);

			// 최초 분석 후보 생성
			MExpression meCur = meArr[tailCutPos - 1] = getMExpression(substr, firstOffset);

			// 직전 결과를 바탕으로 불가능한 결과 삭제
			meCur.pruneWithPrev(mePrev);

			// 현재 결과내에서 가능한 조합 확인
			for( int headCutPos = 1; headCutPos < tailCutPos; headCutPos++ ) {
				writeLog("==========================================[" + substr + "]");
				tail = substr.substring(headCutPos, tailCutPos);
				meTailTemp = getMExpression(tail, firstOffset + headCutPos);
				meHeadTemp = meArr[headCutPos - 1];
				meNew = meHeadTemp.derive(meTailTemp);
				meNew.pruneWithPrev(mePrev);
				writeLog("[     HEAD ] " + meHeadTemp);
				writeLog("[     TAIL ] " + meTailTemp);
				writeLog("[GENERATED ] " + meNew);
				writeLog("[   STORED ] " + meCur);
				meCur.merge(meNew);
				writeLog("[   MERGED ] " + meCur);
				writeLog("==================================================");
			}
			
			// 마지막 지점에서는 head 추출하지 않는다.
			if( tailCutPos == strlen ) continue;

			// 불완전한 경우는 head를 추출하지 않는다.
			if( !meCur.isComplete() ) continue;

			
			// TODO 확률기반 모델을 활용해서 처리 성능 개선하도록 해보자.
			// 후보가 3개 이상이거나, 후보가 2개 이상이며 문자열의 길이가 5이상이면 공통된 앞부분(commonHead)을 확인한다.
			if( strlen > 5 && meCur.size() > 3 && tailCutPos > 4 ) {
				// commonHead 를 지정해줌
				String strHead = meCur.getCommonHead();

				// 공통된 앞부분이 있을 때 이를 추출해줌 -> 띄어쓰기 처리!!
				if( strHead != null ) {
					// commonHead를 분리해준다.
					writeLog("[HEAD]==============" + strHead);
					int headLen = strHead.length();
					String tailStr = meCur.getExp().substring(headLen);
					MExpression[] meHeadTail = meCur.divideHeadTailAt(strHead, firstOffset, tailStr, firstOffset + headLen);
					MExpression headME = meHeadTail[0];
					ret.add(headME);
					mePrev = headME;
					writeLog(ret);

					// 기분석 결과들을 기존에다가 그대로 유지해준다.
					MExpression[] newExps = new MExpression[tailCutPos - headLen];
					for( int k = headLen, l = 0; k < tailCutPos; k++, l++ ) {
						meHeadTail = meArr[k].divideHeadTailAt(strHead, token.index, tailStr.substring(0, l + 1), token.index + k);
						newExps[l] = meHeadTail[1];
					}

					// 새로운 문자열을 분석하도록 분석 대상 문자열 정보 수정
					string = string.substring(strHead.length());
					strlen = string.length();
					meArr = new MExpression[strlen];
					tailCutPos = 0;

					// 기존 결과 copy
					for( int j = 0, stop = newExps.length; j < stop; j++) {
						meArr[j] = newExps[j];
					}
					tailCutPos = tailStr.length();
					
					// offset 정보를 증가시켜준다.
					firstOffset += strHead.length();
				}
			}
		}
		
		// 마지막 결과 저장
		if( tailCutPos > 1 ) ret.add(meArr[meArr.length - 1]);
		
		// 마지막 결과가 완결어가 아닌 경우 미등록어도 추가하여 반환
		for( int i = 0, stop = ret.size(); i < stop; i++ ) {
			MExpression me = ret.get(i);
			if( me.size() == 0 || me.get(0).getDicLenOnlyReal() == 0 ) {
				me.add(new MCandidate(me.exp, token.index));
			}
		}
		
		return ret;
	}


	/**
	 * <pre>
	 * 기분석 사전으로부터 후보 분석 결과를 얻어오거나, 비사전 결과로 후보를 생성한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @return
	 */
	private MExpression getMExpression(String string, int index)
		throws Exception
	{
		MExpression ret = dic.getMExpression(string);
		if( ret == null ) {
			float lnprOfSpacing = SpacingPDDictionary.getProb(string);
			MCandidate mc = new MCandidate(string, index);
			mc.setLnprOfSpacing(lnprOfSpacing);
			ret = new MExpression(string, mc);
			ret.setLnprOfSpacing(lnprOfSpacing);
		} else {
			ret.setIndex(index);
		}
		
		return ret;
	}


	/**
	 * <pre>
	 * 형태소 분석 결과를 바탕으로 띄어쓰기 수정, 문장 구분 등의 작업을 수행한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @param melAnalResult
	 * @return 
	 * @throws Exception 
	 */
	public List<MExpression> postProcess(List<MExpression> melAnalResult)
		throws Exception
	{
		// 띄어쓰기 오류 수정
		MExpression me1 = null, me2 = null, me3 = null;
		List<MExpression> temp = melAnalResult;
		melAnalResult = new ArrayList<MExpression>();

		// 앞뒤 조건 확인하면서 재정렬
		int tempSize = temp == null ? 0 : temp.size();
		
		// 정렬된 결과를 바탕으로 띄어쓰기 수행
		for( int i = 0; i < tempSize; i++ ) {
			try {
				me1 = temp.get(i);
				me1.prune();
				me1.sortBfrSpacing();
				melAnalResult.addAll(me1.split());
			} catch (Exception e) {
				System.err.println(me1);
				throw e;
			}
		}
		
		// 결과들 확인하면서 붙여쓰기 되어야 하는 것은 붙여쓰기 수행
		for( int i = 1; i < melAnalResult.size(); i++ ) {
			me1 = melAnalResult.get(i - 1);
			me2 = melAnalResult.get(i);
			if( !me2.isComplete() || me1.isOneEojeolCheckable() ) {
				// 한글 이외의 것 처리, 한 어절로 처리해야하는 것 처리
				// ex) 4km: '4' + 'km' -> '4km'
				if( me1.isNotHangul() && me2.isNotHangul() ) {
					MCandidate mc1 = me1.get(0), mc2 = me2.get(0);
					// 원래 붙여쓰기 되어 있었는지 확인
					if( mc1.firstMorp.index + mc1.getExp().length() == mc2.firstMorp.index ) {
						me1.exp = me1.exp + me2.exp;
						mc1.addAll(mc2);
						mc1.setExp(me1.exp);
						melAnalResult.remove(i);
						i--;
					}
				}
				// 한글 붙여쓰기 해줌
				else {
					me3 = me1.derive(me2);
					if( me3.isOneEojeol() ) {
						melAnalResult.remove(i-1);
						melAnalResult.remove(i-1);
						melAnalResult.add(i-1, me3);
						i--;
					}
				}
			}
		}
		
		// 첫번째 후보 분석 결과가 앞에 무엇인가 연결되어야만 하는 경우에는 없앤다.
		me1 = melAnalResult.get(0);
		for( int i = 0, size = 0; i < (size = me1.size()) && size > 1; i++ ) {
			MCandidate mc = me1.get(i);
			if( (mc.cclEnc != Condition.ENG && mc.cclEnc != 0) 
					|| mc.firstMorp.isTagOf(POSTag.J | POSTag.E | POSTag.XS) ) 
			{
				me1.remove(i);
				i--;
			}
		}
		
		return setBestPrevMC(melAnalResult);
	}


	/**
	 * <pre>
	 * 확률값 처리를 추가하여 재정렬 한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 19
	 * @param meList
	 * @return
	 */
	private List<MExpression> setBestPrevMC(List<MExpression> meList)
	{
		// 확률값 재계산
		MExpression mePrev = null, meCurr = null;
		for( int i = 0, size = meList.size(); i < size; i++ ) {
			meCurr = meList.get(i);
			meCurr.setBestPrevMC(mePrev);
			mePrev = meCurr;
		}

		int idx = meList.size() - 1;
		MExpression me = meList.get(idx);
		me.sortByScoreNProb();
		MCandidate mc = me.get(0);
		for( idx--; mc != null && idx >= 0; idx-- ) {
			mc = mc.prevBestMC;
			me = meList.get(idx);
			me.remove(mc);
			me.add(0, mc);
		}
		return meList;
	}


	/**
	 * <pre>
	 * 최적 분석 후보만 남긴다.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 01
	 * @param meList
	 * @return
	 */
	public List<MExpression> leaveJustBest(List<MExpression> meList)
	{
		for( int i = 0; i < meList.size(); i++ ) {
			MExpression me = meList.get(i);
			MCandidate mc = me.get(0);
			me.clear();
			me.add(mc);
		}
		return meList;
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2008. 02. 14
	 * @param melAnalResult
	 * @return
	 */
	public List<Sentence> divideToSentences(List<MExpression> melAnalResult)
	{
		List<Sentence> ret = new ArrayList<Sentence>();
		
		// 띄어쓰기 오류 수정
		MExpression me1 = null;
		
		// 문장 단위로 쪼개기
		Eojeol eojeol = null, prevEojeol = null;
		Sentence sentence = null;
		for( int i = 0; i < melAnalResult.size(); i++ ) {
			if( sentence == null) {
				sentence = new Sentence();
			}
			me1 = melAnalResult.get(i);
			
			if( prevEojeol != null && me1.get(0).isTagOf(POSTag.S) 
					&& prevEojeol.getStartIndex() + prevEojeol.exp.length() == me1.get(0).firstMorp.index ) 
			{
				// addAll로 처리함으로써 기호를 lastMorp로 처리하지 않음.
				eojeol.addAll(me1.get(0));
				eojeol.exp += me1.exp;
			} else {
				eojeol = new Eojeol(me1);
				sentence.add(eojeol);
				prevEojeol = eojeol;
			}

			if( eojeol.isEnding() ) {
				if( i < melAnalResult.size() - 1 ) {
					while( i < melAnalResult.size() - 1 ) {
						me1 = melAnalResult.get(i + 1);
						if( me1.getExp().startsWith(".")
								|| me1.getExp().startsWith(",")
								|| me1.getExp().startsWith("!")
								|| me1.getExp().startsWith("?")
								|| me1.getExp().startsWith(";")
								|| me1.getExp().startsWith("~")
								|| me1.getExp().startsWith(")")
								|| me1.getExp().startsWith("]")
								|| me1.getExp().startsWith("}") )
						{
							// 한 어절로 처리되도록 함
							if( eojeol.firstMorp.index + eojeol.exp.length() == me1.get(0).firstMorp.index) {
								// addAll로 처리함으로써 기호를 lastMorp로 처리하지 않음.
								eojeol.addAll(me1.get(0));
								eojeol.exp += me1.exp;
							}
							// 새로운 어절로 처리되도록 함
							else {
								sentence.add(new Eojeol(me1));
							}
							i++;
						} else {
							break;
						}
					}
				}
				ret.add(sentence);
				sentence = null;
				prevEojeol = null;
			}
			
		}
		// 마지막 문장 저장
		if( sentence != null && sentence.size() > 0 ) {
			ret.add(sentence);
		}

		return ret;
	}


	PrintWriter	logger		= null;	// 로깅 객체
	boolean		doLogging	= false;	// 로깅 여부 설정


	/**
	 * <pre>
	 * 로거를 생성한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	public void createLogger(String fileName)
	{
		try {
			System.out.println("DO LOGGING!!");
			if( fileName == null )	logger = new PrintWriter(System.out, true);
			else 	logger = new PrintWriter(new FileWriter(fileName), true);
			doLogging = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * <pre>
	 * 로깅을 종료한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	public void closeLogger()
	{
		if( doLogging && logger != null ) logger.close();
		doLogging = false;
	}	
	
	
	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 03
	 * @param obj
	 */
	private void writeLog(Object obj)
	{
		if( DEBUG ) {
			if( logger != null ) {
				logger.println(obj);
			} else {
				System.out.println(obj);
			}
		}
	}
	
	
	public static final boolean	DEBUG	= "DO_DEBUG".equals(System.getProperty("DO_DEBUG"));
}
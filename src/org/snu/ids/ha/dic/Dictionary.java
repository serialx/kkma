/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.dic;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.ma.MCandidate;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.util.Hangul;
import org.snu.ids.ha.util.StringSet;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * Singleton 으로 사용할 수 있는 형태소 사전
 * 표층 형태소에 의한 Set으로 구성되어 있다.
 * 표층 형태소에 대해 가질 수 있는,
 * 실재 형태소의 기분석 결과와와 이들의 접속 제한 조건등에 대한 정보를 가진다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class Dictionary
{
	/**
	 * <pre>
	 * </pre>
	 * @since	2008. 02. 14
	 * @author	therocks
	 */
	public static final String DIC_ROOT = getDicRoot();

	/**
	 * <pre>
	 * 사전 저장 경로를 읽어들인다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 28
	 * @return
	 */
	private static final String getDicRoot()
	{
		String ret = System.getProperty("dicRoot");
		if( ret == null ) ret = "dic/";

		File dicRoot = new File(ret);
		if( !dicRoot.isDirectory() ) {
			System.err.println("Cannot load dictionary");
		} else {
			ret = dicRoot.getAbsolutePath() + File.separator;
		}

		return ret;
	}

	/**
	 * <pre>
	 * Singleton으로 사용하기 위한 사전 객체
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	private static Dictionary	dictionary	= null;


	/**
	 * <pre>
	 * 사전 객체를 얻어온다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public static final synchronized Dictionary getInstance()
	{
		if( !isLoading ) {
			if( dictionary == null ) {
				isLoading = true;
				dictionary = new Dictionary();
				isLoading = false;
			}
		}
		return dictionary;
	}
	
	
	static boolean isLoading = false;


	/**
	 * <pre>
	 * 표층 형태소에 대한 정보를 저장하기 위한 Hashtable
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	final private Hashtable<String, MExpression>	table			= new Hashtable<String, MExpression>(530000);
	private List<MExpression>						meList			= null;
	final private Hashtable<String, String[]>		compNounTable	= new Hashtable<String, String[]>();
	final private HashSet<String>					verbStemSet		= new HashSet<String>();
	private int										maxLen			= 0;


	/**
	 * <pre>
	 * singleton으로 사용하기 위해 private 으로 지정함
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	protected Dictionary()
	{
		Timer timer = new Timer();
		try {
			timer.start();
			loadDic();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timer.stop();
			timer.printMsg("Dictionary Loading Time");
			System.out.println("Loaded Item " + table.size());
		}
	}


	/**
	 * <pre>
	 * 사전 데이터를 다시 읽어들인다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 20
	 */
	public static void reload()
	{
		if( !isLoading && dictionary != null ) {
			Timer timer = new Timer();
			try {
				System.out.println("reloading");
				timer.start();
				dictionary.clear();
				dictionary.loadDic();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				timer.stop();
				timer.printMsg("Dictionary Loading Time");
				System.out.println("Loaded Item " + dictionary.table.size());
			}
		}
	}


	/**
	 * <pre>
	 * DicReader들로부터 사전을 재적재하기 위한 함수로, DB에서 읽어서 재적재하는 기능을 구현할 수 있도록 한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 21
	 * @param dicReadList
	 */
	public static void reload(List<DicReader> dicReadList)
	{
		if( !isLoading && dictionary != null ) {
			Timer timer = new Timer();
			try {
				System.out.println("reloading");
				timer.start();
				dictionary.clear();
				for(int i=0; i < dicReadList.size(); i++) {
					dictionary.load(dicReadList.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				timer.stop();
				timer.printMsg("Dictionary Loading Time");
				System.out.println("Loaded Item " + dictionary.table.size());
			}
		}
	}


	/**
	 * <pre>
	 * 사전 데이터를 모두 없앤다.
	 * 재 로딩 하기 위함임.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 20
	 */
	public void clear()
	{
		table.clear();
		compNounTable.clear();
		verbStemSet.clear();
		maxLen = 0;
	}


	//private static long	fSLEEP_INTERVAL	= 5000;


	/**
	 * <pre>
	 * '동사의 기본형에 대한 분석 후보를 생성해줌'
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 29
	 * @param string
	 * @param posTag
	 * @return
	 * @throws Exception
	 */
	public static MCandidate getVerbBasicMC(String string, String posTag)
		throws Exception
	{
		String stem = null;
		if( string.charAt(string.length() - 1) == '다' )
			stem = string.substring(0, string.length() - 1);
		else
			stem = string;

		// 기본 어간 출력
		String exp = stem;
		MCandidate mCandidate = new MCandidate(exp, posTag);
		mCandidate.addPreferedCond(Condition.A);
		mCandidate.setCandDicLen((byte) exp.length());
		mCandidate.setExp(exp);

		return mCandidate;
	}


	final static StringSet	MO_SET1	= new StringSet(new String[] { "ㅏ", "ㅓ", "ㅐ", "ㅔ" });
	final static StringSet	MO_SET2	= new StringSet(new String[] { "ㅗ", "ㅜ", "ㅡ" });


	/**
	 * <pre>
	 * 동사, 형용사의 기본형분석 후보를 받아들여, 사전에 저장될 표제어를 포함한 MCandidate를 생성하여 반환
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 29
	 * @param mCandidate
	 * @return
	 */
	public static List<MCandidate> getVerbExtendedMC(MCandidate mCandidate)
	{
		List<MCandidate> ret = new ArrayList<MCandidate>();

		String stem = mCandidate.getExp();
		int stemLen = stem.length();
		String preStem = stem.substring(0, stemLen - 1);

		char lastCh = stem.charAt(stemLen - 1), preLastCh = 0, mo = 0;
		Hangul lastHg = Hangul.split(lastCh), preLastHg = null;
		if( stemLen > 1 ) {
			preLastCh = stem.charAt(stemLen - 2);
			preLastHg = Hangul.split(preLastCh);
		} else {
			preLastCh = 0;
		}

		String exp = null;
		MCandidate mCandidateClone = null;
		
		// TODO
		// 사 주다 -> 사+아+주+다 와 같이 한글자 어간 'ㅏ'로 끝나는 말
		// 2007-07-06 너무 많은 후보군들이 생성되버려서 문제 생김
		// 많이 사용되는 것만 따로 사전에 추가하도록 함
		// 2009-10-17 일단 넣어줌.
		if( stem.length() == 1 && !lastHg.hasJong() && lastHg.cho != 'ㅎ' ) {
			exp = stem;
			if( lastHg.jung == 'ㅏ' ) {
				mCandidateClone = mCandidate.copy();
				mCandidateClone.add(new Morpheme("아", POSTag.ECS));
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.addHavingCond(Condition.AH);
				mCandidateClone.setRealDicLen((byte) exp.length());
				ret.add(mCandidateClone);
			} else if( lastHg.jung == 'ㅓ' ) {
				mCandidateClone = mCandidate.copy();
				mCandidateClone.add(new Morpheme("어", POSTag.ECS));
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.addHavingCond(Condition.AH);
				mCandidateClone.setRealDicLen((byte) exp.length());
				ret.add(mCandidateClone);
			}
		}


		// 겹모음 'ㄶ'의 경우 'ㅎ'을 빼먹고 사용하는 경우가 많으므로 이를 처리해줌
		if( lastCh == '찮' || lastCh == '잖' ) {
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄴ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			mCandidateClone.decreaseNumOfPrfrdCond();
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.setExp(exp);
			ret.add(mCandidateClone);
		}


		// 과거형 붙여주기
		if( lastCh == '하' ) {
			// 했 -> 하였
			mCandidateClone = mCandidate.copy();
			exp = preStem + "했";
			mCandidateClone.add(new Morpheme("였", POSTag.EPT));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.EUT);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// 해 -> 하여
			mCandidateClone = mCandidate.copy();
			exp = preStem + "해";
			mCandidateClone.add(new Morpheme("여", POSTag.ECS));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.AH);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);
			
			// 종결형
			mCandidateClone = mCandidate.copy();
			exp = preStem + "해";
			mCandidateClone.add(new Morpheme("여", POSTag.EFN));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.AH);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// 형용사는 하지 -> 치 로 줄여질 수 있다.
			if( mCandidate.isTagOf(POSTag.VA | POSTag.VXA) ) {
				mCandidateClone = mCandidate.copy();
				exp = preStem + "치";
				mCandidateClone.add(new Morpheme("지", POSTag.ECS));
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.setRealDicLen((byte) exp.length());
				ret.add(mCandidateClone);
			}
		}
		// '이'로 끝나는 말
		else if( !lastHg.hasJong() && lastHg.jung == 'ㅣ' ) {
			// ㅣ -> ㅣ었->ㅕㅆ
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, 'ㅕ', 'ㅆ');
			mCandidateClone.add(new Morpheme("었", POSTag.EPT));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.EUT);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// ㅣ -> ㅣ어->ㅕ
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, 'ㅕ', ' ');
			mCandidateClone.add(new Morpheme("어", POSTag.ECS));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.MOEUM | Condition.EUMSEONG | Condition.AH);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// ㅆ, ㅏㅆ, ㅐㅆ, ㅕㅆ  결합에 의한 어간 출력
		else if( !lastHg.hasJong() && MO_SET1.contains(lastHg.jung) ) {
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㅆ');
			mCandidateClone.add(new Morpheme("었", POSTag.EPT));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.EUT);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// '르'불규칙
		else if( lastCh == '르' ) {
			// 았
			mCandidateClone = mCandidate.copy();
			mCandidateClone.clearHavingCondition();
			if( preLastCh == '따' ) {
				exp = preStem + "랐";
				mCandidateClone.add(new Morpheme("았", POSTag.EPT));
				mCandidateClone.addHavingCond(Condition.EUT);
			} else if( preLastCh == '푸' ) {
				exp = stem + "렀";
				mCandidateClone.add(new Morpheme("었", POSTag.EPT));
				mCandidateClone.addHavingCond(Condition.EUT);
			} else {
				mo = getMoeum(lastHg, preLastHg);
				exp = stem.substring(0, stemLen - 2)
				+ Hangul.combine(preLastHg.cho, preLastHg.jung, 'ㄹ')
				+ Hangul.combine(lastHg.cho, mo, 'ㅆ');
				if( mo == 'ㅏ' ) {
					mCandidateClone.add(new Morpheme("았", POSTag.EPT));
				} else {
					mCandidateClone.add(new Morpheme("었", POSTag.EPT));
				}
				mCandidateClone.addHavingCond(Condition.EUT);
			}
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setCandDicLen((byte)exp.length());
			
			ret.add(mCandidateClone);

			// 아
			mCandidateClone = mCandidate.copy();
			mCandidateClone.clearHavingCondition();
			if( preLastCh == '따' ) {
				exp = preStem + "라";
				mCandidateClone.add(new Morpheme("아", POSTag.ECS));
				mCandidateClone.addHavingCond(Condition.AH);
			} else if( preLastCh == '푸' ) {
				exp = stem + "러";
				mCandidateClone.add(new Morpheme("어", POSTag.ECS));
				mCandidateClone.addHavingCond(Condition.AH);
			} else {
				mo = getMoeum(lastHg, preLastHg);
				exp = stem.substring(0, stemLen - 2)
				+ Hangul.combine(preLastHg.cho, preLastHg.jung, 'ㄹ')
				+ Hangul.combine(lastHg.cho, mo, ' ');
				if( mo == 'ㅏ' ) {
					mCandidateClone.add(new Morpheme("아", POSTag.ECS));
					mCandidateClone.addHavingCond(Condition.AH);
				} else {
					mCandidateClone.add(new Morpheme("어", POSTag.ECS));
					mCandidateClone.addHavingCond(Condition.AH);
				}
			}
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

		}
		// 'ㅡ' 결합에 의한 어간 출력
		else if( !lastHg.hasJong() && lastHg.jung == 'ㅡ' ) {
			// 양성으로 한번 결합
			mo = getMoeum(lastHg, preLastHg);
			mCandidateClone = mCandidate.copy();
			mCandidateClone.clearHavingCondition();
			exp = preStem + Hangul.combine(lastHg.cho, mo, 'ㅆ');
			if( mo == 'ㅏ' ) {
				mCandidateClone.add(new Morpheme("았", POSTag.EPT));
				mCandidateClone.addHavingCond(Condition.EUT);
			} else {
				mCandidateClone.add(new Morpheme("었", POSTag.EPT));
				mCandidateClone.addHavingCond(Condition.EUT);
			}
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// ㅓ, ㅏ
			mCandidateClone = mCandidate.copy();
			mCandidateClone.clearHavingCondition();
			exp = preStem + Hangul.combine(lastHg.cho, mo, ' ');
			if( mo == 'ㅏ' ) {
				mCandidateClone.add(new Morpheme("아", POSTag.ECS));
				mCandidateClone.addHavingCond(Condition.AH);
			} else {
				mCandidateClone.add(new Morpheme("어", POSTag.ECS));
				mCandidateClone.addHavingCond(Condition.AH);
			}
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// ㅜ, ㅗ결합에 의한 어간 출력
		else if( !lastHg.hasJong() && MO_SET2.contains(lastHg.jung) ) {
			// 었, 았
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, getMoeum(lastHg, preLastHg), 'ㅆ');
			if( lastHg.jung == 'ㅜ' ) {
				mCandidateClone.add(new Morpheme("었", POSTag.EPT));
			} else {
				mCandidateClone.add(new Morpheme("았", POSTag.EPT));
			}
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.EUT);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// 어, 아
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, getMoeum(lastHg, preLastHg), ' ');
			if( lastHg.jung == 'ㅜ' ) {
				mCandidateClone.add(new Morpheme("어", POSTag.ECS));
			} else {
				mCandidateClone.add(new Morpheme("아", POSTag.ECS));
			}
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.AH);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// 겹모음 처리
		else if( !lastHg.hasJong() && lastHg.jung != 'ㅚ' ) {
			// 'ㅓ' 결합
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, 'ㅙ', ' ');
			mCandidateClone.add(new Morpheme("어", POSTag.ECS));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.AH);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// '었' 결합
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, 'ㅙ', 'ㅆ');
			mCandidateClone.add(new Morpheme("었", POSTag.EPT));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.EUT);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}


		// ㅂ 불규칙
		// ㅂ불규칙 활용하는 어간의 마지막 어절
		// '뵙뽑씹업입잡접좁집' 들은 활용 안함~
		if( "갑겁겹곱굽깁깝껍꼽납눕답덥돕둡땁떱랍럽렵롭립맙맵밉볍섭쉽습엽줍쭙춥탑".indexOf(lastCh) > -1 ) {

			// ㅂ탈락된 음절 생성
			char bChar = Hangul.combine(lastHg.cho, lastHg.jung, ' ');

			// 럽은 '러운' 뿐만 아니라 짧게 '런' 등으로도 활용됨
			if( lastCh == '럽' ) {
				mCandidateClone = mCandidate.copy();
				exp = preStem + '런';
				mCandidateClone.add(new Morpheme("ㄴ", POSTag.ETD));
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.decreaseNumOfPrfrdCond();
				mCandidateClone.setRealDicLen((byte)exp.length());
				ret.add(mCandidateClone);
			}

			// 워, 와
			mCandidateClone = mCandidate.copy();
			if( lastHg.jung == 'ㅗ') {
				mo = 'ㅘ';
				mCandidateClone.add(new Morpheme("아", POSTag.ECS));
			} else {
				mo = 'ㅝ';
				mCandidateClone.add(new Morpheme("어", POSTag.ECS));
			}
			exp = preStem + bChar + Hangul.combine('ㅇ', mo, ' ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.AH);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// 웠, 왔
			mCandidateClone = mCandidate.copy();
			mCandidateClone.clearHavingCondition();
			if( lastHg.jung == 'ㅗ') {
				mo = 'ㅘ';
				mCandidateClone.add(new Morpheme("았", POSTag.EPT));
			} else {
				mo = 'ㅝ';
				mCandidateClone.add(new Morpheme("었", POSTag.EPT));
			}
			exp = preStem + bChar + Hangul.combine('ㅇ', mo, 'ㅆ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.EUT);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// 우
			mCandidateClone = mCandidate.copy();
			exp = preStem + bChar + '우';
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// ㄴ, ㄹ, ㅁ 에 의한 활용
			mCandidateClone = mCandidate.copy();
			exp = preStem + bChar + '운';
			mCandidateClone.add(new Morpheme("ㄴ", POSTag.ETD));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			mCandidateClone = mCandidate.copy();
			mCandidateClone.add(new Morpheme("ㄹ", POSTag.ETD));
			exp = preStem + bChar + '울';
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			mCandidateClone = mCandidate.copy();
			mCandidateClone.add(new Morpheme("ㅁ", POSTag.ETN));
			exp = preStem + bChar + '움';
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// 'ㅅ' 뷸규칙
		else if( "젓짓긋낫붓잇".indexOf(lastCh) > -1 )
		{
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.MINUS_SIOT);
			mCandidateClone.decreaseNumOfPrfrdCond();
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// 'ㄷ' 뷸규칙
		else if( lastHg.jong == 'ㄷ' ) {
			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄹ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.addHavingCond(Condition.MINUS_SIOT);
			mCandidateClone.decreaseNumOfPrfrdCond();
			mCandidateClone.setCandDicLen((byte)exp.length());
			ret.add(mCandidateClone);
		}
		// 그외 처리
		else if( !lastHg.hasJong() || lastHg.jong == 'ㄹ'
			// ㅎ 불규칙 처리
			|| lastCh == '맣' || lastCh == '갛' || lastCh == '랗'
			)
		{
			// ㄴ, ㄹ, ㅁ, ㅂ 에 의한 활용
			mCandidateClone = mCandidate.copy();
			mCandidateClone.add(new Morpheme("ㄴ", POSTag.ETD));
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄴ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄹ');
			mCandidateClone.add(new Morpheme("ㄹ", POSTag.ETD));
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setRealDicLen((byte)exp.length());
			ret.add(mCandidateClone);

			// 'ㄹ' 불규칙
			if( lastHg.jong == 'ㄹ' ) {
				mCandidateClone = mCandidate.copy();
				exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄻ');
				mCandidateClone.add(new Morpheme("ㅁ", POSTag.ETN));
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.setRealDicLen((byte)exp.length());
				ret.add(mCandidateClone);

				// ㄹ탈락 현상 처리
				mCandidateClone = mCandidate.copy();
				exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.addHavingCond(Condition.MINUS_LIEUL);
				mCandidateClone.decreaseNumOfPrfrdCond();
				mCandidateClone.setCandDicLen((byte)exp.length());
				ret.add(mCandidateClone);
			}
			// 'ㅎ' 불규칙
			else if( lastHg.jong == 'ㅎ' ) {
				mCandidateClone = mCandidate.copy();
				exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.addHavingCond(Condition.MINUS_HIEUT);
				mCandidateClone.decreaseNumOfPrfrdCond();
				mCandidateClone.setCandDicLen((byte)exp.length());
				ret.add(mCandidateClone);
			} else {
				mCandidateClone = mCandidate.copy();
				mCandidateClone.add(new Morpheme("ㅁ", POSTag.ETN));
				exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㅁ');
				mCandidateClone.setExp(exp);
				mCandidateClone.setAutoExtd(true);
				mCandidateClone.clearHavingCondition();
				mCandidateClone.initHavingCond(exp);
				mCandidateClone.setRealDicLen((byte) exp.length());
				ret.add(mCandidateClone);
			}

			mCandidateClone = mCandidate.copy();
			exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㅂ');
			mCandidateClone.setExp(exp);
			mCandidateClone.setAutoExtd(true);
			mCandidateClone.clearHavingCondition();
			mCandidateClone.addHavingCond(Condition.BIEUB);
			mCandidateClone.initHavingCond(exp);
			mCandidateClone.setCandDicLen((byte) exp.length());
			ret.add(mCandidateClone);
		}
		
		
		return ret;
	}
	
	
	/**
	 * <pre>
	 * ㅗ, ㅜ, ㅡ에 ㅏㅆ, ㅓㅆ 이 결합될 때의 모음을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 5
	 * @param mo1
	 * @return
	 */
	static char getMoeum(Hangul lastHg, Hangul preLastHg)
	{
		char mo = 0;
		char mo1 = lastHg.jung;
		if( mo1 == 'ㅗ' ) {
			mo = 'ㅘ';
		} else if( mo1 == 'ㅜ' ) {
			if( lastHg.cho == 'ㅍ') {
				mo = 'ㅓ';
			} else {
				mo = 'ㅝ';
			}
		} else if( mo1 == 'ㅡ' ) {
			if( preLastHg != null && Hangul.MO_POSITIVE_SET.contains(preLastHg.jung) ) {
				mo = 'ㅏ';
			} else {
				mo = 'ㅓ';
			}
		}
		return mo;
	}


	/**
	 * <pre>
	 * 해당 표층형에 대한 가능한 기분석 결과를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param mc
	 */
	private void add(String exp, MCandidate mc)
		throws Exception
	{
		// 점수 계산
		mc.getScore();
		// hashCode계산
		mc.calcHashCode();
		
		MExpression me = get(exp);

		if( me == null ) {
			me = new MExpression(exp, mc);
			float lnprOfSpacing = SpacingPDDictionary.getProb(exp);
			mc.setLnprOfSpacing(lnprOfSpacing);
			me.setLnprOfSpacing(lnprOfSpacing);
			table.put(exp, me);
			if( maxLen < exp.length() ) {
				maxLen = exp.length();
			}
		} else {
			mc.setLnprOfSpacing(me.getLnprOfSpacing());
			me.add(mc);
		}
	}
	
	
	public boolean containVerbStem(String exp)
	{
		return verbStemSet.contains(exp);
	}


	synchronized private MExpression get(String exp)
	{
		return table.get(exp);
	}


	/**
	 * <pre>
	 * 표층 형태소에 대해서 가능한 기분석 결과를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @return
	 */
	synchronized public MExpression getMExpression(String exp)
	{
		MExpression ret = get(exp);
		return ret == null ? null : ret.copy();
	}
	
	
	/**
	 * <pre>
	 * 복합 명사의 분해된 문자열을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 04
	 * @param noun
	 * @return
	 */
	synchronized public String[] getCompNoun(String noun)
	{
		return compNounTable.get(noun);
	}


	/**
	 * <pre>
	 * 줄별로 사전 정의행을 읽어들여 기분석 사전을 구축한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 21
	 * @param simpleDicReader
	 * @throws Exception
	 */
	private void load(SimpleDicReader simpleDicReader)
		throws Exception
	{
		String line = null;
		try {

			String[] strArrTemp = null;
			while( (line = simpleDicReader.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();

				String exp = null, mpInfo = null, condInfo = null;

				if( line.indexOf(';') > 0 ) {
					strArrTemp = line.split(";");
					mpInfo = strArrTemp[0];
					if( strArrTemp.length > 1 ) {
						condInfo = strArrTemp[1];
					}
				} else {
					mpInfo = line;
				}

				exp = mpInfo.split("/")[0];

				String atl = null, hcl = null, ccl = null, pcl = null, ecl = null, compResult = null;
				if( condInfo != null ) {
					// 부가 정보들에 대한 처리 수행
					StringTokenizer st = new StringTokenizer(condInfo, MCandidate.DLMT_ATL + MCandidate.DLMT_HCL + MCandidate.DLMT_CCL + MCandidate.DLMT_ECL + MCandidate.DLMT_PCL + MCandidate.DLMT_CNL, true);
					while( st.hasMoreTokens() ) {
						String token = st.nextToken();
						// 접속 가능한 품사 정보
						if( token.equals(MCandidate.DLMT_ATL) ) {
							token = st.nextToken().trim();
							atl = token.substring(1, token.length() - 1);
						}
						// 현재 후보가 가진 접속 조건
						else if( token.equals(MCandidate.DLMT_HCL) ) {
							token = st.nextToken().trim();
							hcl = token.substring(1, token.length() - 1);
						}
						// 접속할 때 확인해야 하는 조건
						else if( token.equals(MCandidate.DLMT_CCL) ) {
							token = st.nextToken().trim();
							ccl = token.substring(1, token.length() - 1);
						}
						// 접속할 때 배제해야 하는 조건
						else if( token.equals(MCandidate.DLMT_ECL) ) {
							token = st.nextToken().trim();
							ecl = token.substring(1, token.length() - 1);
						}
						// 띄어쓰기 되었을 때 선호 조건 
						else if( token.equals(MCandidate.DLMT_PCL) ) {
							token = st.nextToken().trim();
							pcl = token.substring(1, token.length() - 1);
						}
						// 복합명사 분석 정보
						else if( token.equals(MCandidate.DLMT_CNL) ) {
							token = st.nextToken().trim();
							compResult = token.substring(1, token.length() - 1);
						}
					}
				}

				MCandidate mCandidate = MCandidate.create(exp, mpInfo, atl, hcl, ccl, ecl, pcl);

				add(mCandidate.getExp(), mCandidate);
				
				// TODO
				if( mCandidate.isTagOf(POSTag.V) ) {
					verbStemSet.add(exp);
					List<MCandidate> mcList = Dictionary.getVerbExtendedMC(mCandidate);
					for( int i = 0, size = mcList.size(); i < size; i++ ) {
						MCandidate mc = mcList.get(i);
						add(mc.getExp(), mc);
					}
				}
				// 복합 명사 저장
				else {
					// TODO
					// 명사는 '는', '를'에 대한 조사 줄임말 처리를 해준다.
					// 모음으로 끝난 말에 대해서만 처리해줌
					// 일단 줄임말 처리하지 않음 -- 2007-07-23
//					if( mCandidate.isFirstTagOf(POSTag.N)
//							&& mCandidate.isHavingCond(Condition.MOEUM) )
//					{
//						MCandidate mCandidateClone = null;
//						char lastCh = exp.charAt(exp.length() - 1);
//						String stem = exp.substring(0, exp.length() - 1), temp = null;
//						Hangul lastHg = Hangul.split(lastCh);
//
//						// '는' 하나는 -> 하난
//						mCandidateClone = mCandidate.copy();
//						mCandidateClone.clearHavingCondition();
//						mCandidateClone.add(new Morpheme("는", POSTag.JKS));
//						mCandidateClone.addHavingCond(Condition.JAEUM);
//						temp = stem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄴ');
//						add(temp, mCandidateClone);
//
//						// '를' 하나를 -> 하날
//						mCandidateClone = mCandidate.copy();
//						mCandidateClone.clearHavingCondition();
//						mCandidateClone.add(new Morpheme("를", POSTag.JKO));
//						mCandidateClone.addHavingCond(Condition.JAEUM);
//						temp = stem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄹ');
//						add(temp, mCandidateClone);
//					}
					if( Util.valid(compResult) ) {
						compNounTable.put(exp, compResult.split("[+]"));
					}
				}
			}
		} catch (Exception e) {
			System.err.println(line);
			throw e;
		} finally {
			simpleDicReader.cleanup();
		}
	}
	
	
	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 21
	 * @param rawDicReader
	 * @throws Exception
	 */
	private void load(RawDicReader rawDicReader)
		throws Exception
	{
		String line = null;
		try {
			String[] arr = null;
			String string = null, temp = null;
			while( (line = rawDicReader.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				arr = line.split(":");
				string = arr[0];
				if( arr.length < 2 ) continue;
				arr = arr[1].split(";");
				for( int i = 0, stop = arr.length; i < stop; i++ ) {
					temp = arr[i].trim();
					add(string, MCandidate.create(string, temp.substring(1, temp.length() - 1)));
				}
			}
		} catch (Exception e) {
			System.err.println(line);
			throw e;
		} finally {
			rawDicReader.cleanup();
		}
	}


	/**
	 * <pre>
	 * DicReader를 이용해서 기분석 사전을 적재한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 21
	 * @param dicReader
	 * @throws Exception
	 */
	private void load(DicReader dicReader)
		throws Exception
	{
		if( dicReader instanceof SimpleDicReader ) {
			load((SimpleDicReader) dicReader);
		} else if( dicReader instanceof RawDicReader ) {
			load((RawDicReader) dicReader);
		} else {
			throw new Exception("Unknown dictionary reader type.");
		}
	}


	/**
	 * <pre>
	 * 단일 분석 결과를 가지는 사전으로부터 어휘 정보를 읽어들인다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @param fileName
	 * @throws Exception
	 */
	void loadSimple(String fileName)
		throws Exception
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();
		try {
			load(new SimpleDicFileReader(fileName));
		} finally {
			timer.stop();
			System.out.println("Loaded " + timer.getInterval() + "secs");
		}
	}


	/**
	 * <pre>
	 * 사전 형태로 작성된 사전 파일로부터 정보를 읽어들여서 저장해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param fileName
	 * @throws Exception
	 */
	private void loadRaw(String fileName)
		throws Exception
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		try {
			load(new RawDicFileReader(fileName));
		} catch (Exception e) {
			throw e;
		} finally {
			timer.stop();
			System.out.println("Loaded " + timer.getInterval() + "secs");
		}
	}


	protected void loadDic()
		throws Exception
	{
		loadSimple(DIC_ROOT + "noun.dic");
		loadSimple(DIC_ROOT + "verb.dic");
		loadSimple(DIC_ROOT + "simple.dic");
		loadRaw(DIC_ROOT + "raw.dic");
	}


	/**
	 * <pre>
	 * 로딩된 사전을 주어진 파일에 작성한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param fileName
	 */
	public void printToFile(String fileName)
	{
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(fileName)));
			ArrayList<MExpression> list = new ArrayList<MExpression>(table.values());
			Collections.sort(list);
			for( int i = 0, stop = list.size(); i < stop; i++ ) {
				MExpression me = list.get(i);
				pw.println(me);
				pw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( pw != null ) pw.close();
		}
	}


	/**
	 * <pre>
	 * 사전에 있는 MExpression을 리스트로 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 08. 07
	 * @return
	 */
	public List<MExpression> getAsList()
	{
		if( table == null ) return null;
		if( meList == null ) meList = new ArrayList<MExpression>(table.values());
		return meList;
	}


	public List<MCandidate> search(String str)
	{
		Timer timer = new Timer();
		timer.start();
		List<MCandidate> ret = new ArrayList<MCandidate>();
		getAsList();
		for( int i = 0; i < meList.size(); i++ ) {
			MExpression me = meList.get(i);
			if( me.getExp().indexOf(str) > -1 ) {
				ret.addAll(me);
			}
		}
		timer.printMsg(ret.size() + " candidates found.");
		timer.stop();
		return ret;
	}
}
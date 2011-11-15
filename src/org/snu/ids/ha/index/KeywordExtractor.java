/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2008. 04. 30
 */
package org.snu.ids.ha.index;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.dic.Dictionary;
import org.snu.ids.ha.ma.CharSetType;
import org.snu.ids.ha.ma.MCandidate;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Token;
import org.snu.ids.ha.ma.Tokenizer;
import org.snu.ids.ha.util.StringSet;
import org.snu.ids.ha.util.Util;
import org.tartarus.snowball.EnglishStemmer;


/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2008. 04. 30
 */
public class KeywordExtractor
	extends MorphemeAnalyzer
{
	static WordDic		UOMDic			= null;
	static WordDic		ChemFormulaDic	= null;
	static WordDic		CompNounDic		= null;
	static WordDic		VerbNounDic		= null;
	static WordDic		JunkWordDic		= null;
	static WordDic		VerbJunkWordDic	= null;
	static final int	MAX_UOM_SIZE	= 7;

	static {
		UOMDic = new WordDic(Dictionary.DIC_ROOT + "ecat/UOM.dic");
		ChemFormulaDic = new WordDic(Dictionary.DIC_ROOT + "ecat/ChemFormula.dic");
		CompNounDic = new WordDic(Dictionary.DIC_ROOT + "ecat/CompNoun.dic");
		VerbNounDic = new WordDic(Dictionary.DIC_ROOT + "ecat/VerbNoun.dic");
		JunkWordDic = new WordDic(Dictionary.DIC_ROOT + "ecat/JunkWord.dic");
		VerbJunkWordDic = new WordDic(Dictionary.DIC_ROOT + "ecat/VerbJunkWord.dic");
	}
	
	
	public KeywordList extractKeyword(JProgressBar progressBar, JLabel label, String string, boolean onlyNoun)
	{
		KeywordList ret = null;

		String line = null;
		int offset = 0;

		String[] strArr = string.split("\n");
		
		if( progressBar != null ) {
			progressBar.setIndeterminate(false);
			progressBar.setMaximum(strArr.length);
			progressBar.setStringPainted(true);
			label.setText("0");
		}

		for( int lineNo = 0, len = strArr.length; lineNo < len; lineNo++ ) {
			line = strArr[lineNo];
			if( Util.valid(line) ) {
				KeywordList keywordList = extractKeyword(line, onlyNoun);

				if( offset > 0 ) {
					for( int i = 0, size = keywordList.size(); i < size; i++ ) {
						Keyword keyword = keywordList.get(i);
						keyword.setIndex(offset + keyword.getIndex());
					}
				}

				// 생성된 키워드 추가
				if( keywordList != null && keywordList.size() > 0 ) {
					if( ret == null ) {
						ret = new KeywordList(keywordList);
					} else {
						ret.addAll(keywordList);
					}
				}
			}
			if( progressBar != null ) {
				progressBar.setValue(lineNo + 1);
				label.setText((lineNo + 1) + "");
			}
			offset += line.length() + 1;
		}
		if( progressBar != null ) {
			progressBar.setStringPainted(false);
		}

		return ret;
	}


	/**
	 * <pre>
	 * extract index word from the given string
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 01
	 * @param string
	 * @param onlyNoun	명사만 추출할지 여부 설정
	 * @return the keyword list
	 */
	public KeywordList extractKeyword(String string, boolean onlyNoun)
	{
		List<Keyword> ret = new ArrayList<Keyword>();
		EnglishStemmer engStemmer = new EnglishStemmer();
		
		try {
			List<MExpression> meList = leaveJustBest(postProcess(analyze(string)));

			Morpheme mp = null;
			MCandidate mc = null;
			MExpression me = null;
			Keyword keyword = null;
			List<Morpheme> mpList = new ArrayList<Morpheme>();
			for( int i = 0, size = meList == null ? 0 : meList.size(); i < size; i++ ) {
				me = meList.get(i);
				mc = me.get(0);

				int jSize = mc.size();
				if( jSize == 1 ) {
					mp = mc.get(0);
					mp.setString(me.getExp());
					mpList.add(mp);
				} else {
					// 분할되지 않은 리스트 형태로 형태소를 넣어준다.
					for( int j = 0; j < jSize; j++ )
						mpList.add(mc.get(j));
				}

			}

			// 복합 UOM 확인
			for( int endIdx = mpList.size() - 1; endIdx > 0; endIdx-- ) {
				for( int startIdx = Math.max(endIdx - MAX_UOM_SIZE, 0); startIdx < endIdx; startIdx++ ) {
					String tempName = "";
					for( int i = startIdx; i <= endIdx; i++ ) {
						tempName += mpList.get(i).getString();
					}

					// 다수의 토큰으로 이루어진 UOM 확인
					if( UOMDic.contains(tempName) ) {
						for( ; startIdx < endIdx; endIdx-- ) {
							mpList.remove(startIdx + 1);
						}
						mp = mpList.get(startIdx);
						mp.setString(tempName);
						mp.setCharSet(CharSetType.COMBINED);
						mp.setTag(POSTag.NNM);
					}
					// 다수의 토큰으로 이루어진 화학식 확인
					else if( ChemFormulaDic.contains(tempName) ) {
						for( ; startIdx < endIdx; endIdx-- ) {
							mpList.remove(startIdx + 1);
						}
						mp = mpList.get(startIdx);
						mp.setString(tempName);
						mp.setCharSet(CharSetType.COMBINED);
						mp.setTag(POSTag.UN);
					}
					// 다수의 토큰으로 이루어진 명사 확인 ((주), Web2.0)류의 키워드
					else if( CompNounDic.contains(tempName) ) {
						for( ; startIdx < endIdx; endIdx-- ) {
							mpList.remove(startIdx + 1);
						}
						if( !JunkWordDic.contains(tempName) ) {
							mp = mpList.get(startIdx);
							mp.setString(tempName);
							mp.setCharSet(CharSetType.COMBINED);
							mp.setTag(POSTag.NNG);
							mp.setComposed(true);
						}
					}
				}
			}

			// 키워드 추출
			for( int i = 0, size = mpList.size(); i < size; i++ ) {
				mp = mpList.get(i);
				mp.setString(mp.getString().toLowerCase());

				// stemming 및 키워드 추출
				if( (!onlyNoun || mp.isTagOf(POSTag.N) )  
						&& !JunkWordDic.contains(mp.getString()) ) 
				{

					// do stemming english word
					if( mp.isTagOf(POSTag.UN) 
							&& mp.getCharSet() == CharSetType.ENGLISH ) 
					{
						keyword = new Keyword(mp);
						engStemmer.setCurrent(keyword.getString().toLowerCase());
						engStemmer.stem();
						keyword.setString(engStemmer.getCurrent());
						ret.add(keyword);
					}
					// 사랑하 로 추출된 경우 명사 '사랑'을 색인어로 추출
					else if( mp.isTagOf(POSTag.V) ) {
						String temp = mp.getString();
						int tempLen = temp.length();
						char ch = temp.charAt(tempLen - 1);
						if( tempLen > 2 && (ch == '하' || ch == '되') 
								&& VerbNounDic.contains(temp = temp.substring(0, tempLen - 1)))
						{
							keyword = new Keyword(mp);
							keyword.setString(temp);
							keyword.setTag(POSTag.NNG);
							ret.add(keyword);
						}
						// 일반 용언 처리
						else {
							keyword = new Keyword(mp);
							ret.add(keyword);
						}
					}
					// 이외 적합한 경우에 추가
					else if( !mp.isTagOf(POSTag.NP) || true ) {
						keyword = new Keyword(mp);
						ret.add(keyword);
					}
				}
			}
			
			Morpheme mp0 = null, mp1 = null, mp2 = null, mp3 = null;
			for(int i=0, size = mpList.size(), step = 0; i < size; i++) {
				mp0 = mpList.get(i);
				step = 0;
				
				// 복합 명사 추출 --------------
				// 두글자 복합 명사 추출
				if( i + 1 < size
						&& mp0.isTagOf(POSTag.NN) 
						&& (mp1 = mpList.get(i + 1)).isTagOf(POSTag.NN)
						&& mp0.getIndex() + mp0.getString().length() == mp1.getIndex() ) 
				{
					// 세글자 복합 명사 추출
					if( i + 2 < size
							&& (mp2 = mpList.get(i + 2)).isTagOf(POSTag.NN)
							&& mp1.getIndex() + mp1.getString().length() == mp2.getIndex() )
					{
						// 네글자 복합명사 추출
						if( i + 3 < size 
								&& (mp3 = mpList.get(i + 3)).isTagOf(POSTag.NN)
								&& mp2.getIndex() + mp2.getString().length() == mp3.getIndex() )
						{
							keyword = new Keyword(mp0);
							keyword.setComposed(true);
							keyword.setString(mp0.getString() + mp1.getString() + mp2.getString() + mp3.getString());
							ret.add(keyword);
							step++;
						} else {
							keyword = new Keyword(mp0);
							keyword.setComposed(true);
							keyword.setString(mp0.getString() + mp1.getString() + mp2.getString());
							ret.add(keyword);
						}
						step++;
					} else {
						keyword = new Keyword(mp0);
						keyword.setComposed(true);
						keyword.setString(mp0.getString() + mp1.getString());
						ret.add(keyword);
					}
					step++;
				}
				i += step;
			}

			
			// 조건 확인으로 불용어 제거
			for( int i = 0; i < ret.size(); i++ ) {
				keyword = ret.get(i);

				// 접두사, 접미사 제거, 보조 용언 제거, 불용어 제거
				if( keyword.isTagOf(POSTag.XP | POSTag.XS | POSTag.VX) || JunkWordDic.contains(mp.getString()) ) {
					ret.remove(i);
					i--;
				}
			}
			
			// 복합 명사의 분석 결과를 읽어온다.
			List<Keyword> cnKeywordList = new ArrayList<Keyword>();
			String[] cnKeywords = null;
			for(int i=0, size = ret.size(); i < size; i++) {
				Keyword k = ret.get(i);
				if( k.isComposed() && (cnKeywords = dic.getCompNoun(k.getString())) != null ) {
					int addIdx = 0;
					for(int j=0, len = cnKeywords.length; j < len; j++) {
						if( JunkWordDic.contains(cnKeywords[j]) ) continue;
						Keyword newKeyword = new Keyword(k);
						newKeyword.setVocTag("E");
						newKeyword.setString(cnKeywords[j]);
						newKeyword.setComposed(false);
						newKeyword.setIndex(k.getIndex() + addIdx);
						addIdx += newKeyword.getString().length();
						cnKeywordList.add(newKeyword);
					}
				}
			}
			ret.addAll(cnKeywordList);
			
			// index순서대로 정렬한다.
			Collections.sort(ret, new Comparator<Keyword>()
			{
				public int compare(Keyword o1, Keyword o2)
				{
					if( o1.getIndex() == o2.getIndex() ) {
						return o1.getString().length() - o2.getString().length();
					}
					return o1.getIndex() - o2.getIndex();
				}
			});

		} catch (Exception e) {
			System.err.println(string);
			e.printStackTrace();
		}

		return new KeywordList(ret);
	}


	public KeywordList removeJunkWord(KeywordList keywordList)
	{
		for( int i = 0, size = keywordList == null ? 0 : keywordList.size(); i < size; i++ ) {

		}
		return keywordList;
	}

	
	/**
	 * <pre>
	 * 후보 어휘에서 접두사, 접미사가 결합되어 복합명사를 만들어내는 경우 복합명사를 색인어로 만들어서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 01
	 * @param mc
	 * @return the composite noun 
	 */
	public Keyword getCompositeNoun(MCandidate mc)
	{
		Keyword ret = null;
		if( mc == null || mc.size() < 2 ) return null;

		int nnCnt = 0;
		for( int i = 0; i < mc.size(); i++ ) {
			Morpheme mp = mc.get(i);
			if( mp.isTagOf(POSTag.NN) ) {
				if( ret == null ) {
					ret = new Keyword(mp);
					ret.setComposed(true);
					nnCnt++;
				} else if( nnCnt == 0 ) {
					return null;
				} else {
					ret.setString(ret.getString() + mp.getString());
					nnCnt++;
				}
			} else if( ret != null && nnCnt > 1 ) {
				return ret;
			} else {
				nnCnt = 0;
			}
		}
		if( nnCnt == 0 ) return null;
		return ret;
	}


	/**
	 * <pre>
	 * 입력된 어휘에서 슷자만 추출하여  "숫자*숫자*...*숫자" 형태로 변환
	 * UOM Value 칼럼을 대상으로한다.
	 * 생성시스템 및 로더시스템에 사용
	 * </pre>
	 * @author	therocks
	 * @since	2005. 9. 20
	 * @param inputString
	 * @return formated uom value
	 */
	public static String getFormatedUOMValues(String inputString)
	{
		String resultString = "";
		List<Token> list = Tokenizer.tokenize(inputString);
		Token token = null;

		for( int i = 0; i < list.size(); i++ ) {
			token = list.get(i);
			if( token.isCharSetOf(CharSetType.NUMBER) ) {
				resultString += token.getString();
			} else if( isUOMConnector(token.getString()) ) {
				resultString += STD_UOM_CONNECTOR;
			} else if( !(token.getString().equals(" ") || token.getString().equals("\t")) ) {
				resultString += token.getString();
			}
		}

		return resultString;
	}


	public static final StringSet	MULTIPLYERS			= new StringSet(new String[] { "*", "x", "X", "×", "Ⅹ" });
	public static final StringSet	RANGE_INDICATOR		= new StringSet(new String[] { "-", "±", "~", "+" });
	public static final String		STD_UOM_CONNECTOR	= "*";


	/**
	 * UOM 연결자 인지를 체크  ("*" 고정)
	 */
	private static boolean isUOMConnector(String uomCon)
	{
		return MULTIPLYERS.contains(uomCon);
	}


	@SuppressWarnings("unused")
	private static boolean isUOMConnector2(String uomCon)
	{
		return MULTIPLYERS.contains(uomCon) || RANGE_INDICATOR.contains(uomCon);
	}


	/**
	 * <pre>
	 * 테스트 하기 위한 코드
	 * </pre>
	 * @author	therocks
	 * @since	2008. 05. 02
	 * @param args
	 */
	public static void main(String[] args)
	{
		String string = "문서 엔터티의 개념이 명확하지 못하다. 즉, 문서 엔터티에 저장되는 단위개체인 문서가 다른 부서로 발신을 하면 다른 문서가 되는 것인지 수정을 할 때는 문서가 새로 생성되지 않는 것인지, 혹은 결재선으로 발신하면 문서가 그대로 있는 것인지 등에 대한 명확한 정의가 없다. 개발 담당자 마저도 이러한 개념을 명확히 설명하지 못하고 있다.";
		string += "\n사용노즐 : Variojet 045\n작동압력 : 10∼135 bar\n최대압력 : 150 bar\n물토출량 : 1400 rpm 11 L/min\n물흡입허용최고온도 : 70 ℃\n최대물흡입높이 : 2.5 m\n소비전력(시작) : 3.1 kW\n소비전력(정상작동) : 2.3 kW\n크기 : 350×330×900 mm\n무게 : 32 kg\n세제흡입가능 HClO4 ClO4 KClO4 CH3OC6H4OH H2(SO4)2";

		KeywordExtractor ke = new KeywordExtractor();

		Keyword keyword = null;
		List<Keyword> ret = ke.extractKeyword(string, false);
		int size = ret == null ? 0 : ret.size();
		for( int i = 0; i < size; i++ ) {
			keyword = ret.get(i);
			System.out.println(i + "\t" + keyword);
		}
	}
}
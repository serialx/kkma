package org.snu.ids.ha.sp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.ma.Eojeol;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;


public class Parser
{
	private List<ParseGrammar>	grammarList	= null;

	private static Parser		parser		= null;


	public static Parser getInstance()
	{
		if( parser == null ) parser = new Parser();
		return parser;
	}


	public Parser()
	{
		initGrammars();
	}


	protected void initGrammars()
	{
		grammarList = new ArrayList<ParseGrammar>();

		// add grammar
		grammarList.add(new ParseGrammar("동일", POSTag.N | POSTag.XSN, POSTag.NP, 1, 1));
		grammarList.add(new ParseGrammar("명사구", POSTag.N | POSTag.XSN, POSTag.N | POSTag.XPN, 1, 1));
		grammarList.add(new ParseGrammar("부사어", POSTag.JKM, POSTag.VP | POSTag.XPV, 1, 1));
		grammarList.add(new ParseGrammar("부사어", POSTag.JKM, POSTag.VP | POSTag.XPV, 10, 10));
		grammarList.add(new ParseGrammar("수식", POSTag.MD | POSTag.ETD | POSTag.JKG, POSTag.N | POSTag.XPN, 1, 1));
		grammarList.add(new ParseGrammar("수식", POSTag.MD | POSTag.ETD | POSTag.JKG, POSTag.N | POSTag.XPN, 3, 10));
		grammarList.add(new ParseGrammar("수식", POSTag.MAG, POSTag.VP | POSTag.MAG | POSTag.MD, 1, 1));
		grammarList.add(new ParseGrammar("수식", POSTag.MAG, POSTag.VP | POSTag.MAG | POSTag.MD, 10, 10));
		grammarList.add(new ParseGrammar("보조 연결", POSTag.ECS, POSTag.VP, 1, 1));
		grammarList.add(new ParseGrammar("의존 연결", POSTag.ECD, POSTag.VP, 10, 1));
		grammarList.add(new ParseGrammar("대등 연결", POSTag.ECE, POSTag.VP, 10, 1));
		grammarList.add(new ParseGrammar("체언 연결", POSTag.JC, POSTag.N | POSTag.XPN, 1, 1));
		grammarList.add(new ParseGrammar("주어", POSTag.JKS, POSTag.VP, 10, 1));
		grammarList.add(new ParseGrammar("주어", POSTag.N | POSTag.XSN | POSTag.JKS | POSTag.JX, POSTag.VCP, 10, 10));
		grammarList.add(new ParseGrammar("보어", POSTag.JKC | POSTag.JX, POSTag.VCN, 3, 10));
		grammarList.add(new ParseGrammar("목적어", POSTag.JKO, POSTag.VP, 10, 1));
		grammarList.add(new ParseGrammar("(주어,목적)대상", POSTag.N | POSTag.XSN | POSTag.JX | POSTag.ETN, POSTag.VP | POSTag.XPV, 10, 100));
		grammarList.add(new ParseGrammar("인용", POSTag.JKQ, POSTag.VV, 1, 1));

		Collections.sort(grammarList, new Comparator<ParseGrammar>()
		{

			@Override
			public int compare(ParseGrammar arg0, ParseGrammar arg1)
			{
				return arg0.priority - arg1.priority;
			}

		});
	}


	public ParseTree parse(Sentence sentence)
	{
		List<ParseTreeNode> nodeList = new ArrayList<ParseTreeNode>();
		for( Iterator<Eojeol> itr = sentence.iterator(); itr.hasNext(); ) {
			nodeList.add(new ParseTreeNode(itr.next()));
		}

		for( int i = 0; i < nodeList.size() - 1; i++ ) {
			ParseTreeNode ptnPrev = nodeList.get(i);

			for( int j = i + 1; j < nodeList.size(); j++ ) {
				ParseTreeNode ptnNext = nodeList.get(j);
				ParseTreeEdge arc = dominate(ptnPrev, ptnNext, j - i);
				if( arc != null ) {
					ptnNext.addChildEdge(arc);
					ptnPrev.setParentNode(ptnNext);
					break;
				}
			}
		}

		ParseTree tree = new ParseTree();
		tree.setSentenec(sentence.getSentence());
		for( Iterator<ParseTreeNode> itr = nodeList.iterator(); itr.hasNext(); ) {
			ParseTreeNode ptn = itr.next();
			if( ptn.getParentNode() == null ) tree.setRoot(ptn);
		}
		tree.setId();
		tree.setAllList();
		return tree;
	}


	public ParseTreeEdge dominate(ParseTreeNode ptnPrev, ParseTreeNode ptnNext, int distance)
	{
		ParseTreeEdge arc = null;
		for( int i = 0, size = grammarList.size(); i < size; i++ ) {
			if( (arc = grammarList.get(i).dominate(ptnPrev, ptnNext, distance)) != null ) {
				return arc;
			}
		}
		return null;
	}


	public static void main(String[] args)
	{
		Parser parser = Parser.getInstance();

		//System.setProperty("DO_DEBUG", "DO_DEBUG");

		MorphemeAnalyzer ma = new MorphemeAnalyzer();
		List<MExpression> mel;

		String string = "제가 엔딩 알려드릴께요 ㅎㅎ 31번곡 Dearest 이게 엔딩곡이에요~ 너무죠아요!!강추강추";
		string = "기장이 조금 길어요 그래도 잘 입어 보려구요";
		string = "아직 입어보진않았는데요 괜찮은거 같네요 여러가지 몇개주문했는데 나머지도 입고되는데로 빠른배송부탁드려요.";
		string = "사무라이를 본받고 싶다고 하신 분들- 우리역사 속의 무자비한 사무라이를 본받고 싶다고한건 아닌것같아요 영화속에서 보여준 그런 무사도 정신을 본받고 싶다고 한것 같아요";
		string = "그리고 배경과 소제가 일본과 사무라이일뿐 감독이 일본인인건 아니니까 이영화 감독이 일본역사에 관심이 많다라고 그러더라구요 담번엔 우리의 무사도정신도 영화화 됐으면 좋겠네요";
		string = "승객들의 불편이 적지 않았고, 무엇보다 산업 현장의 피해가 컸습니다.";
		string = "그밤은내가어제먹은밤이다.";
		string = "범죄로부터 아이들을 구하기 위해서는 먼저 폭력에 노출된 어린이들을 보호하는 것이 필요하다.";

		try {
			mel = ma.postProcess(ma.analyze(string));
			//System.out.println(mel);
			mel = ma.leaveJustBest(mel);
			List<Sentence> stl = ma.divideToSentences(mel);

			for( int i = 0, size = stl.size(); i < size; i++ ) {
				ParseTree tree = parser.parse(stl.get(i));
				StringBuffer sb = new StringBuffer();
				tree.traverse(sb);
				System.out.println(sb);

				List<ParseTreeNode> nodeList = tree.getNodeList();
				List<ParseTreeEdge> edgeList = tree.getEdgeList();

				for( int j = 0; j < nodeList.size(); j++ ) {
					ParseTreeNode node = nodeList.get(j);
					System.out.println("<Node id=\"" + node.getId() + " name=\"" + node.getExp() + "\" label=\"" + node.getMorpXmlStr() + "\" />");
				}
				for( int j = 0; j < edgeList.size(); j++ ) {
					ParseTreeEdge edge = edgeList.get(j);
					System.out.println("<Edge fromId=\"" + edge.getFromId() + " toId=\"" + edge.getToId() + "\" label=\"" + edge.getRelation() + "\" />");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

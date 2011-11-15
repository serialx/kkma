package org.snu.ids.ha.sp;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.snu.ids.ha.ma.Eojeol;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.util.Util;


public class ParseTreeNode
{
	int							id	= 0;
	private Eojeol				eojeol;
	private ParseTreeNode		parentNode;
	private List<ParseTreeEdge>	childEdges;


	protected ParseTreeNode(Eojeol eojeol)
	{
		this.eojeol = eojeol;
	}


	public List<ParseTreeEdge> getChildEdges()
	{
		return childEdges;
	}


	public void addChildEdge(ParseTreeEdge arc)
	{
		if( childEdges == null ) childEdges = new ArrayList<ParseTreeEdge>();
		childEdges.add(arc);
	}


	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}


	public Eojeol getEojeol()
	{
		return eojeol;
	}


	/**
	 * @return the parentNode
	 */
	public ParseTreeNode getParentNode()
	{
		return parentNode;
	}


	/**
	 * @param parentNode the parentNode to set
	 */
	public void setParentNode(ParseTreeNode parentNode)
	{
		this.parentNode = parentNode;
	}


	/**
	 * <pre>
	 * child node로 연결되어 있는지 확인.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @param node
	 * @return
	 */
	public boolean contains(ParseTreeNode node)
	{
		if( this.eojeol == node.eojeol ) return true;
		for( Iterator<ParseTreeEdge> iterChildEdge = childEdges.iterator(); iterChildEdge.hasNext(); ) {
			ParseTreeEdge edge = iterChildEdge.next();
			ParseTreeNode child = edge.getChildNode();

			if( child.contains(node) ) {
				return true;
			}
		}
		return false;
	}


	public void traverse(int depth, String relation, StringBuffer sb)
	{
		for( int i = 0; i < depth; i++ )
			sb.append("\t");
		if( relation != null ) sb.append("<=[" + relation + "]=| ");
		sb.append(id + "\t" + this.eojeol + "\n");
		for( int i = 0, size = childEdges == null ? 0 : childEdges.size(); i < size; i++ ) {
			ParseTreeEdge edge = childEdges.get(i);
			edge.getChildNode().traverse(depth + 1, edge.getRelation(), sb);
		}
	}


	/**
	 * <pre>
	 * traverse하면서 id를 설정해준다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 04
	 * @param id
	 * @return
	 */
	public int traverse(int id)
	{
		this.id = id;
		int ret = id;
		for( int i = 0, size = childEdges == null ? 0 : childEdges.size(); i < size; i++ ) {
			ParseTreeEdge edge = childEdges.get(i);
			ret = edge.getChildNode().traverse(ret + 1);
		}
		return ret;
	}


	/**
	 * <pre>
	 * nodeList에 node를 넣고, edgeList에는 모든 edge를 넣는다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 04
	 * @param nodeList
	 * @param edgeList
	 */
	public void traverse(List<ParseTreeNode> nodeList, List<ParseTreeEdge> edgeList)
	{
		nodeList.add(this);
		for( int i = 0, size = childEdges == null ? 0 : childEdges.size(); i < size; i++ ) {
			ParseTreeEdge edge = childEdges.get(i);
			edgeList.add(edge);
			edge.getChildNode().traverse(nodeList, edgeList);
		}
	}


	/**
	 * <pre>
	 * 어절 표층형을 반환한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 04
	 * @return
	 */
	public String getExp()
	{
		return eojeol == null ? "ROOT" : eojeol.getExp();
	}


	/**
	 * <pre>
	 * 형태소의 분석 결과를 xml에 출력할 수 있도록 반환한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 04
	 * @return
	 */
	public String getMorpXmlStr()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, size = (eojeol == null ? 0 : eojeol.size()); i < size; i++ ) {
			Morpheme morp = eojeol.get(i);
			if( i > 0 ) sb.append("+");
			sb.append(Util.rplcXMLSpclChar(morp.getString()) + "/" + morp.getTag());
		}
		return sb.toString();
	}
}

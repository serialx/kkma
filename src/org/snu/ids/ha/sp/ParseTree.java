/**
 * <pre>
 * </pre>
 * @author	Dongjoo
 * @since	2009. 10. 26
 */
package org.snu.ids.ha.sp;


import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 26
 */
public class ParseTree
{
	String				sentenec	= null;
	ParseTreeNode		root		= new ParseTreeNode(null);
	List<ParseTreeNode>	nodeList	= null;
	List<ParseTreeEdge>	edgeList	= null;


	public void setRoot(ParseTreeNode ptn)
	{
		root.addChildEdge(new ParseTreeEdge("연결", ptn, root, 1, 1));
	}


	public void traverse(StringBuffer sb)
	{
		root.traverse(0, null, sb);
	}


	public void setId()
	{
		root.traverse(0);
	}


	public void setAllList()
	{
		nodeList = new ArrayList<ParseTreeNode>();
		edgeList = new ArrayList<ParseTreeEdge>();
		root.traverse(nodeList, edgeList);
	}


	public List<ParseTreeNode> getNodeList()
	{
		return nodeList;
	}


	public List<ParseTreeEdge> getEdgeList()
	{
		return edgeList;
	}


	/**
	 * @return the sentenec
	 */
	public String getSentenec()
	{
		return sentenec;
	}


	/**
	 * @param sentenec the sentenec to set
	 */
	public void setSentenec(String sentenec)
	{
		this.sentenec = sentenec;
	}

}

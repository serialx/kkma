package org.snu.ids.ha.sp;


public class ParseTreeEdge
{
	private String			relation	= null;
	private ParseTreeNode	childNode	= null;
	private int				distance	= 0;
	private int				priority	= 0;


	/**
	 * <pre>
	 * 특정 릴레이션으로 연결해준다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @param relation
	 * @param childNode
	 */
	public ParseTreeEdge(String relation, ParseTreeNode childNode, ParseTreeNode parentNode, int distance, int priority)
	{
		this.relation = relation;
		this.childNode = childNode;
		this.childNode.setParentNode(parentNode);
		this.distance = distance;
		this.priority = priority;
	}


	/**
	 * @return the relation
	 */
	public String getRelation()
	{
		return relation;
	}


	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation)
	{
		this.relation = relation;
	}


	/**
	 * @return the childNode
	 */
	public ParseTreeNode getChildNode()
	{
		return childNode;
	}


	/**
	 * @param childNode the childNode to set
	 */
	public void setChildNode(ParseTreeNode childNode)
	{
		this.childNode = childNode;
	}


	public int getFromId()
	{
		return childNode.getParentNode().getId();
	}


	public int getToId()
	{
		return childNode.getId();
	}


	public int getDistance()
	{
		return distance;
	}


	public void setDistance(int distance)
	{
		this.distance = distance;
	}


	public int getPriority()
	{
		return priority;
	}


	public void setPriority(int priority)
	{
		this.priority = priority;
	}

}

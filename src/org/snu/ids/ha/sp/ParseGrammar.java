/**
 * <pre>
 * </pre>
 * @author	Dongjoo
 * @since	2009. 10. 26
 */
package org.snu.ids.ha.sp;


import org.snu.ids.ha.constants.POSTag;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 26
 */
public class ParseGrammar
{
	String	relation		= null;
	String	dependantMorp	= null;
	long	dependantTag	= 0l;
	String	dominantMorp	= null;
	long	dominantTag		= 0l;
	int		distance		= 1;
	int		priority		= 10;


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @param relation
	 * @param dependantTag
	 * @param dominantTag
	 * @param distance
	 * @param priority
	 */
	public ParseGrammar(String relation, long dependantTag, long dominantTag, int distance, int priority)
	{
		super();
		this.relation = relation;
		this.dependantTag = dependantTag;
		this.dominantTag = dominantTag;
		this.distance = distance;
		this.priority = priority;
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @param prevNode
	 * @param nextNode
	 * @param distance
	 * @return
	 */
	public ParseTreeEdge dominate(ParseTreeNode prevNode, ParseTreeNode nextNode, int distance)
	{
		// 서술격 조사에 의한 서술
		if( dominantTag == POSTag.VCP ) {
			if( prevNode.getEojeol().isLastTagOf(dependantTag)//
					&& nextNode.getEojeol().containsTagOf(dominantTag)//
					&& distance <= this.distance ) //
			{
				return new ParseTreeEdge(relation, prevNode, nextNode, distance, priority);
			}
		} else {
			if( prevNode.getEojeol().isLastTagOf(dependantTag)//
					&& nextNode.getEojeol().getFirstMorp().isTagOf(dominantTag)//
					&& distance <= this.distance ) //
			{
				return new ParseTreeEdge(relation, prevNode, nextNode, distance, priority);
			}
		}
		return null;
	}
}

/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2008. 08. 20
 */
package org.snu.ids.ha.index;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2008. 08. 20
 */
public class KeywordList
	extends ArrayList<Keyword>
{
	int									docLen	= 0;
	private Hashtable<String, Keyword>	table	= null;


	public KeywordList(List<Keyword> list)
	{
		super();
		table = new Hashtable<String, Keyword>();
		addAll(list);

	}


	public void addAll(List<Keyword> list)
	{
		for( int i = 0, size = list.size(); i < size; i++ ) {
			Keyword keyword = list.get(i), org = null;
			org = table.get(keyword.getKey());
			docLen += keyword.getCnt();
			if( org == null ) {
				table.put(keyword.getKey(), keyword);
				add(keyword);
			} else {
				org.increaseCnt(keyword.getCnt());
			}
		}

		for( int i = 0, size = size(); i < size; i++ ) {
			Keyword keyword = get(i);
			keyword.setFreq((double) keyword.getCnt() / (double) docLen);
		}
	}


	/**
	 * @return the docLen
	 */
	public int getDocLen()
	{
		return docLen;
	}
}

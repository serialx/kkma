/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2008. 05. 01
 */
package org.snu.ids.ha.index;


import org.snu.ids.ha.ma.Morpheme;


/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2008. 05. 01
 */
public class Keyword
	extends Morpheme
{
	int		id		= 0;
	String	vocTag	= "S";
	int		cnt		= 1;	// number of occurrence
	double	freq	= 1;	// frequency = cnt/docLen


	public Keyword()
	{
		super();
	}


	public Keyword(Morpheme mp)
	{
		super(mp);
	}


	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = Integer.parseInt(id);
	}


	/**
	 * @return the freq
	 */
	public double getFreq()
	{
		return freq;
	}


	/**
	 * @param freq the freq to set
	 */
	public void setFreq(double freq)
	{
		this.freq = freq;
	}


	/**
	 * @return the vocTag
	 */
	public String getVocTag()
	{
		return vocTag;
	}


	/**
	 * @param vocTag the vocTag to set
	 */
	public void setVocTag(String vocTag)
	{
		this.vocTag = vocTag;
	}


	public int getCnt()
	{
		return cnt;
	}


	public void setCnt(int cnt)
	{
		this.cnt = cnt;
	}


	public void increaseCnt()
	{
		this.cnt++;
	}


	public void increaseCnt(int cntToAdd)
	{
		this.cnt += cntToAdd;
	}


	public String getKey()
	{
		return super.getString() + ":" + super.getTag();
	}


	public String toString()
	{
		return super.toString() + "\t" + id + "\t" + vocTag + "\t" + cnt + "\t" + freq;
	}

}

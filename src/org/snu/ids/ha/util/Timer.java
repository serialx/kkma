/*
 * Created on 2005. 3. 20
 */
package org.snu.ids.ha.util;


/**
 * @author therocks
 */
public class Timer
{
	long	startTime	= 0;
	long	endTime		= 0;


	/**
	 *
	 *
	 */
	public void start()
	{
		startTime = System.currentTimeMillis();
	}


	/**
	 *
	 *
	 */
	public void stop()
	{
		endTime = System.currentTimeMillis();
	}


	/**
	 *
	 * @return
	 */
	public long getStartTime()
	{
		return startTime;
	}


	/**
	 *
	 * @return
	 */
	public long getEndTime()
	{
		return endTime;
	}


	/**
	 *
	 * @return
	 */
	public double getInterval()
	{
		return getIntervalL() / 1000.0;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2005. 9. 2
	 * @return
	 * @throws Exception
	 */
	public long getIntervalL()
	{
		if( startTime < endTime ) return endTime - startTime;
		return 0;
	}


	/**
	 * print interval time and given msg
	 * @param msg
	 */
	public void printMsg(String msg)
	{
		try {
			System.out.println(msg + "::" + getInterval() + " seconds");
		} catch (Exception e) {
			System.err.println("print error [" + msg + "]");
		}
	}
}

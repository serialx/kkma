/**
 * <pre>
 * </pre>
 * @author	Dongjoo
 * @since	2009. 10. 21
 */
package org.snu.ids.ha.dic;


import java.io.IOException;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 21
 */
public interface DicReader
{
	public String readLine()
		throws IOException;


	public void cleanup()
		throws IOException;
}

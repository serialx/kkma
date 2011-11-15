/**
 * <pre>
 * </pre>
 * @author	Dongjoo
 * @since	2009. 10. 21
 */
package org.snu.ids.ha.dic;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * <pre>
 * 사전을 재로딩할 수 있게하고, 확장성을 가지도록 함.
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 21
 */
public class SimpleDicFileReader
	implements SimpleDicReader
{
	BufferedReader	br	= null;


	public SimpleDicFileReader(String fileName)
		throws UnsupportedEncodingException, FileNotFoundException
	{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
	}


	public String readLine()
		throws IOException
	{
		return br.readLine();
	}


	public void cleanup()
		throws IOException
	{
		if( br != null ) br.close();
	}
}

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
 * 완전한 형태의 사전정의행을 통해서 기분석 후보를 사전에 적재한다. 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 21
 */
public class RawDicFileReader
	implements RawDicReader
{
	BufferedReader	br	= null;


	public RawDicFileReader(String fileName)
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

/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2008. 05. 02
 */
package org.snu.ids.ha.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.snu.ids.ha.util.Timer;



/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2008. 05. 02
 */
public class WordDic
	extends HashSet<String>
{
	int	maxLen	= Integer.MIN_VALUE;
	int	minLen	= Integer.MAX_VALUE;


	public WordDic(String fileName)
	{
		super();
		load(fileName);
	}


	public void load(String fileName)
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			String line = null;
			while( (line = br.readLine()) != null ) {
				int len = line.length();
				if( len > maxLen ) maxLen = len;
				if( len < minLen ) minLen = len;
				super.add(line);
			}
		} catch (IOException e) {
			System.err.println("Loading Error!");
		} finally {
			timer.stop();
			System.out.println("Loaded " + timer.getInterval() + "secs");
		}
	}
}
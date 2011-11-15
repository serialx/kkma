/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2009. 10. 07
 */
package org.snu.ids.ha.constants;


import java.util.Hashtable;


/**
 * <pre>
 * 기호에 대한 구분을 하기 위한 클래스
 * </pre>
 * @author 	therocks
 * @since	2009. 10. 07
 */
public class Symbol
{
	private static final Hashtable<String, Long>	SYMBOL_TYPE_HASH	= new Hashtable<String, Long>();

	static {
		// 줄임표
		SYMBOL_TYPE_HASH.put("...", POSTag.SE);
		SYMBOL_TYPE_HASH.put("‥", POSTag.SE);
		SYMBOL_TYPE_HASH.put("…", POSTag.SE);

		// 마침표물음표,느낌표
		SYMBOL_TYPE_HASH.put("!", POSTag.SF);
		SYMBOL_TYPE_HASH.put(".", POSTag.SF);
		SYMBOL_TYPE_HASH.put("?", POSTag.SF);
		SYMBOL_TYPE_HASH.put("？", POSTag.SF);

		// 붙임표(물결,숨김,빠짐)
		SYMBOL_TYPE_HASH.put("­", POSTag.SO);
		SYMBOL_TYPE_HASH.put("~", POSTag.SO);
		SYMBOL_TYPE_HASH.put("～", POSTag.SO);
		SYMBOL_TYPE_HASH.put("∼", POSTag.SO);

		// 쉼표,가운뎃점,콜론,빗금
		SYMBOL_TYPE_HASH.put(",", POSTag.SP);
		SYMBOL_TYPE_HASH.put("，", POSTag.SP);
		SYMBOL_TYPE_HASH.put("/", POSTag.SP);
		SYMBOL_TYPE_HASH.put("／", POSTag.SP);
		SYMBOL_TYPE_HASH.put(":", POSTag.SP);
		SYMBOL_TYPE_HASH.put("：", POSTag.SP);
		SYMBOL_TYPE_HASH.put(";", POSTag.SP);
		SYMBOL_TYPE_HASH.put("；", POSTag.SP);
		SYMBOL_TYPE_HASH.put("·", POSTag.SP);

		// 따옴표,괄호표
		SYMBOL_TYPE_HASH.put("'", POSTag.SS);
		SYMBOL_TYPE_HASH.put("\"", POSTag.SS);
		SYMBOL_TYPE_HASH.put("(", POSTag.SS);
		SYMBOL_TYPE_HASH.put(")", POSTag.SS);
		SYMBOL_TYPE_HASH.put("）", POSTag.SS);
		SYMBOL_TYPE_HASH.put("[", POSTag.SS);
		SYMBOL_TYPE_HASH.put("]", POSTag.SS);
		SYMBOL_TYPE_HASH.put("`", POSTag.SS);
		SYMBOL_TYPE_HASH.put("｀", POSTag.SS);
		SYMBOL_TYPE_HASH.put("{", POSTag.SS);
		SYMBOL_TYPE_HASH.put("}", POSTag.SS);
		SYMBOL_TYPE_HASH.put("˝", POSTag.SS);
		SYMBOL_TYPE_HASH.put("‘", POSTag.SS);
		SYMBOL_TYPE_HASH.put("’", POSTag.SS);
		SYMBOL_TYPE_HASH.put("“", POSTag.SS);
		SYMBOL_TYPE_HASH.put("”", POSTag.SS);
		SYMBOL_TYPE_HASH.put("〈", POSTag.SS);
		SYMBOL_TYPE_HASH.put("〉", POSTag.SS);
		SYMBOL_TYPE_HASH.put("《", POSTag.SS);
		SYMBOL_TYPE_HASH.put("》", POSTag.SS);
		SYMBOL_TYPE_HASH.put("「", POSTag.SS);
		SYMBOL_TYPE_HASH.put("」", POSTag.SS);
		SYMBOL_TYPE_HASH.put("『", POSTag.SS);
		SYMBOL_TYPE_HASH.put("』", POSTag.SS);
		SYMBOL_TYPE_HASH.put("【", POSTag.SS);
		SYMBOL_TYPE_HASH.put("】", POSTag.SS);
		SYMBOL_TYPE_HASH.put("〔", POSTag.SS);
		SYMBOL_TYPE_HASH.put("〕", POSTag.SS);
		SYMBOL_TYPE_HASH.put("〃", POSTag.SS);
		SYMBOL_TYPE_HASH.put("<", POSTag.SS);
		SYMBOL_TYPE_HASH.put("＜", POSTag.SS);
		SYMBOL_TYPE_HASH.put(">", POSTag.SS);
		SYMBOL_TYPE_HASH.put("＞", POSTag.SS);
		SYMBOL_TYPE_HASH.put("≪", POSTag.SS);
		SYMBOL_TYPE_HASH.put("≫", POSTag.SS);
		
		// UOM (Unit Of Measure) NNM
		SYMBOL_TYPE_HASH.put("㎀", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎁", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎂", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎃", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎄", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎈", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎉", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎊", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎋", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎌", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎍", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎎", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎏", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎐", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎑", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎒", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎓", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎔", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎕", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎖", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎗", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎘", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎙", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎚", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎛", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎜", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎝", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎞", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎟", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎠", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎡", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎢", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎣", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎤", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎥", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎦", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎧", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎨", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎩", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎪", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎫", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎬", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎭", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎮", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎯", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎰", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎱", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎲", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎳", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎴", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎵", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎶", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎷", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎸", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎹", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎺", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎻", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎼", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎽", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎾", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㎿", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏀", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏁", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏂", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏃", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏄", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏅", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏆", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏇", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏈", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏉", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏊", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏏", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏐", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏓", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏖", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏘", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏛", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏜", POSTag.NNM);
		SYMBOL_TYPE_HASH.put("㏝", POSTag.NNM);
	}


	/**
	 * <pre>
	 * 등록된 심볼에 대한 태그 부착하여 반환
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 14
	 * @param symbol
	 * @return
	 */
	public static final long getSymbolTag(String symbol)
	{
		Long lv = SYMBOL_TYPE_HASH.get(symbol);
		if( lv == null ) return POSTag.SW;
		return lv.longValue();
	}
}

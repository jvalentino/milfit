package com.blogspot.jvalentino.milfit.util

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextUtilTest {

	@Test
	public void testWrap() {
		String a = "";
		for (int i = 0 ; i < 96; i++) {
			a += "A";
		}
		//String text = a+a;
		
		//String result = TextUtil.wrapMonospace(text);
		
		String textToCount = "sssssssssssssssssssssssssssssssssssssssssssssss"; //Address limit 32
		System.out.println(textToCount.length());
		
		String text = "ENS Valentino is a hard charging officer. He is driven and focused on completing his PQS to become a fully qualified IW Officer.\n\n- Delivered training documentation for ....";

		String[] result = TextUtil.splitIntoLine(text, 76);
		for (String line : result) {
			System.out.println(line);
		}
		assertEquals(result[0], "ENS Valentino is a hard charging officer. He is driven and focused on");
		assertEquals(result[1], "completing his PQS to become a fully qualified IW Officer.");
		assertEquals(result[2], "");
		assertEquals(result[3], "- Delivered training documentation for ....");
		
	}
	
	@Test
	public void testSplitIntoLine() {
		String a = "";
		for (int i = 0 ; i < 92; i++) {
			a += "A";
		}
		String text = a+a+a;
		
		String[] result = TextUtil.splitIntoLine(text, 92);
		assertEquals(a, result[0]);
		assertEquals(a, result[1]);
		assertEquals(a, result[1]);
		
		text = "This is a stupid test of line wrapping since someone decided to write a PDF library without the ability to do it. I mean, what the crap, don't you think that wanting to draw a blockoffreaking text would be a common need?";
		
		result = TextUtil.splitIntoLine(text, 92);
		assertEquals("This is a stupid test of line wrapping since someone decided to write a PDF library without", result[0]);
		assertEquals("the ability to do it. I mean, what the crap, don't you think that wanting to draw a", result[1]);
		assertEquals("blockoffreaking text would be a common need?", result[2]);
		
		
		text = "ENS Valentino is a hard charging officer. He is driven and focused on completing his PQS to become a fully qualified IW Officer.\n\n- Delivered training documentation for ....";
		result = TextUtil.splitIntoLine(text, 91);
		for (String line : result) {
			System.out.println(line);
		}
		assertEquals(result[0], "ENS Valentino is a hard charging officer. He is driven and focused on completing his PQS to");
		assertEquals(result[1], "become a fully qualified IW Officer.");
		assertEquals(result[2], "");
		assertEquals(result[3], "- Delivered training documentation for ....");
		
		System.out.println("-------------");
		
		result = TextUtil.splitIntoLine(text, 76);
		for (String line : result) {
			System.out.println(line);
		}
		assertEquals(result[0], "ENS Valentino is a hard charging officer. He is driven and focused on");
		assertEquals(result[1], "completing his PQS to become a fully qualified IW Officer.");
		assertEquals(result[2], "");
		assertEquals(result[3], "- Delivered training documentation for ....");
		
		//		list.add( this.data("41", "Comments on Performance", "ENS Smith is a hard charging officer. He is driven and focused on completing his PQS to become a fully qualified IW Officer.\n\nDelivered training documentation for stuff\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20", "10"));
		
		System.out.println("-------------");
		text = "lkj dkljsa dlkjsadlkjsa dlkjas lkdj aslkj sadlkj sadlkj saldkj salkd jlaskdjlaksjdlkjad d-jdsdsds";
		result = TextUtil.splitIntoLine(text, 91);
		for (String line : result) {
			System.out.println(line);
		}
		assertEquals(result[0], "lkj dkljsa dlkjsadlkjsa dlkjas lkdj aslkj sadlkj sadlkj saldkj salkd jlaskdjlaksjdlkjad d");
		assertEquals(result[1], "-jdsdsds");
	}
	
	
}

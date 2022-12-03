package com.blogspot.jvalentino.milfit.acceptance;





/**
 * This class acts as an entry point into the system for the sole purpose 
 * of generating and then displaying a Navy enlisted EVAL of version 11-11.
 * 
 * @author john.valentino
 *
 */
public class NavyEnlistedGenerator extends PdfOutputTester {

	public static void main(String[] args) {
		NavyEnlistedGenerator pdf = new NavyEnlistedGenerator("doc/NAVY_E6_EVAL_08_10.template.json");
		pdf.generate(true);
	}
	
	public NavyEnlistedGenerator(String templateFile) {
		super(templateFile);
	}

	
	protected void generateFormData() {
		this.data("1", "Last, Firm, MI Suffix", "SMITH, JOHN NMN II") ;
		this.data("2", "Grade/Rate", "O2") ;
		this.data("3", "Design", "1815") ;
		this.data("4", "SSN", "99-99-9999") ;
		this.data("5", "Duty Type", ["ACT","FTS","INACT","AT/ADSW/265"] as String[]) ; 
		this.data("6", "UIC", "88875") ;
		this.data("7", "Ship/Station", "NR NIOC FORT WORTH") ;
		this.data("8", "Promotion Status", "REGULAR") ;
		this.data("9", "Date Reported", "11FEB12") ;
		this.data("10", "Periodic", "true") ;
		this.data("11", "Detachment of Individual", "true") ;
		this.data("12", "Promotion Frocking", "true") ;
		this.data("13", "Special", "true") ;
		this.data("14", "Period of Report From", "11DEC01") ;
		this.data("15", "Period of Report To", "12MAY01") ;
		this.data("16", "Not Observed Report", "true") ;
		this.data("17", "Type of Report: Regular", "true") ;
		this.data("18", "Type of Report: Concurrent", "true") ;
		//19 is not present
		this.data("20", "Physical Readiness", "PP") ;
		this.data("21", "Billet Subcategory", "NA") ;
		this.data("22", "Reporting Senior", "DOE, JOHN") ;
		this.data("23", "Grade", "CDR") ;
		this.data("24", "Design", "1815") ;
		this.data("25", "Title", "CO") ;
		this.data("26", "UIC", "88875") ;
		this.data("27", "SSN", "22-22-2222") ;
		this.data("28", "Command employment", "Conducts joint baseball operations at Navy Fun Agency/Central Super Service Texas. Conducts Fun Information Operations Center (FIOC) and DIRSUP missions in support of 14th Fleet, JOUX-South, COMNAVFOO, & COMNAVFOO requirements.")  ;
		this.data("29.A", "Primary duties", "N7 DIVOFF") ;
		this.data("29.B", "Collateral Duties", "N7/Training DIV)FF-6; directly supports 34 unit members maintaining and tracking operation and mobilization readiness. Top Secret Control Officer-6. PFA: 12-1. MOB: IAP\ntest\ntest") ;
		this.data("30", "Date Counseled", "NOT REQ");
		this.data("31", "Counseler", "FOO BAR");
		this.data("32", "Signature of Individual Counseled", "-");
		this.data("33", "Professional Knowledge", "5.0");
		this.data("34", "Quality of Work", "1.0");
		this.data("35", "EO Climage", "2.0");
		this.data("36", "Military Bearing", "3.0");
		this.data("37", "Job Accomplishment", "4.0");
		this.data("38", "Teamwork", "3.0");
		this.data("39", "Leadership", "5.0");
		this.data("40", "Individual Trait Average", "3.45");
		this.data("41.A", "Recommendation Block 1", "123456789ABCDEFGHIJKL\n2\n- SDLKSJDDL");
		this.data("41.B", "Recommendation Block 2", "123456789ABCDEFGHIJKL\n2\n- SDLKSJDDL");		
		this.data("42.A", "Rater Name and Rate", "CTR1 JOHN SMITH SKJS DKJHS KJSHD KJSH DKJHS KDJHSKJHDKSJHDKSJHD KHJSDKJHSJDHSHDKSJHD JHSDKHSDJ");
		this.data("42.C", "Rater Signed Date", "13Jan02");
		this.data("43", "Comments on Performance", "ENS Smith is a hard charging officer. He is driven and focused on completing his PQS to become a fully qualified IW Officer.\n\nDelivered training documentation for stuff\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18", "10");
		this.data("44", "Qualification Achievements", "List achievements here... l lsdj lksajd lksajd lkasjdlk jsadlkjaslkdjkasld ljasdlk jaskldjaslkdjlaksjdlkjsadlkjsadlkjas lkdj aslkdj alskdj laskjd lkasjd lkasjd lkasjdlkajs dlkajs dlkasjdlkasjd lkasjd lkasj dlkasj dlaksjd laksjd laksjd laskjd laskjd alskdj aslkdj aslkdj aslkdjs skljd j sdk sdj sdj s sj sjsjs jsj sj sj sj sj sj sdk sd jsdj skjdk jdkjsdk sdksk djsj  jsdj sd jsdj sdj sdj s jsjdkj sdkjs dkj sdkj dskj dksj sdkj");
		this.data("45", "Individual Performance", "Early Promote");
		this.data("46.A", "Summary: Significant Problems", "0");
		this.data("46.B", "Summary: Progressing", "0");
		this.data("46.C", "Summary: Promotable", "2");
		this.data("46.D", "Summary: Must Promote", "0");
		this.data("46.E", "Summary: Early Promote", "0");
		this.data("47", "Recommendation for Retention", "false");
		this.data("48", "Reporting Senior Address", "123 Fake Street\nHurst, TX\n3\n4");
		this.data("49.A", "Senior Rater Typed Name", "CTRCM RYAN JONES\n2\n3\n4");
		this.data("49.C", "Senior Rater Signature Date", "13Jan02");
		this.data("50.A", "Signature of Reporting Senior", "-");
		this.data("50.B", "Signature of Reporting Senior Date", "13Jan03");
		this.data("50.C", "Summary Group Average", "3.23");
		this.data("51.A", "Intend to submit statement", "true");
		this.data("51.B", "Signature", "-");
		this.data("51.C", "Signature Date", "13Jan02");
		this.data("52.A", "UIC, Name, Grade Concurrent", "sdj slkdjslkdj slkjdslkj lskjd lksjd lksj dlksj dlksj dlksj dlksj dlk jsdlkj sdlkj sldkj slkdjs ds as asdlk asjdkl asdklj askldj aslkdj lkasjd klasj dklajsdlkjas dkj aslkdj lkasj dlkasj dklasj");
		this.data("52.B", "Concurrent Signature", "-");
		this.data("52.C", "Concurrent Signature Date", "13Jan02");
	}

}

package com.blogspot.jvalentino.milfit.acceptance;




/**
 * This class acts as an entry point into the system for the sole purpose 
 * of generating and then displaying a Navy officer FITREP of version 11-11.
 * 
 * @author john.valentino
 *
 */
public class NavyFitrepGenerator extends PdfOutputTester {

	public static void main(String[] args) {
		NavyFitrepGenerator pdf = new NavyFitrepGenerator("doc/NAVY_FITREP_11_11.template.json");
		pdf.generate(true);
	}
	
	public NavyFitrepGenerator(String templateFile) {
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
		this.data("12", "Detachment of Reporting Senior", "true") ;
		this.data("13", "Special", "true") ;
		this.data("14", "Period of Report From", "11DEC01") ;
		this.data("15", "Period of Report To", "12MAY01") ;
		this.data("16", "Not Observed Report", "true") ;
		this.data("17", "Type of Report: Regular", "true") ;
		this.data("18", "Type of Report: Concurrent", "true") ;
		this.data("19", "Type of Report: Ops Cdr", "true") ;
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
		this.data("33", "Professional Expertise", "5.0");
		this.data("34", "Command/EO Climate", "1.0");
		this.data("35", "Military Bearing", "2.0");
		this.data("36", "Teamwork", "3.0");
		this.data("37", "Mission Accomplishment", "4.0");
		this.data("38", "Leadership", "3.0");
		this.data("39", "Tactical Performance", "5.0");
		this.data("40.A", "Recommend this individual", "IWBC School");
		this.data("41", "Comments on Performance", "ENS Smith is a hard charging officer. He is driven and focused on completing his PQS to become a fully qualified IW Officer.\n\nDelivered training documentation for stuff\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18", "12");
		this.data("42", "Promotion Recommendation", "Early Promote");
		this.data("43.A", "Summary: Significant Problems", "0");
		this.data("43.B", "Summary: Progressing", "0");
		this.data("43.C", "Summary: Promotable", "2");
		this.data("43.D", "Summary: Must Promote", "0");
		this.data("43.E", "Summary: Early Promote", "0");
		this.data("44", "Reporting Senior Address", "123 Fake Street\n+Hurst, TX 76053");
		this.data("45.B", "Date", "12Jul01");
		this.data("45.C", "Member Trait Average", "3.45");
		this.data("45.D", "Summary Group Average", "3.33");
		this.data("46.A", "I intend to submit a statement", "true");
		this.data("46.B", "Sign", "-");
		this.data("46.C", "Sign Date", "12Jul21");
		this.data("47.A", "Regular Reporting Senior", "Nothing here");
		this.data("47.B", "Reg Reporting Senior Sig", "-");
		this.data("47.C", "Date Reg Reporting Sig", "12Jul01");
		
	}

}

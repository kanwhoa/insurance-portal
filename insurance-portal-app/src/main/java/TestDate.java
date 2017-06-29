import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class TestDate {
	public static void main(String[] args) throws Exception {
		
		DatatypeFactory df = DatatypeFactory.newInstance();
		XMLGregorianCalendar in = df.newXMLGregorianCalendarDate(1965, 1, 1, -60);
		
		System.out.println("Input Date: "+in.toString());
		System.out.println("Input Date: "+in.normalize().toString());
		System.out.println("Input TimeZone: "+in.toGregorianCalendar().getTimeZone());
		
		Integer years = new Integer(Math.round(
						new Date(
								(new GregorianCalendar(TimeZone.getTimeZone("UTC")).getTime().getTime() - in.normalize().toGregorianCalendar().getTime().getTime())
						).getTime() / 31536000000L
				));
		
		
		// TODO: how to calculate month differences because months in 1970 are no the the same as
		// The target year. This means we need to calculate the difference by addinng up, i.e. 
		// add years, then add days...
		// Decimal months won't work to round up/down
		System.out.println("Now: "+years);

	}
}

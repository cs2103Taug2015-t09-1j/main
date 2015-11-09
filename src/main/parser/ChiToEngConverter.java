/**
 *
 */
package main.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dalton
 *
 */
public class ChiToEngConverter {
	private ChiToEngConverter() {}

	// Chinese To-Be-Converted Constants
	public static final String O_CLOCK = "\u70b9";

	public static final String ONE = "\u4e00";
	public static final String COUNT_TWO = "\u4e24";
	public static final String TWO = "\u4e8c";
	public static final String THREE = "\u4e09";
	public static final String FOUR = "\u56db";
	public static final String FIVE = "\u4e94";
	public static final String SIX = "\u516d";
	public static final String SEVEN = "\u4e03";
	public static final String EIGHT = "\u516b";
	public static final String NINE = "\u4e5d";
	public static final String TEN = "\u5341";
	public static final String ELEVEN = TEN + ONE;
	public static final String TWELVE = TEN + TWO;

	public static final String FROM = "\u4ece";
	public static final String TO = "\u5230";
	public static final String THE_FOLLOWING = "\u540e";
	public static final String THE_PREVIOUS = "\u524d";
	public static final String THE_LAST = "\u53bb";
	public static final String THE_UPCOMING = "\u4e0b(\u4e2a|" + ONE + ")";
	public static final String TONIGHT = "\u665a\u4e0a";
	public static final String MORNING = "\u65e9\u4e0a";
	public static final String AFTER_NOON = "\u4e0b\u5348";
	public static final String BEFORE_NOON = "\u4e0a\u5348";
	public static final String DAY = "\u5929";
	public static final String FOLLOWING_DAY = THE_FOLLOWING + DAY;
	public static final String PREVIOUS_DAY = THE_PREVIOUS + DAY;
	public static final String UPCOMING_DAY = THE_UPCOMING + DAY;

	public static final String YESTERDAY = "\u6628" + DAY;
	public static final String TODAY = "\u4eca" + DAY;
	public static final String TOMORROW = "\u660e" + DAY;

	public static final String DATE = "\u53f7|\u65e5";

	public static final String MONTH = "\u6708";
	public static final String FOLLOWING_MONTH = THE_FOLLOWING + MONTH;
	public static final String PREVIOUS_MONTH = THE_PREVIOUS + MONTH;
	public static final String UPCOMING_MONTH = THE_UPCOMING + MONTH;
	public static final String LAST_MONTH = THE_LAST + MONTH;
	public static final String JAN = ONE + MONTH;
	public static final String FEB = TWO + MONTH;
	public static final String MAR = THREE + MONTH;
	public static final String APR = FOUR + MONTH;
	public static final String MAY = FIVE + MONTH;
	public static final String JUN = SIX + MONTH;
	public static final String JUL = SEVEN + MONTH;
	public static final String AUG = EIGHT + MONTH;
	public static final String SEP = NINE + MONTH;
	public static final String OCT = TEN + MONTH;
	public static final String NOV = ELEVEN + MONTH;
	public static final String DEC = TWELVE + MONTH;

	public static final String YEAR = "\u5e74";
	public static final String FOLLOWING_YEAR = THE_FOLLOWING + YEAR;
	public static final String PREVIOUS_YEAR = THE_PREVIOUS + YEAR;
	public static final String UPCOMING_YEAR = THE_UPCOMING + YEAR;
	public static final String LAST_YEAR = THE_LAST + YEAR;

	public static final String PM = "(" + TONIGHT + "|" + AFTER_NOON + ")";
	public static final String AM = "(" + MORNING + "|" + BEFORE_NOON + ")";
	public static final String ONE_AM = "((?<=" + AM + ")(" + ONE + "|1)" + O_CLOCK + ")|((" + ONE + "|1)" + O_CLOCK + "(?=" + AM + "))";
	public static final String ONE_PM = "((?<=" + PM + ")(" + ONE + "|1)" + O_CLOCK + ")|((" + ONE + "|1)" + O_CLOCK + "(?=" + PM + "))";
	public static final String TWO_AM = "((?<=" + AM + ")(" + COUNT_TWO + "|2)" + O_CLOCK + ")|((" + COUNT_TWO + "|2)" + O_CLOCK + "(?=" + AM + "))";
	public static final String TWO_PM = "((?<=" + PM + ")(" + COUNT_TWO + "|2)" + O_CLOCK + ")|((" + COUNT_TWO + "|2)" + O_CLOCK + "(?=" + PM + "))";
	public static final String THREE_AM = "((?<=" + AM + ")(" + THREE + "|3)" + O_CLOCK + ")|((" + THREE + "|3)" + O_CLOCK + "(?=" + AM + "))";
	public static final String THREE_PM = "((?<=" + PM + ")(" + THREE + "|3)" + O_CLOCK + ")|((" + THREE + "|3)" + O_CLOCK + "(?=" + PM + "))";
	public static final String FOUR_AM = "((?<=" + AM + ")(" + FOUR + "|4)" + O_CLOCK + ")|((" + FOUR + "|4)" + O_CLOCK + "(?=" + AM + "))";
	public static final String FOUR_PM = "((?<=" + PM + ")(" + FOUR + "|4)" + O_CLOCK + ")|((" + FOUR + "|4)" + O_CLOCK + "(?=" + PM + "))";
	public static final String FIVE_AM = "((?<=" + AM + ")(" + FIVE + "|5)" + O_CLOCK + ")|((" + FIVE + "|5)" + O_CLOCK + "(?=" + AM + "))";
	public static final String FIVE_PM = "((?<=" + PM + ")(" + FIVE + "|5)" + O_CLOCK + ")|((" + FIVE + "|5)" + O_CLOCK + "(?=" + PM + "))";
	public static final String SIX_AM = "((?<=" + AM + ")(" + SIX + "|6)" + O_CLOCK + ")|((" + SIX + "|6)" + O_CLOCK + "(?=" + AM + "))";
	public static final String SIX_PM = "((?<=" + PM + ")(" + SIX + "|6)" + O_CLOCK + ")|((" + SIX + "|6)" + O_CLOCK + "(?=" + PM + "))";
	public static final String SEVEN_AM = "((?<=" + AM + ")(" + SEVEN + "|7)" + O_CLOCK + ")|((" + SEVEN + "|7)" + O_CLOCK + "(?=" + AM + "))";
	public static final String SEVEN_PM = "((?<=" + PM + ")(" + SEVEN + "|7)" + O_CLOCK + ")|((" + SEVEN + "|7)" + O_CLOCK + "(?=" + PM + "))";
	public static final String EIGHT_AM = "((?<=" + AM + ")(" + EIGHT + "|8)" + O_CLOCK + ")|((" + EIGHT + "|8)" + O_CLOCK + "(?=" + AM + "))";
	public static final String EIGHT_PM = "((?<=" + PM + ")(" + EIGHT + "|8)" + O_CLOCK + ")|((" + EIGHT + "|8)" + O_CLOCK + "(?=" + PM + "))";
	public static final String NINE_AM = "((?<=" + AM + ")(" + NINE + "|9)" + O_CLOCK + ")|((" + NINE + "|9)" + O_CLOCK + "(?=" + AM + "))";
	public static final String NINE_PM = "((?<=" + PM + ")(" + NINE + "|9)" + O_CLOCK + ")|((" + NINE + "|9)" + O_CLOCK + "(?=" + PM + "))";
	public static final String TEN_AM = "((?<=" + AM + ")(" + TEN + "|10)" + O_CLOCK + ")|((" + TEN + "|10)" + O_CLOCK + "(?=" + AM + "))";
	public static final String TEN_PM = "((?<=" + PM + ")(" + TEN + "|10)" + O_CLOCK + ")|((" + TEN + "|10)" + O_CLOCK + "(?=" + PM + "))";
	public static final String ELEVEN_AM = "((?<=" + AM + ")(" + ELEVEN + "|11)" + O_CLOCK + ")|((" + ELEVEN + "|11)" + O_CLOCK + "(?=" + AM + "))";
	public static final String ELEVEN_PM = "((?<=" + PM + ")(" + ELEVEN + "|11)" + O_CLOCK + ")|((" + ELEVEN + "|11)" + O_CLOCK + "(?=" + PM + "))";
	public static final String TWELVE_AM = "((?<=" + AM + ")(" + TWELVE + "|12)" + O_CLOCK + ")|((" + TWELVE + "|12)" + O_CLOCK + "(?=" + AM + "))";
	public static final String TWELVE_PM = "((?<=" + PM + ")(" + TWELVE + "|12)" + O_CLOCK + ")|((" + TWELVE + "|12)" + O_CLOCK + "(?=" + PM + "))";

	public static final String[] chineseConstants = {TWELVE_AM, TWELVE_PM,
													ELEVEN_AM, ELEVEN_PM,
													TEN_AM, TEN_PM,
													ONE_AM, ONE_PM,
													TWO_AM, TWO_PM,
													THREE_AM, THREE_PM,
													FOUR_AM, FOUR_PM,
													FIVE_AM, FIVE_PM,
													SIX_AM, SIX_PM,
													SEVEN_AM, SEVEN_PM,
													EIGHT_AM, EIGHT_PM,
													NINE_AM, NINE_PM,
													FROM, TO,
													TONIGHT, MORNING, AFTER_NOON, BEFORE_NOON,
													YESTERDAY, TODAY, TOMORROW,
													FOLLOWING_DAY, PREVIOUS_DAY, UPCOMING_DAY,
													FOLLOWING_MONTH, PREVIOUS_MONTH, UPCOMING_MONTH, LAST_MONTH,
													FOLLOWING_YEAR, PREVIOUS_YEAR, UPCOMING_YEAR, LAST_YEAR,
													DEC, NOV, OCT, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP,
													YEAR,
													(THREE+TEN), (THREE+ONE),
													(TWO+TEN), (TWO+ONE), (TWO+TWO), (TWO+THREE), (TWO+FOUR), (TWO+FIVE), (TWO+SIX), (TWO+SEVEN), (TWO+EIGHT), (TWO+NINE),
													(ONE+THREE), (ONE+FOUR), (ONE+FIVE), (ONE+SIX), (ONE+SEVEN), (ONE+EIGHT), (ONE+NINE), TWELVE, ELEVEN, TEN, ONE, TWO, COUNT_TWO, THREE, FOUR, FIVE, SIX,
													SEVEN, EIGHT, NINE, DATE, O_CLOCK};

	// English Replacement Constants
	public static final String ENG_O_CLOCK = " ";

	public static final String ENG_ONE = " 1 ";
	public static final String ENG_TWO = " 2 ";
	public static final String ENG_THREE = " 3 ";
	public static final String ENG_FOUR = " 4 ";
	public static final String ENG_FIVE = " 5 ";
	public static final String ENG_SIX = " 6 ";
	public static final String ENG_SEVEN = " 7 ";
	public static final String ENG_EIGHT = " 8 ";
	public static final String ENG_NINE = " 9 ";
	public static final String ENG_TEN = " 10 ";
	public static final String ENG_ELEVEN = " 11 ";
	public static final String ENG_TWELVE = " 12 ";

	public static final String ENG_FROM = " from ";
	public static final String ENG_TO = " to ";
	public static final String ENG_TONIGHT = " tonight ";
	public static final String ENG_MORNING = " morning ";
	public static final String ENG_AFTER_NOON = " ";
	public static final String ENG_BEFORE_NOON = " ";

	public static final String ENG_DAY = " day ";
	public static final String ENG_FOLLOWING_DAY = " following day ";
	public static final String ENG_PREVIOUS_DAY = " previous day ";
	public static final String ENG_UPCOMING_DAY = " next day ";
	public static final String ENG_YESTERDAY = " yesterday ";
	public static final String ENG_TODAY = " today ";
	public static final String ENG_TOMORROW = " tomorrow ";

	public static final String ENG_DATE = " ";

	public static final String ENG_MONTH = " ";
	public static final String ENG_FOLLOWING_MONTH = " following month ";
	public static final String ENG_PREVIOUS_MONTH = " previous month ";
	public static final String ENG_UPCOMING_MONTH = " next month ";
	public static final String ENG_LAST_MONTH = " last month ";
	public static final String ENG_JAN = " january ";
	public static final String ENG_FEB = " february ";
	public static final String ENG_MAR = " march ";
	public static final String ENG_APR = " april ";
	public static final String ENG_MAY = " may ";
	public static final String ENG_JUN = " june ";
	public static final String ENG_JUL = " july ";
	public static final String ENG_AUG = " august ";
	public static final String ENG_SEP = " september ";
	public static final String ENG_OCT = " october ";
	public static final String ENG_NOV = " november ";
	public static final String ENG_DEC = " december ";

	public static final String ENG_YEAR = " ";
	public static final String ENG_FOLLOWING_YEAR = " following year ";
	public static final String ENG_PREVIOUS_YEAR = " previous year ";
	public static final String ENG_UPCOMING_YEAR = " next year ";
	public static final String ENG_LAST_YEAR = " last year ";
	public static final String ENG_PM = " am ";
	public static final String ENG_AM = " pm ";
	public static final String ENG_ONE_AM = " 1am ";
	public static final String ENG_ONE_PM = " 1pm ";
	public static final String ENG_TWO_AM = " 2am ";
	public static final String ENG_TWO_PM = " 2pm ";
	public static final String ENG_THREE_AM = " 3am ";
	public static final String ENG_THREE_PM = " 3pm ";
	public static final String ENG_FOUR_AM = " 4am ";
	public static final String ENG_FOUR_PM = " 4pm ";
	public static final String ENG_FIVE_AM = " 5am ";
	public static final String ENG_FIVE_PM = " 5pm ";
	public static final String ENG_SIX_AM = " 6am ";
	public static final String ENG_SIX_PM = " 6pm ";
	public static final String ENG_SEVEN_AM = " 7am ";
	public static final String ENG_SEVEN_PM = " 7pm ";
	public static final String ENG_EIGHT_AM = " 8am ";
	public static final String ENG_EIGHT_PM = " 8pm ";
	public static final String ENG_NINE_AM = " 9am ";
	public static final String ENG_NINE_PM = " 9pm ";
	public static final String ENG_TEN_AM = " 10am ";
	public static final String ENG_TEN_PM = " 10pm ";
	public static final String ENG_ELEVEN_AM = " 11am ";
	public static final String ENG_ELEVEN_PM = " 11pm";
	public static final String ENG_TWELVE_AM = " 12am ";
	public static final String ENG_TWELVE_PM = " 12pm ";

	public static final String[] englishConstants = {ENG_TWELVE_AM, ENG_TWELVE_PM,
													ENG_ELEVEN_AM, ENG_ELEVEN_PM,
													ENG_TEN_AM, ENG_TEN_PM,
													ENG_ONE_AM, ENG_ONE_PM,
													ENG_TWO_AM, ENG_TWO_PM,
													ENG_THREE_AM, ENG_THREE_PM,
													ENG_FOUR_AM, ENG_FOUR_PM,
													ENG_FIVE_AM, ENG_FIVE_PM,
													ENG_SIX_AM, ENG_SIX_PM,
													ENG_SEVEN_AM, ENG_SEVEN_PM,
													ENG_EIGHT_AM, ENG_EIGHT_PM,
													ENG_NINE_AM, ENG_NINE_PM,
													ENG_FROM, ENG_TO,
													ENG_TONIGHT, ENG_MORNING, ENG_AFTER_NOON, ENG_BEFORE_NOON,
													ENG_YESTERDAY, ENG_TODAY, ENG_TOMORROW,
													ENG_FOLLOWING_DAY, ENG_PREVIOUS_DAY, ENG_UPCOMING_DAY,
													ENG_FOLLOWING_MONTH, ENG_PREVIOUS_MONTH, ENG_UPCOMING_MONTH, ENG_LAST_MONTH,
													ENG_FOLLOWING_YEAR, ENG_PREVIOUS_YEAR, ENG_UPCOMING_YEAR, ENG_LAST_YEAR,
													ENG_DEC, ENG_NOV, ENG_OCT, ENG_JAN, ENG_FEB, ENG_MAR, ENG_APR, ENG_MAY, ENG_JUN, ENG_JUL, ENG_AUG, ENG_SEP,
													ENG_YEAR,
													" 30 ", " 31 ",
													" 20 ", " 21 ", " 22 ", " 23 ", " 24 ", " 25 ", " 26 ", " 27 ", " 28 ", " 29 ",
													" 13 ", " 14 ", " 15 ", " 16 ", " 17 ", " 18 ", " 19 ",
													ENG_TWELVE, ENG_ELEVEN, ENG_TEN, ENG_ONE, ENG_TWO, ENG_TWO, ENG_THREE, ENG_FOUR, ENG_FIVE, ENG_SIX,
													ENG_SEVEN, ENG_EIGHT, ENG_NINE, ENG_DATE, ENG_O_CLOCK};


	public static String convertChineseToEnglishUnicode(String input) {
		for (int i = 0; i < chineseConstants.length; i++) {
			Pattern pat = Pattern.compile("\\s*" + chineseConstants[i] + "\\s*", Pattern.UNICODE_CHARACTER_CLASS);
			Matcher mat = pat.matcher(input);
			if (mat.find()) {
				input = input.replaceAll(mat.group(), englishConstants[i]);
			}
		}
		return input;
	}

	public static boolean isChineseString(String input) {
		if(input.matches("^[\u4E00-\u62FF\u6300-\u77FF\u7800-\u8CFF\u8D00-\u9FFF\\p{IsDigit}]+")) {
			return true;
		}
		return false;
	}
}

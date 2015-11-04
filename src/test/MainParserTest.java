package test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import main.model.ParsedObject;
import main.model.EnumTypes;
import main.model.EnumTypes.COMMAND_TYPE;
import main.model.EnumTypes.TASK_TYPE;
import main.parser.Parser;

//@@author Xiang Jie
public class MainParserTest {
	private String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m"};
	private String[] deleteCmdList = {"delete", "del", "/d", "remove", "rm", "/r"};
	private String[] undoCmdList = {"undo", "/un"};
	private String[] redoCmdList = {"redo", "/re"};
	private Parser testParser = Parser.getInstance();

	@Test
	public void testGetInstance() {
		//fail("Not yet implemented");
	}

	@Test
	public void testDetermineCommandType() {
		assertEquals(COMMAND_TYPE.ADD, testParser.determineCommandType("do something"));
		assertEquals(COMMAND_TYPE.DELETE, testParser.determineCommandType("delete 4"));
		assertEquals(COMMAND_TYPE.INVALID, testParser.determineCommandType(""));
		//assertEquals(COMMAND_TYPE.UPDATE, testParser.determineCommandType("update 4 something"));
	}

	@Test
	public void testIsValidCommand() {
		//Parser test = Parser.getInstance();
		//assertTrue(test.isValidCommand("update", updateCmdList, "\\s+\\d+\\s+\\d+"));
		//assertFalse(test.isValidCommand("what", updateCmdList, "\\s+\\d+\\s+\\d+"));
	}

	@Test
	public void testRemoveCommandWord() {
		//fail("Not yet implemented");
	}

	@Test
	public void testFormatDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.OCTOBER, 19);
		Date date = cal.getTime();
		assertEquals("Mon, 19 Oct 2015", testParser.formatDate(date, "EEE, d MMM yyyy"));
		assertNotEquals("Fri, 30 Nov 2015", testParser.formatDate(date, "EEE, d MMM yyyy"));

		Calendar cal2 = Calendar.getInstance();
		cal2.set(2013, Calendar.NOVEMBER, 29);
		Date date2 = cal2.getTime();
		assertEquals("Fri, 29 Nov 2013", testParser.formatDate(date2, "EEE, d MMM yyyy"));
		assertNotEquals("Random", testParser.formatDate(date2, "EEE, d MMM yyyy"));
	}

	@Test
	public void testGetDateList() {
		assertNotNull(testParser.parseDateGroups("29 November"));
		assertNull(testParser.parseDateGroups("nothing"));
	}

	@Test
	public void testParseDates() {
		assertNotNull(testParser.getDateGroups("29 NOvember"));
		assertNull(testParser.getDateGroups("random"));
	}

	@Test
	public void testGetTaskDesc() {

	}

	@Test
	public void testGetCommandParameters() {
		assertEquals("3", testParser.getCommandParameters("3 to 10", COMMAND_TYPE.DELETE).get(0));
	}

	@Test
	public void testGetAddParsedObject() {
		ParsedObject testObj0 = testParser.getAddParsedObject("do something by 29 Nov");
		assertEquals(COMMAND_TYPE.ADD, testObj0.getCommandType());
		assertEquals(TASK_TYPE.DEADLINE, testObj0.getTaskType());

		ParsedObject testObj1 = testParser.getAddParsedObject("29 Nov do something");
		assertEquals(COMMAND_TYPE.ADD, testObj1.getCommandType());
		assertEquals(TASK_TYPE.SINGLE_DATE_EVENT, testObj1.getTaskType());

		ParsedObject testObj2 = testParser.getAddParsedObject("do something from 28 Nov to 29 Nov");
		assertEquals(COMMAND_TYPE.ADD, testObj2.getCommandType());
		assertEquals(TASK_TYPE.DOUBLE_DATE_EVENT, testObj2.getTaskType());

		ParsedObject testObj3 = testParser.getAddParsedObject("do something");
		assertEquals(COMMAND_TYPE.ADD, testObj3.getCommandType());
		assertEquals(TASK_TYPE.TODO, testObj3.getTaskType());
	}

	@Test
	public void testGetUpdateParsedObject() {
		ParsedObject testObj = testParser.getUpdateParsedObject("update 4 something");
		assertEquals(COMMAND_TYPE.UPDATE, testObj.getCommandType());
		assertEquals(null, testObj.getTaskType());
	}

	@Test
	public void testParseInteger() {
		assertSame(3, testParser.parseInteger("3"));
		assertSame(0, testParser.parseInteger("something"));
		assertSame(20, testParser.parseInteger("20"));
	}

	@Test
	public void testGetDeleteParsedObject() {
		ParsedObject testObj = testParser.getDeleteParsedObject("delete 3");
		assertEquals(COMMAND_TYPE.DELETE, testObj.getCommandType());
		assertEquals(null, testObj.getTaskType());
		assertEquals(3, testObj.getObjects().get(0));
	}

	@Test
	public void testGetUndoParsedObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRedoParsedObject() {
		fail("Not yet implemented");
	}

	/*	@@author Dalton
	 *  Command is deemed invalid when the string is empty regardless of any whitespaces
	 */
	@Test
	public void determineCommandTypeInvalid() {
		assertEquals(EnumTypes.COMMAND_TYPE.INVALID, testParser.determineCommandType("          "));
	}

	/*	@@author Dalton
	 *  Add command is flexible as it it parsed using Natural Language Processing
	 */
	@Test
	public void determineCommandTypeAdd() {
		assertEquals(EnumTypes.COMMAND_TYPE.ADD, testParser.determineCommandType("lunch with john at 9pm tomorrow"));
		assertEquals(EnumTypes.COMMAND_TYPE.ADD, testParser.determineCommandType("lunch at 9pm tomorrow with john"));
		assertEquals(EnumTypes.COMMAND_TYPE.ADD, testParser.determineCommandType("at 9pm tomorrow lunch with john"));
	}

	/*	@@author Dalton
	 *  Update command is parsed using the regex pattern ("(?i)^" + command + "\\s+\\d+\\s+\\d+")
	 *  where command is a word from the string array updateCmdList with values {"update", "/u", "edit", "/e", "modify", "/m"};
	 *	The following test is to test the boundary of the regex pattern for the Update command
	 */
	@Test
	public void testDetermineCommandTypeUpdate() {
		determineCommandTypeUpdateValid();
		determineCommandTypeUpdateInvalid();
	}

	// @@author Dalton
	private void determineCommandTypeUpdateValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update   1   4   testing one two three"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("/u 1  4   one  two    three"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("edit 1  4  four   five  six"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("/e 1  4  789"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("modify 1  4   abc"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("/m 1   4   def"));
	}

	// @@author Dalton
	private void determineCommandTypeUpdateInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update test 124"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update 1 test"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("test update"));
	}

	/*	@@author Dalton
	 *  Delete command is parsed using the regex pattern ("(?i)^" + command + "\\s+\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)$")
	 *  where command is a word from the string array deleteCmdList with values {"delete", "del", "/d", "remove", "rm", "/r"};
	 *	The following test is to test the boundary of the regex pattern for the Delete command
	 */
	@Test
	public void testDetermineCommandTypeDelete() {
		determineCommandTypeDeleteValid();
		determineCommandTypeDeleteInvalid();
	}

	// @@author Dalton
	private void determineCommandTypeDeleteValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete 1   to   10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete  5  6   7   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("del 1  -    10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("del  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/d 1-10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/d  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("remove 1-10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("remove  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("rm 1to10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("rm  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/r 1-10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/r  567   "));
	}

	// @@author Dalton
	private void determineCommandTypeDeleteInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("123 delete"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete 1to10 1 2 3"));
	}

	/*	@@author Dalton
	 *  Undo command is parsed using the regex pattern ("(?i)^" + command + "(\\s+\\d+\\s*)?$")
	 *  where command is a word from the string array undoCmdList with values {"undo", "/un"};
	 *	The following test is to test the boundary of the regex pattern for the Undo command
	 */
	@Test
	public void testDetermineCommandTypeUndo() {
		determineCommandTypeUndoValid();
		determineCommandTypeUndoInvalid();
	}

	// @@author Dalton
	private void determineCommandTypeUndoValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("undo"));
		assertEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("undo  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("/un"));
		assertEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("/un  567   "));
	}

	// @@author Dalton
	private void determineCommandTypeUndoInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("undo 235 2"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("/un 235 2"));
	}

	/*	@@author Dalton
	 *  Redo command is parsed using the regex pattern ("(?i)^" + command + "(\\s+\\d+\\s*)?$")
	 *  where command is a word from the string array redoCmdList with values {"redo", "/re"};
	 *	The following test is to test the boundary of the regex pattern for the Redo command
	 */
	@Test
	public void testDetermineCommandTypeRedo() {
		determineCommandTypeRedoValid();
		determineCommandTypeRedoInvalid();
	}

	// @@author Dalton
	private void determineCommandTypeRedoValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("redo"));
		assertEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("redo  234   "));
		assertEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("/re"));
		assertEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("/re  234   "));
	}

	// @@author Dalton
	private void determineCommandTypeRedoInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("redo 32 2"));
	}

	/*	@@author Dalton
	 *  Exit command is parsed using the regex pattern ("(?i)^" + command + "\\s*$")
	 *  where command is a word from the string array exitCmdList with values {"exit", "/e", "quit", "/q"}
	 *	The following test is to test the boundary of the regex pattern for the Exit command
	 */
	@Test
	public void testDetermineCommandTypeExit() {
		determineCommandTypeExitValid();
		determineCommandTypeExitInvalid();
	}

	// @@author Dalton
	private void determineCommandTypeExitValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("exit"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("exit   "));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("/e"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("/e   "));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("quit"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("quit   "));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("/q"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("/q   "));
	}

	// @@author Dalton
	private void determineCommandTypeExitInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("exit22141"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("5234exit"));
	}

}

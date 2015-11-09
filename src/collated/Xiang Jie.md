# Xiang Jie
###### test\MainParserTest.java
``` java
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

```
###### test\ModelTest.java
``` java
public class ModelTest {

	@Test
	public void testCloneTask() {
		List<Task> tasks = Storage.getInstance().getAllTask(TASK_TYPE.EVENT);
		Task event = tasks.get(0);
		Task eventClone = event.clone();
		Date d1 = ((Event)event).getFromDate(),
			 d2 = ((Event)eventClone).getFromDate();
		if (d1 == d2) {
			System.out.println("equal");
		} else {
			System.out.println("not equal");
		}
	}

	@Test
	public void testModels() {
		List<Task> tasks = Storage.getInstance().getAllTask(TASK_TYPE.EVENT);
		Task event = tasks.get(0);
		System.out.println(event.getType());
	}
}
```

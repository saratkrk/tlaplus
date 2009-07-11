package tlc2.output;

import util.ToolIO;
import junit.framework.TestCase;

/**
 * @author Simon Zambrovski
 * @version $Id$
 */
public class ErrorPrinterTest extends TestCase
{

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        ToolIO.setMode(ToolIO.TOOL);
        ToolIO.reset();
    }

    /**
     * Test method for {@link tlc2.output.MP#printError(int)}.
     */
    public void testPrintErrorInt()
    {
        MP.printError(EC.UNIT_TEST);
        String[] allMessages = ToolIO.getAllMessages();
        assertEquals(allMessages.length, 1);
        assertEquals(allMessages[0], "Error: [%1%][%2%]");
    }

    /**
     * Test method for {@link tlc2.output.MP#printError(int, java.lang.String)}.
     */
    public void testPrintErrorIntString()
    {
        String parameter = "EXPECTED";
        MP.printError(EC.UNIT_TEST, parameter);
        String[] allMessages = ToolIO.getAllMessages();
        assertEquals(allMessages.length, 1);
        assertEquals(allMessages[0], "Error: [" + parameter + "][%2%]");
    }

    /**
     * Test method for {@link tlc2.output.MP#printError(int, java.lang.String[])}.
     */
    public void testPrintErrorIntStringArray()
    {
        String[] parameters = new String[] { "EXPECTED", "EXPECTED2", "TOO MANY" };
        MP.printError(EC.UNIT_TEST, parameters);
        String[] allMessages = ToolIO.getAllMessages();
        assertEquals(allMessages.length, 1);
        assertEquals(allMessages[0], "Error: [" + parameters[0] + "][" + parameters[1] + "]");
    }

}

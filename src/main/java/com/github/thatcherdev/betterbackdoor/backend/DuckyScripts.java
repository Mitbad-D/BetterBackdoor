package com.github.thatcherdev.betterbackdoor.backend;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DuckyScripts {

	private static Robot robot;
	private static int defaultDelay;
	private static ArrayList<Character> regKeys = (ArrayList<Character>) "abcdefghijklmnopqrstuvwxyz`1234567890-=[]\\;',./ "
			.chars().mapToObj((i) -> Character.valueOf((char) i)).collect(Collectors.toList());
	private static ArrayList<Character> shiftKeys = (ArrayList<Character>) "ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+{}|:\"<>?"
			.chars().mapToObj((i) -> Character.valueOf((char) i)).collect(Collectors.toList());

	/**
	 * Cycles though lines from DuckyScript corresponding to {@link scriptName}
	 * using {@link in}. If applicable, spaces at end of line are removed and line
	 * is passed to {@link #handleLine(String line)}.
	 *
	 * @param scriptName name of DuckyScript to execute
	 * @return state of completion
	 */
	public static boolean run(String scriptName) {
		Scanner in = null;
		try {
			robot = new Robot();
			in = new Scanner(new File("scripts\\" + scriptName));
			while (in.hasNextLine()) {
				String line = in.nextLine();
				while (line.endsWith(" "))
					line = line.substring(0, line.length() - 1);
				if (!line.isEmpty())
					handleLine(line);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (in != null)
				in.close();
		}
	}

	/**
	 * {@link line} is split into {@link command} and {@link args} which are then
	 * mutated to work with robot {@link #robot}.
	 *
	 * @param line line from DuckyScript to execute
	 * @throws InterruptedException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	private static void handleLine(String line) throws InterruptedException, IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		String command = line;
		String args = "";
		if (line.contains(" ")) {
			command = line.substring(0, line.indexOf(" "));
			args = line.substring(line.indexOf(" ") + 1);
		}

		if (command.equals("GUI"))
			command = "WINDOWS";
		else if (args.equals("GUI"))
			args = "WINDOWS";
		else if (command.equals("PAGEUP") || command.equals("PAGEDOWN"))
			command = command.replace("PAGE", "PAGE_");
		else if (args.equals("PAGEUP") || args.equals("PAGEDOWN"))
			args = args.replace("PAGE", "PAGE_");
		else if (command.equals("UPARROW") || command.equals("DOWNARROW") || command.equals("LEFTARROW")
				|| command.equals("RIGHTARROW"))
			command = command.replace("ARROW", "");
		else if (args.equals("UPARROW") || args.equals("DOWNARROW") || args.equals("LEFTARROW")
				|| args.equals("RIGHTARROW"))
			args = args.replace("ARROW", "");
		else if (command.equals("MENU") || command.equals("APP")) {
			command = "SHIFT";
			args = "F10";
		} else if (command.equals("CTRL"))
			command = "CONTROL";
		else if (command.equals("CAPSLOCK"))
			command = "CAPS_LOCK";
		else if (command.equals("NUMLOCK"))
			command = "NUM_LOCK";
		else if (command.equals("SCROLLLOCK"))
			command = "SCROLL_LOCK";
		else if (args.equals("ESC"))
			args = "ESCAPE";
		else if (args.equals("BREAK"))
			args = "PAUSE";

		if (command.equals("DEFAULT_DELAY") || command.equals("DEFAULTDELAY"))
			defaultDelay = Integer.parseInt(args);
		else if (command.equals("DELAY"))
			Thread.sleep(Integer.parseInt(args));
		else if (command.equals("STRING")) {
			type(args);
		} else if (command.equals("WINDOWS") || command.equals("SHIFT") || command.equals("CONTROL")
				|| command.equals("ALT")) {
			robot.keyPress(KeyEvent.class.getField("VK_" + command).getInt(null));
			if (!args.isEmpty()) {
				robot.keyPress(KeyEvent.class.getField("VK_" + args.toUpperCase()).getInt(null));
				robot.keyRelease(KeyEvent.class.getField("VK_" + args.toUpperCase()).getInt(null));
			}
			robot.keyRelease(KeyEvent.class.getField("VK_" + command).getInt(null));
		} else if (!line.startsWith("REM")) {
			robot.keyPress(KeyEvent.class.getField("VK_" + command).getInt(null));
			robot.keyRelease(KeyEvent.class.getField("VK_" + command).getInt(null));
		}
		Thread.sleep(defaultDelay);
	}

	/**
	 * {@link #robot} is used to type {@link toType}.
	 *
	 * @param toType String to type
	 */
	private static void type(String toType) {
		for (char c : toType.toCharArray())
			if (regKeys.indexOf(c) != -1) {
				robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
				robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
			} else {
				robot.keyPress(KeyEvent.VK_SHIFT);
				robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(regKeys.get(shiftKeys.indexOf(c))));
				robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(regKeys.get(shiftKeys.indexOf(c))));
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
	}
}

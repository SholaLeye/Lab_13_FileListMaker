import java.util.Scanner;
import java.util.regex.Pattern;

public class SafeInput {

    /**
     * Get a string with at least one non-whitespace character.
     *
     * @param pipe   Scanner instance to read input
     * @param prompt Prompt message
     * @return A trimmed, non-empty string
     */
    public static String getNonZeroLenString(Scanner pipe, String prompt) {
        String retString = "";
        do {
            System.out.print(prompt + ": ");
            retString = pipe.nextLine().trim();
            if (retString.length() == 0) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (retString.length() == 0);
        return retString;
    }

    /**
     * Get an integer within a specified range (inclusive).
     *
     * @param pipe   Scanner instance to read input
     * @param prompt Prompt message
     * @param low    Minimum acceptable value
     * @param high   Maximum acceptable value
     * @return The integer value entered by the user
     */
    public static int getRangedInt(Scanner pipe, String prompt, int low, int high) {
        int retVal = 0;
        boolean done = false;
        do {
            System.out.print(prompt + " [" + low + " - " + high + "]: ");
            if (pipe.hasNextInt()) {
                retVal = pipe.nextInt();
                pipe.nextLine(); // clear buffer
                if (retVal >= low && retVal <= high) {
                    done = true;
                } else {
                    System.out.println("Number must be between " + low + " and " + high + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                pipe.nextLine(); // clear bad input
            }
        } while (!done);
        return retVal;
    }

    /**
     * Prompt the user for a Y/N confirmation.
     *
     * @param pipe   Scanner instance to read input
     * @param prompt Prompt message
     * @return true if user enters Y, false if N
     */
    public static boolean getYNConfirm(Scanner pipe, String prompt) {
        String input;
        boolean valid = false;
        boolean result = false;

        do {
            System.out.print(prompt + " [Y/N]: ");
            input = pipe.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("YES")) {
                valid = true;
                result = true;
            } else if (input.equals("N") || input.equals("NO")) {
                valid = true;
                result = false;
            } else {
                System.out.println("Please enter Y or N.");
            }
        } while (!valid);

        return result;
    }

    /**
     * Get a string that matches a regular expression pattern.
     *
     * @param pipe   Scanner instance to read input
     * @param prompt Prompt message
     * @param regEx  Regular expression pattern
     * @return A string that matches the pattern
     */
    public static String getRegExString(Scanner pipe, String prompt, String regEx) {
        String response;
        boolean done = false;
        do {
            System.out.print(prompt + ": ");
            response = pipe.nextLine().trim();
            if (Pattern.matches(regEx, response)) {
                done = true;
            } else {
                System.out.println("Input must match pattern: " + regEx);
            }
        } while (!done);
        return response;
    }
}

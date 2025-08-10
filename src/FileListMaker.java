import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileListMaker {

    private static final Scanner console = new Scanner(System.in);
    private static final ArrayList<String> list = new ArrayList<>();
    private static Path currentFile = null;
    private static boolean needsToBeSaved = false;

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayListAndMenu();
            String choice = SafeInput.getRegExString(console, "Enter command [A D I M O S C V Q]: ",
                    "[AaDdIiMmOoSsCcVvQq]").toUpperCase();

            try {
                switch (choice) {
                    case "A": addItem(); break;
                    case "D": deleteItem(); break;
                    case "I": insertItem(); break;
                    case "M": moveItem(); break;
                    case "O": openList(); break;
                    case "S": saveCurrentList(); break;
                    case "C": clearList(); break;
                    case "V": viewList(); break;
                    case "Q": running = !quitProgram(); break;
                    default: System.out.println("Unknown command.");
                }
            } catch (IOException ex) {
                System.out.println("File operation failed: " + ex.getMessage());
            }
        }

        System.out.println("Exiting. Goodbye.");
    }

    private static void displayListAndMenu() {
        System.out.println("\nCurrent List (file: " + (currentFile == null ? "none" : currentFile.getFileName()) + "):");
        if (list.isEmpty()) {
            System.out.println("[empty]");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%d: %s%n", i+1, list.get(i));
            }
        }
        System.out.println("\nMenu:");
        System.out.println("A - Add item");
        System.out.println("D - Delete item");
        System.out.println("I - Insert item");
        System.out.println("M - Move item");
        System.out.println("O - Open list from disk");
        System.out.println("S - Save current list");
        System.out.println("C - Clear list");
        System.out.println("V - View list");
        System.out.println("Q - Quit");
    }

    // Edit operations
    private static void addItem() {
        String item = SafeInput.getNonZeroLenString(console, "Enter item to add");
        list.add(item);
        needsToBeSaved = true;
    }

    private static void insertItem() {
        int pos = SafeInput.getRangedInt(console, "Insert position (1-" + (list.size()+1) + ")", 1, list.size()+1);
        String item = SafeInput.getNonZeroLenString(console, "Enter item to insert");
        list.add(pos - 1, item);
        needsToBeSaved = true;
    }

    private static void deleteItem() {
        if (list.isEmpty()) { System.out.println("List empty."); return; }
        int pos = SafeInput.getRangedInt(console, "Delete item # (1-" + list.size() + ")", 1, list.size());
        String removed = list.remove(pos - 1);
        System.out.println("Removed: " + removed);
        needsToBeSaved = true;
    }

    private static void moveItem() {
        if (list.size() < 1) { System.out.println("No items to move."); return; }
        int from = SafeInput.getRangedInt(console, "Move which item (1-" + list.size() + ")?", 1, list.size()) - 1;
        int to = SafeInput.getRangedInt(console, "Move to position (1-" + list.size() + ")?", 1, list.size()) - 1;
        String item = list.remove(from);
        if (from < to) to--;
        list.add(to, item);
        needsToBeSaved = true;
        System.out.println("Moved item.");
    }

    private static void clearList() {
        if (SafeInput.getYNConfirm(console, "Clear entire list?")) {
            list.clear();
            currentFile = null;
            needsToBeSaved = true;
            System.out.println("List cleared.");
        } else {
            System.out.println("Clear cancelled.");
        }
    }

    private static void viewList() {
        System.out.println();
        if (list.isEmpty()) System.out.println("[empty]");
        else for (int i=0;i<list.size();i++) System.out.printf("%d: %s%n", i+1, list.get(i));
    }

    // File IO helpers (throw IOException)
    public static void saveFile(Path filePath, List<String> content) throws IOException {
        Files.write(filePath, content);
    }

    public static List<String> loadFile(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }

    private static Path promptForSavePath() {
        String base = SafeInput.getNonZeroLenString(console, "Enter base filename (no extension)");
        if (!base.endsWith(".txt")) base += ".txt";
        return Path.of("src", base);
    }

    // Actions that use file helpers
    private static void openList() throws IOException {
        if (needsToBeSaved) {
            boolean saveFirst = SafeInput.getYNConfirm(console, "Unsaved changes exist. Save before opening another file?");
            if (saveFirst) saveCurrentList();
        }
        String base = SafeInput.getNonZeroLenString(console, "Enter filename to open (no extension)");
        if (!base.endsWith(".txt")) base += ".txt";
        Path p = Path.of("src", base);
        List<String> loaded = loadFile(p); // may throw
        list.clear();
        list.addAll(loaded);
        currentFile = p;
        needsToBeSaved = false;
        System.out.println("Loaded " + p.toAbsolutePath());
    }

    private static void saveCurrentList() throws IOException {
        if (currentFile == null) {
            currentFile = promptForSavePath();
        }
        saveFile(currentFile, list);
        needsToBeSaved = false;
        System.out.println("Saved to " + currentFile.toAbsolutePath());
    }

    private static boolean quitProgram() throws IOException {
        if (needsToBeSaved) {
            boolean save = SafeInput.getYNConfirm(console, "Unsaved changes. Save before quitting?");
            if (save) {
                saveCurrentList(); // may throw
                return true; // ok to quit
            } else {
                boolean discard = SafeInput.getYNConfirm(console, "Quit and discard unsaved changes?");
                return discard;
            }
        } else {
            return SafeInput.getYNConfirm(console, "Are you sure you want to quit?");
        }
    }
}

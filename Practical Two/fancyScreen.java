import java.io.OutputStream;
import java.io.IOException;

public class fancyScreen {
    private OutputStream out;
    
    // dim
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // birght
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String NEON_RED = "\u001B[91m";
    public static final String NEON_GREEN = "\u001B[92m";
    public static final String NEON_YELLOW = "\u001B[93m";
    public static final String NEON_BLUE = "\u001B[94m";
    public static final String NEON_MAGENTA = "\u001B[95m";
    public static final String NEON_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    
    // Background 
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_DARK_GRAY = "\u001B[100m";
    public static final String BG_NEON_MAGENTA = "\u001B[105m";
    public static final String BG_NEON_CYAN = "\u001B[106m";
    
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    
    public static final String TITLE = NEON_CYAN + BOLD;
    public static final String COMMAND = NEON_MAGENTA + BOLD;
    public static final String INPUT = NEON_GREEN;
    public static final String SUCCESS = NEON_GREEN + BOLD;
    public static final String ERROR = NEON_RED + BOLD;
    public static final String ACCENT = NEON_YELLOW;
    public static final String SECONDARY = NEON_BLUE;
    public static final String DIVIDER = NEON_CYAN + DIM;
    
    public fancyScreen(OutputStream out) {
        this.out = out;
    }
    

    public void clearScreen() throws IOException {
        write("\u001B[2J");
        moveCursor(1, 1);
        write(BG_BLACK);
    }
    
    public void moveCursor(int row, int col) throws IOException {
        write("\u001B[" + row + ";" + col + "H");
    }
    
    public void writeLine(String text) throws IOException {
        write(text + "\n");
    }
    
    public void writeColored(String text, String color) throws IOException {
        write(color + text + RESET);
    }
    
    public void writeColoredLine(String text, String color) throws IOException {
        write(color + text + RESET + "\n");
    }
    

    public void write(String text) throws IOException {
        out.write(text.getBytes());
        out.flush();
    }
    
    public void writeByte(int b) throws IOException {
        out.write(b);
        out.flush();
    }
    
    public void drawBox(int startRow, int startCol, int width, int height, String color) throws IOException {
        String topLeft = "┌";
        String topRight = "┐";
        String bottomLeft = "└";
        String bottomRight = "┘";
        String horizontal = "─";
        String vertical = "│";
        
        // Top border
        moveCursor(startRow, startCol);
        write(color);
        write(topLeft);

        for (int i = 0; i < width - 2; i++) 
            write(horizontal);

        write(topRight);
        write(RESET);
        
        // Side border
        for (int i = 1; i < height - 1; i++) 
        {
            moveCursor(startRow + i, startCol);
            write(color + vertical + RESET);
            moveCursor(startRow + i, startCol + width - 1);
            write(color + vertical + RESET);
        }
        
        // Bottom border
        moveCursor(startRow + height - 1, startCol);
        write(color);
        write(bottomLeft);

        for (int i = 0; i < width - 2; i++) 
            write(horizontal);

        write(bottomRight);
        write(RESET);
    }

    public void drawDivider(int row, int startCol, int length) throws IOException {
        moveCursor(row, startCol);
        writeColoredLine("═".repeat(length), DIVIDER);
    }

    public void drawNeonHeader(int row, int col, String text) throws IOException {
        moveCursor(row, col);
        writeColored("▮ " + text + " ▮", TITLE);
    }

    public void drawSectionTitle(int row, int col, String title) throws IOException {
        moveCursor(row, col);
        String divider = "━━━━━━━━━━━━━━━━━";
        writeColoredLine(divider, DIVIDER);
        moveCursor(row + 1, col);
        writeColoredLine("  " + title, TITLE);
        moveCursor(row + 2, col);
        writeColoredLine(divider, DIVIDER);
    }
    
    public void drawCommandPrompt(int row, int col) throws IOException {
        moveCursor(row, col);
        writeColored("> ", COMMAND);
    }
}

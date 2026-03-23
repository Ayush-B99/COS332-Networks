import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket socket;
    private OutputStream rawOut;
    private PushbackInputStream rawIn;
    private PrintWriter out;
    private fancyScreen screen;
    private Prac2DB db;
    
    public ClientHandler(Socket socket, Prac2DB db) {
        this.socket = socket;
        this.db = db;
    }
    
    public void run() {
        try {
            rawIn = new PushbackInputStream(socket.getInputStream());
            rawOut = socket.getOutputStream();
            out = new PrintWriter(rawOut, true);
            screen = new fancyScreen(rawOut);
            
            // Welcome message
            showWelcome();
            
            String input;
            while ((input = readLineWithEcho()) != null) 
            {
                if (input.trim().isEmpty()) 
                    continue;
                
                String[] parts = input.toLowerCase().trim().split("\\s+", 2);
                String command = parts[0];
                String args = parts.length > 1 ? parts[1] : "";
                
                switch (command) 
                {
                    case "add":
                        handleAdd();
                        break;
                    case "search":
                        handleSearch();
                        break;
                    case "view":
                        handleView(args);
                        break;
                    case "modify":
                        handleModify(args);
                        break;
                    case "upcoming":
                        handleUpcoming();
                        break;
                    case "delete":
                        handleDelete();
                        break;
                    case "list":
                        handleList(args);
                        break;
                    case "quit":
                        out.println("Goodbye!");
                        out.flush();
                        return;
                    case "help":
                        out.println();
                        showMenu();
                        break;
                    default:
                        out.println(fancyScreen.NEON_RED + "✗ Unknown command. Try: add, search, view, modify, upcoming, delete, list, quit, help" + fancyScreen.RESET);
                }
                out.println();
            }
            
            socket.close();
            
        } 
        catch (IOException e) 
        {
            // Connection closed
        }
    }
    
    private String readLineWithEcho() throws IOException {
        StringBuilder line = new StringBuilder();
        int ch;
        
        while ((ch = rawIn.read()) != -1) 
        {
            if (ch == '\r') 
            {
                rawOut.write('\r');
                rawOut.write('\n');
                rawOut.flush();
                
                int nextCh = rawIn.read();
                if (nextCh != '\n' && nextCh != -1) 
                {
                    rawIn.unread(nextCh);
                }
                
                return line.toString();
            } 

            else if (ch == '\n') 
            {
                rawOut.write('\r');
                rawOut.write('\n');
                rawOut.flush();
                return line.toString();
            }

            else if (ch == 127 || ch == '\b') 
            {
                if (line.length() > 0) 
                {
                    line.deleteCharAt(line.length() - 1);
                    rawOut.write('\b');
                    rawOut.write(' ');
                    rawOut.write('\b');
                    rawOut.flush();
                }
            }

            else if (ch >= 32 && ch < 127) 
            {
                line.append((char) ch);
                rawOut.write(ch);
                rawOut.flush();
            }
        }
        
        return null;
    }
    
    private void showWelcome() throws IOException {
        screen.clearScreen();
        
        out.println();
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "╔════════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "║                                                            ║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "║      ⚡ Prac2 appointment manager v2.0 ⚡                ║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "║                                                            ║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "╚════════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        showMenu();
    }
    
    private void showMenu() throws IOException {
        out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ COMMANDS ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "add" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "      - Add new appointment" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "search" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "    - Search by person" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "view" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "      - View by date" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "modify" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "    - Modify appointment" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "upcoming" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "  - Next 7 days" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "list" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "      - All appointments" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "delete" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "    - Delete appointment" + fancyScreen.RESET);
        out.println("  " + fancyScreen.NEON_MAGENTA + fancyScreen.BOLD + "quit" + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + "      - Exit" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓" + fancyScreen.RESET);
        out.println();
        out.print(fancyScreen.NEON_YELLOW + fancyScreen.BOLD + "→ " + fancyScreen.RESET);
    }
    
    private void handleAdd() throws IOException {
        out.println();
        out.println(fancyScreen.NEON_MAGENTA + "╔═══════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_MAGENTA + "║ " + fancyScreen.RESET + fancyScreen.NEON_CYAN + fancyScreen.BOLD + "ADD APPOINTMENT" + fancyScreen.RESET + fancyScreen.NEON_MAGENTA + " " + "║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_MAGENTA + "╚═══════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        
        out.print(fancyScreen.NEON_CYAN + "[1/4] Date (YYYY-MM-DD): " + fancyScreen.RESET);
        out.flush();
        String date = readLineWithEcho();
        
        if (date == null || date.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        out.print(fancyScreen.NEON_CYAN + "[2/4] Time (HH:MM): " + fancyScreen.RESET);
        out.flush();
        String time = readLineWithEcho();
        
        if (time == null || time.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        out.print(fancyScreen.NEON_CYAN + "[3/4] Person/Contact: " + fancyScreen.RESET);
        out.flush();
        String person = readLineWithEcho();
        
        if (person == null || person.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        out.print(fancyScreen.NEON_CYAN + "[4/4] Details/Description: " + fancyScreen.RESET);
        out.flush();
        String details = readLineWithEcho();
        
        if (details == null || details.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        db.addAppointment(date.trim(), time.trim(), person.trim(), details.trim());
        out.println();
        out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "✓ Appointment added! [" + date + " @ " + time + "]" + fancyScreen.RESET);
    }
    
    private void handleSearch() throws IOException {
        out.println();
        out.println(fancyScreen.NEON_YELLOW + "╔═══════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_YELLOW + "║ " + fancyScreen.RESET + fancyScreen.NEON_CYAN + fancyScreen.BOLD + "SEARCH APPOINTMENTS" + fancyScreen.RESET + fancyScreen.NEON_YELLOW + " " + "║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_YELLOW + "╚═══════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        
        out.print(fancyScreen.NEON_CYAN + "Search for (person name): " + fancyScreen.RESET);
        out.flush();
        String searchTerm = readLineWithEcho();
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        out.println();
        ArrayList<Appointment> results = db.searchByPerson(searchTerm.trim());
        
        if (results.isEmpty()) {
            out.println(fancyScreen.NEON_RED + "✗ No appointments found for: " + searchTerm + fancyScreen.RESET);
        } else {
            out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "✓ Found " + results.size() + " appointment(s):" + fancyScreen.RESET);
            for (Appointment app : results) {
                out.println(fancyScreen.NEON_BLUE + "  ▸ " + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + app.toString() + fancyScreen.RESET);
            }
        }
    }
    
    private void handleView(String date) throws IOException {
        out.println();
        out.println(fancyScreen.NEON_BLUE + "╔═══════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_BLUE + "║ " + fancyScreen.RESET + fancyScreen.NEON_CYAN + fancyScreen.BOLD + "VIEW BY DATE" + fancyScreen.RESET + fancyScreen.NEON_BLUE + " " + "║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_BLUE + "╚═══════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        
        if (date == null || date.trim().isEmpty()) {
            out.print(fancyScreen.NEON_CYAN + "Enter date (YYYY-MM-DD): " + fancyScreen.RESET);
            out.flush();
            date = readLineWithEcho();
        }
        
        if (date == null || date.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        out.println();
        ArrayList<Appointment> results = db.getAppointmentsByDate(date.trim());
        
        if (results.isEmpty()) {
            out.println(fancyScreen.NEON_RED + "✗ No appointments on: " + date + fancyScreen.RESET);
        } else {
            out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "✓ Appointments on " + date + ":" + fancyScreen.RESET);
            for (Appointment app : results) {
                out.println(fancyScreen.NEON_BLUE + "  ▸ " + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + app.toString() + fancyScreen.RESET);
            }
        }
    }
    
    private void handleModify(String idStr) throws IOException {
        out.println();
        out.println(fancyScreen.NEON_MAGENTA + "╔═══════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_MAGENTA + "║ " + fancyScreen.RESET + fancyScreen.NEON_CYAN + fancyScreen.BOLD + "MODIFY APPOINTMENT" + fancyScreen.RESET + fancyScreen.NEON_MAGENTA + " " + "║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_MAGENTA + "╚═══════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        
        if (idStr == null || idStr.trim().isEmpty()) {
            out.print(fancyScreen.NEON_CYAN + "Enter appointment ID to modify: " + fancyScreen.RESET);
            out.flush();
            idStr = readLineWithEcho();
        }
        
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Cancelled" + fancyScreen.RESET);
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr.trim());
            Appointment existing = db.getAppointmentById(id);
            
            if (existing == null) {
                out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Appointment not found (ID: " + id + ")" + fancyScreen.RESET);
                return;
            }
            
            out.println(fancyScreen.NEON_BLUE + "Current: " + existing.toString() + fancyScreen.RESET);
            out.println();
            
            out.print(fancyScreen.NEON_CYAN + "New Date (YYYY-MM-DD) [" + existing.getDate() + "]: " + fancyScreen.RESET);
            out.flush();
            String newDate = readLineWithEcho();
            if (newDate == null || newDate.trim().isEmpty()) newDate = existing.getDate();
            
            out.print(fancyScreen.NEON_CYAN + "New Time (HH:MM) [" + existing.getTime() + "]: " + fancyScreen.RESET);
            out.flush();
            String newTime = readLineWithEcho();
            if (newTime == null || newTime.trim().isEmpty()) newTime = existing.getTime();
            
            out.print(fancyScreen.NEON_CYAN + "New Person [" + existing.getPerson() + "]: " + fancyScreen.RESET);
            out.flush();
            String newPerson = readLineWithEcho();
            if (newPerson == null || newPerson.trim().isEmpty()) newPerson = existing.getPerson();
            
            out.print(fancyScreen.NEON_CYAN + "New Details [" + existing.getDetails() + "]: " + fancyScreen.RESET);
            out.flush();
            String newDetails = readLineWithEcho();
            if (newDetails == null || newDetails.trim().isEmpty()) newDetails = existing.getDetails();
            
            if (db.modifyAppointment(id, newDate.trim(), newTime.trim(), newPerson.trim(), newDetails.trim())) {
                out.println();
                out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "✓ Appointment modified!" + fancyScreen.RESET);
            } else {
                out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Failed to modify appointment" + fancyScreen.RESET);
            }
        } catch (NumberFormatException e) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Invalid ID" + fancyScreen.RESET);
        }
    }
    
    private void handleUpcoming() throws IOException {
        out.println();
        out.println(fancyScreen.NEON_BLUE + "╔═══════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_BLUE + "║ " + fancyScreen.RESET + fancyScreen.NEON_CYAN + fancyScreen.BOLD + "UPCOMING (NEXT 7 DAYS)" + fancyScreen.RESET + fancyScreen.NEON_BLUE + " " + "║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_BLUE + "╚═══════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        
        ArrayList<Appointment> upcoming = db.getUpcomingAppointments();
        
        if (upcoming.isEmpty()) {
            out.println(fancyScreen.NEON_YELLOW + "  (No appointments in the next 7 days)" + fancyScreen.RESET);
        } else {
            out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "✓ " + upcoming.size() + " appointment(s) coming up:" + fancyScreen.RESET);
            for (Appointment app : upcoming) {
                out.println(fancyScreen.NEON_BLUE + "  ▸ " + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + app.toString() + fancyScreen.RESET);
            }
        }
    }
    
    private void handleDelete() throws IOException {
        out.println();
        out.println(fancyScreen.NEON_RED + "╔═══════════════════════════════════════════════════════════╗" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_RED + "║ " + fancyScreen.RESET + fancyScreen.NEON_CYAN + fancyScreen.BOLD + "DELETE APPOINTMENT" + fancyScreen.RESET + fancyScreen.NEON_RED + " " + "║" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_RED + "╚═══════════════════════════════════════════════════════════╝" + fancyScreen.RESET);
        out.println();
        
        ArrayList<Appointment> apps = db.getAllAppointments();
        
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_CYAN + "  ALL APPOINTMENTS" + fancyScreen.RESET);
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
        
        if (apps.isEmpty()) {
            out.println(fancyScreen.NEON_YELLOW + "  (No appointments scheduled)" + fancyScreen.RESET);
        } else {
            for (Appointment app : apps) {
                out.println(fancyScreen.NEON_BLUE + "  ▸ " + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + app.toString() + fancyScreen.RESET);
            }
        }
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
        out.println();
        
        out.print(fancyScreen.NEON_CYAN + "Enter appointment ID to delete: " + fancyScreen.RESET);
        out.flush();
        String idStr = readLineWithEcho();
        
        out.println();
        try {
            int id = Integer.parseInt(idStr.trim());
            if (db.deleteAppointment(id)) {
                out.println(fancyScreen.NEON_GREEN + fancyScreen.BOLD + "✓ Appointment deleted!" + fancyScreen.RESET);
            } else {
                out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Appointment not found (ID: " + id + ")" + fancyScreen.RESET);
            }
        } catch (NumberFormatException e) {
            out.println(fancyScreen.NEON_RED + fancyScreen.BOLD + "✗ Invalid ID" + fancyScreen.RESET);
        }
    }
    
    private void handleList(String sortBy) throws IOException {
        out.println();
        
        ArrayList<Appointment> apps;
        
        // Check for sort command
        if (sortBy != null && sortBy.toLowerCase().startsWith("sort")) {
            String[] parts = sortBy.split("\\s+");
            if (parts.length > 1) {
                apps = db.getSortedAppointments(parts[1]);
                out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
                out.println(fancyScreen.NEON_CYAN + "  ALL APPOINTMENTS (SORTED BY " + parts[1].toUpperCase() + ")" + fancyScreen.RESET);
                out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
            } else {
                apps = db.getAllAppointments();
                out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
                out.println(fancyScreen.NEON_CYAN + "  ALL APPOINTMENTS" + fancyScreen.RESET);
                out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
            }
        } else {
            apps = db.getAllAppointments();
            out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
            out.println(fancyScreen.NEON_CYAN + "  ALL APPOINTMENTS" + fancyScreen.RESET);
            out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
        }
        
        if (apps.isEmpty()) {
            out.println(fancyScreen.NEON_YELLOW + "  (No appointments scheduled)" + fancyScreen.RESET);
        } else {
            for (Appointment app : apps) {
                out.println(fancyScreen.NEON_BLUE + "  ▸ " + fancyScreen.RESET + fancyScreen.BRIGHT_WHITE + app.toString() + fancyScreen.RESET);
            }
        }
        out.println(fancyScreen.NEON_CYAN + fancyScreen.BOLD + "═══════════════════════════════════════════════════════════" + fancyScreen.RESET);
    }
}

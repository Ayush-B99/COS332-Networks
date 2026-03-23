public class NumberGame {
    public static void main(String[] args) {
        // Generate two random numbers between 1 and 100
        int num1 = (int)(Math.random() * 100) + 1;
        int num2 = (int)(Math.random() * 100) + 1;
        
        // Make sure they're different
        while (num1 == num2) {
            num2 = (int)(Math.random() * 100) + 1;
        }
        
        // Determine which link goes to right.htm and which to wrong.htm
        String link1, link2;
        if (num1 > num2) {
            link1 = "/right.htm";
            link2 = "/wrong.htm";
        } else {
            link1 = "/wrong.htm";
            link2 = "/right.htm";
        }
        
        // Print HTTP headers first
        System.out.println("Content-Type: text/html");
        System.out.println("Cache-Control: no-cache, no-store, must-revalidate");
        System.out.println("Pragma: no-cache");
        System.out.println("Expires: 0");
        System.out.println(); // Blank line between headers and content
        
        // Print HTML content
        System.out.println("<!DOCTYPE html>");
        System.out.println("<html>");
        System.out.println("<head>");
        System.out.println("    <title>Number Game</title>");
        System.out.println("    <meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\">");
        System.out.println("    <meta http-equiv=\"Pragma\" content=\"no-cache\">");
        System.out.println("    <meta http-equiv=\"Expires\" content=\"0\">");
        System.out.println("    <style>");
        System.out.println("        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; }");
        System.out.println("        .numbers { font-size: 48px; margin: 40px; }");
        System.out.println("        .numbers a { margin: 20px; padding: 15px; text-decoration: none; color: #333; }");
        System.out.println("        .numbers a:hover { background-color: #f0f0f0; }");
        System.out.println("    </style>");
        System.out.println("</head>");
        System.out.println("<body>");
        System.out.println("    <h1>Click the Larger Number</h1>");
        System.out.println("    <div class=\"numbers\">");
        System.out.println("        <a href=\"" + link1 + "\">" + num1 + "</a>");
        System.out.println("        <a href=\"" + link2 + "\">" + num2 + "</a>");
        System.out.println("    </div>");
        System.out.println("    <br>");
        System.out.println("    <p><a href=\"/cgi-bin/NumberGame.cgi\">New Game</a></p>");
        System.out.println("</body>");
        System.out.println("</html>");
    }
}

<%@page import="java.io.*" %>
<%@page import="java.net.*" %>
<%@page import="java.util.*" %>
<%@page import="java.text.*" %>
<%@page import="org.apache.commons.io.EndianUtils" %>
<%@page trimDirectiveWhitespaces="true" %>
<%
    String url = request.getParameter("url");
    String output = request.getParameter("output");
    
    if (url == null || url.isEmpty()) {
    	response.sendError(400, "Input URL must be provided");
        return;
    }
    if (output == null || output.isEmpty()) {
        output = "text";
    }
    
    OutputStream r = response.getOutputStream();
    
    System.out.println("txt2bin.jsp: " + url);
    
    if (output.equals("binary")) {
    	response.setContentType("application/octet-stream");
        DataOutputStream os = new DataOutputStream(r);
        Scanner is = new Scanner(new BufferedReader(new InputStreamReader(new URL(url).openStream())));
        while (is.hasNext()) { // each line
            // parse time
            String dateStr = is.next() + " " + is.next();
            Date date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS", Locale.ENGLISH).parse(dateStr);
            os.writeDouble(EndianUtils.swapDouble(date.getTime()));

            // parse numbers
            for (int i = 0; i < 4; i++) {
                os.writeDouble(EndianUtils.swapDouble(is.nextDouble()));
            }
        }
        os.flush();
        os.close();// *important* to ensure no more jsp output
        return;
    }
    
    if (output.equals("text")) {
        response.setContentType("text/plain-text");
        PrintWriter os = new PrintWriter(r);
        Scanner is = new Scanner(new BufferedReader(new InputStreamReader(new URL(url).openStream())));
        while (is.hasNext()) { // each line
            // parse time
            String dateStr = is.next() + " " + is.next();
            Date date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS", Locale.ENGLISH).parse(dateStr);
            os.printf("%d\n", date.getTime());

            // parse numbers
            for (int i = 0; i < 4; i++) {
                os.printf("%e\n", is.nextDouble());
            }
        }
        os.flush();
        os.close();// *important* to ensure no more jsp output
        return;
    }

%>
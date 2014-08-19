<%@page import="java.io.*" %>
<%@page import="java.net.*" %>
<%@page import="java.util.*" %>
<%@page import="java.text.*" %>
<%@page trimDirectiveWhitespaces="true" %>
<%--<%@page import="org.json.simple.*" %>--%>
<%
	//http://mag.gmu.edu:8081/TSDSFE/transform.jsp?output=binary%26url=http://cdaweb.gsfc.nasa.gov/WS/cdasr/1/dataviews/sp_phys/datasets/AC_H1_MFI/data/20050101T000000Z,20050102T000000Z/Magnitude,BGSEc?format=text

	//http://mag.gmu.edu:8081/TSDSFE/transform.jsp?output=binary&url=http://aurora.gmu.edu:8000/tsds_fe?type=data%26update=false%26url=http://cdaweb.gsfc.nasa.gov/WS/cdasr/1/dataviews/sp_phys/datasets/AC_H1_MFI/data/20050101T000000Z,20050102T000000Z/Magnitude,BGSEc?format=text"

	String url = request.getParameter("url");
    String process = request.getParameter("process");
    String output = request.getParameter("output");

    if (url == null || url.isEmpty()) {
        response.sendError(400, "Input URL must be provided");
        return;
    }
	if (process == null || process.isEmpty()) {
		process = "http://localhost:8081/TSDSFE/txt2bin.jsp";
	}
	if (output == null || output.isEmpty()) {
		output = "text";
	}
	
    // Call TSDSCache, which makes request, stores it, and postprocess it (removes header).  Response is actual data.
    //String cacheUrl = "http://aurora.gmu.edu:8000/tsds_fe?type=data&update=true&url="+url;
	String cacheUrl = url;
    
    OutputStream o = response.getOutputStream();
    
    // Call TSDSTransformer, which tranforms the raw (but postprocessed) response from TSDSCache and converts it to a different format.
    System.out.println("transformer.jsp: " + process + "&output=" + output + "&url=" + URLEncoder.encode(cacheUrl));
    URLConnection conn = new URL(process + "?output=" + output + "&url=" + URLEncoder.encode(cacheUrl)).openConnection();

    response.setContentType(conn.getContentType());
    InputStream is = conn.getInputStream();
    byte[] buf = new byte[32 * 1024]; // 32k buffer
    int nRead = 0;
    while( (nRead=is.read(buf)) != -1 ) {
        o.write(buf, 0, nRead);
    }
    o.flush();
    o.close();// *important* to ensure no more jsp output
    return;  
%>
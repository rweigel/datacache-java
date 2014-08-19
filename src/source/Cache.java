import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class Cache
 */
// http://www.javacodegeeks.com/2011/12/using-threadpoolexecutor-to-parallelize.html
// http://www.codeproject.com/Questions/203978/use-java-thread-to-download-large-number-of-web-pa
public class Cache extends HttpServlet {
	private static final long serialVersionUID = 1L;
	  
	  private class Downloader implements Runnable {
		    private final URL url;
		    
		    public Downloader(URL url) {
		      this.url = url;
		    }
		    
		    private String readAll(Reader reader) throws IOException {
		      StringBuilder builder = new StringBuilder();
		      int read = 0;
		      while((read = reader.read()) != -1) {
		        builder.append((char)read);
		      }
		      return builder.toString();
		    }
		    
		    @Override
		    public void run() {
		      try {
		        Reader reader = null;
		        try {
		        	String filename = URLEncoder.encode(url.toString(), "UTF-8");
		        	//String fullpath = basepath + filename;
		        	String fullpath = "/tmp/"+ filename + ".xml";
		        	File file = new File(fullpath);
		        	if (!file.isFile()) {
			        	System.out.println("File not found.  Generating: " + fullpath);
		        		org.apache.commons.io.FileUtils.copyURLToFile(url, new File(fullpath));
		        		//System.out.println("Done.");
		        	} else {
			        	System.out.println("File found.  Not re-generating: " + fullpath);
		        	}
		        	//System.out.println(response);
		        	String response = org.apache.commons.io.FileUtils.readFileToString(file);
		        	String pattern = "(.*)<Name>(.*)</Name>(.*)";
		        	
		        	URL urldata = new URL(response.replaceAll(pattern,"$2"));

		        	String fullpathdata = "/tmp/"+ filename + ".data";
		        	File filedata = new File(fullpathdata);

		        	if (!filedata.isFile()) {
			        	System.out.println("File not found.  Generating: " + fullpathdata);
		        		org.apache.commons.io.FileUtils.copyURLToFile(urldata, new File(fullpathdata));
		        		//System.out.println("Done.");
		        	} else {
			        	System.out.println("File found.  Not re-generating: " + fullpathdata);
		        	}


		          //reader = new BufferedReader(new InputStreamReader(url.openStream()));
		          //String result = readAll(reader);
		          //System.out.printf("Read %d characters from %s\n", result.length(), url);
		        }
		        finally {
		          if (reader != null) 
		              reader.close();
		        }        
		      }
		      catch(IOException e) {
		        System.err.println(e);
		      }
		    }
		  }	

	  public void runIt() throws MalformedURLException {
		  	int cores = Runtime.getRuntime().availableProcessors();
		    BlockingQueue<Runnable> runnables = new ArrayBlockingQueue<Runnable>(1024);
		    ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, runnables);
        	
		    String fullpath = "/tmp/aggregation.ncml";
        	File file = new File(fullpath);
        	String response;
			try {
				response = org.apache.commons.io.FileUtils.readFileToString(file);
	        	String pattern = "(.*)<Name>(.*)</Name>(.*)";
	        	System.out.println(response.replaceAll(pattern,"$2"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	BufferedReader buf;
			try {
				buf = new BufferedReader(new FileReader("/tmp/jobid_remote.txt"));
	        	String line = null;
				try {
					while((line = buf.readLine())!=null) {
						System.out.println(line);        		
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

        	// use rows.toArray(...) to convert to array if necessary
        	
		    String url = "http://cdaweb.gsfc.nasa.gov/WS/cdasr/1/dataviews/sp_phys/datasets/AC_H1_MFI/data/20050101T000000Z,20050102T000000Z/Magnitude,BGSEc?format=text";
		    executor.submit(new Downloader(new URL(url)));
		    executor.submit(new Downloader(new URL(url)));
		    executor.submit(new Downloader(new URL(url)));
		    executor.submit(new Downloader(new URL(url)));

		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    //executor.submit(new Downloader(new URL("http://www.google.com")));
		    
		    executor.shutdown();
		    try {
				executor.awaitTermination(1000L, TimeUnit.SECONDS);
				System.out.printf("Downloads finished.\n");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.printf("Downloads did not finish after 1000 seconds.\n");
				//e.printStackTrace();
			}
		  }

	  /**
     * @see HttpServlet#HttpServlet()
     */
    public Cache() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		pw.println("");
		pw.println("");
		pw.println("");
		pw.println("<h1>Hello World</h1>");
		pw.println("");

    	//String basepath = getServletConfig().getServletContext().getRealPath("/data/a.txt"); 
    	///System.out.printf(basepath);

	    System.out.printf("\nStarting downloads.\n");
	    Cache program = new Cache();
	    program.runIt();
	    
	    //System.in.read();
	    
	    
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

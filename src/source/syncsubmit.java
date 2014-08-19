package source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

public class syncsubmit {
	 private class dataDownloader implements Runnable {
		  String body=new String();
		  String md5=new String();
		  String header=new String();
		  Format formatter=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		  Date date = new Date();
		  String formatedDate = formatter.format(date);
		  long elapsedTime=0;
		  URL url;
		  URL urldata;
		  
		  String filePath=new String();
		   public dataDownloader(URL url, String path) {
			      this.url = url;
			      this.filePath=path;
			    }
		  long startTime = System.currentTimeMillis();
		  @Override
		    public void run() {
			  try 
			  {  		  
			  URLConnection conn = url.openConnection();
	//	      String fileType = conn.getContentType().split(";")[0].split("/")[1];
		//      String fileType = conn.getContentType().split("/")[1];
			  String fileType = conn.getContentType();
			  System.out.println(fileType);
		  //    System.out.println(fileType);
		      if(fileType.contains("xml")){
		    	  InputStream in = conn.getInputStream();
				  String encoding = conn.getContentEncoding();
				  encoding = encoding == null ? "UTF-8" : encoding;
				  body = org.apache.commons.io.IOUtils.toString(in, encoding);			  			  
				  String pattern = "(.*)<Name>(.*)</Name>(.*)";
			      urldata = new URL(body.replaceAll(pattern,"$2"));	
		      }else{
		    	  urldata=url;
		      }
			 			      		      
			//  long startTime = System.currentTimeMillis();
			  org.apache.commons.io.FileUtils.copyURLToFile(urldata, new File(filePath+DigestUtils.md5Hex(URLEncoder.encode(url.toString(), "UTF-8"))+".data"));
	     	//  org.apache.commons.io.FileUtils.copyURLToFile(urldata, new File(filePath+DigestUtils.md5Hex(url.toString())+".data"));
			//  elapsedTime = System.currentTimeMillis() - startTime;						  
			  URLConnection dataconn = urldata.openConnection();
			  InputStream datain = dataconn.getInputStream();
			  String dataencoding = dataconn.getContentEncoding();
			  dataencoding = dataencoding == null ? "UTF-8" : dataencoding;
			  String databody = org.apache.commons.io.IOUtils.toString(datain, dataencoding);
			  md5=DigestUtils.md5Hex(databody); 
			  System.out.println("MD5 of Real Data is: "+md5);	  
			  System.out.println("Report: " +formatedDate+"/"+elapsedTime);
			 			  
			  for (int i=0; ; i++) 
			  {
			  String name = conn.getHeaderFieldKey(i);
			  String value = conn.getHeaderField(i);
			  if (name == null && value == null){
			  break; 
			  }
			  if (name == null){
			  System.out.println("Server HTTP version, Response code:");
			  System.out.println(value);
			  header+="Server HTTP version, Response code:"+"\n";
			  header+=value+"\n";
			  }
			  else{
			  System.out.println(name + "=" + value);
			  header+=name + "=" + value+"\n";
			  }
			  }
			  elapsedTime = System.currentTimeMillis() - startTime;						  
			  } 
			  catch (Exception e) {}
		    try {
		           
		       // 	String urlEncoded = DigestUtils.md5Hex(url.toString());
		    	String urlEncoded = URLEncoder.encode(url.toString(), "UTF-8");
		            BufferedWriter headerWriter = new BufferedWriter(new FileWriter(filePath+ DigestUtils.md5Hex(urlEncoded.replace("%0D", ""))+".header"));                     
		            headerWriter.write(header);
		            headerWriter.flush();
		            headerWriter.close();
		            BufferedWriter md5Writer = new BufferedWriter(new FileWriter(filePath+DigestUtils.md5Hex(urlEncoded.replace("%0D", ""))+".md5"));                     
		            md5Writer.write(md5);
		            md5Writer.flush();
		            md5Writer.close();
		            BufferedWriter logWriter = new BufferedWriter(new FileWriter(filePath+DigestUtils.md5Hex(urlEncoded.replace("%0D", ""))+".log",true));                     
		            logWriter.write(formatedDate+" "+elapsedTime+"\r\n");
		            logWriter.flush();
		            logWriter.close();
		            
		        } catch (FileNotFoundException ex) {
		            ex.printStackTrace();
		        } catch (IOException ex) {
		            ex.printStackTrace();
		        }
              long wholeTime=System.currentTimeMillis()-startTime;
			  System.out.println("The whole running time is: "+wholeTime+" for url: "+url);
			  
		  }
	 }
	 public void downloadFile(String[] urls,boolean forceUpdate, int maxThread, String cacheDirectory) throws Exception {		 
		    BlockingQueue<Runnable> runnables = new ArrayBlockingQueue<Runnable>(1024);
		    ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThread/2, maxThread, 60, TimeUnit.SECONDS, runnables);
		    Format formatter=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
			Date date = new Date();
			String formatedDate = formatter.format(date);
			for(String line:urls){
			//	line = line.replace("%0A", "");
			//	line = line.replace("%0D", "");
			//	System.out.println(line);
			//	System.out.println(DigestUtils.md5Hex(line));
		//		System.out.println(URLEncoder.encode(line, "UTF-8"));
		  //      String urlEncoded = DigestUtils.md5Hex(line);
				String urlEncoded = URLEncoder.encode(line, "UTF-8");
		 //       System.out.println(urlEncoded);
				String org=line.split("/")[2];
		        File folder = new File(cacheDirectory+org);  
		    	if(!folder.exists());
		    	folder.mkdir();
		    	String fullPath=cacheDirectory+org+"/";
		    	File file = new File(fullPath+DigestUtils.md5Hex(urlEncoded.replace("%0D", ""))+".data");
		    	if(!file.exists()){
		    		//System.out.println(urlEncoded);
		    		executor.submit(new dataDownloader(new URL(line),fullPath));
		    	}else{
		    		if(forceUpdate){
			    		executor.submit(new dataDownloader(new URL(line),fullPath));
		    		}else{
		    			 try {				  		          
		    			            BufferedWriter logWriter = new BufferedWriter(new FileWriter(fullPath+DigestUtils.md5Hex(urlEncoded.replace("%0D", ""))+".log",true));                     
		    			            logWriter.write(formatedDate+" "+0+"\r\n");
		    			            logWriter.flush();
		    			            logWriter.close();
		    			            
		    			        } catch (FileNotFoundException ex) {
		    			            ex.printStackTrace();
		    			        } catch (IOException ex) {
		    			            ex.printStackTrace();
		    			        }
		    		}
		    	}
						
			}

		    
		    executor.shutdown();

		 
	 }
	 public void getFiles(String[] urls, String maxThread, String forceUpdate){
		//  String forceUpdate=request.getParameter("forceUpdate");
		//  String maxThread=request.getParameter("maxThread");
	
		//  String basePath="/Users/shengli/me/";
		  String cacheDirectory="/tmp/";
		  try { 		  
		  int maxThreadNumber=Integer.parseInt(maxThread);
		  boolean update=false;
		  if(forceUpdate.contains("true")){
			  update=true; 
		  }
		 downloadFile(urls,update,maxThreadNumber,cacheDirectory);
			  
		  } catch(Exception e){
			  System.out.println(e);
		  }
	}
}

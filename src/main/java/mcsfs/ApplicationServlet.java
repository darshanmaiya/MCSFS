/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcsfs;

import java.io.*;
import java.util.logging.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

//import com.tiemens.secretshare.main.cli.*;

import mcsfs.utils.ConversionUtils;
import mcsfs.utils.CryptUtils;

/**
 * Servlet implementation class ServiceServlet
 */
@WebServlet(name = "ApplicationServlet", urlPatterns = {"/mcsfs"})
@MultipartConfig
public class ApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = 
            Logger.getLogger(ApplicationServlet.class.getCanonicalName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApplicationServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String passphrase = request.getParameter("down-passphrase");
		System.out.println(passphrase);
		response.setContentType(getServletContext().getMimeType(passphrase));
		response.setHeader("Content-Disposition", "attachment;filename=\"" + passphrase + "\"");
		
		OutputStream out = response.getOutputStream();
		FileInputStream in = new FileInputStream(passphrase);
		byte[] buffer = new byte[4096];
		int length;
		while ((length = in.read(buffer)) > 0){
		    out.write(buffer, 0, length);
		}
		in.close();
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String passphrase = request.getParameter("up-passphrase");
		
		// Create path components to save the file
	    Part filePart = request.getPart("file-input");
	    String fileName = String.valueOf(System.currentTimeMillis())
	    		+ Constants.DELIMITER_IN_FILENAME + getFileName(filePart)
	    		+ Constants.DELIMITER_IN_FILENAME + passphrase;
	    
	    OutputStream out = null;
	    InputStream fileContent = null;

	    try {
	    	byte[] secretKey = new byte[Constants.ACCESS_KEY_LENGTH];
	    	byte[] accessKey = new byte[Constants.ACCESS_KEY_LENGTH];
	    	String accessKeyStr = null;
	    	
	    	CryptUtils.getKeys(fileName, secretKey, accessKey);

	    	int[] accessKeyInt = ConversionUtils.byteArrayToIntArray(accessKey);
	    	accessKeyStr = ConversionUtils.intArrayToMixedString(accessKeyInt);
	    	
	    	File inputFile = new File("mcsfs_files/" + accessKeyStr);
	    	byte[] readBuffer = new byte[Constants.BUFFER_SIZE];
	    	
	    	out = new FileOutputStream(inputFile);
	    	fileContent = filePart.getInputStream();
	    	
	    	int read;
	    	
	    	// Read the file from the client
	        while ((read = fileContent.read(readBuffer)) != -1) {
	        	out.write(readBuffer, 0, read);
	        }
	        
	        File encFile = new File("mcsfs_files/" + accessKeyStr + ".enc");
	        CryptUtils.encrypt(secretKey, inputFile, encFile);
	        
	        inputFile.delete();
	        
	        //File decFile = new File("mcsfs_files/" + getFileName(filePart));
	        //CryptUtils.decrypt(secretKey, encFile, decFile);

	        response.getWriter().print("{\"result\": \"success\", \"accessKey\": \""
	        		+ accessKeyStr + "\"}");
	        
	        //String[] args = new String[]{"split", "-k","3","-n", "6", "-sS", "Cat In The Hat"};
	        //Main.main(args);
	    } catch (Exception fne) {
	        fne.printStackTrace();
	        response.getWriter().print("{\"result\": \"failure\"");
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (fileContent != null) {
	            fileContent.close();
	        }
	    }
	}
	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String passphrase = request.getParameter("del-passphrase");
		String adminkey = request.getParameter("del-adminkey");
		System.out.println(passphrase);
		System.out.println(adminkey);
		File file = new File(passphrase);
    	
		if(file.delete()) {
			System.out.println(file.getName() + " is deleted!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}

	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
}

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

import mcsfs.queue.JobQueue;
import mcsfs.store.StorageManager;
import mcsfs.utils.ConversionUtils;
import mcsfs.utils.CryptUtils;
import mcsfs.utils.LogUtils;

/**
 * Servlet implementation class ServiceServlet
 */
@WebServlet(name = "ApplicationServlet", urlPatterns = {"/mcsfs"})
@MultipartConfig
public class ApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = 
            Logger.getLogger(ApplicationServlet.class.getCanonicalName());
	private StorageManager storageManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApplicationServlet() {
        super();
    }

	@Override
	public void init() throws ServletException {
		LogUtils.setLogLevel(3);
		storageManager = new StorageManager();
		JobQueue.startQueueProcessing();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		OutputStream out = null;
		
		try {
			String downAccessKey = request.getParameter("down-access-key");
			
			String fileName = storageManager.retrieveKey(downAccessKey);
			File encFile = storageManager.retrieveFile(downAccessKey);
			
	        String userFileName = fileName.substring(
	        	fileName.indexOf(Constants.DELIMITER_IN_FILENAME) + Constants.DELIMITER_IN_FILENAME.length(),
				fileName.lastIndexOf(Constants.DELIMITER_IN_FILENAME));
			
	        // Retrieve file and decrypt it
	        byte[] secretKey = new byte[Constants.ACCESS_KEY_LENGTH];
	    	byte[] accessKey = new byte[Constants.ACCESS_KEY_LENGTH];
	    	
	    	CryptUtils.getKeys(fileName, secretKey, accessKey);
	    	
	        File decFile = new File(Constants.MCSFS_WORKING_DIR + userFileName);
	        decFile.createNewFile();
	        CryptUtils.decrypt(secretKey, encFile, decFile);
	        
			response.setContentType(getServletContext().getMimeType(downAccessKey));
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ userFileName
					+ "\"");
			
			out = response.getOutputStream();
			
			byte[] buffer = new byte[4096];
			int length;
			FileInputStream dec = new FileInputStream(decFile);
			while ((length = dec.read(buffer)) > 0){
			    out.write(buffer, 0, length);
			}
			
			dec.close();
			decFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
			
			response.getWriter().print("No such file found or internal server error.");
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String passphrase = request.getParameter("up-passphrase");
		// Can be used for verifying file size.
		// int fileSize = request.getParameter("file-size");
		
		// Create path components to save the file
	    Part filePart = request.getPart("file-input");
	    String fileName = String.valueOf(System.currentTimeMillis())
	    		+ Constants.DELIMITER_IN_FILENAME + getFileName(filePart)
	    		+ Constants.DELIMITER_IN_FILENAME + passphrase;
	    
	    OutputStream out = null;
	    InputStream fileContent = null;

	    try {
	    	// Check if working directory exists, if not create it
	    	// Working directory will be used to temporarily store uploaded files until
	    	// they are encrypted and stored. Then the files will be deleted
	    	File workingDir = new File(Constants.MCSFS_WORKING_DIR);
	        if (!workingDir.exists())
	            workingDir.mkdir();
	        
	    	// Read the uploaded file from client
	    	File inputFile = new File(Constants.MCSFS_WORKING_DIR + fileName);
	    	byte[] readBuffer = new byte[Constants.BUFFER_SIZE];
	    	
	    	out = new FileOutputStream(inputFile);
	    	fileContent = filePart.getInputStream();
	    	
	    	int read;
	    	
	    	// Read the file from the client
	        while ((read = fileContent.read(readBuffer)) != -1) {
	        	out.write(readBuffer, 0, read);
	        }
	        
	        byte[] secretKey = new byte[Constants.ACCESS_KEY_LENGTH];
	    	byte[] accessKey = new byte[Constants.ACCESS_KEY_LENGTH];
	    	String accessKeyStr = null;
	    	 
	    	// Get the accessKey and secretKey in byte[] format from the generated file name
	    	CryptUtils.getKeys(fileName, secretKey, accessKey);

	    	int[] accessKeyInt = ConversionUtils.byteArrayToIntArray(accessKey);
	    	accessKeyStr = ConversionUtils.intArrayToMixedString(accessKeyInt);
	    	
	        File encFile = new File(Constants.MCSFS_WORKING_DIR + accessKeyStr);
	        CryptUtils.encrypt(secretKey, inputFile, encFile);
	        
	        // Delete uploaded file
	        inputFile.delete();
	        
	        storageManager.storeKey(accessKeyStr, fileName);
	        storageManager.storeFile(encFile);
	        
	        //File decFile = new File("mcsfs_files/" + getFileName(filePart));
	        //CryptUtils.decrypt(secretKey, encFile, decFile);
	        
	        // Delete encrypted file
	        encFile.delete();

	        response.getWriter().print("{\"result\": \"success\", \"accessKey\": \""
	        		+ accessKeyStr + "\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
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
		
		String delPassphrase = request.getParameter("del-passphrase");
		String delAccessKey = request.getParameter("del-access-key");
		OutputStream out = null;
		
		try {
			String fileName = storageManager.retrieveKey(delAccessKey);
			
	        String passphrase = fileName.substring(fileName.lastIndexOf(Constants.DELIMITER_IN_FILENAME) + 3);
			
	        if(!passphrase.equals(delPassphrase)) {
	        	throw new Exception("Incorrect passphrase provided.");
	        }
	        
	        storageManager.remove(delAccessKey);
	        response.getWriter().print("{\"result\": \"success\"}");
		} catch (Exception e) {
			e.printStackTrace();
			
			response.getWriter().print("{\"result\": \"failure\", \"description\": \"No such file found or incorrect access key.\"}");
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
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

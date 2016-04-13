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
	    final Part filePart = request.getPart("file-input");
	    final String fileName = String.valueOf(System.currentTimeMillis()) + "___" + getFileName(filePart);
	    
	    OutputStream out = null;
	    InputStream filecontent = null;

	    try {
	    	System.out.println("Plain Text Before Encryption: " + fileName);

			String encryptedText = CryptUtils.encrypt(fileName, fileName + "___" + passphrase);
			System.out.println("Encrypted Text After Encryption: " + encryptedText);

			String decryptedText = CryptUtils.decrypt(encryptedText, fileName + "___" + passphrase);
			System.out.println("Decrypted Text After Decryption: " + decryptedText);
			
	    	File f = new File(fileName);
	        out = new FileOutputStream(f);
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        
	        response.getWriter().print("{\"result\": \"success\", \"accessKey\": \"" + encryptedText + "\"}");
	    } catch (Exception fne) {
	        fne.printStackTrace();
	        response.getWriter().print("{\"result\": \"failure\"");
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
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

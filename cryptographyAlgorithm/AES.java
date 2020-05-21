package cryptographyAlgorithm;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

/**
 * AES class
 * @param cipher: the cipher for cryptography
 * @param filePathString: the directory of file 
 * @param fileName: name of file
 * @param outTextString: string that all shown in progress TextArea
 * @param jTextArea: progress TextArea
 */
public class AES  {
	
	public SecretKey sKey;
	public Cipher cipher;
	public String filePathString;
	public String fileName;
	private String outTextString;
	private JTextArea jTextArea;
	String md5HashCodeString;
	
	public AES(JTextArea jTextArea,String filePath,String fileName,String keyPath ) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException  {
	   if (keyPath.length()==0) {
		   KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		   sKey = keyGenerator.generateKey();
	   }
	   else {
		   byte[] keyb = Files.readAllBytes(Paths.get(keyPath));
		   sKey = new SecretKeySpec(keyb, "AES");
	   }
	   
	   this.filePathString = filePath;
	   this.fileName = fileName;
	   this.jTextArea = jTextArea;
	   outTextString = jTextArea.getText();
	   cipher = Cipher.getInstance("AES");
	   
	  
	}
	   

	
	private void proccessFile(Cipher ci,String inFile, String outFile) throws Exception {
		/**
		 * 
		 */
		File fileOrDir = new File(inFile);
		
		boolean mustDelete = false;
		if (fileOrDir.isDirectory()) {
			mustDelete = true;
			String outPathString = fileOrDir.getParent();
			ZipFiles zipFiles = new ZipFiles(inFile, outPathString+"\\"+fileOrDir.getName()+".zip");
			inFile = outPathString+"\\"+fileOrDir.getName()+".zip";
		}
		try(FileInputStream inputStream = new FileInputStream(inFile);
				FileOutputStream outputStream = new FileOutputStream(outFile);
				){
			byte[] inbuf = new byte[1024];
			int len;
			while((len = inputStream.read(inbuf)) != -1) {
				byte[] outbuf = ci.update(inbuf,0,len);
				if(outbuf != null)
					outputStream.write(outbuf);
					
			}
			try {
				byte[] outbuf = ci.doFinal();
				if(outbuf != null)
					outputStream.write(outbuf);
			} catch (Exception e) {
				// TODO: handle exception
				jTextArea.setText(jTextArea.getText()+"\n"+e.getMessage());
				
			}
			String isType= mustDelete?".zip":"";
			
			md5HashCodeString = MD5Checksum.getMD5Checksum(filePathString+isType);
			//delete file .zip if that's directory
			if(mustDelete) {
				File deleteFile = new File(fileOrDir.getParent()+"\\"+fileOrDir.getName()+".zip");
				deleteFile.delete();
			}
			inputStream.close();
			outputStream.close();
		}
		
		
	}
	
	/**
	 * To create key, encrypt file and save skey in the same directory with encrypted file
	 * @throws Exception 
	 */
	public void Encrypt() throws Exception {
		
		//add md5 code to skey
		showProgress(new ItemToShow(fileName, "e", false,""),false);
		String savePathString = SaveFile(getName(fileName, 0)+".bat");
		try (FileOutputStream out1 = new FileOutputStream(savePathString+"\\skey_"+getName(fileName,0))) {
		    byte[] keyb = sKey.getEncoded();
		    out1.write(keyb);
		    out1.close();
		}
		cipher.init(Cipher.ENCRYPT_MODE, sKey);
		proccessFile(cipher, filePathString , savePathString+"\\"+fileName+".en");
		
		try {
            FileWriter fw = new FileWriter(savePathString+"\\verify_"+getName(fileName, 0)+".txt");
            fw.write(md5HashCodeString);
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
		showProgress(new ItemToShow(fileName, "e", true,md5HashCodeString),true);
		
		
	}
	
	/**
	 * To decrypt and save file in specific directory
	 * @throws Exception 
	 */
	public void Decrypt() throws Exception {
		String t =getName(fileName, 1).equals("en")?"zip":getName(fileName, 1);
		showProgress(new ItemToShow(fileName, "d", false,""),false);
		String savePathString = SaveFile("decrypted_"+getName(fileName, 0)+"."+ t);
		cipher.init(Cipher.DECRYPT_MODE, sKey);
		proccessFile(cipher, filePathString, savePathString+"\\decrypted_"+getName(fileName, 0)+"."+t);
		//compare 2 md5 codes to verify integrity of file
		String verifyMD5CodeString = "";
		try {
		      File myObj = new File(savePathString+"\\verify_"+getName(fileName, 0)+".txt");
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        verifyMD5CodeString = myReader.nextLine();
		       
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    }
		String md5HashCodeString = MD5Checksum.getMD5Checksum(savePathString+"\\decrypted_"+getName(fileName, 0)+"."+t);
		boolean check = md5HashCodeString.equals(verifyMD5CodeString);
		showProgress(new ItemToShow(fileName, "d", false,check ? md5HashCodeString : "Danger! Your file has been changed!"),true);
		
	}
	/**
	 * The file name format is abc.x
	 * So, this function converts name to string array [abc,x] 
	 * @param fileName : String
	 * @param i : int
	 */
	public String getName(String fileName, int i) {
		
		String[] nString = fileName.split("\\.");
		return nString[i];
	}
	
	
	/**
	 * return the directory where the file after proccess is stored
	 * @param s : String
	 */
	private String SaveFile(String s) {
		
		JFileChooser chooser = new JFileChooser("D:");
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Save File "+ s +" at");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int tf = chooser.showOpenDialog(null);
		if(tf==JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			return file.getAbsolutePath();
		}
		else {
			return null;
		}
	}
	
	/**
	 * show information in procTextArea
	 * @param i : ItemToShow
	 * @param isDone : boolean
	 */
	private void showProgress(ItemToShow i,boolean isDone) {
		
		if(isDone==false) {
			outTextString += 		"\n===============================================================";
			outTextString += 		"\nFile in progress: "+ i.fileName;
			outTextString += 		"\nCreate key: Done\n";
			outTextString +=		i.mode.equals("e") ? "Encrypting file...":"Decrypting file...";
			
		}
		else {
			outTextString += 		"\nDone";
			outTextString +=		"\nMD5 check code: "+i.md5Code;
			outTextString += 		"\n===============================================================";
		}
		jTextArea.setText(outTextString);
		
	}
	 
	/**
	 * class to store information of progresses in procTextArea
	 */
	private class ItemToShow{
		public String fileName;
		public String mode;
		public boolean status; 
		public String md5Code = "";
		public ItemToShow(String fileName,String mode, boolean status, String md5Code ) {
			this.fileName = fileName;
			this.mode = mode;
			this.status = status;
			this.md5Code = md5Code;
		}
	}
	
}
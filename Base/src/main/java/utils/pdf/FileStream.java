package utils.pdf;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.junit.Assert;
import org.springframework.stereotype.Component;

import java.io.*;

import static constants.Constants.formsConstants.*;

@Component
@ScenarioScope
public class FileStream {

	public static void moveFilesToNewFolder (String policyNum) {
		try {
			moveFiles(policyNum);
		} catch (IOException var4) {
			var4.printStackTrace();
		}
	}

	public static void moveFiles (String policyNum) throws IOException {

		File directory = new File(FORMSPATH);
		if (!(directory.exists()))
			directory.mkdirs();
		new File(FORMSPATH + "\\" + policyNum).mkdirs();
		File source = new File(USERHOME + "\\Downloads"+"\\"+ YOURINSURANCEDOCUMENTS);
		if (!source.isFile())
			source = new File("\\\\Hiscox.nonprod\\Profiles\\Citrix\\XDRedirect\\" + USERNAME + "\\Downloads\\YOUR INSURANCE DOCUMENTS.pdf");
		File dest = new File(FORMSPATH + "\\" + policyNum + "\\" + YOURINSURANCEDOCUMENTS);
		Assert.assertTrue("--------- " + source.getAbsolutePath() + " doesn't exist", source.isFile());
		/**copy file conventional way using Stream*/
		long start = System.nanoTime();
		copyFileUsingStream(source, dest);
		source.delete();
		System.out.println("Time taken by Stream  = " + (System.nanoTime() - start));
	}


	public static void copyFileUsingStream (File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[ 1024 ];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			Assert.assertTrue("InputStream|OutputStream is null ", is != null && os != null);
			is.close();
			os.close();
		}
	}

}

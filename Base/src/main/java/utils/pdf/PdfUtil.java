package utils.pdf;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.state.TestContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static constants.Constants.formsConstants.DOWNLOAD_FORMS;

@Component
@ScenarioScope
public class PdfUtil {
	@Autowired @Lazy
	private TestContext testContext;
	private String file0 = null;


	public void extractFormsFromPdf (List<String> formNumbers, String policyNumber) {
		File file = null;
		PDDocument document = null;
		try {
			File policyDir = new File("target/" + testContext.getBatchName() + File.separator + policyNumber + "/");
			if (policyDir.exists()) {
				FileUtils.deleteDirectory(policyDir);
			}
			policyDir.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String curr : DOWNLOAD_FORMS) {
			file0 = "target/" + testContext.getBatchName() + File.separator + curr + ".PDF";
			try {
				/*  Get pdf deck and split into individual pages  */
				file = new File(file0);
				document = PDDocument.load(file);
				Splitter splitter = new Splitter();
				List<PDDocument> Pages = splitter.split(document);
				/* Find pages that contains the form number in it and add it to a collection  */
				Map<String, PDDocument> map = new HashMap<>();

				for (int i = 0; i < Pages.size(); i++) {
					String formNumber = null;
					PDDocument pd = null;
					PDFTextStripper stripper = new PDFTextStripper();
					String txt = null;

					pd = Pages.get(i);
					txt = stripper.getText(pd);
					for (String form : formNumbers) {
						boolean flag=txt.contains("INT D001") || txt.contains("BOP D0001A CW");
						if ((txt.contains(form) && !flag) || (!txt.contains(form) && flag)) {
							formNumber = form;
							break;
						}
					}
					if (formNumber == null) {
						continue;
					}
					map.putIfAbsent(formNumber, new PDDocument());
					pd = map.get(formNumber);
					pd.addPage(document.getPage(i));
					map.put(formNumber, pd);
				}
				// Save document objects-
				File policyDir = new File("target/" + testContext.getBatchName() + File.separator + policyNumber + "/");
				for (Map.Entry<String, PDDocument> entry : map.entrySet()) {
					entry.getValue().save(policyDir.getAbsolutePath() + File.separator + entry.getKey().replace("/", "").replace(" ", "") + ".pdf");
					entry.getValue().close();
				}


			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (document != null) {
						document.close();
					}
				} catch (IOException exp) {
					System.out.println("Document closure failed");
				}
				FileUtils.deleteQuietly(new File(file0));
			}
		}
	}
	public void deleteForms () {
		File file = null;
		PDDocument document = null;

		for (String curr : DOWNLOAD_FORMS) {
			file0 = "target/" + testContext.getBatchName() + File.separator + curr + ".PDF";
			try {
				/*  Get pdf deck and split into individual pages  */
				file = new File(file0);
				document = PDDocument.load(file);
				if (document != null) {
					document.close();
					FileUtils.deleteQuietly(new File(file0));
				}
			} catch (Exception ignored) {
			}
		}
	}
}
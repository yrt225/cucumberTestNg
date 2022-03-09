package utils.pdf;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.BaseTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static constants.Constants.formsConstants.YOURINSURANCEDOCUMENTS;
@Component
@ScenarioScope
public class DynamicValidation {
	static String templatePath=DynamicValidation.class.getClassLoader().getResource("templates").getPath(),pdfPath=null;
	private static Logger log= LoggerFactory.getLogger(DynamicValidation.class);


	public Map<String,String> getVariables(String batchName, BaseTemplate template){
		try {
			Map<String,String> variableMap=new LinkedHashMap<>();
			pdfPath="target/" + batchName + File.separator + YOURINSURANCEDOCUMENTS;
			String[] templateFile= FileUtils.readFileToString(new File(templatePath+File.separator+template.getFileName()),"UTF-8").split("\\n");
			String[] pdfText=getTextFromPdf(template.getFileName()).split("\\n");
			//add error condition to check if template matches
			//validation to check if its mapping to right line
			for(int i=0;i<templateFile.length;i++){
				if(templateFile[i].contains("${")){
					retrieveValue(variableMap,templateFile[i],pdfText[i]);
				}
			}
			return variableMap;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getTextFromPdf(String formName) {
		try {
			/*  Get pdf deck and split into individual pages  */
			File file = new File(pdfPath);
			PDDocument document = PDDocument.load(file);
			Splitter splitter = new Splitter();
			List<PDDocument> Pages = splitter.split(document);
			/* Find pages that contains the form number in it and add it to a collection  */
			for(int i=0;i<Pages.size();i++){
				String formNumber=null;
				PDDocument pd=null;
				PDFTextStripper stripper = new PDFTextStripper();
				String txt=null;

				pd = Pages.get(i);
				txt = stripper.getText(pd);
				if(txt.contains(formName.replace(".txt",""))){
					return txt;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Assert.fail("Form not found in the Insurance Document");
		return null;
	}

	public void retrieveValue(Map<String,String> map,String template,String pdfText){
		String[] templateTokens;
		StringBuilder startToken,endToken;
		String value="",variable=null;
		boolean flag=false,matched=false;

		startToken=new StringBuilder();
		endToken=new StringBuilder();

		templateTokens=template.trim().split(" ");
		// determine start index and end index- ToDO wrap text,multiple variables in single line
		for(int i=0;i<templateTokens.length;i++){
			if(templateTokens[i].contains("${")&&templateTokens[i].charAt(templateTokens[i].length()-1)=='.'){
				templateTokens[i]=templateTokens[i].substring(0,templateTokens[i].length()-1);
			}
			if(templateTokens[i].matches("^(\\$\\{[a-zA-Z0-9.]*\\})$")){
				variable=templateTokens[i].replaceAll("[${}]","");
				matched=true;
			}else if(matched){
				endToken.append(templateTokens[i]+" ");
			}else {
				startToken.append(templateTokens[i]+" ");
			}
		}
		value= pdfText.replace(startToken.toString().trim(),"")
				.replace(endToken.toString().trim(),"");
		map.put(variable,value.trim());
	}
}

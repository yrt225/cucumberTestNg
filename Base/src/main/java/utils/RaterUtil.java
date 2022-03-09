package utils;

import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import model.CellMap;
import model.policy.DataSheetType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
@Component
@ScenarioScope
public class RaterUtil {

	public static String premium;
	private static final Logger log = LoggerFactory.getLogger(RaterUtil.class);
	File outputPath = new File("target/" + File.separator + "rater_files" + "/");

	public static Map<DataSheetType, Integer> raterConfig = new HashMap<>();

	public void setRaterConfig () {
		raterConfig.put(DataSheetType.CYBER_RATER, 3);
		raterConfig.put(DataSheetType.GL_RATER, 2);
		raterConfig.put(DataSheetType.PL_RATER, 7);
		raterConfig.put(DataSheetType.BOP_RATER, 8);
	}

	public RaterUtil () {
		setRaterConfig();
		if (!outputPath.exists())
			 outputPath.mkdirs();
	}


	public static double calculatePremium (String stateSpecificFileName, List<CellMap> cellMapList, String fileName, int premiumRow, int premiumCol) {
		double premium = 0.0;
		try {
			FileInputStream file = new FileInputStream(stateSpecificFileName);
			Workbook workbook = new XSSFWorkbook(file);
			CellStyle cellStyle = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM dd,yyyy"));

			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			workbook.setForceFormulaRecalculation(true);
			for (CellMap cellMap : cellMapList) {
				if (cellMap.getCellValue() == null)
					continue;
				try {
					if (cellMap.getCellValue() instanceof String) {
						workbook.getSheet("Main").getRow(cellMap.getRowNumber()).getCell(cellMap.getColumnNumber()).setCellValue((String) cellMap.getCellValue());
					} else {
						if (cellMap.getCellValue() instanceof Integer)
							workbook.getSheet("Main").getRow(cellMap.getRowNumber()).getCell(cellMap.getColumnNumber()).setCellValue((Integer) cellMap.getCellValue());
						else if (cellMap.getCellValue() instanceof Double)
							workbook.getSheet("Main").getRow(cellMap.getRowNumber()).getCell(cellMap.getColumnNumber()).setCellValue((Double) cellMap.getCellValue());
						else
							try {
								workbook.getSheet("Main").getRow(cellMap.getRowNumber()).getCell(cellMap.getColumnNumber()).setCellValue((Date) cellMap.getCellValue());
								workbook.getSheet("Main").getRow(cellMap.getRowNumber()).getCell(cellMap.getColumnNumber()).setCellStyle(cellStyle);
							} catch (Exception ex) {
							}
					}
					log.info(cellMap.toString());
				} catch (Exception exp) {
					log.error("Error while entering:" + cellMap.toString());
				}
			}
			if (TestRunConfig.isLocal) {
				FileOutputStream os = new FileOutputStream("./target/rater_files/" + fileName);
				workbook.write(os);
				os.close();
			}
			try {
				evaluator.evaluateInCell(workbook.getSheet("Main").getRow(premiumRow).getCell(premiumCol));
			} catch (Exception e) {
				workbook.close();
			}
			printWorkBookData(workbook);
			premium = workbook.getSheet("Main").getRow(premiumRow).getCell(premiumCol).getNumericCellValue();
			workbook.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Failed while calculating premium:" + ex.getMessage());
		}
		log.debug("Premium Amount" + premium);
		return premium;
	}

	public static void printWorkBookData (Workbook workbook) {
		System.out.println("------------------------------------------");
		Iterator<Row> rowIterator = workbook.getSheetAt(0).iterator();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			//For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int rowIndex = cell.getRowIndex();
				int index = cell.getColumnIndex();
				switch (cell.getCellType()) {
					case BOOLEAN:
						log.debug(rowIndex + "," + index + "(B)-" + cell.getBooleanCellValue() + "\t\t");
						break;
					case NUMERIC:
						log.debug(rowIndex + "," + index + "(N)-" + cell.getNumericCellValue() + "\t\t");
						break;
					case STRING:
						log.debug(rowIndex + "," + index + "(S)-" + cell.getStringCellValue() + "\t\t");
						break;
					case FORMULA:
						if (cell.getCachedFormulaResultType().equals(NUMERIC)) {
							log.debug(rowIndex + "," + index + "(FN)-" + cell.getNumericCellValue() + "\t\t");
						} else if (cell.getCachedFormulaResultType().equals(STRING)) {
							log.debug(rowIndex + "," + index + "(FS)-" + cell.getRichStringCellValue() + "\t\t");
						}
					case ERROR:
						log.debug(evaluator.evaluateInCell(cell) + "\t\t");
					case _NONE:
						log.debug(rowIndex + "," + index + "\t");
				}
			}
			log.debug("------------------------------------------");
		}
	}
}

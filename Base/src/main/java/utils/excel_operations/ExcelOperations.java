package utils.excel_operations;
@Component
@ScenarioScope
public class ExcelOperations<T> {
    private static final String NATIVE_CONFIG = "native_config_1.0.xlsx";
    public static Logger log = LoggerFactory.getLogger(ExcelOperations.class);
    private static String filePath = null;

    public List<T> get(DataSheetType dataSheetType) {
        filePath = this.getClass().getClassLoader().getResource("config_workbook").getPath();
        filePath = filePath + File.separator + NATIVE_CONFIG;
        try {
            PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().withCasting(new MyCasting())
                    .sheetName(dataSheetType.getSheetName()).trimCellValue(true).preferNullOverDefault(false).build();
            List<T> tList = new ArrayList<>();
            ZipSecureFile.setMinInflateRatio(0);
            switch (dataSheetType) {
             
                case USERS:
                    tList = (List<T>) Poiji.fromExcel(new File(filePath), User.class, options);
                    break;
             
            }
            return tList;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }

    public List<T> getPremiumGenerated() {
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().withCasting(new MyCasting())
                .sheetName("Sheet2").trimCellValue(true).preferNullOverDefault(false).build();
        filePath = this.getClass().getClassLoader().getResource("config_workbook").getPath();
        filePath = filePath + File.separator + "BOP_Premiums.xls";
        return (List<T>) Poiji.fromExcel(new File(filePath), GeneratedPremium.class,options);
    }
}



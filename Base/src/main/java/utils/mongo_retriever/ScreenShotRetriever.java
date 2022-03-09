package utils.mongo_retriever;

public class ScreenShotRetriever {
    /*
    public static void main(String[] args) throws Exception{
        System.out.println("-------------- Screen Shot Retriever -------------");
        Scanner scanner=new Scanner(System.in);
        File dir=new File("target/scenario_screenshot");
        System.out.println("Enter the scenario name:");
        String scenarioName= scanner.nextLine();
        MongoCollection mongoCollection = DB_Connection.getJongoConnection().getCollection(FF_EXECUTION_RESULTS);
        TestExecutionMain record = mongoCollection.findOne("{'scenario_name':#}", scenarioName).as(TestExecutionMain.class);
        FileUtils.deleteDirectory(dir);
        dir.mkdir();
        for(TestExecutionResults results:record.getTestExecutionResultsList()){
            File file=new File(dir.getAbsolutePath()+File.separator+results.getCreatedOn().replaceAll("[ :.-]","_")+".png");

            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(results.getScreenShot());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(image, "png", file);
        }
    }*/
}

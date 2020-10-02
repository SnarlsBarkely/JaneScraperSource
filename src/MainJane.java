import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.server.handler.FindActiveElement;

//jars: jsoup, selenium

public class MainJane {
	static WebDriver driver;
	
	private String storeID;
	private String url;
	/** Contains all of the items on the menu of choice as one single string (seperated by line); used to print to a file*/
	private String allItemStr;
	private String location;
	private String outputPath;
	private String filename;
	
	
	public MainJane(String storeID, String outputPath) {
		this.storeID = storeID;
		this.url = "https://www.iheartjane.com/embed/stores/" + storeID 
				+ "/menu?refinementList[root_types][0]=vape&refinementList[root_types][1]=flower&refinementList[root_types][2]=extract&refinementList[root_types][3]=tincture&refinementList[root_types][4]=topical";
		this.outputPath = outputPath;
	}

	public static void main(String[] args) {
		/** User input for building the object that will hold the id of the jane store as well as our output path*/
		String id = (args[0] != null) ? args[0] : obtainJaneID();
		String path = (args[1] != null) ? args[1] : new String();
		
		MainJane storeObject = new MainJane(id, path);
		/** String for full html of 'url'*/
		String html;
		
		/**the webDriver object, use either setupFirefoxDriver() or setupChromeDriver() to get it configured to ff/chrome*/
		Document doc;
		
		/** The string that we will eventually output to a txt file at {OUTPUTPATHBASE}*/
		String itemsOnMenuAsOneStr;
		
		//TODO: Lets the user set up their preferred browser
		//TODO: try with chrome driver because you don't have chrome on here, that couldve been the problem when trying the family laptop (i doubt they have firefox)
//		selectBrowser();
		//firefox for the mean time
		setupFirefoxDriver("geckodriver.exe");
//		setupChromeDriver();
		
		
        // launch Fire fox and direct it to the Base URL1
        driver.get(storeObject.url);
        try {
        	//sleep to let the js run
			Thread.sleep(3000);
			
			//find all buttons with text "Load More" and click em2
			boolean btnClicked = findButtonFromStringAndClick("Load More");
			while(btnClicked) {
				btnClicked = findButtonFromStringAndClick("Load More");
			}
			Thread.sleep(3000);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			//this finally gets the HTML of the 
			html = fullHTML();
	
			//from the jsoup jar and parses html into elements
			doc = Jsoup.parse(html);
			
			storeObject.printItemStrings(doc, false);
			
			//This section here adds the locations name based off the stores ids
			String filename;
			
			stringToFile(storeObject.allItemStr, storeObject.outputPath, false);
			
			//now we need something that takes the strings and makes them into items, like a parser (for the new files
	        System.out.println("WebElement = "+html);
		} catch (StaleElementReferenceException | NoSuchElementException | InterruptedException nse) {
			nse.printStackTrace();
		}
			
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//											Store Selection Menu										   //
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Lets you choose which dispensary you would like to crawl
	 *  - you do have to find your stores id by searching it while on the page
	 */
	public static String obtainJaneID() {
		/** the iheartjane embedded link for ALL dispensaries*/
		System.out.println("Please enter the iheartjane id of your prefferred store (hit f12 and in the searchbar while on your stores iheartjane page and search iheartjane.com/embed/stores/ and the id will be in here)");
		Scanner sc = new Scanner(System.in);
		String storeID = sc.nextLine();
		
		return storeID;	
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//clean up methods
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** setupFirefoxDriver() 
	 * 	static method to set you up with a selenium firefox based session
	 *  driver will most likely be your main variable when navigating the web
	 */
	private static void setupFirefoxDriver(String pathToFFDriver) {
		//uncomment below line to setup the driver for FF, download is on selenium download page under the browsers section toward the bottom
		System.setProperty("webdriver.gecko.driver", pathToFFDriver);
		driver = new FirefoxDriver();
	}
	
	/*
	 * private static void setupChromeDriver() { //uncomment line below to setup
	 * properties for the chrome driver String chromeDriverPath = getCurrentPath() +
	 * "\\chromedriver.exe"; System.setProperty("webdriver.chrome.driver",
	 * chromeDriverPath);
	 * 
	 * System.out.println(chromeDriverPath);
	 * 
	 * driver = new ChromeDriver(); }
	 */
	
	/** 
	 * 
	 * @return return the parent directory if there is one, null otherwise
	 */
	private static String getCurrentPath() {
		try {
			Path path = Paths.get(MainJane.class.getResource(".").toURI());
			return path.getParent().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	//Function returns true if there is a button with tag name contentToSearch that is clicked. False otherwise
	public static boolean findButtonFromStringAndClick(String contentToSearch) {
		//have a flag for if there was any buttons clicked, set to true in the if, assign it false to start, have it return that value
		boolean clickedButtonFlag = false;
		
		List<WebElement> btnElmnts = driver.findElements(By.tagName("button"));
		for(WebElement btn : btnElmnts) {
			try {
				if(btn.getText().contentEquals(contentToSearch)) {
					btn.click();
					clickedButtonFlag = true;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return clickedButtonFlag;
	}
	
	/** grabs the full html from the url passed in
	 * 
	 * @return html of whatever page the driver grabbed last
	 */
	public static String fullHTML() {
    	JavascriptExecutor js = (JavascriptExecutor) driver;
    	//for full html in the console
        //String sgVal = (String) js.executeScript("console.log(document.getElementsByTagName('html')[0].innerHTML)");

		String sgVal = (String) js.executeScript("return document.getElementsByTagName('html')[0].innerHTML");
		return sgVal;
    }
	
	
	/** pass in a Jsoup document, it grabs elements from the stores janeID represented with /embed/stores/{id}/products
	 *  -this grabs all the items based off the string /embed/stores/0000/products and puts em in a 
	 * @param doc - the parsed html of a webpage in a Document element
	 * @param printStrings - boolean that prints out all the elements it gets 
	 * @return 
	 */
	public void printItemStrings(Document doc, boolean printStrings) {
		String link = "/embed/stores/" + this.storeID + "/products";
		Elements elementsByStoreUrl = doc.getElementsByAttributeValueContaining("href", link);
		List<String> elementsByUrlStringList = elementsByStoreUrl.eachText();
		String returnString = "";
		
		for(String elementString : elementsByUrlStringList) {
			returnString += elementString+"\n";
			if(printStrings)
				System.out.println("ELEMENT:\n"+elementString+"\n\n");
		}
		this.allItemStr =  returnString;
	}
	

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//											HELPER METHODS												   //
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void stringToFile(String str, String pathToWrite, boolean deletePrev) {
    	String OUTPUTPATH = pathToWrite + "\\";
    	OUTPUTPATH += timeString()+".txt";
    	OUTPUTPATH.replace("\\", "/");
    	System.out.println("Output path: "+OUTPUTPATH);
		try {
			FileWriter myWriter = new FileWriter(OUTPUTPATH);
			myWriter.write(str);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} 
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
    }
	
	
	public static String timeString() {
    	Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY_HH-mm");
        return sdf.format(cal.getTime()).toString();
    }

}

package com.redPanda.Assignmen1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class MyDriver {
	
	
	private WebDriver driver;
	
	public MyDriver(WebDriver driver){
		this.driver = driver;
	}
	
	public WebDriver setupDriver() throws IOException{
		/*Write your code here to initiate your driver*/
		Properties property = new Properties();
		FileInputStream config = new FileInputStream("Resource/config.properties");
		property.load(config);
		String myBrowser = property.getProperty("browser");
		if(myBrowser.equals("chrome")){
			System.setProperty("webdriver.chrome.driver", "Resource/chromeDriver");
			driver = new ChromeDriver();
		}
		else
		{
			System.setProperty("webdriver.gecko.driver", "Resource/geckodriver");
			driver = new FirefoxDriver();
		}
		
		driver.manage().window().fullscreen();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}
	
	//Select drop down menu by visible text
    public void selectByVisibleText(WebElement we, String VisibleText){
    	Select select = new Select(we);
    	select.selectByVisibleText(VisibleText);
    }
    //Open a new Tab/Window and switch to it.
    public void openNewTab()
	{   
		
		String CH = driver.getWindowHandle();
		
		JavascriptExecutor jse =(JavascriptExecutor)driver;
		jse.executeScript("window.open();");
		
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		for(String t: tabs)
		{
			if(t.equals(CH))
			{
				int Cix= tabs.indexOf(CH);
				
				driver.switchTo().window(tabs.get(Cix+1));
				break;
			}
		}
	}

    
    //Switch to a tab by its index.
    public void switchToTab(int p )
	{
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(p-1));
	}
    
    //Verify the response of all the links in current page.
    public boolean verifyIfAllLinksActive()
	{
    	List<WebElement> links=driver.findElements(By.tagName("a"));
		System.out.println("Total links are "+links.size());
		ArrayList<String> Urls = new ArrayList<String>();
		for(WebElement e: links)
		{
			
			String url= e.getAttribute("href");	
			Urls.add(url);
			
		}
		boolean value = true;
        for (String linkUrl: Urls) {
			try {
				URL url = new URL(linkUrl);

				HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();

				httpURLConnect.setConnectTimeout(3000);

				httpURLConnect.connect();
				
				boolean result = httpURLConnect.getResponseCode() == 200;
				
				if (result== true) {
					
				}
				else
				{
					System.out.println(linkUrl + " - " + httpURLConnect.getResponseMessage() + " - "
							+ HttpURLConnection.HTTP_NOT_FOUND);
					value = false;
					
				}
			} catch (Exception e) {

			} 
		}
		return value;
		
		
    } 
    
    
  //Scroll down
    public void scrollToBottom()
    {
    	((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
    
  //Scroll down by pixel
    public void scrollDownByPixel(int pixel)
    {
    	String pixel1 = "window.scrollBy(0,";
    	String pixel2 = ")";
    	((JavascriptExecutor) driver).executeScript(pixel1 + pixel+ pixel2);
    }
    
  //Scroll Up by pixel
    public void scrollUpByPixel(int pixel)
    {
    	String pixel1 = "window.scrollBy(0,";
    	String pixel2 = ")";
    	((JavascriptExecutor) driver).executeScript(pixel1 + -pixel + pixel2);
    }
  //Scroll up to header
    public void scrollToTop()
    {
    	((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -document.body.scrollHeight)");
    }  
  //Scroll to element
    public void scrollToElement(WebElement element){
    	((JavascriptExecutor) driver).executeScript("'window.scrollBy(0,'+' element.getLocation().getY()'", "");
    } 
  //Mouse Hover on an element
    public void mouseHoverOn(WebElement webelement)
    {
    	Actions action = new Actions(driver);
    	action.moveToElement(webelement).build().perform(); 
    }
    
    //file upload under send keys
    public String getTestDataPath(String fileName){
		return Paths.get(".").toAbsolutePath().normalize().toString()+"/testdata/"+fileName;
    }
    
    //take screenshot as desired name
    public void takeScreenshotAs(String name_of_screenshot) throws Exception 
    {
    	Thread.sleep(1000);  
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File(Paths.get(".").toAbsolutePath().normalize().toString()+"/test-output/Screenshots/"+name_of_screenshot+".png"));
    }
	
    //get normalized path
    public String getNormalizedPath(){
    	return Paths.get(".").toAbsolutePath().normalize().toString()+"/test-input/";
    }

}

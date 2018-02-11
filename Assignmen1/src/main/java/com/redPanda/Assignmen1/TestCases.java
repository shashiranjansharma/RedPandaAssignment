package com.redPanda.Assignmen1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Hello world!
 *
 */
public class TestCases 
{
	WebDriver driver;
	MyDriver myDriver = new MyDriver(driver);
	@BeforeSuite(alwaysRun = true)
	public WebDriver setupDriver() throws IOException{
		driver = myDriver.setupDriver();
		return driver;
	}

	@AfterSuite(alwaysRun = true)
	public void closeDriver(){
		
		driver.quit();
	}
	
	@Test(priority = 1)
	public void verifyAdvanceSearch() throws Exception{
		driver.get("https://imdb.com");
		myDriver.takeScreenshotAs("TC_1_Step_1");
		WebElement advanceDropdown = driver.findElement(By.id("quicksearch"));
		advanceDropdown.click();
		myDriver.takeScreenshotAs("TC_1_Step_2");
		myDriver.selectByVisibleText(advanceDropdown, "Advanced Search »");
		myDriver.takeScreenshotAs("TC_1_Step_3");
		String header = driver.findElement(By.xpath("//*[@id='header']/h1")).getText();
		Assert.assertEquals(header, "Advanced Search");
	}
	
	@Test(priority = 2)
	public void Verify3waySearch() throws Exception{
		driver.findElement(By.xpath("//*[@id='main']/div[2]/a[1]")).click();
		myDriver.takeScreenshotAs("TC_2_Step_1");
		String header = driver.findElement(By.xpath("//*[@id='header']/h1")).getText();
		Assert.assertEquals(header, "Advanced Title Search");
	}
    
	@Test(priority = 3)
	public void verifyIMDB_Top250() throws Exception{
		//WebElement titleGroup = driver.findElement(By.xpath("//*[@id='main']/div[7]/div[2]"));
		myDriver.scrollDownByPixel(500);
		myDriver.takeScreenshotAs("TC_3_Step_1");
		driver.findElement(By.xpath("//*[@id='groups-2']")).click();
		Boolean checkTop250 = driver.findElement(By.xpath("//*[@id='groups-2']")).isSelected();
		Assert.assertTrue(checkTop250, "IMDB 'Top 250' check box is not selected");
		myDriver.takeScreenshotAs("TC_3_Step_2");
		myDriver.scrollDownByPixel(900);
		driver.findElement(By.xpath("//*[@id='main']/p[3]/button")).click();
		updateDB();
		System.out.println("page1 updated to DB");
		myDriver.scrollToBottom();
		driver.findElement(By.xpath("//*[@id='main']/div/div/div[4]/div/a")).click();
		myDriver.takeScreenshotAs("TC_3_Step_3");
		updateDB();
		myDriver.scrollToBottom();
		System.out.println("page2 updated to DB");
		driver.findElement(By.xpath("//*[@id='main']/div/div/div[4]/div/a[2]")).click();
		myDriver.takeScreenshotAs("TC_3_Step_4");
		updateDB();
		myDriver.scrollToBottom();
		System.out.println("page3 updated to DB");
		driver.findElement(By.xpath("//*[@id='main']/div/div/div[4]/div/a[2]")).click();
		myDriver.takeScreenshotAs("TC_3_Step_5");
		updateDB();
		myDriver.scrollToBottom();
		System.out.println("page4 updated to DB");
		driver.findElement(By.xpath("//*[@id='main']/div/div/div[4]/div/a[2]")).click();
		updateDB();
		myDriver.takeScreenshotAs("TC_3_Step_6");
	    
	}
	
	public void updateDB() throws IOException{
		List<WebElement> index = driver.findElements(By.className("lister-item-index"));
		ArrayList<String> indexed = new ArrayList<String>();
		for(WebElement e : index){
			indexed.add(e.getText().replace(".",""));
		}
		List<WebElement> name = driver.findElements(By.xpath("//*[@class='lister-item-header']/a"));
		ArrayList<String> names = new ArrayList<String>();
		for(WebElement e : name){
			names.add(e.getText().replace("'",""));
		}
		List<WebElement> year = driver.findElements(By.className("lister-item-year"));
		ArrayList<String> years = new ArrayList<String>();
		for(WebElement e : year){
			years.add(e.getText());
		}
		List<WebElement> rating = driver.findElements(By.className("ratings-imdb-rating"));
		ArrayList<String> ratings = new ArrayList<String>();
		for(WebElement e : rating){
			ratings.add(e.getAttribute("data-value"));
		}
		int size = index.size();
		
		//===============================================================================================//
		Connection c = null;
	    Statement stmt = null;
	    
	    Files.deleteIfExists(Paths.get("test-output/SqLite/test.db"));
	    
	    try {
	       Class.forName("org.sqlite.JDBC");
	       c = DriverManager.getConnection("jdbc:sqlite:test-output/SqLite/test.db");
	       System.out.println("Opened database successfully");
	
	       stmt = c.createStatement();
	       String sql = "CREATE TABLE IMDB_Top250 " +
	                      "(ID INT PRIMARY KEY     NOT NULL," +
	                      " NAME           TEXT    NOT NULL, " + 
	                      " YEAR            TEXT     NOT NULL, " + 
	                      " RATING         TEXT)"; 
	       System.out.println("created table successfully");
	       stmt.executeUpdate(sql);
	       for(int i=0; i<size; i++){
	    	   sql = "INSERT INTO IMDB_Top250 (ID,NAME,YEAR,RATING) " +
                       "VALUES ("+indexed.get(i) +", '"+names.get(i) +"', '"+years.get(i) +"', '"+ratings.get(i)+"');"; 
	    	   String st = (i+1) +", '"+names.get(i) +"', '"+years.get(i) +"', '"+ratings.get(i);
	    	   System.out.println(st);
	    	   stmt.executeUpdate(sql);
			}
	       System.out.println("Table updated successfully");
 
	       ResultSet rs = stmt.executeQuery( "SELECT * FROM IMDB_Top250;" );
	         
	         while ( rs.next() ) {
	         int id = rs.getInt("ID");
	         String  name1 = rs.getString("NAME");
	         int year1  = rs.getInt("YEAR");
	         float rating1 = rs.getFloat("RATING");
	         
	         System.out.println( "ID = " + id );
	         System.out.println( "NAME = " + name1 );
	         System.out.println( "YEAR = " + year1 );
	         System.out.println( "RATING = " + rating1);
	         System.out.println();
	         }
	       
	       stmt.close();
	       c.close();
	    } catch ( Exception e ) {
	       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	       e.printStackTrace();
	       System.exit(0);
	    }
	}

	
}

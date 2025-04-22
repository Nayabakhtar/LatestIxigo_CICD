package pages;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {
	  WebDriver driver;
	    WebDriverWait wait;
	    

    public HomePage(WebDriver driver) {
    	 this.driver = driver;
         this.wait = new WebDriverWait(driver, Duration.ofSeconds(80));
    }

 // locators for ixigo
    By roundTripTab = By.xpath("//button[@role='tab' and contains(text(),'Round Trip')]");
    
    //Locator For Departure Date(From)
    By fromField = By.xpath("//p[@data-testid='originId']");
    
    //Locators for Destination (To)
    private By toClickableField = By.xpath("//p[@data-testid='destinationId']");
    private By suggestionsLocator = By.xpath("//div[@role='listitem']");

    By cityOption = By.xpath("//li//div[contains(text(),'Kolkata')]"); // dynamic based on input

    By searchField = By.xpath("//input[@placeholder='Enter city or airport']");
    
    
    

    
    public void selectRoundTrip(String tabName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(80));
        try {
            By tabLocator = By.xpath("//button[@role='tab' and contains(text(),'" + tabName + "')]");
            WebElement tabElement = wait.until(ExpectedConditions.presenceOfElementLocated(tabLocator));

            wait.until(ExpectedConditions.elementToBeClickable(tabElement)).click();
            System.out.println("✅ " + tabName + " tab clicked");

        } catch (TimeoutException e) {
            System.out.println("⚠️ " + tabName + " tab not found in time — may be hidden or not rendered.");
        }
    }

    
    
    
    
    public void enterFromCity(String fromCity) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

    // Click the 'From' field
    WebElement from = wait.until(ExpectedConditions.elementToBeClickable(fromField));
    from.click();
    }
    
// To field
    public void enterToCity(String toCity) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);
try {
        // Step 1: Click on the container to activate the input
        WebElement toClickable = wait.until(ExpectedConditions.presenceOfElementLocated(toClickableField));
        js.executeScript("arguments[0].scrollIntoView(true);", toClickable);
        js.executeScript("arguments[0].click();", toClickable);
        Thread.sleep(4000);  // Let the dropdown render
        // Step 2: Send keys to the actual input field
        WebElement toInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='outline-none w-full bg-transparent placeholder:text-disabled pt-3 text-primary placeholder:opacity-0 focus:placeholder:opacity-100 font-medium text-lg !pt-5']")));
        toInput.clear();  // Optional
        toInput.sendKeys(toCity);
        
        System.out.println("🔍 Typed city: " + toCity);

     // Step 3: Wait for list items
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@role='listitem']")));
        Thread.sleep(1000); // Allow dropdown rendering

        // Step 4: Iterate over dropdown items and match desired city
        List<WebElement> options = driver.findElements(By.xpath("//div[@role='listitem']"));
        boolean selected = false;
        for (WebElement option : options) {
            String text = option.getText().trim();
            if (text.contains(toCity)) {
                js.executeScript("arguments[0].scrollIntoView(true);", option);
                wait.until(ExpectedConditions.elementToBeClickable(option)).click();
                System.out.println("✅ Successfully selected: " + toCity);
                selected = true;
                break;
            }
        }

        if (!selected) {
            throw new NoSuchElementException("City not found in dropdown: " + toCity);
        }

    } catch (Exception e) {
        System.out.println("❌ Exception while selecting city: " + toCity);
        e.printStackTrace();
    }
    }
  
    
    
    
   public void selectDepartureDate(String date) {
    try {
       
        // Click the Next button to move to the correct month
        System.out.println("👉 Clicking the Next arrow once...");
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.react-calendar__navigation__next-button")));
        nextBtn.click();

        // Wait for the calendar month to update
        System.out.println("👉 Waiting for 'June 2025' to appear...");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.cssSelector("span.react-calendar__navigation__label__labelText--to"), "June 2025"));
        System.out.println("✅ 'June 2025' is visible.");
        
        nextBtn.click();

        // Wait for calendar dates to load
        System.out.println("👉 Waiting for calendar dates to load...");
        List<WebElement> allDates = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
            By.xpath("//button[@type='button' and not(contains(@class,'navigation')) and not(@disabled)]")));

        // Select the specific date
        for (WebElement dateElement : allDates) {
            WebElement abbr = dateElement.findElement(By.tagName("abbr"));
            if (abbr.getText().equals(date)) {
                abbr.click();  // Click the matching date
                System.out.println("✅ Departure date " + date + " clicked.");
                break;
            }
        }

    } catch (Exception e) {
        System.out.println("❌ Error occurred while selecting the departure date:");
        e.printStackTrace();
    }
   }
   
   public void selectReturnDate(String date, String expectedMonthYear) {
    try {
        System.out.println("👉 Trying to click on Return field...");
        WebElement returnField = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//p[text()='Return']")));
        returnField.click();
        System.out.println("✅ Return field clicked.");

        System.out.println("👉 Clicking the Next arrow once...");
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.react-calendar__navigation__next-button")));
        nextBtn.click();

        System.out.println("👉 Waiting for '" + expectedMonthYear + "' to appear...");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.cssSelector("span.react-calendar__navigation__label__labelText--to"), expectedMonthYear));
        System.out.println("✅ '" + expectedMonthYear + "' is visible.");
        nextBtn.click();
        // Wait for the calendar dates to be loaded
        System.out.println("👉 Waiting for calendar dates to load...");
        List<WebElement> allDates = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
            By.xpath("//button[@type='button' and not(contains(@class,'navigation')) and not(@disabled)]")));

        for (WebElement dateElement : allDates) {
            WebElement abbr = dateElement.findElement(By.tagName("abbr"));
            if (abbr.getText().equals(date)) {
                abbr.click();
                System.out.println("✅ Return date " + date + " clicked.");
                break;
            }
        }

    } catch (Exception e) {
        System.out.println("❌ Error occurred while selecting the Return date:");
        e.printStackTrace();
    }
   }


   public void selectTravellers(int adults, int children, int infants) {
        try {
            // Click on "Travellers & Class" field
            System.out.println("👉 Clicking on 'Travellers & Class'...");
            WebElement travellerField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//p[@data-testid='pax']")));
            travellerField.click();
            System.out.println("✅ 'Travellers & Class' clicked.");

            // Select Adults
            System.out.println("👉 Selecting " + adults + " Adults...");
            WebElement adultOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[1]/div[2]/div/button[@data-testid='" + adults + "']")));
            adultOption.click();
            System.out.println("✅ " + adults + " Adults selected.");

            // Select Children
            System.out.println("👉 Selecting " + children + " Children...");
            WebElement childOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[2]/div[2]/div/button[@data-testid='" + children + "']")));
            childOption.click();
            System.out.println("✅ " + children + " Children selected.");

            // Select Infants
            System.out.println("👉 Selecting " + infants + " Infants...");
            WebElement infantOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[3]/div[2]/div/button[@data-testid='" + infants + "']")));
            infantOption.click();
            System.out.println("✅ " + infants + " Infants selected.");

            // Click the "Done" button
            System.out.println("👉 Clicking on Done button...");
            WebElement doneButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Done']")));
            doneButton.click();
            System.out.println("✅ Done button clicked. Traveller selection complete.");

        } catch (Exception e) {
            System.out.println("❌ Error occurred while selecting Travellers & Class:");
            e.printStackTrace();
        }
    }

   
   public void clickSearchButton() {
       try {
           System.out.println("👉 Waiting for Search button...");
           WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
               By.xpath("//button[contains(text(),'Search')]")));
           searchBtn.click();
           System.out.println("✅ Search button clicked.");
       } catch (Exception e) {
           System.out.println("❌ Error while clicking Search button:");
           e.printStackTrace();
       }
   }
}
   
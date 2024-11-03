package redfin.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class SearchPage {
    protected WebDriver driver;
    private final By NUMBER_OF_RESULTS = By.xpath("//*[@id=\"sidepane-header-sticky-container\"]/div[2]/div/div[1]");
    private final By RESULTS_LOCATION = By.xpath("//div[contains(@class, \"descriptionAndModeContainer\")]//h1");
    private final By ALL_FILTERS_BUTTON = By.xpath("//div[@data-rf-test-id=\"filterButton\"]//button");
    private final By MIN_PRICE_TEXTBOX = By.xpath("//input[@placeholder=\"Enter min\"]");
    private final By MAX_PRICE_TEXTBOX = By.xpath("//input[@placeholder=\"Enter max\"]");
    private final By BEDS_BUTTONS = By.xpath("//div[@aria-label=\"Number of bedrooms\"]//div");
    private final By BATHS_BUTTONS = By.xpath("//div[@aria-label=\"Number of bathrooms\"]//div");
    private final By SEE_HOMES_BUTTON = By.xpath("//div[@class=\"applyButtonContainer\"]//button");
    private final String PAGE_TITLE = "Homes for Sale & Real Estate | Redfin";
    private String listingPriceXpath = "//div[contains(@id, \"MapHomeCard\")][INDEX]/div/div/div[last()]//span[@class=\"bp-Homecard__Price--value\"]";
    private String listingBedsXpath = "//div[contains(@id, \"MapHomeCard\")][INDEX]/div/div/div[last()]//span[contains(@class, \"bp-Homecard__Stats--beds\")]";
    private String listingBathsXpath = "//div[contains(@id, \"MapHomeCard\")][INDEX]/div/div/div[last()]//span[contains(@class, \"bp-Homecard__Stats--baths\")]";


    public SearchPage(WebDriver driver) {
        this.driver = driver;

        // Make sure being called on the correct page
        if (!driver.getTitle().contains(PAGE_TITLE)) {
            throw new IllegalStateException("Not on the Search page! Driver's URL: " + driver.getCurrentUrl());
        }
    }

    public void filterResults(int min, int max, int bedsIndexStart, int bedsIndexEnd, int bathsIndex) {
        // Get the starting number of results
        String results = getNumberOfResults();

        driver.findElement(ALL_FILTERS_BUTTON).click();
        driver.findElement(MIN_PRICE_TEXTBOX).sendKeys(String.valueOf(min));
        driver.findElement(MAX_PRICE_TEXTBOX).sendKeys(String.valueOf(max));

        // Gets the array of bath options and selects the correct one via index
        driver.findElements(BATHS_BUTTONS).get(bathsIndex).click();

        // Sets the 2 values for the bed range. If you select the same value twice it will only select that value
        driver.findElements(BEDS_BUTTONS).get(bedsIndexStart).click();
        driver.findElements(BEDS_BUTTONS).get(bedsIndexEnd).click();

        // Search and wait for the results to populate
        new WebDriverWait(driver, Duration.of(5, ChronoUnit.SECONDS))
                .until(ExpectedConditions.attributeToBe(SEE_HOMES_BUTTON, "class", "bp-Button applyButton bp-Button__type--primary"));
        driver.findElement(SEE_HOMES_BUTTON).click();

        // The page takes a few seconds to filter completely
        new WebDriverWait(driver, Duration.of(5, ChronoUnit.SECONDS))
                .until(ExpectedConditions.not(ExpectedConditions.textToBe(NUMBER_OF_RESULTS, results)));
    }

    public String getResultsLocation() {
        return driver.findElement(RESULTS_LOCATION).getText();
    }

    public String getNumberOfResults() {
        return driver.findElement(NUMBER_OF_RESULTS).getText();
    }

    public String getPriceFromListing(int index) {
        return driver.findElement(By.xpath(listingPriceXpath.replace("INDEX", String.valueOf(index)))).getText();
    }

    public String getBedsFromListing(int index) {
        return driver.findElement(By.xpath(listingBedsXpath.replace("INDEX", String.valueOf(index)))).getText();
    }

    public String getBathsFromListing(int index) {
        return driver.findElement(By.xpath(listingBathsXpath.replace("INDEX", String.valueOf(index)))).getText();
    }

}

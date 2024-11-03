package redfin.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    protected WebDriver driver;
    private final By SEARCH_TEXT_BOX = By.name("searchInputBox");
    private final By SEARCH_BUTTON = By.xpath("//div[@id=\"tabContentId0\"]//button");
    private final String PAGE_TITLE = "Redfin | Real Estate & Homes for Sale, Rentals, Mortgages & Agents";

    public HomePage(WebDriver driver) {
        this.driver = driver;

        new WebDriverWait(driver, Duration.ofSeconds(7)).until(ExpectedConditions.elementToBeClickable(SEARCH_BUTTON));

        // Make sure being called on the correct page
        if (!driver.getTitle().equals(PAGE_TITLE)) {
            throw new IllegalStateException("Not on the Home page! Driver's URL: " + driver.getCurrentUrl());
        }
    }

    public SearchPage search(String searchText) {
        driver.findElement(SEARCH_TEXT_BOX).sendKeys(searchText);
        driver.findElement(SEARCH_BUTTON).click();
        return new SearchPage(driver);
    }
}

package redfin;

import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import redfin.pages.HomePage;
import redfin.pages.SearchPage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Main {
    private static final String LOCATION = "Portland, OR";
    private static final String PAGE_TITLE = "Portland, OR Homes for Sale & Real Estate | Redfin";
    private static final int MIN_PRICE = 300000;
    private static final int MAX_PRICE = 325000;
    private static final double BATHS = 1.5;
    private static final int BEDS = 2;
    private static final int BEDS_START_INDEX = 3;
    private static final int BEDS_END_INDEX = 3;
    private static final int BATHS_INDEX = 2;
    private static WebDriver driver;

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadTimeout(Duration.of(10, ChronoUnit.SECONDS));
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.of(5, ChronoUnit.SECONDS));

//        try {
            driver.get("https://www.redfin.com/");
            testFilteredPropertySearch(MIN_PRICE, MAX_PRICE, BEDS_START_INDEX, BEDS_END_INDEX, BATHS_INDEX);
//            System.out.println("TEST PASSED!");
//        } catch (Exception e) {
//            System.out.println("TEST FAILED!");
//        }

        driver.quit();
    }

    public static void testFilteredPropertySearch(int min, int max, int bedStartIndex, int bedEndIndex,
                                                    int BathsIndex) {
        // Search
        SearchPage search = new HomePage(driver).search(LOCATION);
        System.out.println("Searching " + LOCATION);

        // Validate the correct location was searched and the page title
        assertEquals(PAGE_TITLE, driver.getTitle(),
                "Incorrect Page Title. Expected: " + PAGE_TITLE + " Actual: " + driver.getTitle());
        assertTrue(search.getResultsLocation().contains(LOCATION),
                "Incorrect location. Expected: " + LOCATION + " Actual: " + search.getResultsLocation());

        // Get the number of results to confirm the listings were filtered
        int allResults = Integer.parseInt(search.getNumberOfResults().split(" ")[2].replace(",", ""));

        // Filter the results
        System.out.println("Filtering results!");
        search.filterResults(min, max, bedStartIndex, bedEndIndex, BathsIndex);

        // Validate the correct location was searched and the page title again
        assertEquals(PAGE_TITLE, driver.getTitle(),
                "Incorrect Page Title. Expected: " + PAGE_TITLE + " Actual: " + driver.getTitle());
        assertTrue(search.getResultsLocation().contains(LOCATION),
                "Incorrect location. Expected: " + LOCATION + " Actual: " + search.getResultsLocation());

        // Get the number of results found and validate results were filtered
        int numOfListings = Integer.parseInt(search.getNumberOfResults().split(" ")[2]);
        assertTrue(numOfListings < allResults,
                "Results were not filtered. Expected less than " + allResults + " but found " + numOfListings);

        // Validate each of the properties is within the set filters
        int price, foundBeds;
        double foundBaths;
        for (int i = 1; i <= numOfListings; i++) {

            // Remove the $ and , and the labels and assert the price
            price = Integer.parseInt(search.getPriceFromListing(i).substring(1).replace(",",""));
            assertTrue(price >= min, "Result below min price. min: " + min + " price: " + price);
            assertTrue(price <= max, "Result above max price. max: " + max + " price: " + price);

            // Sometimes we get plots of land with - for the bed and baths. Catch these and move on.
            try {
                foundBeds = Integer.parseInt(search.getBedsFromListing(i).split(" ")[0]);
                // Asserting the beds is a tiny more complex as the user could have searched a range or single value
                // If selected a single value
                if (bedEndIndex == bedStartIndex) {
                    assertEquals(BEDS, foundBeds,
                            "Incorrect number of beds. Beds expected: " + BEDS + " Actual: " + foundBeds);
                } else {
                    assertTrue(foundBeds >= BEDS ,
                            "Incorrect number of beds. Minimum beds expected: " + BEDS + " Actual: " + foundBeds);
                }

                foundBaths = Double.parseDouble(search.getBathsFromListing(i).split(" ")[0]);
                assertTrue(foundBaths >= BATHS,
                        "Incorrect number of baths. Baths expected: " + BATHS + " Actual: " + foundBaths);
                System.out.println("Listing " + i + " Validated. Price: " + price + " Beds: " + foundBeds + " Baths: " + foundBaths);
            } catch (NumberFormatException e) {
                System.out.println("Found a plot of land!");
                System.out.println("Listing " + i + " Validated. Price: " + price + " Beds: - Baths: -");
            }
        }
    }
}
package runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;



@RunWith(Cucumber.class)
@CucumberOptions(
	    features = "src/test/resources/features", // Reads Test from Feature file
	    glue = "stepdefinitions", //Connects Gherkin Language to Java Code
	    plugin = {
	        "pretty", // Removes all unnecessary Colors from Console for clean output
	        "html:target/cucumber-reports/CucumberReport.html",  
	        "json:target/cucumber-reports/Cucumber.json",
	        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
	    },
	    monochrome = true
	)

public class TestRunner {
}


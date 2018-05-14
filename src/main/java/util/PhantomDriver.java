package util;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PhantomDriver {
    
    public static PhantomJSDriver getDriver() {
        DesiredCapabilities dcaps = new DesiredCapabilities();
        dcaps.setCapability("acceptSslCerts", true);
        dcaps.setCapability("takesScreenshot", true);
        dcaps.setCapability("cssSelectorsEnabled", true);
        dcaps.setJavascriptEnabled(true);
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"C:\\Projects\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");

        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        return  driver;
    }
    
}

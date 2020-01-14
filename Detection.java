import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.*;
import java.util.List;
import javax.imageio.*;
import java.util.Scanner;
import org.openqa.selenium.firefox.*;
import java.awt.image.BufferedImage;

class OpenChrome extends CaptchaVerification {

    private void getResult(WebDriver driver) {
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        buttons.get(1).click();
    }

    private int getIndexOfData(String find, String HTML_OF_PAGE){
        int len=find.length();
        for(int i=0;i<HTML_OF_PAGE.length()-len;i++){
            if(HTML_OF_PAGE.substring(i, i+len).equals(find))
                return i;
        }
        return -1;
    }

    private void getALLData(String HTML_OF_PAGE){
        int registrationAuthorityStart=getIndexOfData("Registering Authority:  ", HTML_OF_PAGE)+24;
        int registrationAuthorityEnd=HTML_OF_PAGE.indexOf('<', registrationAuthorityStart);
        System.out.println("Registering Authority: "+HTML_OF_PAGE.substring(registrationAuthorityStart, registrationAuthorityEnd-1));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(registrationAuthorityEnd, HTML_OF_PAGE.length());

        int registrationDateStart=getIndexOfData("Registration Date:", HTML_OF_PAGE)+34;
        registrationDateStart=HTML_OF_PAGE.indexOf('>', registrationDateStart)+1;
        int registrationDateEnd=HTML_OF_PAGE.indexOf('<', registrationDateStart);
        System.out.println("Registration Date: "+HTML_OF_PAGE.substring(registrationDateStart, registrationDateEnd));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(registrationDateEnd, HTML_OF_PAGE.length());

        int ownerNameStart=getIndexOfData("Owner Name:", HTML_OF_PAGE)+39;
        ownerNameStart=HTML_OF_PAGE.indexOf('>', ownerNameStart)+1;
        int ownerNameEnd=HTML_OF_PAGE.indexOf('<', ownerNameStart);
        System.out.println("Owner Name: "+HTML_OF_PAGE.substring(ownerNameStart, ownerNameEnd));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(ownerNameEnd, HTML_OF_PAGE.length());

        int vehicleClassStart=getIndexOfData("Vehicle Class:", HTML_OF_PAGE)+39;
        vehicleClassStart=HTML_OF_PAGE.indexOf('>', vehicleClassStart)+1;
        int vehicleClassEnd=HTML_OF_PAGE.indexOf('<', vehicleClassStart);
        System.out.println("Vehicle class: "+HTML_OF_PAGE.substring(vehicleClassStart, vehicleClassEnd));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(vehicleClassEnd, HTML_OF_PAGE.length());

        int fuelTypeStart=getIndexOfData("Fuel:", HTML_OF_PAGE)+39;
        fuelTypeStart=HTML_OF_PAGE.indexOf('>', fuelTypeStart)+1;
        int fuelTypeEnd=HTML_OF_PAGE.indexOf('<', fuelTypeStart);
        System.out.println("Fuel: "+HTML_OF_PAGE.substring(fuelTypeStart, fuelTypeEnd));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(fuelTypeEnd, HTML_OF_PAGE.length());

        int modelTypeStart=getIndexOfData("Maker / Model:", HTML_OF_PAGE)+39;
        modelTypeStart=HTML_OF_PAGE.indexOf('>', modelTypeStart)+1;
        int modelTypeEnd=HTML_OF_PAGE.indexOf('<', modelTypeStart);
        System.out.println("Model: "+HTML_OF_PAGE.substring(modelTypeStart, modelTypeEnd));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(modelTypeEnd, HTML_OF_PAGE.length());

        int registrationUptoStart=getIndexOfData("REGN Upto:", HTML_OF_PAGE)+49;
        registrationUptoStart=HTML_OF_PAGE.indexOf('>', registrationUptoStart)+1;
        int registrationUptoEnd=HTML_OF_PAGE.indexOf('<', registrationUptoStart);
        System.out.println("Resgistration Upto: "+HTML_OF_PAGE.substring(registrationUptoStart, registrationUptoEnd));

        //Update the output
        HTML_OF_PAGE=HTML_OF_PAGE.substring(registrationUptoEnd, HTML_OF_PAGE.length());

        int RCActiveStart=getIndexOfData("RC Status:", HTML_OF_PAGE)+49;
        RCActiveStart = HTML_OF_PAGE.indexOf('>', RCActiveStart)+1;
        int RCActiveEnd=HTML_OF_PAGE.indexOf('<', RCActiveStart);
        System.out.println("RC Status: "+HTML_OF_PAGE.substring(RCActiveStart, RCActiveEnd));
    }

    public static void main(String args[]) throws Exception{

        Scanner sc=new Scanner(System.in);

        System.out.println("Enter the vehicle number: ");
        String veh=sc.nextLine();
        sc.close();

        OpenChrome vehicleQuery =  new OpenChrome();
    
        // Initialize browser
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        WebDriver driver=new FirefoxDriver(options);
        
        
        
        // Open my site
        driver.get("https://vahan.nic.in/nrservices/faces/user/searchstatus.xhtml");
        
        //Find elements



        WebElement vehicle_number=driver.findElement(By.id("regn_no1_exact"));
        WebElement captcha_image=driver.findElement(By.xpath("//div[@id=\"capatcha\"]/img"));
        WebElement captcha_text=driver.findElement(By.id("txt_ALPHA_NUMERIC"));

        //Download the captcha image
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor)driver;
        String captcha_image_base64=(String)javascriptExecutor.executeAsyncScript("var ele = arguments[0], callback = arguments[1];"+
        "ele.addEventListener('load', function fn(){"+
        "ele.removeEventListener('load', fn, false);"+
        "var cnv = document.createElement('canvas');"+
        "cnv.width = this.width; cnv.height = this.height;"+
        "cnv.getContext('2d').drawImage(this, 0, 0);"+
        "callback(cnv.toDataURL('image/jpeg').substring(22));"+
        "}, false);"+
        "ele.dispatchEvent(new Event('load'));", captcha_image);

        String base64Image = captcha_image_base64.split(",")[1];
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        File outputfile = new File("captcha.jpg");
        ImageIO.write(img, "jpg", outputfile);


        //Send the keys
        vehicle_number.sendKeys(veh);

        startMakingABetterImage();

        ProcessBuilder pb = new ProcessBuilder("python3", "ocr.py");
        Process p = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String captchaText=br.readLine();
        ImprovingCaptcha improvingCaptcha = new ImprovingCaptcha();
        captchaText=improvingCaptcha.improve(captchaText);
        System.out.println("Captcha inserted: "+captchaText);


        captcha_text.sendKeys(captchaText);

        //click to get data
        vehicleQuery.getResult(driver);

        //Response time of the package is 1 second
        try{Thread.sleep(1000);}
        catch(Exception e){}

        //This is the site
        String HTML_OF_PAGE=driver.getPageSource();

        //System.out.println(HTML_OF_PAGE);
        vehicleQuery.getALLData(HTML_OF_PAGE);
        // Close browser
        driver.close();
    }
}
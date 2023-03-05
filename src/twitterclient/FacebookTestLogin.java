/*
 *  This class connects to facebook account and provides a share function
 */
package twitterclient;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.restfb.Parameter;
import com.restfb.Version;
import static com.restfb.Version.LATEST;
import com.restfb.types.FacebookType;
import java.util.ArrayList;
import javax.swing.JLabel;
/**
 *
 * @author Caleb Norris
 */
public class FacebookTestLogin
{
    private final String FB_APP_ID = "2476456445914876";
    private final String FB_APP_SECRET = "7da5032621c7b28d8a733268975e7c0d";
    private final String REDIRECT_URI = "http://localhost:8080/Facebook_Login"
            + "/fbhome";
    private String usersName;
    private final String accessToken;
    private final ArrayList<JLabel> userLabels;
    private User me;
    private FacebookClient fbClient;
    private Connection<Post> myFeed;
    private final String SUCCESS_URL = "https://www.facebook.com/connect/"
            + "login_success.html";
    private String code;
    private AccessToken token;
    private FacebookClient startClient;
    
    public FacebookTestLogin()
    {
        accessToken = "EAAjMUvLCrvwBAJtC0IKTRFRwHi5WjRFTofAinhAsZCOZAXEheej1WbT"
                    + "WUTveZC7JzjgDzjpOtSikQtXlelEHqbm6nA2MBqjwj5UaZAg2mhllVUG"
                    + "gk1IIvHB7ZATHsVIZAn9Ug0muORsnKJUYRdzk0Q8S1uCZAHJLu39PqwL"
                    + "54bgsb2lAhAhw5bZAZB20DYLqhGzsfsiLnmiaG4bkpdZAdWC4r5lOyIi"
                    + "ppajDsZD";
        userLabels = new ArrayList<>();
        startClient = new DefaultFacebookClient(Version.LATEST);
    }
    
    /*
        Creates webpage popup to ask for facebook login.
        Provides access token for sharing.
    */
    public void loginPlease() throws UnsupportedEncodingException
    {
        String fbLoginUrl = "http://www.facebook.com/dialog/oauth?" 
                                        + "client_id="
					+ FB_APP_ID + "&redirect_uri="
					+ URLEncoder.encode(REDIRECT_URI,
                                                "UTF-8")
					+ "&scope=email, user_likes, user_link"
                                        + ", user_photos"
                                        + ",user_posts, user_status, "
                                        + "user_videos"
                                        + ", publish_to_groups, manage_pages, "
                                        + "publish_pages, pages_messaging"
                                        + ", pages_manage_cta"
                                        + ", pages_show_list";
        
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
        driver.get(fbLoginUrl);
        boolean test = true;
        while(test == true)
        {
            if(!driver.getCurrentUrl().contains("facebook.com"))
            {
                String url = driver.getCurrentUrl();
                if (url.contains("localhost:8080/Facebook_Login/fbhome?")) 
                {
                    int pos = url.indexOf("code=");
                    code = url.substring(pos + "code=".length());
                    System.out.println(code);
                    token = startClient.obtainUserAccessToken(FB_APP_ID,
                          FB_APP_SECRET, REDIRECT_URI, code);
                    
                    test = false;
                    driver.quit();
                }
                else
                {
                //if (url.startsWith(SUCCESS_URL)) 
                    
                    
                }
                
                
                fbClient = new DefaultFacebookClient(token.getAccessToken(),
                        LATEST);
                
                //fbClient = new DefaultFacebookClient(accessToken, LATEST);
             
                me = fbClient.fetchObject("me", User.class, Parameter.with("fields", "accounts, name, id"));
                
                usersName = me.getName();
            }
        }
    }
    
    /*
        Testing method for getting stuff from api. No real function at the moment
    */
    public void facebookStuff()
    {
        //Page page = fbClient.fetchObject("me", Page.class);
        
        //Post post = fbClient.fetchObject("me", Post.class);
        
        myFeed = fbClient.fetchConnection("me/feed", Post.class, 
                Parameter.with("fields", "from, message"));
        // Connections support paging and are iterable

        // Iterate over the feed to access the particular pages
        for (List<Post> myFeedPage : myFeed) 
        {
            // Iterate over the list of contained data 
            // to access the individual object
            for (Post post : myFeedPage) 
            {
                String username = post.getFrom().getName();
            }
        }
        
        usersName = me.getName();
    }
    
    /*
        Creates the Facebook user labels to be displayed in the JFrame
    */
    public void facebookLabels()
    {
        for (List<Post> myFeedPage : myFeed) 
        {
            // Iterate over the list of contained data 
            // to access the individual object
            for (Post post : myFeedPage) 
            {
                String username = post.getFrom().getName();
                
                JLabel label = new JLabel(username + ":  " 
                        + post.getMessage());
                userLabels.add(label);
            }
        }
    }
    
    /*
        Returns the ArrayList of user labels
        @return Users feed in labels.
    */
    public ArrayList getUserLabels()
    {
        return userLabels;
    }
    
    /*
        Returns the username of this object's account.
    */
    public String getUsername()
    {
        return usersName;
    }
    
    /*
        Testing method for posting to facebook.
    */
    public void makePostTest(String post)
    {
        //fbClient.publish(usersName + "/feed", Post.class,
        //   Parameter.with("message", post));
        
        
        Page page = fbClient.fetchObject("me/feed", Page.class, Parameter.with("field", "id"));
        
        FacebookClient fbPageClient = new DefaultFacebookClient("EAAjMUvLCrvwBAIEWHnxmar6MZC20cWY9QsZCm5pKOwKsncqLaD3ONquGYP30ZBcKLFHkrd1A3Npq4ZBFE9HOPy4kFihfnbXxToxGr6jgrLAcMYxdDdTEezFrMPDt1FhdDj5E1I4OFV4OKhRRHitZBpaZCWm7ZCd7ZCfzzQOy0h3nyDtprnByoAdiqPV7MGd4005I6FLGORnCKXQPVxZCVF4AK",
                        LATEST);
        fbPageClient.publish("2313447525611582/feed", FacebookType.class,
            Parameter.with("message", post));
        
        Post post1 = fbClient.fetchObject("me", Post.class);
        post1.setMessage(post);
        //FacebookType response = fbClient.publish(page.getId(), FacebookType.class, Parameter.with("message", post));
        //System.out.println("fb.com/" + response.getId());
        
    }
    
    /*
        Creates popup of facebook share dialog to post to facebook.
    */
    public void makePost()
    {
        String postDialog = "https://www.facebook.com/dialog/feed?app_"
                        + "id=2476456445914876&display=popup&link=https%3A%2F%2"
                        + "Fdevelopers.facebook.com%2Fdocs%2F&redirect_uri=htt"
                        + "ps%3A%2F%2Flocalhost%2F&ext=1556332366&hash=AeYil7m"
                        + "CZM2cQviO";
        
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
        driver.get(postDialog);
        boolean test = true;
        while(test == true)
        {
            if(!driver.getCurrentUrl().contains("facebook.com"))
            {
                driver.quit();
            }
        }
        
    }
}

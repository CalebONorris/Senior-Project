/*
 * Provides login for Twitter interacting with Twitter api.
 */
package twitterclient;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Caleb Norris
 */
public class TwitterLogIn 
{
    private TwitterFactory tf1;
    private Twitter twitter1;
    private RequestToken requestToken;
    private String pin;
    private AccessToken accessToken;
    private List<Status> status1;
    private List<Status> status2;
    private BufferedReader br;
    private MediaEntity[] media;
    private URL url2;
    private Image newImage;
    private ArrayList<JLabel> userLabels;
    private ArrayList<JLabel> homeLabels;
    private ArrayList<JButton> likeButtons;
    private ArrayList<JButton> retweetButtons;
    
    /*
        Constructor and initializes Arrays.
    */
    public TwitterLogIn()
    {
        userLabels = new ArrayList<>();
        homeLabels = new ArrayList<>();
        likeButtons = new ArrayList<>();
        retweetButtons = new ArrayList<>();

    }

    /*
        Handles authentication of user by directing the user to twitter pin 
        webpage
        and getting access tokens.
    */
    public void auth()
    {
        try
        {

            ConfigurationBuilder cf1 = new ConfigurationBuilder();
            cf1.setDebugEnabled(true)
                    .setOAuthConsumerKey("gtl9EuM7eaGHUlnmBITnpNVBD")
                    .setOAuthConsumerSecret("OAnZsRGY5561FHhqd5DGW59s97iL6ViD"
                            + "iOK7BFEp4caPKciYyP");
                    //.setOAuthAccessToken("User's Access Token")
                    //.setOAuthAccessTokenSecret("User's Token Secret");


            tf1 = new TwitterFactory(cf1.build());
            twitter1 = tf1.getInstance();

            br = new BufferedReader(new InputStreamReader(System.in));
            requestToken = twitter1.getOAuthRequestToken("oob");
            String url = requestToken.getAuthenticationURL();
            if(Desktop.isDesktopSupported())
            {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    // TODO Auto-generated catch block

                }
            }
            else
            {
                Runtime runtime = Runtime.getRuntime();
                try 
                {
                    runtime.exec("xdg-open " + url);
                }
                catch (IOException e) 
                {
                    // TODO Auto-generated catch block

                }
            }

            pin = JOptionPane.showInputDialog("Please input given Pin #: ");

            accessToken = twitter1.getOAuthAccessToken(requestToken, pin);

            twitter1.verifyCredentials();
            JOptionPane.showMessageDialog(null, "You're logged in!");

            setStatus();



        }
        catch (TwitterException e)
        {
            JOptionPane.showMessageDialog(null, "Login Failed");
        }
    }
    
    /*
        Sets the arrays for getting user/home timelines.
    */
    public void setStatus() throws TwitterException
    {
        status1 = twitter1.getUserTimeline();
        status2 = twitter1.getHomeTimeline();
    }

    /*
        Creates labels to be used by TwitterJFrame to display user and home
        feeds.
        Creates buttons for liking and retweeting status's found in the home 
        feed,
        which are also displayed by TwitterJFrame.
    */
    public void createStatusLabels() throws IOException
    {
        userLabels.clear();
        homeLabels.clear();
        likeButtons.clear();
        retweetButtons.clear();
        for(Status st1: status1)
        {

            media = st1.getMediaEntities();
            JLabel label = new JLabel(getPostText(st1));
            for(MediaEntity med: media)
            {
                url2 = new URL(med.getMediaURL());

                BufferedImage image = ImageIO.read(url2);
                newImage = image.getScaledInstance(150, 150, java.awt.
                        Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(newImage));
            }
            label.setSize(500,150);
            label.setVerticalTextPosition(JLabel.TOP);
            label.setHorizontalTextPosition(JLabel.RIGHT);
            //JButton button = new JButton("Like");
            userLabels.add(label);
            //FeedText2.add(button);  
        }

        for(Status st2: status2)
        {
            media = st2.getMediaEntities();
            JLabel label = new JLabel(getPostText(st2));
            for(MediaEntity med: media)
            {
                url2 = new URL(med.getMediaURL());

                BufferedImage image = ImageIO.read(url2);
                newImage = image.getScaledInstance(150, 150, java.awt.
                        Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(newImage));

            }
            label.setVerticalTextPosition(JLabel.TOP);
            label.setHorizontalTextPosition(JLabel.RIGHT);
            homeLabels.add(label);
            JButton button = new JButton("Like");
            likeButtons.add(button);
            JButton button2 = new JButton("Retweet");
            
            retweetButtons.add(button2);
        }
    }

    /*
        Returns the array of user feed labels.
    */
    public ArrayList getUserLabels()
    {
        return userLabels;
    }

    /*
        Returns the array of home feed labels.
    */
    public ArrayList getHomeLabels()
    {
        return homeLabels;
    }

    /*
        Returns the array of like buttons
    */
    public ArrayList getLikeButtons()
    {
        return likeButtons;
    }

    /*
        Returns the array of retweet buttons
    */
    public ArrayList getRetweetButtons()
    {
        return retweetButtons;
    }

    /*
        Likes status of given id
    */
    public void createLike(long id) throws TwitterException
    {
        twitter1.createFavorite(id);
    }

    /*
        Returns the text of a given status and the name of the user who posted
        it.
    */
    public String getPostText(Status st)
    {
        return (" " + st.getUser().getName()+ ": " + st.getText());
    }

    /*
        Returns list of user feed status's
    */
    public List<Status> getStatus1()
    {
        return status1;
    }

    /*
        Returns list of home feed status's
    */
    public List<Status> getStatus2()
    {
        return status2;
    }

    /*
        Returns the screenname of the user associated with the current 
        TwitterLogIn object.
    */
    public String getUsername() throws TwitterException
    {
        return accessToken.getScreenName();
    }
    
    /*
        Makes a post with current object's user's account using given string
    */
    public void makePost(String str) throws TwitterException
    {
        twitter1.updateStatus(str);
    }

    /*
        Retweets a status of given id with this current object's user's account
    */
    public void retweet(Long id) throws TwitterException
    {
        twitter1.retweetStatus(id);
    }

    /*
        Checks if a status has already been retweeted.
    */
    public boolean checkRetweeted(Status status)
    {
        return status.isRetweetedByMe();
    }
    
    /*
        Used to unlike posts.
    */
    public void unlikePost(long id) throws TwitterException
    {
        twitter1.destroyFavorite(id);
    }
}

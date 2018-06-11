package gov.healthit.chpl.scheduler.job;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import gov.healthit.chpl.auth.SendMailUtil;

/**
 * Job run by Scheduler to send email when the cache is "too old".
 * @author alarned
 *
 */
public class CacheStatusAgeJob implements Job {
    private static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
    private Properties properties;

    /**
     * Default constructor.
     * @throws IOException if unable to load properties
     */
    public CacheStatusAgeJob() throws IOException {
        if (properties == null || properties.isEmpty()) {
            InputStream in = CacheStatusAgeJob.class.getClassLoader()
                    .getResourceAsStream(DEFAULT_PROPERTIES_FILE);
            if (in == null) {
                properties = null;
                throw new FileNotFoundException("Environment Properties File not found in class path.");
            } else {
                properties = new Properties();
                properties.load(in);
                in.close();
            }
        }
    }

    /**
     * Main method. Checks to see if the cache is old, then, if it is,
     * sends email messages to subscribers of that notification.
     * @param jobContext for context of the job
     * @throws JobExecutionException if necessary
     */
    @Override
    public void execute(final JobExecutionContext jobContext) throws JobExecutionException {
        try {
            if (isCacheOld()) {
                String recipient = jobContext.getMergedJobDataMap().getString("email");
                try {
                    sendEmail(recipient);
                } catch (IOException | MessagingException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isCacheOld() throws UnsupportedEncodingException, IOException {
        Pattern agePattern = Pattern.compile("\"age\": (\\d*)");
        URL statusUrl = new URL(properties.getProperty("cacheStatusMaxAgeUrl"));
        InputStreamReader isr =  new InputStreamReader(statusUrl.openStream(), "UTF-8");
        BufferedReader in = new BufferedReader(isr);

        try {
            String status = in.readLine();
            in.close();
            isr.close();
            if (status != null && !status.contains("\"status\": \"OK\"")) {
                return false;
            }
            Matcher ageMatcher = agePattern.matcher(status);
            if (!ageMatcher.find()) {
                return false;
            }
            long age = Long.parseLong(ageMatcher.group(1));
            return (age > Long.parseLong(properties.getProperty("cacheStatusMaxAge")));
        } finally {
            in.close();
            isr.close();
        }
    }

    private void sendEmail(final String recipient)
            throws IOException, AddressException, MessagingException {

        String subject = properties.getProperty("cacheStatusMaxAgeSubject");
        String htmlMessage = createHtmlEmailBody();

        SendMailUtil mailUtil = new SendMailUtil();
        mailUtil.sendEmail(recipient, subject, htmlMessage, null, properties);
    }

    private String createHtmlEmailBody() {
        String htmlMessage = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@<br/>"
                + "@ WARNING: CACHE IS NOT REFRESHING!<br/>"
                + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@<br/>"
                + "IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!<br/>"
                + "Someone could be eavesdropping on you right now (man-in-the-middle attack)!<br/>"
                + "It is also possible that an ACB just screwed something up somewhere "
                + "that we didn't catch. ~\\(-.-)/~</pre>";
        return htmlMessage;
    }
}

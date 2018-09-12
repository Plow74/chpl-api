package gov.healthit.chpl.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import org.springframework.core.env.Environment;

public class EmailOverrider {
    private Environment env;
    
    public EmailOverrider(Environment env) {
        this.env = env;
    }
    
    public Address[] getRecipients(List<String> toAddresses) throws MessagingException {
        List<Address> addresses = new ArrayList<Address>();
        if (shouldEmailBeRedirected(toAddresses)) {
            Address address = new InternetAddress(getForwardToEmail());
            addresses.add(address);
        } else {
            for (String addr : toAddresses) {
                Address address = new InternetAddress(addr);
                addresses.add(address);
            }
        }
        Address[] addressArr = new Address[addresses.size()];
        return addresses.toArray(addressArr);
    }
    
    public BodyPart getBody(String htmlBody, List<String> toAddresses) throws MessagingException {
        StringBuffer message = new StringBuffer();
        
        if (shouldEmailBeRedirected(toAddresses)) {
            message.append("<b>");
            message.append("The intended recipients: ");
            message.append(getToAddressesAsString(toAddresses));
            message.append("</b>");
            message.append("<br/><br/>");
        }
        message.append(htmlBody);
        BodyPart messageBodyPartWithMessage = new MimeBodyPart();
        messageBodyPartWithMessage.setContent(message.toString(), "text/html");
        return messageBodyPartWithMessage;
    }
    
    private String getToAddressesAsString(List<String> toAddresses) {
        String addresses = "";
        for (String address : toAddresses) {
            if (!addresses.equals("")) {
                addresses += ", ";
            }
            addresses += address;
            
        }
        return addresses;
    }
    
    private Boolean shouldEmailBeRedirected(List<String> toAddresses) throws MessagingException {
        //ASSUMPTION:
        //If any of the recipients are not in the whitelisted domain, we are going to redirect the email.
        
        if (isNonProductionEmailEnvironment()) {
            List<String> whitelistedDomains = getWhitelistedDomains();
            for (String address : toAddresses) {
                if (!whitelistedDomains.contains(getEmailDomain(address))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private List<String> getWhitelistedDomains() {
        List<String> domains = new ArrayList<String>();
        String domainList =  env.getProperty("emailNonProductionEnvironmentWhitelist");
        if (domainList != null) {
            domains.addAll(Arrays.asList(domainList.split(",")));
        }
        return domains;
    }
    
    private Boolean isNonProductionEmailEnvironment() {
        String isNonProductionEmailEnvironment = env.getProperty("emailNonProductionEnvironment");
        //We are going to assume that if the property was not found that we are in a PROD environment
        if (isNonProductionEmailEnvironment == null) {
            return false;
        } else {
            return Boolean.parseBoolean(isNonProductionEmailEnvironment);
        }
    }
    
    private String getEmailDomain(String someEmail)
    {
        return  someEmail.substring(someEmail.indexOf("@") + 1);
    }
    
    private String getForwardToEmail() {
        return env.getProperty("emailNonProductionEnvironmentForwardAddress");
    }
}

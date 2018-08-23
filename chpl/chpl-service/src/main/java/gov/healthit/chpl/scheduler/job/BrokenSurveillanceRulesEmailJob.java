package gov.healthit.chpl.scheduler.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;

import gov.healthit.chpl.auth.SendMailUtil;
import gov.healthit.chpl.dao.scheduler.BrokenSurveillanceRulesDAO;
import gov.healthit.chpl.domain.SurveillanceOversightRule;
import gov.healthit.chpl.dto.scheduler.BrokenSurveillanceRulesDTO;
import gov.healthit.chpl.scheduler.JobConfig;

/**
 * The BrokenSurveillanceRulesEmailJob implements a Quartz job and is available to ROLE_ADMIN and ROLE_ACB. When
 * invoked it emails relevant individuals with Surveillance error reports.
 * @author alarned
 *
 */
public class BrokenSurveillanceRulesEmailJob extends QuartzJob {
    private static final Logger LOGGER = LogManager.getLogger(BrokenSurveillanceRulesEmailJob.class);
    private static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
    private BrokenSurveillanceRulesDAO brokenSurveillanceRulesDAO;
    private DateTimeFormatter dateFormatter;
    private Properties props;
    private AbstractApplicationContext context;
    private Map<SurveillanceOversightRule, Integer> allBrokenRulesCounts;

    private static final String TRIGGER_DESCRIPTIONS = "<h4>Description of Surveillance Rules</h4>" + "<ol>" + "<li>"
            + SurveillanceOversightRule.LONG_SUSPENSION.getTitle() + ": "
            + SurveillanceOversightRule.LONG_SUSPENSION.getDescription() + "</li>" + "<li>"
            + SurveillanceOversightRule.CAP_NOT_APPROVED.getTitle() + ": "
            + SurveillanceOversightRule.CAP_NOT_APPROVED.getDescription() + "</li>" + "<li>"
            + SurveillanceOversightRule.CAP_NOT_STARTED.getTitle() + ": "
            + SurveillanceOversightRule.CAP_NOT_STARTED.getDescription() + "</li>" + "<li>"
            + SurveillanceOversightRule.CAP_NOT_COMPLETED.getTitle() + ": "
            + SurveillanceOversightRule.CAP_NOT_COMPLETED.getDescription() + "</li>" + "<li>"
            + SurveillanceOversightRule.CAP_NOT_CLOSED.getTitle() + ": "
            + SurveillanceOversightRule.CAP_NOT_CLOSED.getDescription() + "</li>" + "<li>"
            + SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE.getTitle() + ": "
            + SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE.getDescription() + "</li>" + "</ol>";

    /**
     * Constructor that initializes the BrokenSurveillanceRulesEmailJob object.
     * @throws Exception if thrown
     */
    public BrokenSurveillanceRulesEmailJob() throws Exception {
        super();
        setLocalContext();
        context = new AnnotationConfigApplicationContext(JobConfig.class);
        initiateSpringBeans(context);
        loadProperties();
        allBrokenRulesCounts = new HashMap<SurveillanceOversightRule, Integer>();
        allBrokenRulesCounts.put(SurveillanceOversightRule.LONG_SUSPENSION, 0);
        allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_APPROVED, 0);
        allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_STARTED, 0);
        allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_COMPLETED, 0);
        allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_CLOSED, 0);
        allBrokenRulesCounts.put(SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE, 0);
        dateFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd");
    }

    @Override
    public void execute(final JobExecutionContext jobContext) throws JobExecutionException {
        List<BrokenSurveillanceRulesDTO> errors = getAppropriateErrors(jobContext);
        String filename = null;
        if (jobContext.getMergedJobDataMap().getString("type").equalsIgnoreCase("All")) {
            filename = props.getProperty("oversightEmailWeeklyFileName");
        } else {
            filename = props.getProperty("oversightEmailDailyFileName");
        }
        File output = null;
        List<File> files = new ArrayList<File>();
        if (errors.size() > 0) {
            output = getOutputFile(errors, filename);
            files.add(output);
        }
        String to = jobContext.getMergedJobDataMap().getString("email");
        String subject = null;
        String htmlMessage = null;
        if (jobContext.getMergedJobDataMap().getString("type").equalsIgnoreCase("All")) {
            subject = props.getProperty("oversightEmailWeeklySubject");
            if (jobContext.getMergedJobDataMap().getBoolean("acbSpecific")) {
                subject = jobContext.getMergedJobDataMap().getString("acb").replaceAll("\u263A", ", ") + " " + subject;
                htmlMessage = props.getProperty("oversightEmailAcbWeeklyHtmlMessage");
            } else {
                htmlMessage = props.getProperty("oversightEmailWeeklyHtmlMessage");
            }
            htmlMessage += createHtmlEmailBody(errors.size(), props.getProperty("oversightEmailWeeklyNoContent"));
        } else {
            subject = props.getProperty("oversightEmailDailySubject");
            if (jobContext.getMergedJobDataMap().getBoolean("acbSpecific")) {
                subject = jobContext.getMergedJobDataMap().getString("acb").replaceAll("\u263A", ", ") + " " + subject;
                htmlMessage = props.getProperty("oversightEmailAcbDailyHtmlMessage");
            } else {
                htmlMessage = props.getProperty("oversightEmailDailyHtmlMessage");
            }
            htmlMessage += createHtmlEmailBody(errors.size(), props.getProperty("oversightEmailDailyNoContent"));
        }
        LOGGER.info("Sending email to {} with contents {} and a total of {} broken rules",
                to, htmlMessage, errors.size());
        SendMailUtil mailUtil = new SendMailUtil();
        try {
            mailUtil.sendEmail(to, subject, htmlMessage, files, props);
        } catch (MessagingException e) {
            LOGGER.error(e);
        } finally {
            context.close();
        }
    }

    private List<BrokenSurveillanceRulesDTO> getAppropriateErrors(final JobExecutionContext jobContext) {
        List<BrokenSurveillanceRulesDTO> allErrors = brokenSurveillanceRulesDAO.findAll();
        List<BrokenSurveillanceRulesDTO> filteredErrors = new ArrayList<BrokenSurveillanceRulesDTO>();
        List<BrokenSurveillanceRulesDTO> errors = new ArrayList<BrokenSurveillanceRulesDTO>();
        if (jobContext.getMergedJobDataMap().getString("type").equalsIgnoreCase("Overnight")) {
            Date today = new Date();
            LocalDateTime brokenToday = LocalDateTime.ofInstant(Instant.ofEpochMilli(today.getTime()),
                    ZoneId.systemDefault());
            String formattedToday = dateFormatter.format(brokenToday);
            for (BrokenSurveillanceRulesDTO error : allErrors) {
                if (happenedOvernight(error, formattedToday)) {
                    filteredErrors.add(error);
                }
            }
        } else {
            filteredErrors.addAll(allErrors);
        }
        if (jobContext.getMergedJobDataMap().getBooleanValue("acbSpecific")) {
            for (BrokenSurveillanceRulesDTO error : filteredErrors) {
                if (jobContext.getMergedJobDataMap().getString("acb").indexOf(error.getAcb()) > -1) {
                    errors.add(error);
                }
            }
        } else {
            errors.addAll(filteredErrors);
        }
        return errors;
    }

    private boolean happenedOvernight(final BrokenSurveillanceRulesDTO error, final String formattedToday) {
        if (formattedToday.equalsIgnoreCase(error.getLengthySuspensionRule())) {
            return true;
        }
        if (formattedToday.equalsIgnoreCase(error.getCapNotApprovedRule())) {
            return true;
        }
        if (formattedToday.equalsIgnoreCase(error.getCapNotStartedRule())) {
            return true;
        }
        if (formattedToday.equalsIgnoreCase(error.getCapNotCompletedRule())) {
            return true;
        }
        if (formattedToday.equalsIgnoreCase(error.getCapNotClosedRule())) {
            return true;
        }
        if (formattedToday.equalsIgnoreCase(error.getClosedCapWithOpenNonconformityRule())) {
            return true;
        }
        return false;
    }

    private File getOutputFile(final List<BrokenSurveillanceRulesDTO> errors, final String reportFilename) {
        File temp = null;
        OutputStreamWriter writer = null;
        CSVPrinter csvPrinter = null;
        try {
            temp = File.createTempFile(reportFilename, ".csv");
            temp.deleteOnExit();
            writer = new OutputStreamWriter(
                    new FileOutputStream(temp),
                    Charset.forName("UTF-8").newEncoder()
                    );
            csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL);
            csvPrinter.printRecord(getHeaderRow());
            for (BrokenSurveillanceRulesDTO error : errors) {
                List<String> rowValue = generateRowValue(error);
                csvPrinter.printRecord(rowValue);
                updateSummary(error);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (csvPrinter != null) {
                    csvPrinter.flush();
                    csvPrinter.close();
                }
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        return temp;
    }

    private List<String> getHeaderRow() {
        List<String> result = new ArrayList<String>();
        result.add("Developer");
        result.add("Product");
        result.add("Version");
        result.add("CHPL ID");
        result.add("URL");
        result.add("ONC-ACB");
        result.add("Certification Status");
        result.add("Date of Last Status Change");
        result.add("Surveillance ID");
        result.add("Date Surveillance Began");
        result.add("Date Surveillance Ended");
        result.add("Surveillance Type");
        result.add("Lengthy Suspension Rule");
        result.add("CAP Not Approved Rule");
        result.add("CAP Not Started Rule");
        result.add("CAP Not Completed Rule");
        result.add("CAP Not Closed Rule");
        result.add("Closed CAP with Open Nonconformity Rule");
        result.add("Non-conformity (Y/N)");
        result.add("Nonconformity Status");
        result.add("Non-conformity Criteria");
        result.add("Date of Determination of Non-Conformity");
        result.add("Corrective Action Plan Approved Date");
        result.add("Date Corrective Action Began");
        result.add("Date Corrective Action Must Be Completed");
        result.add("Date Corrective Action Was Completed");
        result.add("Number of Days from Determination to CAP Approval");
        result.add("Number of Days from Determination to Present");
        result.add("Number of Days from CAP Approval to CAP Began");
        result.add("Number of Days from CAP Approval to Present");
        result.add("Number of Days from CAP Began to CAP Completed");
        result.add("Number of Days from CAP Began to Present");
        result.add("Difference from CAP Completed and CAP Must Be Completed");
        return result;
    }

    private List<String> generateRowValue(final BrokenSurveillanceRulesDTO data) {
        List<String> result = new ArrayList<String>();
        result.add(data.getDeveloper());
        result.add(data.getProduct());
        result.add(data.getVersion());
        result.add(data.getChplProductNumber());
        result.add(data.getUrl());
        result.add(data.getAcb());
        result.add(data.getCertificationStatus());
        result.add(data.getDateOfLastStatusChange());
        result.add(data.getSurveillanceId());
        result.add(data.getDateSurveillanceBegan());
        result.add(data.getDateSurveillanceEnded());
        result.add(data.getSurveillanceType());
        result.add(data.getLengthySuspensionRule());
        result.add(data.getCapNotApprovedRule());
        result.add(data.getCapNotStartedRule());
        result.add(data.getCapNotCompletedRule());
        result.add(data.getCapNotClosedRule());
        result.add(data.getClosedCapWithOpenNonconformityRule());
        result.add(data.getNonconformity() ? "Y" : "N");
        result.add(data.getNonconformityStatus());
        result.add(data.getNonconformityCriteria());
        result.add(data.getDateOfDeterminationOfNonconformity());
        result.add(data.getCorrectiveActionPlanApprovedDate());
        result.add(data.getDateCorrectiveActionBegan());
        result.add(data.getDateCorrectiveActionMustBeCompleted());
        result.add(data.getDateCorrectiveActionWasCompleted());
        result.add(data.getNumberOfDaysFromDeterminationToCapApproval() > Long.MIN_VALUE
                ? "" + data.getNumberOfDaysFromDeterminationToCapApproval() : "");
        result.add(data.getNumberOfDaysFromDeterminationToPresent() > Long.MIN_VALUE
                ? "" + data.getNumberOfDaysFromDeterminationToPresent() : "");
        result.add(data.getNumberOfDaysFromCapApprovalToCapBegan() > Long.MIN_VALUE
                ? "" + data.getNumberOfDaysFromCapApprovalToCapBegan() : "");
        result.add(data.getNumberOfDaysFromCapApprovalToPresent() > Long.MIN_VALUE
                ? "" + data.getNumberOfDaysFromCapApprovalToPresent() : "");
        result.add(data.getNumberOfDaysFromCapBeganToCapCompleted() > Long.MIN_VALUE
                ? "" + data.getNumberOfDaysFromCapBeganToCapCompleted() : "");
        result.add(data.getNumberOfDaysFromCapBeganToPresent() > Long.MIN_VALUE
                ? "" + data.getNumberOfDaysFromCapBeganToPresent() : "");
        result.add(data.getDifferenceFromCapCompletedAndCapMustBeCompleted() > Long.MIN_VALUE
                ? "" + data.getDifferenceFromCapCompletedAndCapMustBeCompleted() : "N/A");

        return result;
    }

    private void updateSummary(final BrokenSurveillanceRulesDTO data) {
        if (!StringUtils.isEmpty(data.getLengthySuspensionRule())) {
            allBrokenRulesCounts.put(SurveillanceOversightRule.LONG_SUSPENSION,
                    allBrokenRulesCounts.get(SurveillanceOversightRule.LONG_SUSPENSION) + 1);
        }
        if (!StringUtils.isEmpty(data.getCapNotApprovedRule())) {
            allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_APPROVED,
                    allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_APPROVED) + 1);
        }
        if (!StringUtils.isEmpty(data.getCapNotStartedRule())) {
            allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_STARTED,
                    allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_STARTED) + 1);
        }
        if (!StringUtils.isEmpty(data.getCapNotCompletedRule())) {
            allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_COMPLETED,
                    allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_COMPLETED) + 1);
        }
        if (!StringUtils.isEmpty(data.getCapNotClosedRule())) {
            allBrokenRulesCounts.put(SurveillanceOversightRule.CAP_NOT_CLOSED,
                    allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_CLOSED) + 1);
        }
        if (!StringUtils.isEmpty(data.getClosedCapWithOpenNonconformityRule())) {
            allBrokenRulesCounts.put(SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE,
                    allBrokenRulesCounts.get(SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE) + 1);
        }
    }

    private String createHtmlEmailBody(final int numRecords, final String noContentMsg) {
        String htmlMessage = "";
        if (numRecords == 0) {
            htmlMessage = noContentMsg;
        } else {
            htmlMessage += "<ul>";
            htmlMessage += "<li>" + SurveillanceOversightRule.LONG_SUSPENSION.getTitle() + ": "
                    + allBrokenRulesCounts.get(SurveillanceOversightRule.LONG_SUSPENSION) + "</li>";
            htmlMessage += "<li>" + SurveillanceOversightRule.CAP_NOT_APPROVED.getTitle() + ": "
                    + allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_APPROVED) + "</li>";
            htmlMessage += "<li>" + SurveillanceOversightRule.CAP_NOT_STARTED.getTitle() + ": "
                    + allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_STARTED) + "</li>";
            htmlMessage += "<li>" + SurveillanceOversightRule.CAP_NOT_COMPLETED.getTitle() + ": "
                    + allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_COMPLETED) + "</li>";
            htmlMessage += "<li>" + SurveillanceOversightRule.CAP_NOT_CLOSED.getTitle() + ": "
                    + allBrokenRulesCounts.get(SurveillanceOversightRule.CAP_NOT_CLOSED) + "</li>";
            htmlMessage += "<li>" + SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE.getTitle() + ": "
                    + allBrokenRulesCounts.get(SurveillanceOversightRule.NONCONFORMITY_OPEN_CAP_COMPLETE) + "</li>";
            htmlMessage += "</ul>";
        }

        htmlMessage += TRIGGER_DESCRIPTIONS;
        return htmlMessage;
    }

    @Override
    protected void initiateSpringBeans(final AbstractApplicationContext context) throws IOException {
        setBrokenSurveillanceRulesDAO((BrokenSurveillanceRulesDAO) context.getBean("brokenSurveillanceRulesDAO"));
    }

    private Properties loadProperties() throws IOException {
        InputStream in =
                BrokenSurveillanceRulesEmailJob.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
        if (in == null) {
            props = null;
            throw new FileNotFoundException("Environment Properties File not found in class path.");
        } else {
            props = new Properties();
            props.load(in);
            in.close();
        }
        return props;
    }

    private void setBrokenSurveillanceRulesDAO(final BrokenSurveillanceRulesDAO brokenSurveillanceRulesDAO) {
        this.brokenSurveillanceRulesDAO = brokenSurveillanceRulesDAO;
    }
}

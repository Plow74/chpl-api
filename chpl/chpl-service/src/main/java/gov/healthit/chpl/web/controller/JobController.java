package gov.healthit.chpl.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.auth.dto.UserDTO;
import gov.healthit.chpl.auth.manager.UserManager;
import gov.healthit.chpl.domain.Job;
import gov.healthit.chpl.dto.job.JobDTO;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.manager.JobManager;
import gov.healthit.chpl.web.controller.results.JobResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "jobs")
@RestController
@RequestMapping("/jobs")
public class JobController {

    private static final Logger LOGGER = LogManager.getLogger(JobController.class);
    @Autowired
    JobManager jobManager;
    @Autowired
    UserManager userManager;

    @ApiOperation(value = "Get the list of all jobs currently running in the system and those"
            + "that have completed within a configurable amount of time (usually a short window like the last 7 days).")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody JobResults getAllJobs() throws EntityRetrievalException {
        List<JobDTO> jobDtos = new ArrayList<JobDTO>();
        if(Util.isUserRoleAdmin()) {
            jobDtos = jobManager.getAllJobs();
        } else {
            UserDTO currentUser = new UserDTO();
            currentUser.setId(Util.getCurrentUser().getId());
            try {
                jobDtos = jobManager.getJobsForUser(currentUser);
            } catch(EntityRetrievalException ex) {
                String msg = "Could not find jobs for user " + Util.getUsername();
                LOGGER.error(msg);
                throw new EntityRetrievalException(msg);
            }
        }

        List<Job> jobs = new ArrayList<Job>();
        for (JobDTO jobDto : jobDtos) {
            Job job = new Job(jobDto);
            jobs.add(job);
        }

        JobResults results = new JobResults();
        results.setResults(jobs);
        return results;
    }
}

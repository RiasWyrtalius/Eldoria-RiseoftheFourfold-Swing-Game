package Abilities;

import Abilities.Jobs.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JobFactory {
    private static final Map<String, JobClass>
        jobRegistry = new HashMap<>();

    // run once to populate the registry
    static {
        register(new AeroMancer());
        register(new Archer());
        register(new Cleric());
        register(new CryoMancer());
        register(new EarthMage());
        register(new FireMage());
        register(new Paladin());
        register(new Rogue());
        register(new Warrior());
    }

    private static void register(JobClass job) {
        jobRegistry.put(job.getName(), job);
//        job.registerAssets(); // preloading
    }

    /**
     * Retrieves job instance by its name
     * CASE SENSITIVE
     * @param jobName name of job e.g. "FireMage"
     * @return Job
     */
    public static JobClass getJob(String jobName) {
        if (jobName == null || jobName.isEmpty()) {
            return getDefaultJob();
        }
        return jobRegistry.get(jobName);
    }

    public static JobClass getDefaultJob() {
        return jobRegistry.get("Warrior");
    }

    public static Collection<JobClass> getAllJobs() {
        return jobRegistry.values();
    }
}

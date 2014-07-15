package net.gnomeffinway.depenizen.support;

import net.aufdemrand.denizen.objects.ObjectFetcher;
import net.gnomeffinway.depenizen.Depenizen;
import net.gnomeffinway.depenizen.commands.JobsCommands;
import net.gnomeffinway.depenizen.objects.dJob;
import net.gnomeffinway.depenizen.tags.JobsTags;
import org.bukkit.Bukkit;

public class JobsSupport {

    public Depenizen depenizen;

    public JobsSupport(Depenizen depenizen) {
        this.depenizen = depenizen;
    }

    public void register() {
        new JobsTags(depenizen);
        new JobsCommands().activate().as("jobs").withOptions("see documentation", 2);

        try {
            ObjectFetcher.registerWithObjectFetcher(dJob.class);
            ObjectFetcher._initialize();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Depenizen] Error loading Denizen ObjectFetcher. Jobs tags may not function correctly.");
        }
    }

}
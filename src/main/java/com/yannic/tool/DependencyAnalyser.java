package com.yannic.tool;

import java.util.logging.Logger;

/**
 * Created by yannic on 21/10/17.
 */
public class DependencyAnalyser {

    private final static Logger log = Logger.getLogger(DependencyAnalyser.class.getName());

    public void analyse(DependencyRepository projectRepo, DependencyRepository dependencies) {
        if (projectRepo.size() == 0) {
            log.warning("No project to analyze");
            return;
        }
        analyzeProjetRepo(projectRepo);
        analyzeDependencies(projectRepo, dependencies);
    }

    public void analyzeProjetRepo(DependencyRepository projectRepo) {
        boolean isSnapshotBundle = projectRepo.first().isSnapshot();
        String versionBundle = projectRepo.first().getVersion();
        for (Dependency project : projectRepo) {
            /**
             * Tous les projets sont en snapshot ou aucun ne l'est mais pas un mix des deux.
             */
            if (project.isSnapshot() != isSnapshotBundle) {
                if (isSnapshotBundle) {
                    log.severe("Snapshot bundle contains a non snapshot project: " + project + " at " + project.getLocation());
                } else {
                    log.severe("Reference bundle contains a snapshot project: " + project + " at " + project.getLocation());
                }
            }
            /**
             * Si l'ensemble est en snapshot, la version doit être consistante
             */
            if (isSnapshotBundle) {
                if (project.isSnapshot() && (!project.getVersion().equals(versionBundle))) {
                    log.severe("Project has not the correct snapshot version: " + project + " at " + project.getLocation() + ". Version should be " + versionBundle);
                }
            }
        }
    }

    public void analyzeDependencies(DependencyRepository projectRepo, DependencyRepository dependencies) {
        boolean isSnapshotBundle = projectRepo.first().isSnapshot();

        /**
         * Dans un bundle snapshot, vérifie si une dependence ne pointe pas vers la version snapshot du bundle
         */
        if (isSnapshotBundle) {
            for(Dependency dependency: dependencies) {
                String artefactName = dependency.getArtefactName();
                if (projectRepo.containsArtefact(artefactName)) {
                    if ((dependency.getVersion() != null) && (!dependency.getVersion().equals(projectRepo.getArtefact(artefactName).getVersion()))) {
                        log.severe("Unexpected dependency found " + dependency.toString() + " at " + dependency.getLocation() + ". Version is not correct. This project is opened with version " + projectRepo.getArtefact(artefactName).getVersion());
                    }
                }
            }
        }

        /**
         * Dans un bundle snapshot, vérifie si une dependence avec le snapshot du bundle pointerait vers un
         * projet non connu.
         */
        if (isSnapshotBundle) {
            String versionBundle = projectRepo.first().getVersion();
            for(Dependency dependency: dependencies) {
                if (dependency.getVersion() != null && dependency.getVersion().equals(versionBundle)) {
                    if (!projectRepo.containsArtefact(dependency.getArtefactName())) {
                        log.severe("Unexpected dependency found " + dependency.toString() + " at " + dependency.getLocation() +  ". Version is not correct. This project is closed.");
                    }
                }
            }
        }

    }
}

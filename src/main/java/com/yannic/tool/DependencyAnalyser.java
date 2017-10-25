package com.yannic.tool;

import com.sun.deploy.util.StringUtils;

import java.util.logging.Logger;

/**
 * Created by yannic on 21/10/17.
 */
public class DependencyAnalyser {

    private final static Logger log = Logger.getLogger(DependencyAnalyser.class.getName());

    public void analyse(DependencyRepository projectRepo, DependencyRepository dependencies, DependencyRepository ref) {
        if (projectRepo.size() == 0) {
            log.warning("No project to analyze");
            return;
        }
        precheckDependencies(projectRepo);
        precheckDependencies(dependencies);
        precheckDependencies(ref);
        analyzeProjetRepo(projectRepo);
        analyzeDependencies(projectRepo, dependencies);
        if (ref.size() == 0) {
            log.warning("Skipping dependency check with reference list");
            return;
        }
        analyseDependenciesWithRef(projectRepo, dependencies, ref);
    }

    protected void precheckDependencies(DependencyRepository repository) {
        for (Dependency dependency : repository) {
            precheckLabels(dependency);
        }
    }

    protected void precheckLabels(Dependency dependency) {
        if (isIllegalLabel(dependency.getGroupId())) {
            log.severe("Illegal groupId [" + dependency.getGroupId() + "] at " + dependency.getLocation() +  ".");
        }
        if (isIllegalLabel(dependency.getArtefactId())) {
            log.severe("Illegal artefactId [" + dependency.getArtefactId() + "] at " + dependency.getLocation() +  ".");
        }
        if (dependency.getVersion() != null && isIllegalLabel(dependency.getVersion())) {
            log.severe("Illegal version [" + dependency.getVersion() + "] at " + dependency.getLocation() +  ".");
        }
    }

    protected boolean isIllegalLabel(String label) {
        return (!label.replaceAll("[\\n\\t\\s]", "").equals(label));
    }

    private void analyseDependenciesWithRef(DependencyRepository projectRepo, DependencyRepository dependencies, DependencyRepository ref) {
        /**
         * Les versions de la liste de référence doivent être utilisées
         */
        boolean isSnapshotBundle = projectRepo.first().isSnapshot();
        String versionBundle = projectRepo.first().getVersion();
        if (isSnapshotBundle) {
            for (Dependency dependency : dependencies) {
                if (!dependency.isSnapshot()) {
                    if (ref.containsArtefact(dependency.getArtefactName())) {
                        Dependency r = ref.getArtefact(dependency.getArtefactName());
                        if (!r.getVersion().equals(dependency.getVersion())) {
                            log.severe("Unexpected dependency found " + dependency.toString() + " at " + dependency.getLocation() +  ". Version does not match reference");
                            log.info("Migration: " + dependency.toString() + "->" + r.getVersion());
                        }
                    }
                }
            }
        }

        /**
         * Dans un bundle snapshot, vérifie si une dependence avec le snapshot du bundle pointerait vers un non disponible en snapshot mais connu dans la liste de référence
         */
        if (isSnapshotBundle) {
            for(Dependency dependency: dependencies) {
                if (dependency.getVersion() != null && dependency.getVersion().equals(versionBundle)) {
                    if (!projectRepo.containsArtefact(dependency.getArtefactName())) {
                        Dependency r = ref.getArtefact(dependency.getArtefactName());
                        if (r != null) {
                            log.severe("Unexpected dependency found " + dependency.toString() + " at " + dependency.getLocation() +  ". Version is not correct. This project is closed.");
                            log.info("Migration: " + dependency.toString() + "->" + r.getVersion());
                        }
                    }
                }
            }
        }
    }

    public void analyzeProjetRepo(DependencyRepository projectRepo) {
        boolean isSnapshotBundle = projectRepo.first().isSnapshot();
        String versionBundle = projectRepo.first().getVersion();
        for (Dependency project : projectRepo) {
            /**
             * Tous les projets sont en snapshot ou aucun ne l'est mais pas un mix des deux.
             */
            if (project.getVersion() != null && project.isSnapshot() != isSnapshotBundle) {
                if (isSnapshotBundle) {
                    log.severe("Snapshot bundle contains a non snapshot project: " + project + " at " + project.getLocation());
                    log.info("Migration: " + project.toString() + "->" + versionBundle);
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
                    log.info("Migration: " + project.toString() + "->" + versionBundle);
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
                        log.info("Migration: " + dependency.toString() + "->" + projectRepo.getArtefact(artefactName).getVersion());
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

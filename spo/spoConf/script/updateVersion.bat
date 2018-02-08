echo 'version: '%1
mvn versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=%1

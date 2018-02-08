#!/bin/zsh
#скрипт подготовки релиза к отправке в банк.
#он не является платформонезависимым и независимым от компьютера
#возможно потом перепишу его на основе мавена
# @author Andrey Pavlenko drone@drone.ru
fmajor(){
    grep 'SNAPSHOT</version>' ./pom.xml | sed 's/.*<version>\(.*\)\..*-SNAPSHOT<.version>.*/\1/'
}
MAJOR=$(fmajor)
fminor(){
    grep 'SNAPSHOT</version>' ./pom.xml | sed 's/.*<version>.*\.\(.*\)-SNAPSHOT<.version>.*/\1/'
}
MINOR=$(fminor)
fnextminor(){
	((NMINOR = $(fminor) + 1))
	if [[ ${#NMINOR} -eq 1 ]]; then
        print '0'$NMINOR
    else
        print $NMINOR
    fi
}
NEXTMINOR=$(fnextminor)
echo 'будет отгружена версия'
echo 'major number: '$MAJOR
echo 'minor number: '$MINOR
echo 'next version: ' $MAJOR'.'$NEXTMINOR
echo ''
sleep 3
#получить последние версии из svn чтобы ничего не забыть
#если не нужно получать изменения, закоментировать
#считается что в транке лежит только стабильный код
git pull
#прописать стабильную версию
mvn versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=$MAJOR.$MINOR
#перекомпиляция с unit-тестами
mvn clean install package source:jar javadoc:jar deploy scm:tag
sleep 5
#залочить SQL скрипт
#svn lock spoConf/src/db/$MAJOR/SPO-$MAJOR.$MINOR.sql -m 'начинаем работать над версией '$MAJOR'.'$NEXTMINOR
#создать архив с ear, sql скриптами: текущим и old_scripts, changelog.txt
mkdir zip
mkdir zip/flexWorkflow-$MAJOR.$MINOR
mkdir zip//flexWorkflow-$MAJOR.$MINOR/old_scripts
cp spoConf/bin/changelog.txt zip/flexWorkflow-$MAJOR.$MINOR
cp flexWorkflowEAR/target/flexWorkflowEAR-$MAJOR.$MINOR.ear zip/flexWorkflow-$MAJOR.$MINOR
cp spoConf/src/db/$MAJOR/SPO-$MAJOR.$MINOR.sql zip/flexWorkflow-$MAJOR.$MINOR
cp -R spoConf/src/db/* zip/flexWorkflow-$MAJOR.$MINOR/old_scripts
cd zip
zip -r -9 zip.zip ./*
cd ..
#скопировать на диск X:
sudo cp zip/zip.zip /mnt/x/flexWorkflow/flexWorkflow-$MAJOR.$MINOR.zip
cp zip/zip.zip ~/Dropbox/md/flexWorkflow-$MAJOR.$MINOR.zip
rm -R zip
#создать новый SQL скрипт с той же мажорной версей
cp spoConf/src/db/userfull_scripts/template.sql spoConf/src/db/$MAJOR/SPO-$MAJOR.$NEXTMINOR.sql
git add spoConf/src/db/$MAJOR/SPO-$MAJOR.$NEXTMINOR.sql
#перейти на новую снапшотную версию
mvn versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=$MAJOR.$NEXTMINOR-SNAPSHOT
git add --all
git commit -m 'начинаем работать над версией '$MAJOR'.'$NEXTMINOR
#отправить письмо
subj='[СПО '$MAJOR.$MINOR']'
mutt -s $subj -i spoConf/bin/changelog.txt email_list@example.com


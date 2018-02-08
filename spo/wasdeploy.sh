#!/bin/zsh
#скрипт передеплоя на WAS сервер
#на моем ноутбуке
#он не является платформонезависимым и независимым от компьютера
#на его основе можно создать свой скрипт
# @author Andrey Pavlenko drone@drone.ru 2014
#перекомпиляция с unit-тестами
ant -f flexWorkflow/build.xml
mvn -U clean install package -P dev
#copy to tmp
cp flexWorkflowEAR/target/flexWorkflowEAR-*.ear /tmp/flexWorkflowEAR.ear
#redeploy
/opt/IBM/WebSphere/AppServer/profiles/AppSrv04/bin/wsadmin.sh -user adminwf -password adminwf -lang jython -f spoConf/src/jython/redeploySpo.py
rm /tmp/flexWorkflowEAR.ear
google-chrome "http://localhost:9080/ProdflexWorkflow/CreateApplication.jsp"
echo done

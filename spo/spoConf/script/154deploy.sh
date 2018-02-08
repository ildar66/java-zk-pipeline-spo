#!/bin/zsh
#скрипт передеплоя на WAS сервер
#с моего ноутбука на тестовый сервер
#он не является платформонезависимым и независимым от компьютера
#на его основе можно создать свой скрипт
# @author Andrey Pavlenko drone@drone.ru
#перекомпиляция с unit-тестами
ant -f flexWorkflow/build.xml
mvn -U clean install package -P dev
#copy to tmp
cp flexWorkflowEAR/target/flexWorkflowEAR*.ear /tmp/flexWorkflowEAR.ear
scp /tmp/flexWorkflowEAR.ear root@192.128.0.154:/root/spo/
rm /tmp/flexWorkflowEAR.ear
#redeploy
ssh was /root/spo/redeploy.sh
ssh was rm /root/spo/flexWorkflowEAR.ear

firefox "https://192.128.0.154:9443/ProdflexWorkflow/" &

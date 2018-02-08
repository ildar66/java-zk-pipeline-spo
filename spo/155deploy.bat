@echo off
echo ==== ant build
call ant -f flexWorkflow/build.xml
echo ==== mvn build
call mvn clean install package -P dev
ren flexWorkflowEAR\target\flexWorkflowEAR-*.ear flexWorkflowEAR.ear
move flexWorkflowEAR\target\flexWorkflowEAR.ear flexWorkflowEAR.ear
echo ==== redeploy

call C:\app\pscp.exe -pw pa#t13an flexWorkflowEAR.ear root@ci-ap.masterdm.ru:/root/spo/
del flexWorkflowEAR.ear
echo redeploy
call C:\app\plink.exe -load "ci-apavlenko" /root/spo/redeploy.sh
call C:\app\plink.exe -load "ci-apavlenko" rm /root/spo/flexWorkflowEAR.ear

call "C:\Program Files (x86)\Mozilla Firefox\firefox.exe" http://ci-ap.masterdm.ru:9080/ProdflexWorkflow/start.do

@echo off
echo set WAS_PORT=9080
echo set WAS_DIR=C:\IBM\WebSphere\AppServer\profiles\AppSrv01
echo ==== ant build
call ant -f flexWorkflow/build.xml
echo ==== mvn build
call mvn clean install package -P dev 
ren flexWorkflowEAR\target\flexWorkflowEAR-*.ear ear.ear
move flexWorkflowEAR\target\ear.ear c:\tmp\ear.ear
echo ==== redeploy
call "%WAS_DIR%\bin\wsadmin.bat" -user wasadmin -password wasadmin -lang jython -f spoConf/src/jython/redeploySpoWin.py
del c:\tmp\ear.ear
call "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" "http://localhost:%WAS_PORT%/ProdflexWorkflow/test.html"
echo ==== complete

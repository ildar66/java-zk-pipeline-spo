echo ==== ant build === oldstyle js minify
call ant -f flexWorkflow/build.xml
echo ==== webpack js files
call npm run build
echo ==== mvn build
call mvn clean install package -P dev

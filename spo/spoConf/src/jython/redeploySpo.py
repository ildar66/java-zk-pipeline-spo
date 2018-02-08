#скрипт передеплоя на WAS сервер
# @author Andrey Pavlenko drone@drone.ru
appManager = AdminControl.queryNames('type=ApplicationManager,*')
print appManager
#stop all
AdminControl.invoke(appManager, 'stopApplication', 'CompendiumEAR')
AdminControl.invoke(appManager, 'stopApplication', 'flexWorkflowEAR')
#update
AdminApp.update('flexWorkflowEAR','app','[-operation update -contents /tmp/flexWorkflowEAR.ear]')
AdminConfig.save()
#start all
AdminControl.invoke(appManager, 'startApplication', 'CompendiumEAR')
AdminControl.invoke(appManager, 'startApplication', 'flexWorkflowEAR')
#status
print AdminApp.isAppReady('flexWorkflowEAR')


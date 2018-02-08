#скрипт передеплоя на WAS сервер
# @author Andrey Pavlenko drone@drone.ru
appManager = AdminControl.queryNames('type=ApplicationManager,*')
print appManager
#stop all
AdminControl.invoke(appManager, 'stopApplication', 'CompendiumEAR')
AdminControl.invoke(appManager, 'stopApplication', 'flexWorkflowEAR')



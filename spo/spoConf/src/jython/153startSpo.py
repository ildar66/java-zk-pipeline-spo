#скрипт передеплоя на WAS сервер
# @author Andrey Pavlenko drone@drone.ru
appManager = AdminControl.queryNames('type=ApplicationManager,*')
print appManager
AdminControl.invoke(appManager, 'startApplication', 'flexWorkflowEAR')


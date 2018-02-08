#скрипт передеплоя на WAS сервер
# @author Andrey Pavlenko drone@drone.ru
appManager = AdminControl.queryNames('type=ApplicationManager,*')
print appManager
#update
AdminApp.update('flexWorkflowEAR','app','[-operation update -contents /tmp/flexWorkflowEAR.ear]')
AdminConfig.save()


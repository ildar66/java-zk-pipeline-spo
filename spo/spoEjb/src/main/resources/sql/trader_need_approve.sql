select distinct t.id_mdtask,pt.id_user,p.id_status,t.version from mdtask t 
inner join processes p on p.id_process=t.id_pup_process
inner join process_events e on e.id_process=t.id_pup_process and e.id_process_type_event=1
inner join project_team pt on pt.id_mdtask=t.id_mdtask and pt.teamtype='p'
inner join assign a on a.id_user_to=pt.id_user
inner join process_events ae on ae.id_process_event=a.id_process_event and ae.id_process=t.id_pup_process
inner join roles r on a.id_role=r.id_role
inner join users u on pt.id_user=u.id_user and u.is_active=1
inner join user_in_role ur on ur.id_role=r.id_role and ur.id_user=pt.id_user and ur.status='Y'
where (t.trader_approve is null or t.trader_approve='n')--Уведомление должно отправляться до первого нажатия кнопки «Подтверждено Трейдером».
and trader_approve_skip is null -- уведомления по заявкам, созданным ПОСЛЕ установки новой версии СПО 
and (p.id_status=1--Уведомления должны приходить по незавершенным и завершенным со статусом «Одобрено" в СПО версиям заявок.
or p.id_status=4 and t.statusreturn in (select s.fb_spo_return_id from crm_status_return s where s.status_type=1)) 
and NOT EXISTS(SELECT 1 FROM CED_COMMON_DEAL_CONCLUSION COM WHERE COM.TYPE = 'DEAL_PAYMENT' AND STATUS = 'PAYMENT_COMPLETED' --не одобрена в КОД
AND COM.ID_MDTASK in (select ced.id_mdtask from mdtask spo inner join mdtask ced on ced.mdtask_number=spo.mdtask_number and ced.version=spo.version where spo.id_mdtask=t.id_mdtask))
and e.date_event<(SYSDATE-30)--Если с даты создания заявки кнопка «Подтверждено Трейдером» не нажималась в течение 30 календарных дней
and r.name_role='Кредитный аналитик'--Кредитному аналитику, включенному в «Проектную команду» с признаком «Выполнения операции»
and t.version is not null
and t.tasktype='p'
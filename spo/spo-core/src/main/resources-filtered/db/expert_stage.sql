--liquibase formatted sql

--changeset apavlenko:spo-18.00-VTBSPO-954 logicalFilePath:spo-18.00-VTBSPO-954 endDelimiter:/ runOnChange:true
BEGIN
  PKG_DDL_UTILS.ADD_OBJECT('spo_expert_stage',
                           'CREATE TABLE spo_expert_stage (stage NVARCHAR2(256))');
END;
/
COMMENT ON TABLE spo_expert_stage IS 'название операций экспертиз'
/
delete from spo_expert_stage
/
insert into spo_expert_stage(stage) values('Проведение экспертизы безопасности')
/
insert into spo_expert_stage(stage) values('Утверждение результатов экспертизы руководителем подразделения по обеспечению безопасности')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы юридического подразделения (инвестиционные и финансовые операции)')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем юридического подразделения (инвестиционные и финансовые операции)')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы юридического подразделения (банковские операции)')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем юридического подразделения (банковские операции)')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделения по обеспечению безопасности')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по обеспечению безопасности')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделения по работе с залогами')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по работе с залогами')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы Казначейства')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем Казначейства')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы Центральной бухгалтерии')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем Центральной бухгалтерии')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделением по анализу рисков')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по анализу рисков')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделением целевых резервов')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения целевых резервов')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделением по анализу рыночных рисков')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по анализу рыночных рисков')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделения внутреннего контроля')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения внутреннего контроля')
/
insert into spo_expert_stage(stage) values('Экспертиза Юридического подразделения (инвестиционные и финансовые операции)')
/
insert into spo_expert_stage(stage) values('Экспертиза Юридического подразделения (банковские операции)')
/
insert into spo_expert_stage(stage) values('Экспертиза Подразделения по обеспечению безопасности')
/
insert into spo_expert_stage(stage) values('Экспертиза Подразделения по работе с залогами')
/
insert into spo_expert_stage(stage) values('Экспертиза подразделения по анализу рисков')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы безопасности в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение результатов экспертизы руководителем подразделения по обеспечению безопасности в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы юридического подразделения (инвестиционные и финансовые операции) в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем юридического подразделения (инвестиционные и финансовые операции) в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы юридического подразделения (банковские операции) в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем юридического подразделения (банковские операции) в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделения по обеспечению безопасности в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по обеспечению безопасности в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделения по работе с залогами в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по работе с залогами в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы Казначейства в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем Казначейства в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы Центральной бухгалтерии в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем Центральной бухгалтерии в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделением по анализу рисков в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по анализу рисков в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделением целевых резервов в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения целевых резервов в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделением по анализу рыночных рисков в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения по анализу рыночных рисков в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Проведение экспертизы подразделения внутреннего контроля в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение экспертизы руководителем подразделения внутреннего контроля в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Экспертиза Юридического подразделения (инвестиционные и финансовые операции) в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Экспертиза Юридического подразделения (банковские операции) в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Экспертиза Подразделения по обеспечению безопасности в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Экспертиза Подразделения по работе с залогами в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Экспертиза подразделения по анализу рисков в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Контроль технической исполнимости')
/
insert into spo_expert_stage(stage) values('Утверждение технической исполнимости')
/
insert into spo_expert_stage(stage) values('Контроль технической исполнимости в связи с изменениями')
/
insert into spo_expert_stage(stage) values('Утверждение технической исполнимости в связи с изменениями')
/

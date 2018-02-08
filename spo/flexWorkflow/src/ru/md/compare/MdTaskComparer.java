package ru.md.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vtb.util.EjbLocator;
import ru.md.domain.Withdraw;
import ru.md.helper.CompareHelper;
import ru.md.persistence.WithdrawMapper;
import ru.md.spo.dbobjects.IndrateMdtaskJPA;
import ru.md.spo.dbobjects.MdTaskTO;
import ru.md.spo.ejb.TaskFacadeLocal;

import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.model.ActionProcessorFactory;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

/**
 * Сравниватель заявки. До обновления и после.
 * Created by Andrey Pavlenko on 17.10.2016.
 */
public class MdTaskComparer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdTaskComparer.class.getName());
    private static final CompareTaskKeys.CompareTaskBlock[] sections = {CompareTaskKeys.CompareTaskBlock.PARAMETERS,
                                                                        CompareTaskKeys.CompareTaskBlock.PRICE_CONDITIONS,
                                                                        CompareTaskKeys.CompareTaskBlock.GRAPH,
                                                                        CompareTaskKeys.CompareTaskBlock.CONDITIONS,
                                                                        CompareTaskKeys.CompareTaskBlock.SUPPLY};

    private HashMap<CompareTaskKeys.CompareTaskBlock,ObjectElement> taskBeforeCO;
    private HashMap<CompareTaskKeys.CompareTaskBlock,Integer> hashBefore;
    private FloatPartOfActiveRate[] floatPartOfActiveRate;

    public MdTaskComparer(MdTaskTO taskBerore) {
        taskBeforeCO = new HashMap<CompareTaskKeys.CompareTaskBlock, ObjectElement>();
        hashBefore = new HashMap<CompareTaskKeys.CompareTaskBlock, Integer>();
        CompendiumCrmActionProcessor compendiumCrm = (CompendiumCrmActionProcessor) ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        floatPartOfActiveRate = compendiumCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(), null);
        for (CompareTaskKeys.CompareTaskBlock block : sections)
            this.taskBeforeCO.put(block, toCompareObject(taskBerore, block));
        for (CompareTaskKeys.CompareTaskBlock block : sections)//ещё сохранить хешкоды отдельно по секциям
            this.hashBefore.put(block, sectionHashCode(taskBerore, block));
        for (CompareTaskKeys.CompareTaskBlock block : sections)
            LOGGER.info("hash " + block.getDescription() + " = " + this.hashBefore.get(block));
    }

    /**
     * Возвращает список секций, которые отличаются.
     */
    public String[] getSectionDiff(MdTaskTO t2) {
        List<String> res = new ArrayList<String>();
        for (CompareTaskKeys.CompareTaskBlock block : sections)
            if (isDiff(t2, block))
                res.add(block.getDescription());

        String[] resArr = new String[res.size()];
        resArr = res.toArray(resArr);
        return resArr;
    }

    //хешкоды отдельно по секциям
    private Integer sectionHashCode(MdTaskTO to, CompareTaskKeys.CompareTaskBlock block) {
//        4. в секции Стоимостные условия в сделке в целом/если есть периоды, то после добавления нового поля набора индикативных ставок, если добавить/удалить/изменить значение поле "% годовых", "Применяется с". "Основание"
//        5. в периоде, если тип ставки = "фиксированная"и "плавающая", то в поле "Ставка размещения", если добавить/изменить/удалить значения в наборе из полей "Применяется с", "Основание";
        if(block == CompareTaskKeys.CompareTaskBlock.PRICE_CONDITIONS){
            int result = 143;
            for(IndrateMdtaskJPA i : to.jpa.getIndrates())
                result = result * 31 + i.hashCode();
            for(ru.md.spo.dbobjects.FactPercentJPA per : to.jpa.getFactPercents())
                result = result * 31 + per.hashCode();
            return result;
        }
//        1. в секции Основные параметры Сделки/Целевое назначение, если добавить/удалить атрибут "Запрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц)" из справочника значение и сохранить;
//        2. в секции Основные параметры Сделки, если добавить/удалить в разделе "Контроль целевого использования" следующие поля:
//        а) назначение;
//        б) сумма и валюта;
//        в) комментарий
//        г) комментарий к общему блоку
//        3. в секции Основные параметры Сделки/График использования, если отжать/нажать чекбоксы:
//        а) График использования траншей;
//        б) Допускается использование недоиспользованного лимита ;
//        в) Допускается превышение лимита по графику;
//        г) Жесткий график;
//        а также, если отжата чекбокс "График использования траншей заполнить/удалить", то следующие поля:
//        д) сумма выдачи - учитывать все поля, не рассылается при любом формате поля "Формат периода предоставления";
//        е) период предоставления - учитывать все поля, не рассылается при любом формате поля "Формат периода предоставления";
//        если нажата чекбокс "График использования траншей заполнить/удалить", то следующие поля:
//        а) в колонке "Сумма выдачи" значение "не менее"/"не более" при всех значениях в поле "Формат периода предоставления";
//        б) в колонке "период предоставления" при значениях в поле "Месяц", "Квартал", "Полугодие", "Год", "Период от и до".
        if(block == CompareTaskKeys.CompareTaskBlock.PARAMETERS) {
            WithdrawMapper withdrawMapper = (WithdrawMapper) ru.masterdm.spo.utils.SBeanLocator.singleton().getBean("withdrawMapper");
            List<Withdraw> list = withdrawMapper.findByMdtask(to.id);
            int tranceHash = 95;
            for(com.vtb.domain.Trance trance : to.jdbc.getTranceList())
                tranceHash = tranceHash * 7 + withdrawMapper.findByTrance(trance.getId()).hashCode();
            return to.jdbc.getMain().getForbiddens().hashCode() +
                    ru.masterdm.spo.utils.SBeanLocator.singleton().mdTaskMapper().getTargetGroupLimits(to.id).hashCode() +
                    (to.jpa.isTrance_graph() ? 11 : 12) +
                    (to.jpa.isTrance_limit_use() ? 21 : 23) +
                    (to.jpa.isTrance_limit_excess() ? 31 : 34) +
                    (to.jpa.isTrance_hard_graph() ? 41 : 47) +
                    to.jdbc.getTranceList().hashCode()+
                    147 * list.hashCode() + tranceHash;
        }
//        6. График платежей/Погашение основного долга
//        поле "Валюта"
//        7. График платежей/График платежей, если во вкладке "Погашение основного долга" нажата чекбокс "Амортизация ставки"
//        поле "Дата оплаты";
//        От суммы лимита задолженности.
//        8. График платежей/График погашения процентов
//        поле "От даты (дата первой оплаты)"
        if(block == CompareTaskKeys.CompareTaskBlock.GRAPH)
            return to.jdbc.getInterestPay().hashCode() +
                    31 * to.jdbc.getPrincipalPay().hashCode() + 127 * to.jdbc.getPaymentScheduleList().hashCode();
//        9. Обеспечение/Поручительство
//        поле "предел ответственности" и "Валюта"
        if(block == CompareTaskKeys.CompareTaskBlock.SUPPLY)
            return to.jdbc.getSupply().hashCode();
        if(block == CompareTaskKeys.CompareTaskBlock.CONDITIONS)
            return 0;
        return 0;
    }

    private ObjectElement toCompareObject(MdTaskTO to, CompareTaskKeys.CompareTaskBlock block){
        Map<CompareTaskKeys.ContextKey, Object> context = new HashMap<CompareTaskKeys.ContextKey, Object>();
        context.put(CompareTaskKeys.ContextKey.JPA, to.jpa);
        context.put(CompareTaskKeys.ContextKey.FLOAT_PART_OF_ACTIVE_RATE_LIST, floatPartOfActiveRate);
        try {
            if (to.jdbc.getMain() != null && to.jdbc.getMain().getCurrency2() != null){
                TaskFacadeLocal taskFacade = EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
                context.put(CompareTaskKeys.ContextKey.DEPENDING_LOANS,
                            taskFacade.findDependingLoan(to.jdbc.getMain().getCurrency2().getCode(), to.jdbc.getMain().getPeriodInDay()));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return CompareTaskHelper.toCompareObject(to.jdbc, block, context);
    }

    private boolean isDiff(MdTaskTO t2, CompareTaskKeys.CompareTaskBlock block) {
        int hash1 = hashBefore.get(block).intValue();
        int hash2 = sectionHashCode(t2, block).intValue();
        if (hash1 != hash2)
            return true;
        List<ObjectElement> objs = new ArrayList<ObjectElement>();
        objs.add(taskBeforeCO.get(block));
        objs.add(toCompareObject(t2, block));
        Result mainResult = CompareHelper.compare(objs,
                                                  CompareTaskKeys.getTaskHeader(block, true, true),
                                                  0, null);
        //LOGGER.info(mainResult.toString());
        for (ResultObject ro : mainResult.getResultObjects())
            for(ResultElement re : ro.getResults())
                if(re.isWrong())
                    return true;
        return false;
    }
}

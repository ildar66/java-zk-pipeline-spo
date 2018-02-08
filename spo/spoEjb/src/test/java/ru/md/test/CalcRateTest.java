package ru.md.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.md.spo.dbobjects.CdRiskpremiumJPA;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.dbobjects.TranceJPA;

public class CalcRateTest extends Assert {
    private TaskJPA task;

    @Before
    public void setUpTask(){
        task=new TaskJPA();
        FactPercentJPA period = new FactPercentJPA();
        FactPercentJPA trance = new FactPercentJPA();

        task.setRate5(0.36);
        task.setRate6(0.35);
        task.setRate7(0.03);
        task.setRate8(0.05);
        task.setRate9(1.9);
        task.setRate10(2.1);
        period.setTask(task);
        trance.setTask(task);

        period.setFondrate(0.73);
        period.setRiskpremium(4.3);
        CdRiskpremiumJPA pt = new CdRiskpremiumJPA();
        pt.setValue("увеличенная");
        period.setRiskpremiumtype(pt);
        period.setRiskpremium_change(1.0);
        period.setRate3(3.15);
        period.setRate4(11.20);

        trance.setTrance(new TranceJPA(0L));
        trance.setFondrate(0.73);
        trance.setRiskpremium(3.12);
        CdRiskpremiumJPA pttr = new CdRiskpremiumJPA();
        pttr.setValue("уменьшенная");
        trance.setRiskpremiumtype(pttr);
        trance.setRiskpremium_change(4.15);
        trance.setRate3(2.15);
        trance.setRate4(1.2);
        trance.setRate5(0.2);
        trance.setRate6(0.35);
        trance.setRate9(1.7);
        trance.setRate10(2.0);

        List<FactPercentJPA> factPercents = new ArrayList<FactPercentJPA>();
        factPercents.add(period);
        factPercents.add(trance);
        task.setFactPercents(factPercents);
    }

    @Test
    /**поле Расчетная защищенная ставка для периода*/
    public void testCalcRatePeriod() {
        assertNotNull(task);
        for(FactPercentJPA fp : task.getFactPercents()){
            if(fp.getTranceId()!=null)
                continue;
            /*Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск +
            Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение + Покрытие прямых расходов*/
            assertEquals(6.77, fp.getCalcRate2().doubleValue(),0.01);
            /*Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск +
            Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение +
            Покрытие прямых расходов + Покрытие общебанковских расходов + Плата за экономический капитал*/
            assertEquals(9.97, fp.getCalcRate1().doubleValue(),0.01);
            /*эффективная ставка*/
            assertEquals(11.20, fp.getCalcRate3().doubleValue(),0.01);
        }
    }
    @Test
    /**поле Расчетная ставка для Транша*/
    public void testCalcRate1trance() {
        assertNotNull(task);
        for(FactPercentJPA fp : task.getFactPercents()){
            if(fp.getTranceId()==null)
                continue;
            assertEquals(0.27, fp.getCalcRate2().doubleValue(),0.01);
            assertEquals(2.47, fp.getCalcRate1().doubleValue(),0.01);
            assertEquals(1.2, fp.getCalcRate3().doubleValue(),0.01);
        }
    }
}

package ru.md.test;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.TaskJPA;

import com.vtb.domain.Deposit;
import com.vtb.domain.Guarantee;
import com.vtb.domain.SupplyType;
import com.vtb.domain.Task;
import com.vtb.domain.Warranty;
import com.vtb.util.Formatter;

public class SupplyObKindTest extends Assert {
    
	private Task task;
	private TaskJPA taskJpa;

	@Before
	public void init(){
		task = new Task();
		taskJpa = new TaskJPA();
		ArrayList<FactPercentJPA> fps = new ArrayList<FactPercentJPA>();
		FactPercentJPA fp = new FactPercentJPA();
		fp.setId(5L);
		fp.setStart_date(Formatter.parseDate("01.01.2000"));
		fp.setEnd_date(Formatter.parseDate("11.01.2000"));
		fps.add(fp);
		taskJpa.setFactPercents(fps);
		
		Deposit d1 = new Deposit();
		d1.setSupplyvalue(1.0);
		d1.setFromdate(Formatter.parseDate("01.01.2000"));
		d1.setTodate(Formatter.parseDate("11.01.2000"));
		d1.setOb(new SupplyType(3L));
		Deposit d2 = new Deposit();
		d2.setSupplyvalue(1.0);
		d2.setFromdate(null);
		d2.setTodate(null);
		d2.setOb(new SupplyType(4L));
		Deposit d3 = new Deposit();
		d3.setSupplyvalue(111.0);
		d3.setFromdate(Formatter.parseDate("02.02.2000"));
		d3.setTodate(Formatter.parseDate("02.02.2000"));
		d3.setOb(new SupplyType(7L));
		Warranty w= new Warranty();
		w.setSupplyvalue(5.0);
		w.setFromdate(Formatter.parseDate("02.02.2000"));
		w.setTodate(Formatter.parseDate("09.02.2000"));
		w.setOb(new SupplyType(3L));
		Guarantee g = new Guarantee();
		g.setSupplyvalue(3.0);
		g.setFromdate(Formatter.parseDate("02.03.2000"));
		g.setTodate(Formatter.parseDate("09.03.2000"));
		g.setOb(new SupplyType(3L));
		task.getSupply().getDeposit().add(d1);
		task.getSupply().getDeposit().add(d2);
		task.getSupply().getDeposit().add(d3);
		task.getSupply().getWarranty().add(w);
		task.getSupply().getGuarantee().add(g);
	}
	
    @Test
    public void test() {
    	assertNotNull(taskJpa.getPeriodObKind(task));
    }
    
    //Если на форме заявки в СПО поля «Группа обеспечения» имеет повторяющееся значение, 
    //то в МАК в поле «Вид обеспечения» необходимо выводить данное значение единожды.
    @Test
    public void group(){
    	assertEquals(1, taskJpa.getPeriodObKind(task).size());
    	assertTrue(taskJpa.getPeriodObKind(task).containsKey(5L));
    	assertEquals(3, taskJpa.getPeriodObKind(task).get(5L).size());
    }
    //Если сделка не разделена на периоды, то не смотрим даты
    //Если сделка разделена на периоды, то смотрим даты
    @Test
    public void withPeriod(){
    	FactPercentJPA fp = new FactPercentJPA();
		fp.setId(6L);
		fp.setStart_date(Formatter.parseDate("01.02.2000"));
		fp.setEnd_date(Formatter.parseDate("11.02.2000"));
		taskJpa.getFactPercents().add(fp);
		assertEquals(2, taskJpa.getPeriodObKind(task).size());
		assertEquals(1, taskJpa.getPeriodObKind(task).get(5L).size());
		assertEquals(2, taskJpa.getPeriodObKind(task).get(6L).size());
    }
    
    //В поле «Степень обеспечения» необходимо выводить с новой строки значения поля, указанное в одноименном 
    //поле на форме заявки в СПО для соответствующей «Группы обеспечения».
    //Если значения полей «Группа обеспечения» повторяется на форме заявки, в МАК нужно выводить 
    //суммарную величину «Степени обеспечения» для данного «Вида обеспечения»
    @Test
    public void supplyValue() {
    	assertEquals(3, taskJpa.getPeriodObKind(task).get(5L).size());
    	assertEquals(1.0, taskJpa.getPeriodObKind(task).get(5L).get(4L),0.1);
    	assertEquals(9.0, taskJpa.getPeriodObKind(task).get(5L).get(3L),0.1);
    }
    //Если сумма величины «Степень обеспечения» для данного «Вида обеспечения» больше 100, то 
    //значение в поле «Степень обеспечения по данному «Виду обеспечения» должно быть равно «100».
    @Test
    public void supplyValue100() {
    	assertEquals(100.0, taskJpa.getPeriodObKind(task).get(5L).get(7L),0.1);
    }
}

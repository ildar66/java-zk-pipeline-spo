package ru.md.test;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;

public class ReadOnlyUserTest extends Assert {
    private UserJPA user;

	@Before
    public void init(){
    	user = new UserJPA();
    	ProcessTypeJPA p1 = new ProcessTypeJPA(1L);
    	ProcessTypeJPA p2 = new ProcessTypeJPA(2L);
    	ProcessTypeJPA p3 = new ProcessTypeJPA(3L);
    	ProcessTypeJPA p4 = new ProcessTypeJPA(4L);
    	user.setRoles(new ArrayList<RoleJPA>());
    	
    	user.getRoles().add(new RoleJPA(1L, "Аудитор", p1));
    	user.getRoles().add(new RoleJPA(2L, "Структуратор", p1));
    	
    	user.getRoles().add(new RoleJPA(3L, "Аудитор", p2));
    	
    	user.getRoles().add(new RoleJPA(6L, "Структуратор", p3));
    	
    	user.getRoles().add(new RoleJPA(7L, "Аудитор", p4));
    	user.getRoles().add(new RoleJPA(8L, "Аудитор департамента", p4));
    	user.getRoles().add(new RoleJPA(null, "Администратор системы", p4));
    }
	
    @Test
    public void test() {
    	assertTrue(user.isAuditor());
    	
    	assertTrue(user.isAuditor(1L));
    	assertTrue(user.isAuditor(2L));
    	assertTrue(user.isAuditor(3L));
    	
    	assertFalse(user.isReadOnlyUser(1L));
    	assertTrue(user.isReadOnlyUser(2L));
    	assertFalse(user.isReadOnlyUser(3L));
    	assertTrue(user.isReadOnlyUser(4L));
    }
}

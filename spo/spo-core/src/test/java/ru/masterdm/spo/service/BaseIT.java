package ru.masterdm.spo.service;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * BaseIT for Testing Service.
 * Created by Ildar Shafigullin on 06.10.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/test-core-application-config.xml"})
abstract public class BaseIT {

}

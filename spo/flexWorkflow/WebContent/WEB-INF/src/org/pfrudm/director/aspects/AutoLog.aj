package org.uit.director.aspects;
/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 23.01.2006
 * Time: 15:12:43
 * To change this template use File | Settings | File Templates.
 */
public aspect AutoLog {
    pointcut publicMethods() :execution (public * *..*(..));
    
}
package com.vtb.command;

import java.io.Serializable;

/**
 * @author IShafigullin
 */
public interface Command extends Serializable {
	public void execute() throws Exception;
}

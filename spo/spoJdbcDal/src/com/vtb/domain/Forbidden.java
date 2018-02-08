package com.vtb.domain;

/**
 * Запрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц)
 * @author Igor Matushak
 */

public class Forbidden  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String goal;
	
	public String getGoal() {
		if (goal == null) {
			return "";
		}
		return goal.trim();
	}
	
	public void setGoal(String goal) {
		this.goal = goal;
	}
	
	public Forbidden(String goal) {
		super();
		this.goal = goal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Forbidden)) return false;

		Forbidden forbidden = (Forbidden) o;

		if (goal != null ? !goal.equals(forbidden.goal) : forbidden.goal != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return goal != null ? goal.hashCode() : 0;
	}
}
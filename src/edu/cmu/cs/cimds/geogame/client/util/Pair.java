package edu.cmu.cs.cimds.geogame.client.util;

import java.io.Serializable;

public class Pair<T,U> implements Serializable {
	
	private static final long serialVersionUID = 3211205874400379067L;
	
	public T t;
	public U u;
	
	public Pair(T t, U u) {
		this.t = t;
		this.u = u;
	}

	@Override
	public int hashCode() {
		return this.t.hashCode() + this.u.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(o.getClass()!=Pair.class) {
			return false;
		}
		Pair<T,U> otherPair = (Pair<T,U>)o;
		if(this.t.equals(otherPair.t) && this.u.equals(otherPair.u)) {
			return true;
		}
		if(this.t.equals(otherPair.u) && this.u.equals(otherPair.t)) {
			return true;
		}
		return false;
	}
}
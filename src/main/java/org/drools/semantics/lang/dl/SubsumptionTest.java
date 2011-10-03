package org.drools.semantics.lang.dl;

import net.sf.javailp.Problem;

@Deprecated
public class SubsumptionTest implements DLTest {

	private String superClass;
	private String subClass;
	private boolean tau = true;
	
	private Problem problem;

	public SubsumptionTest(String subClass, String superClass, boolean tau) {
		super();
		this.problem = new Problem();
		this.superClass = superClass;
		this.subClass = subClass;
		this.tau = tau;		
	}
	
	public SubsumptionTest(SubsumptionGoal goal) {
		super();
		this.problem = new Problem();
		this.superClass = goal.getSuperClass();
		this.subClass = goal.getSubClass();
		this.tau = goal.isTau();
	}
	
	
	
	
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}
	public String getSuperClass() {
		return superClass;
	}
	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}
	public String getSubClass() {
		return subClass;
	}
	
	

	public void setTau(boolean tau) {
		this.tau = tau;
	}


	public boolean isTau() {
		return tau;
	}


	public void setProblem(Problem problem) {
		this.problem = problem;
	}


	public Problem getProblem() {
		return problem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((subClass == null) ? 0 : subClass.hashCode());
		result = prime * result
				+ ((superClass == null) ? 0 : superClass.hashCode());
		result = prime * result + (tau ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubsumptionTest other = (SubsumptionTest) obj;
		if (subClass == null) {
			if (other.subClass != null)
				return false;
		} else if (!subClass.equals(other.subClass))
			return false;
		if (superClass == null) {
			if (other.superClass != null)
				return false;
		} else if (!superClass.equals(other.superClass))
			return false;
		if (tau != other.tau)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "SubsumptionTest{" +
                "superClass='" + superClass + '\'' +
                ", subClass='" + subClass + '\'' +
                ", tau=" + tau +
                '}';
    }
}

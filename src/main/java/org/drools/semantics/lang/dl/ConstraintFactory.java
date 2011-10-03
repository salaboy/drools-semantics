/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.lang.dl;

import net.sf.javailp.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class ConstraintFactory {




    private static List<Pair<Problem,Integer>> varCounter = new ArrayList<Pair<Problem,Integer>>();



    public static Object newUnitIntervalVariable(Object var, Problem p) {
        return newUnitIntervalVariable(var, 0.0, 1.0, p);
    }


    public static Object newUnitIntervalVariable(Object var, Number low, Number upp, Problem p) {
        String varName = format(var,p);

        Number oldLB = p.getVarLowerBound(varName);
//		System.out.println("Examine varName " + varName + " : found old lower bound " + oldLB +" vs new " + low);
        if (oldLB == null || oldLB.doubleValue() <= low.doubleValue())
            p.setVarLowerBound(varName, low);

        Number oldUB = p.getVarUpperBound(varName);
        if (oldUB == null || oldUB.doubleValue() >= upp.doubleValue())
            p.setVarUpperBound(varName, upp);

        p.setVarType(varName, VarType.REAL);
        return varName;
    }


    public static Object newBooleanVariable(Object var, Problem p) {

        String varName = format(var,p);

        p.setVarType(varName, VarType.BOOL);
        return varName;
    }

    private static String format(Object var, Problem p) {
//        Pair<Problem,Integer> px = null;
//        Integer counter = null;
//        for ( Pair<Problem,Integer> pair : varCounter ) {
//            if ( pair.getKey() == p ) {
//                px = pair;
//                break;
//            }
//        }
//        if ( px == null ) {
//            counter = new Integer( 0 );
//            px = new Pair<Problem, Integer>(p, counter);
//        } else {
//            counter = px.getValue();
//            varCounter.remove( px );
//        }
//
//        px.setValue( new Integer( ++counter ) );
//        varCounter.add(0, px);
//
//        return "x"+(counter);

        return "x"+(var.toString().hashCode() % 10000);

    }

    public static Constraint buildConstraint(String expr, Operator op, Number val) {
        Linear lhs = buildLinear(expr);
        return new Constraint(lhs, op, val);
    }


    public static Linear buildLinear( String lin ) {
//		System.err.println(lin);
        Linear ans = new Linear();

        StringTokenizer tok = new StringTokenizer(lin," ");
        while (tok.hasMoreTokens()) {
            String tx = tok.nextToken();

            StringTokenizer subTokenizer = new StringTokenizer(tx,"*");
            String coeff = subTokenizer.nextToken();
            String var = subTokenizer.nextToken();
            Term t = new Term(var, Double.parseDouble(coeff));
            ans.add(t);
        }

        return ans;
    }


//	public static void addAndConstraint(Object[] vars, Object y, Object l, Problem prob) {
//				
//		addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" +1.0*"+y,Operator.LE, 1.0));		
//		String terms = "";
//		for (int j = 0; j < vars.length; j++) {
//			// xj <= 1 - y
//			addUnique(ConstraintFactory.buildConstraint(" 1.0*"+vars[j]+" +1.0*"+y,Operator.LE, 1.0));
//			terms += "  1.0*" + vars[j];
//		}		
//		addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l+" +1.0*"+y,Operator.EQ, vars.length -1));
//	}

    public static void addAndConstraint(Object[] vars, Object y, Object l, Problem prob) {

        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" -1.0*"+y,Operator.LE, 0.0), prob);
        String terms = "";
        for (int j = 0; j < vars.length; j++) {
            terms += "  1.0*" + vars[j];
        }
        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l,Operator.LE, vars.length -1), prob);
        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l+" -1.0*"+y,Operator.GE, vars.length -2), prob);
    }









//	public static void addOrConstraint(Object[] vars, Object y, Object l, Problem prob) {			
//		String terms = "";
//		for (int j = 0; j < vars.length; j++) {			
//			terms += "  1.0*" + vars[j];
//		}
//				
//		addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l,Operator.EQ, 0), prob);
//		
//	}




    public static void addOrConstraint(Object[] vars, Object y, Object l, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" -1.0*"+y,Operator.GE, 0.0), prob);

        String terms = "";
        for (int j = 0; j < vars.length; j++) {
            terms += "  1.0*" + vars[j];
        }

        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l,Operator.GE, 0), prob);
        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l+" -1.0*"+y,Operator.LE, 0), prob);
    }
////	







    public static void addComplementConstraint(Object v1, Object v2, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+v1+" +1.0*"+v2,Operator.EQ, 1.0), prob);
    }

    public static void addEqualityConstraint(Object v1, Object v2, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+v1+" -1.0*"+v2,Operator.EQ, 0.0), prob);
    }

    public static void addImplicationConstraint(Object xA, Object xB, Object l, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" -1.0*"+xA+" 1.0*"+xB,Operator.LE,0.0), prob);
    }

    public static void addLBConstraint(Object x, Object l, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x+" -1.0*"+l,Operator.GE,0), prob);
    }

    public static void addNumericLBConstraint(Object x, Number tau, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x,Operator.GE,tau), prob);
    }

    public static void addUBConstraint(Object x, Object l, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x+" +1.0*"+l,Operator.LE,1.0), prob);
    }

    public static void addNumericUBConstraint(Object x, Number phi, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x,Operator.LE,phi), prob);
    }


    // ( prop && klass ) <= father
    public static void addExistConstraint(Object prop, Object klass, Object father, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+prop+" +1.0*"+klass+" -1.0*"+father,Operator.LE, 1), prob);
    }

    // ( prop => klass ) >= father
    public static void addForallConstraint(Object prop, Object klass, Object y, Object father, Problem prob) {
        addUnique(ConstraintFactory.buildConstraint(" -1.0*"+father+" -1.0*"+prop+" +1.0*"+klass,Operator.GE,-1), prob);
    }


    private static void addUnique(Constraint c, Problem prob) {
        // Constraint does not redefine equals :(

        //if (! prob.getConstraints().contains(c))
        //	prob.add(c);
//		for (Constraint con : prob.getConstraints()) {
//			if (con.toString().equals(c.toString()))
//				return;
//		}
        prob.add(c);

    }

    private static class Pair<T, K> {
        private T key;
        private K value;

        private Pair(T key, K value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }


        @Override
        public String toString() {
            return "Pair{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        public K getValue() {
            return value;
        }

        public void setValue(K value) {
            this.value = value;
        }
    }
}
 
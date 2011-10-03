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

import com.sun.corba.se.spi.activation._ServerStub;
import net.sf.javailp.*;
import org.drools.semantics.lang.dl.ConstraintFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SolverLoadTest {

    private Problem problem;

    @Before
    public void initProblem() {
        problem = new Problem();

        Linear linear = new Linear();
        linear.add(143, "x");
        linear.add(60, "y");

        problem.setObjective(linear, OptType.MAX);

        linear = new Linear();
        linear.add(120, "x");
        linear.add(210, "y");

        problem.add(linear, "<=", 15000);

        linear = new Linear();
        linear.add(110, "x");
        linear.add(30, "y");

        problem.add(linear, "<=", 4000);

        linear = new Linear();
        linear.add(1, "x");
        linear.add(1, "y");

        problem.add(linear, "<=", 75);

        problem.setVarType("x", Integer.class);
        problem.setVarType("y", Integer.class);

    }

    @Test
    public void testLoad() {

        Solver solver = null;

        try {
            Solver s = new SolverFactoryGLPK().get();
                solver = s;
            solver.setParameter(SolverGLPK.VERBOSE,0);
            testSolver(s);
            System.err.println("GLPK Solver OK");
        } catch (Throwable t) {
            System.err.println("WARN : GLPK Solver not found");
        }
        try {
            Solver s = new SolverFactoryLpSolve().get();
                solver = s;
            solver.setParameter(SolverLpSolve.VERBOSE,0);
            testSolver(s);
            System.err.println("LPSOLVE Solver OK");
        } catch (Throwable t) {
            System.err.println("WARN : LPSOLVE Solver not found");
        }
        try {
            Solver s = new SolverFactoryCPLEX().get();
                solver = s;
            solver.setParameter(SolverCPLEX.VERBOSE,0);
            testSolver(s);
            System.err.println("CPLEX Solver OK");
        } catch (Throwable t) {
            System.err.println("WARN : CPLEX Solver not found");
        }
        try {
            Solver s = new SolverFactoryGurobi().get();
                solver = s;
            solver.setParameter(SolverGurobi.VERBOSE,0);
            testSolver(s);
            System.err.println("GUROBI Solver OK");
        } catch (Throwable t) {
            System.err.println("WARN : GUROBI Solver not found");
        }

        if (solver == null) {
            fail("No suitable MILP solver found");
        }

    }



    private void testSolver(Solver solver) {
        try {

            Result result = solver.solve(problem);

            assertEquals(6266,result.getObjective().intValue());

        } catch (Throwable t) {
            fail(t.getMessage());
        }

    }

}
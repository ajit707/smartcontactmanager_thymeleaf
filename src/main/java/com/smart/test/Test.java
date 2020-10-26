package com.smart.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

	public static void main(String[] args) {

		List<Employee> emps = new ArrayList<Employee>();

		emps.add(new Employee(101, "ABC", 85000));
		emps.add(new Employee(102, "XYZ", 65000));
		emps.add(new Employee(103, "PQR", 75000));
		emps.add(new Employee(104, "PPP", 88000));
		emps.add(new Employee(105, "TTT", 45000));

		int id = 103;

		/*
		 * Iterator<Employee> itr = emps.iterator(); while (itr.hasNext()) { Employee
		 * emp = itr.next();
		 * 
		 * if (emp.getId() == id) { itr.remove(); break; } }
		 */

		emps.removeIf(e -> e.getId() == id);

		System.out.println(emps);

	}

}

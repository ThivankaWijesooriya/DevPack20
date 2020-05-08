package com.ONEzero.DAO;

import java.util.List;

import com.ONEzero.model.Department;


public interface DepartmentDAO {
	
	
	public abstract void createDepartment(Department department);
	public abstract String getAllDepartments();
	public abstract String getDepartmentsToEmployeeMast();
	public abstract void updateDepartmentById(Department department);
    public abstract void deleteDepartmentById(Department department);
    public abstract String getDepartmentsByName(String deptname);

}

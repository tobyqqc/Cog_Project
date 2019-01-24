package com.lumosity.test;

import java.util.List;

import com.github.ExcelUtils;

public class TestExcel {

	public static void main(String[] args) {
		   String path = "E:\\project\\col\\col\\server源码\\cogTraining\\src\\com\\lumosity\\test\\students_01.xlsx";

	        System.out.println("读取全部：");
	        List<Student1> students = null;
			try {
				students = ExcelUtils.getInstance().readExcel2Objects(path, Student1.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        for (Student1 stu : students) {
	            System.out.println(stu);
	        }

	        System.out.println("读取指定行数：");
	        try {
				students = ExcelUtils.getInstance().readExcel2Objects(path, Student1.class, 0, 3, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        for (Student1 stu : students) {
	            System.out.println(stu);
	        }
	}
}

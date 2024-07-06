package com.example.demo.util;

public class ValidatePattern {
	public static final String NAME = "^[A-Z][a-z]{0,10}$";
	public static final String EMAIL = "\\S+@\\S+\\.\\S+";
	public static final String PHONE = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
	public static final String SLUG = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
}
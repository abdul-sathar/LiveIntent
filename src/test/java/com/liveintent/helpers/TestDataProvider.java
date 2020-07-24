package com.liveintent.helpers;

import org.testng.annotations.DataProvider;

public class TestDataProvider {
	
	@DataProvider(name = "validData")
    public static Object[][] validData() 
    {
        return new Object[][] { 
        	{"0", "200","even"},
        	{"1", "200","odd"},
        	{"2", "200","even"},
        	{"-12345", "200","odd"},
        	{"1234567890", "200","even"}
        	
        };
    }
	
	@DataProvider(name = "invalidData")
    public static Object[][] invalidData() 
    {
        return new Object[][] { 
        	{"2.5", "400"},
        	{"abcde", "400"},
        	{"!@#$|%", "400"},
        	{" ", "400"},
        	{null, "400"}
        	
        };
    }

}

package com.example.tool_calling_example;

import com.example.tool_calling_example.tools.WeatherTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SimpleDateTimeToolTests {

    @Autowired
    private WeatherTool weatherTool;
	@Test
	void contextLoads() {
	}

    @Test
    void getWeatherTest(){
     var response = weatherTool.getWeather("Delhi India");
        System.out.println(response);
    }
}

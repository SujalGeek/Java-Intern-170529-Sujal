package com.example.tool_calling_example.tools;

import org.slf4j.Logger;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleDateTimeTool {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Tool(description = "Get the current date and time in users zone.")
    public String getCurrentDateAndTime(){
        this.logger.info("Tool is calling");
        this.logger.info("Get the current date and time in users zone.");
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "Set the alarm for given time")
    void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time){
        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        this.logger.info("Set the alarm for given time. {}",localDateTime);
    }

}

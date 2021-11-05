package com.ruoyi.web.core.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.ruoyi.common.utils.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class P6spySqlFormatConfig implements MessageFormattingStrategy {

    /**
     * sql格式化输出
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return StringUtils.isNotBlank(sql) ? formatFullTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss.SSS")
                + " | 耗时 " + elapsed + " ms | SQL 语句：" + StringUtils.LF + sql.replaceAll("[\\s]+", StringUtils.SPACE) + ";" : StringUtils.EMPTY;
    }

    /**
     * 日期格式化
     */
    public String formatFullTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }
}

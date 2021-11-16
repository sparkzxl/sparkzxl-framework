package com.github.sparkzxl.core.serializer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.sparkzxl.core.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * description: 自定义时间反序列化
 *
 * @author zhouxinlei
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {


    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String date = jsonParser.getText();
        if (StringUtils.isNotEmpty(date)) {
            if (NumberUtil.isNumber(date)) {
                long timeStamp = Long.parseLong(date);
                return DateUtils.date(timeStamp);
            }
            return DateUtils.parse(date, DatePattern.NORM_DATETIME_FORMAT);
        }
        return null;
    }
}

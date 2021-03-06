/* 梦境迷离 (C)2020 */
package cn.edu.jxnu.base.utils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 通过继承JDK 中的 java.beans.PropertyEditorSupport 类来实现自己的编辑器类 ，该类用于实现将String 类型转换成自己需要的数据类型
 *
 * @author 梦境迷离
 * @version V2.0 2020年11月20日
 */
public class DateEditor extends PropertyEditorSupport {

    /** 是否为空 */
    private boolean emptyAsNull;
    /** 日期格式 */
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    /** 格式数组 */
    private static final String[] DATE_PATTERNS = {
        "yyyy",
        "yyyy-MM",
        "yyyyMM",
        "yyyy/MM",
        "yyyy-MM-dd",
        "yyyyMMdd",
        "yyyy/MM/dd",
        "yyyy-MM-dd HH:mm:ss",
        "yyyyMMddHHmmss",
        "yyyy/MM/dd HH:mm:ss"
    };

    /**
     * 构造方法
     *
     * @param emptyAsNull emptyAsNull
     */
    public DateEditor(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }

    public DateEditor(boolean emptyAsNull, String dateFormat) {
        this.emptyAsNull = emptyAsNull;
        this.dateFormat = dateFormat;
    }

    /** 获得格式化的日期字符串 */
    public String getAsText() {
        Date date = (Date) getValue();
        return date != null ? new SimpleDateFormat(this.dateFormat).format(date) : "";
    }

    /** 获得格式化的日期 */
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
        } else {
            String str = text.trim();
            // true 则将空字符串转换为null
            if ((this.emptyAsNull) && ("".equals(str))) setValue(null);
            else
                try {
                    setValue(DateUtils.parseDate(str, DATE_PATTERNS));
                } catch (ParseException e) {
                    setValue(null);
                }
        }
    }
}

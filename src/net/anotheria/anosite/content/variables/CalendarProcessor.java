package net.anotheria.anosite.content.variables;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: h3llka
 */
public class CalendarProcessor implements VariablesProcessor {
    private static final Logger log = Logger.getLogger(CalendarProcessor.class);

    @Override
    /**
     * Returns variable value for incoming variable param. If format - which represented by <a>defValue</a> - valid,
     * then variable value will be represented in  current format, else default format will be used.
     * <a>@see net.anotheria.anosite.content.variables.CalendarVariableNames).</a>
     * When incoming variable does not exists, or wrong -  <a>Wrong or unsupported variable</a> will be returned as well.
     * <a>Variable can't be null</a> will be  returned  when variable - null.
     *
     * @param prefix Processtor prefix
     * @param variable vairiable name
     * @param defValue currently string representetion of dateFormat
     * @param req request instance
     * @return variable value
     */
    public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
        try {
            CalendarVariableNames variableName = CalendarVariableNames.valueOf(CalendarVariableNames.class, variable);
            return variableName.getVariableValue(defValue);
        }
        catch (NullPointerException e){
            log.error("Exception has been occuread while trying to use CallendarProcessor replace method", e);
            log.debug("incoming variable = " + variable);
             return "Variable can't be null";
        }
        catch (IllegalArgumentException e) {
            log.error("Exception has been occuread while trying to use CallendarProcessor replace method", e);
            log.debug("incoming variable = " + variable);
            return "Wrong or unsupported variable";
        }
    }
}


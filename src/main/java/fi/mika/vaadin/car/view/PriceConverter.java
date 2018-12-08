package fi.mika.vaadin.car.view;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class PriceConverter implements Converter<String, BigDecimal> {
    private NumberFormat numberFormat;

    public PriceConverter() {
        numberFormat = new DecimalFormat("###,###.##");
        ((DecimalFormat) numberFormat).setParseBigDecimal(true);
    }

    @Override
    public Result<BigDecimal> convertToModel(String text, ValueContext valueContext) {
        if (isBlank(text)) {
            return Result.ok(null);
        }
        try {
            return Result.ok((BigDecimal) numberFormat.parse(text));
        } catch (Exception e) {
            return Result.error("Price is required. Format is 10,000.00");
        }
    }

    @Override
    public String convertToPresentation(BigDecimal value, ValueContext valueContext) {
        if (value == null) {
            return "";
        }
        return numberFormat.format(value);
    }
}

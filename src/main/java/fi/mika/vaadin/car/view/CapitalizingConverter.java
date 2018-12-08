package fi.mika.vaadin.car.view;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class CapitalizingConverter implements Converter<String, String> {
    @Override
    public Result<String> convertToModel(String text, ValueContext valueContext) {
        return Result.ok(text.toUpperCase());
    }

    @Override
    public String convertToPresentation(String text, ValueContext valueContext) {
        if (text == null) {
            return "";
        }
        return text.toUpperCase();
    }
}

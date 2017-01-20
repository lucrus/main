package com.lucrus.main.validation;

/**
 * Created by lucrus on 12/10/16.
 */

public class ValidatorFactory {
    public static Validator getValidator(String name, Object obj) {
        if (name.equalsIgnoreCase("notEmpty")) {
            return new Required(obj);
        } else if (name.equalsIgnoreCase("between")) {
            return new Between(obj);
        } else if (name.equalsIgnoreCase("emailAddress")) {
            return new EmailAddress(obj);
        } else if (name.equalsIgnoreCase("regexp")) {
            return new RegularExpression(obj);
        } else if (name.equalsIgnoreCase("notEmptyWhen")) {
            return new RequiredWhen(obj);
        } else if (name.equalsIgnoreCase("bic")) {
            return new BIC(obj);
        } else if (name.equalsIgnoreCase("iban")) {
            return new IBAN(obj);
        } else if (name.equalsIgnoreCase("digit")) {
            return new Digit(obj);
        } else if (name.equalsIgnoreCase("stringLength")) {
            return new StringLenght(obj);
        } else if (name.equalsIgnoreCase("vat")) {
            return new PartitaIva(obj);
        } else if (name.equalsIgnoreCase("posCode")) {
            return new PosCode(obj);
        } else if (name.equalsIgnoreCase("codiceFiscale")) {
            return new CodiceFiscale(obj);
        } else if (name.equalsIgnoreCase("isNumber")) {
            return new Decimal(obj);
        } else if (name.equalsIgnoreCase("choice")) {
            return new Choice(obj);
        }
        return null;
    }
}

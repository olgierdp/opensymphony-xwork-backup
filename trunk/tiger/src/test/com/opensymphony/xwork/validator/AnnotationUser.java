/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.validator.annotations.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Test bean.
 *
 * @author Mark Woon
 */
/*
<validator type="expression">
    <param name="expression">email.startsWith('mark')</param>
    <message>Email does not start with mark</message>
</validator>
<validator type="expression">
    <param name="expression">email2.startsWith('mark')</param>
    <message>Email2 does not start with mark</message>
</validator>
*/
@Validation(
        validations = @Validations(
                expressions = {
                    @ExpressionValidator(expression = "email.startsWith('mark')", message = "Email does not start with mark"),
                    @ExpressionValidator(expression = "email2.startsWith('mark')", message = "Email2 does not start with mark")
                }
        )
)
public class AnnotationUser implements AnnotationUserMarker {

    private Collection collection;
    private List list;
    private Map map;
    private String email;
    private String email2;
    private String name;


    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Collection getCollection() {
        return collection;
    }

    /*
    <field name="email">
        <field-validator type="email" short-circuit="true">
            <message>Not a valid e-mail.</message>
        </field-validator>
        <field-validator type="fieldexpression">
            <param name="expression">email.endsWith('mycompany.com')</param>
            <message>Email not from the right company.</message>
        </field-validator>
    </field>
    */
    @EmailValidator(shortCircuit = true, message = "Not a valid e-mail.")
    @FieldExpressionValidator(expression = "email.endsWith('mycompany.com')", message = "Email not from the right company.")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    /*
    <field name="email2">
        <field-validator type="email">
            <message>Not a valid e-mail2.</message>
        </field-validator>
        <field-validator type="fieldexpression">
            <param name="expression">email.endsWith('mycompany.com')</param>
            <message>Email2 not from the right company.</message>
        </field-validator>
    </field>
    */
    @EmailValidator(message = "Not a valid e-mail2.")
    @FieldExpressionValidator(expression = "email2.endsWith('mycompany.com')", message = "Email2 not from the right company.")
    public void setEmail2(String email) {
        email2 = email;
    }

    public String getEmail2() {
        return email2;
    }

    public void setList(List l) {
        list = l;
    }

    public List getList() {
        return list;
    }

    public void setMap(Map m) {
        map = m;
    }

    public Map getMap() {
        return map;
    }

    /*
    <field name="name">
        <field-validator type="required">
            <message key="name.key">You must enter a value for name.</message>
        </field-validator>
    </field>
    */
    @RequiredFieldValidator(key = "name.key", message = "You must enter a value for name.")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package hibernatestudy.web;

import javax.faces.validator.BeanValidator;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * JSFでBean Validationが利用できるように、ValidatorFactoryを登録する。
 */
public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        context.setAttribute(BeanValidator.VALIDATOR_FACTORY_KEY, validatorFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}

package hibernatestudy.spring.scope;

import java.util.Map;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * Springのスコープ定義で、JSFのviewスコープと同等のスコープを実現する。
 */
public class ViewScope implements Scope {
    private static final Logger logger = LoggerFactory.getLogger(ViewScope.class);
    
    private Map<String,Object> getViewMap() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            throw new IllegalStateException("FacesContextがnullです。");
        }
        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot == null) {
            throw new IllegalStateException("ViewRootがnullです。");
        }
        return viewRoot.getViewMap();
    }
    
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String,Object> viewMap = null;
        try {
            viewMap = getViewMap();
        } catch(IllegalStateException e) {
            logger.warn( "viewスコープのオブジェクト{}を取得しようとしましたが、viewスコープにアクセスできません。", name ,e);
            return null;
        }
        
        if (viewMap.containsKey(name)) {
            return viewMap.get(name);
        }
        else {
            Object object = objectFactory.getObject();
            viewMap.put(name, object);
            return object;
        }
    }

    @Override
    public Object remove(String name) {
        Map<String,Object> viewMap = null;
        try {
            viewMap = getViewMap();
        } catch(IllegalStateException e) {
            logger.warn( "viewスコープのオブジェクト{}を削除しようとしましたが、viewスコープにアクセスできません。", name ,e);
            return null;
        }
        return viewMap.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // このスコープでは destruction callbackはサポートしない。
        // サポートしない場合は、registerDestructionCallback の Javadocの説明にしたがい、何もしない。
    }

    @Override
    public Object resolveContextualObject(String arg0) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
    
}

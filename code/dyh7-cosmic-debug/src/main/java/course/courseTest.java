package course;

import kd.bos.base.AbstractBasePlugIn;
import kd.bos.context.RequestContext;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 基础资料插件
 */
public class courseTest extends AbstractBasePlugIn implements Plugin {

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        RequestContext rc = RequestContext.get();
        String nowUser  = rc.getUserName();

        this.getView().showMessage("I am "+nowUser);
    }
}
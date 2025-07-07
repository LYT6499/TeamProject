package teaching_res;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.ShowType;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
//试卷生成按钮
public class exam_creation extends AbstractBillPlugIn implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("dyh7_exam_creation")) {
            //打开试卷列表
            ListShowParameter nxtList = new ListShowParameter(); // 固定的
            nxtList.setFormId("bos_list"); // 固定的
            nxtList.setBillFormId("dyh7_exampapermag"); // 列表对应的单据字段
            nxtList.getOpenStyle().setShowType(ShowType.Modal); // 打开风格，有非常多种
            this.getView().showForm(nxtList);

        }
    }
}
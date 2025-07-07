package teaching_res;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.form.control.RichTextEditor;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class Rich_Text extends AbstractBillPlugIn implements Plugin {

//    将富文本内容赋给大文本
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs e) {

        FormOperate operate =  (FormOperate)e.getSource();

        String key = operate.getOperateKey();

        if(key.equalsIgnoreCase("save")) {

            RichTextEditor edit = this.getView().getControl("dyh7_syllabus");//richtexteditorap 富文本控件

            this.getModel().setValue("dyh7_largetextfield", edit.getText());//largetextfield大文本字段

        }

    }

//    打开页面时将大文本值赋给富文本

    @Override
    public void afterBindData(EventObject e) {

        super.afterBindData(e);

        RichTextEditor edit = this.getView().getControl("dyh7_syllabus");

        edit.setText((String) this.getModel().getValue("dyh7_largetextfield"));

    }

}
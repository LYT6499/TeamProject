package home_page;

import kd.bos.context.RequestContext;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.bos.plugin.sample.bill.list.template.ItemClick;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class homeForm extends AbstractFormPlugin implements Plugin {
//    @Override
//    public void afterCreateNewData(EventObject e) {
//        super.afterCreateNewData(e);
//        RequestContext rc = RequestContext.get();
//        String nowUser  = rc.getUserName();
//
//        this.getView().showMessage("I am "+nowUser);
//    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        // 获取触发事件的控件
        Control control = (Control)evt.getSource();
        String controlKey = control.getKey();

        this.getView().showMessage("点击的控件键值: " + controlKey);

        if("dyh7_imageap1".equalsIgnoreCase(controlKey)) {
            // 打开智能排课表单
            FormShowParameter showParameter = new FormShowParameter();
            showParameter.setFormId("dyh7_course_scheduling");
            showParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(showParameter);
        }
    }

//    @Override
//    public void itemClick(ItemClickEvent evt) {
//        super.itemClick(evt);
//        this.getView().showMessage("点击的按钮键值: " + evt.getItemKey());
//        if(evt.getItemKey().equalsIgnoreCase("dyh7_imageap1")){
//
//            ListShowParameter nxtList =new ListShowParameter();//固定的
//            nxtList.setFormId("bos_list");// 固定的
//            nxtList.setBillFormId("dyh7_course_scheduling");// 列表对应的单据字段
//            nxtList.getOpenStyle().setShowType(ShowType.Modal);// 打开风格，有非常多种
//            this.getView().showForm(nxtList);
//
//
////            FormShowParameter showParameter =new FormShowParameter();
////            showParameter.getOpenStyle().setShowType(ShowType.Modal);
////            showParameter.setFormId("myg6_homepage");// 在这里可以设置打开页面的大小
////            StyleCss style = new StyleCss();
////            style.setWidth("1000");
////            style.setHeight("600");
////            showParameter.getOpenStyle().setInlineStyleCss(style);
////            this.getView().showForm(showParameter);
//        }
//    }
}
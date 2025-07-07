package teaching_res;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

/**
 * 单据界面插件
 */

//该插件不能使用，因为太多嵌套获取了
public class teaching_res extends AbstractBillPlugIn implements Plugin {

    @Override
    public void propertyChanged(PropertyChangedArgs e) {//值改变触发
        String fieldKey = e.getProperty().getName();
        if (StringUtils.equals("dyh7_course_scheduling", fieldKey)){
            // TODO 在此添加业务逻辑
            // 1. 获取当前单据数据包
            DynamicObject billObj = this.getModel().getDataEntity(true);
            //this.getView().showMessage("调试信息编码: "+this.getModel().getDataEntity(true));
            // 2. 获取对应排课记录关键字段值
            DynamicObject cs = (DynamicObject) billObj.get("dyh7_course_scheduling");
           // String  cs = (String) billObj.get("dyh7_course_scheduling");
            //this.getView().showMessage("调试信息编码: "+cs);
            //调试信息编码: dyh7_course_scheduling[billno][2217751391575736320, PK202310001]
            //由于排课记录里面都是使用基础数据所以还得再关联一层,找到课程和教师所对的基础资料
            DynamicObject course_msg = (DynamicObject) cs.get("dyh7_course");
            this.getView().showMessage("调试信息编码: "+course_msg);

           // String courseName = cs.getString("dyh7_course_name") ;
//            String teacherName = cs.getString("dyh7_teacher_name") ;

//
//            // 3.赋值
//            billObj.set("dyh7_course_name", courseName); // 课程名字段
//            billObj.set("dyh7_teacher", teacherName); // 教师姓名字段
            }
        }
}
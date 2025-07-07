package course;

import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.operate.result.IOperateInfo;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单据界面插件
 */
public class Update_Course_Records extends AbstractBillPlugIn implements Plugin {

    private static final String CLASSROOM_FIELD = "dyh7_class"; // 教室字段
    private static final String TIME_FIELD = "dyh7_c_time";          // 时间字段
    private static final String COURSE_FIELD = "dyh7_course";     // 课程字段
    private static final String TEACHER_FIELD = "dyh7_teacher";   // 教师字段
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("bar_save")) {
            // 1. 获取当前单据数据包
            DynamicObject billObj = this.getModel().getDataEntity(true);

            // 2. 获取关键字段值
            DynamicObject classroom = (DynamicObject) billObj.get(CLASSROOM_FIELD);
            String timeSlot = (String) billObj.get(TIME_FIELD);
            DynamicObject course = (DynamicObject) billObj.get(COURSE_FIELD);
            DynamicObject teacher = (DynamicObject) billObj.get(TEACHER_FIELD);

            String classroomName = classroom.getString("name"); // 获取name字段的值
            //String timeSlotId = timeSlot.getPkValue().toString();
            String courseName = course.getString("name") ;
            String teacherName = teacher.getString("name") ;



            // 3. 查询教室排课记录表
            String number = classroom.getString("number");//获取对应教室排课记录的编码
            //DynamicObject pkid = classroom.getDynamicObject("fbasedataid");
//            Long pkId = (Long) classroom.getPkValue();
            //this.getView().showMessage("调试信息编码: "+number);

//            BusinessDataServiceHelper.loadSingle(PkId, "实体名")
            QFilter qFilter = new QFilter("number", QCP.equals, number);
            DynamicObject scheduleRecord  = BusinessDataServiceHelper.loadSingle("dyh7_c_scheduling", new QFilter[]{qFilter});
//            QFilter[] filters = new QFilter[]{
//                    new QFilter(CLASSROOM_FIELD + ".id", QCP.equals, classroomId),
//                    new QFilter(TIME_FIELD + ".id", QCP.equals, timeSlotId),
//                    new QFilter("dyh7_status", QCP.equals, "空闲中") // 状态字段
//            };
//
//            DynamicObject scheduleRecord  = BusinessDataServiceHelper.loadSingle("dyh7_c_scheduling", filters);
//
//            // 4. 更新教室排课记录状态
            scheduleRecord.set("dyh7_status", "占用中"); // 状态字段
            scheduleRecord.set("dyh7_course", courseName); // 课程名字段
            scheduleRecord.set("dyh7_teacher", teacherName); // 教师姓名字段
//
//            // 5. 保存更新（使用当前用户上下文）
           SaveServiceHelper.saveOperate("dyh7_c_scheduling", new DynamicObject[]{scheduleRecord},null);
//            if(operationResult.isSuccess()){
//                this.getView().showMessage("保存成功");
//            }else{
//                String errorMsg="";
//                List<IOperateInfo> allErrorOrValidateInfo = operationResult.getAllErrorOrValidateInfo();
//                for (IOperateInfo operateInfo : allErrorOrValidateInfo){
//                    errorMsg+=operateInfo.getMessage()+"\r\n";
//                }
//                this.getView().showMessage("保存失败！"+errorMsg);
//            }
        }
    }
}
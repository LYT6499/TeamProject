package course;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
 * 动态表单插件
 */
public class Cosmic_courseApply  implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) { // 固定写法
        Map<String, String> result = new HashMap<>();
        System.out.println("1111");
        if ("GET_JSON_STRING".equalsIgnoreCase(action)) {
            System.out.println("进来了");
            // 获取Json字符串
            //String jsonResult = params.get("jsonResult").replaceAll("\\s*|\r|\n|\t", "");
            String jsonResult = params.get("jsonResult");
            if (jsonResult == null) {
                throw new IllegalArgumentException("jsonResult 参数不能为空");
            }
            System.out.println("jsonResult: " + jsonResult);
            jsonResult = jsonResult.replaceAll("\\s*|\r|\n|\t", "");

            JSONObject resultJsonObject = null;
            try {
                resultJsonObject = JSON.parseObject(jsonResult);
            } catch (Exception ee) {
                jsonResult = jsonResult.substring(jsonResult.indexOf("\"courseName\"") - 1, jsonResult.indexOf("}") + 1);
                resultJsonObject = JSON.parseObject(jsonResult);
            }
            System.out.println("resultJsonObject: " + resultJsonObject);

            // 随机一个单据编号
            StringBuilder sb1 = new StringBuilder();
            for (int i = 1; i <= 10; ++i) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }

            // 提炼出json信息
            String courseName = resultJsonObject.getString("courseName");
            String teacherName = resultJsonObject.getString("teacherName");
            String classroom = resultJsonObject.getString("classroom");
            String classTime = resultJsonObject.getString("classTime");
            String classsize = resultJsonObject.getString("classsize");//班级人数
            int classSizeInt = Integer.parseInt(classsize);
            // 筛出该课程的基础资料，方便后续set到排课单据中
            QFilter qFilter = new QFilter("name", QCP.equals, courseName);
            DynamicObject course = BusinessDataServiceHelper.loadSingle("dyh7_course", new QFilter[]{qFilter});

            System.out.println("course: " + course);
            if (course == null) {
                throw new RuntimeException("未找到课程: " + courseName);
            }
            // 获取该课程的对应信息
            String dyh7_name = course.getString("name");//课程名称
            String dyh7_book = course.getString("dyh7_book");//教程信息


            // 筛出该教师的基础资料，方便后续set到排课单据中
            QFilter qFilter2 = new QFilter("name", QCP.equals, teacherName);
            DynamicObject teacher = BusinessDataServiceHelper.loadSingle("dyh7_teacher", new QFilter[]{qFilter2});

            System.out.println("teacher: " + teacher);
            // 获取该教师的对应信息
            String dyh7_isbn = teacher.getString("number");//教师工号
            String dyh7_teachername = teacher.getString("name");//教师姓名



            // 筛出该教室的基础资料，方便后续set到排课单据中  //这个只获取到一条单据
//            QFilter qFilter3 = new QFilter("name", QCP.equals, classroom);
//            DynamicObject classroomresources = BusinessDataServiceHelper.loadSingle("dyh7_c_scheduling", new QFilter[]{qFilter3});

//            DynamicObjectCollection selectedQuestions =
//                    (DynamicObjectCollection) this.getModel().getValue();
//
//            // 获取该教室的对应信息
//            String dyh7_classroomresources = classroomresources.getString("name");//教学楼姓名
//            String dyh7_room_numbe = classroomresources.getString("dyh7_room_number");//具体位置（房间号）


            // 获取当前日期
//            LocalDate today = LocalDate.now();
//            long daysToAdd = Long.parseLong(borrowTime);
//            LocalDate returnDay = today.plusDays(daysToAdd);
//            Date todayDate = Date.valueOf(today);
//            Date ReturnDay = Date.valueOf(returnDay);

            // new 一个 DynamicObject（排课单据） 表单对象
            QFilter qFilter01 = new QFilter("name", QCP.equals, classroom);
            QFilter qFilter02 = new QFilter("dyh7_c_time", QCP.equals, classTime);
            QFilter qFilter03 = new QFilter("dyh7_status", QCP.equals, "0");
            QFilter qFilter04 = new QFilter("dyh7_capacity",QCP.large_equals, classSizeInt);
            // 组合条件：AND关系
            QFilter totalFilter = qFilter01
                    .and(qFilter02)
                    .and(qFilter03)
                    .and(qFilter04);
            // 使用load方法查询多条数据（最多1000条）
            String selectProperties = "id,dyh7_capacity,dyh7_c_time,dyh7_status,dyh7_course,dyh7_teacher";
            DynamicObject[] classroomresources = BusinessDataServiceHelper.load(
                    "dyh7_c_scheduling",  // 实体标识
                    selectProperties,    // 返回的字段（仅需id即可统计数量）
                    new QFilter[]{totalFilter}// 查询条件
            );

            String roomName = classroomresources[0].getString("name");
            //String roomCapacity = classroomresources[0].getString("dyh7_capacity");
            int roomCapacity = classroomresources[0].getInt("dyh7_capacity");
            //Integer roomCapacity = classroomresources[0].getInt("dyh7_capacity");
            String roomTime = classroomresources[0].getString("dyh7_c_time");

            if (classroomresources.length > 0) {
                //this.getView().showMessage("查询到数据条数: " + classroomresources.length);
                DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("dyh7_course_scheduling");
                // 设置对应属性
                dynamicObject.set("billno", sb1.toString());
                dynamicObject.set("dyh7_course", course);
                dynamicObject.set("dyh7_teacher", teacher);
                dynamicObject.set("dyh7_class", classroomresources[0]);
                dynamicObject.set("dyh7_c_time", classTime);
                dynamicObject.set("dyh7_class_size", classsize);
                dynamicObject.set("billstatus", "暂存");

                dynamicObject.set("dyh7_course_name", dyh7_name);
                dynamicObject.set("dyh7_book", dyh7_book);
                dynamicObject.set("dyh7_teacherid", dyh7_isbn);
                dynamicObject.set("dyh7_teacher_name", dyh7_teachername);


                dynamicObject.set("dyh7_roomtxt", roomName);
                dynamicObject.set("dyh7_integerfield", roomCapacity);
                dynamicObject.set("dyh7_textfield1", roomTime);
                dynamicObject.set("dyh7_textfield2", dyh7_name);
                dynamicObject.set("dyh7_textfield3", dyh7_teachername);


                //保存
                SaveServiceHelper.saveOperate("dyh7_course_scheduling", new DynamicObject[]{dynamicObject}, null);
                Long pkId = (Long) dynamicObject.getPkValue();

                //修改教室资源表的状态
                classroomresources[0].set("dyh7_status", "占用中"); // 状态字段
                classroomresources[0].set("dyh7_course", dyh7_name); // 课程名字段
                classroomresources[0].set("dyh7_teacher", dyh7_teachername); // 教师姓名字段
                SaveServiceHelper.saveOperate("dyh7_c_scheduling", new DynamicObject[]{classroomresources[0]},null);

                // 拼接URL字符串
                String targetForm = "bizAction://currentPage?gaiShow=1&selectedProcessNumber=processNumber&gaiAction=showBillForm&gaiParams={\"appId\":\"dyh7_001\",\"billFormId\":\"dyh7_course_scheduling\",\"billPkId\":\"" + pkId + "\"}&title=排课申请单据 &iconType=bill&method=bizAction";
                result.put("formUrl", targetForm);
                //return result;
            }else {
                // 先检查教室是否存在（您确认有10条记录，这步应该不会触发）
                QFilter qFilter05 = new QFilter("name", QCP.equals, classroom);
                QFilter qFilter06 = new QFilter("dyh7_c_time", QCP.equals, classTime);
                QFilter qFilter07 = new QFilter("dyh7_status", QCP.equals, "0");
                QFilter qFilter08 = new QFilter("dyh7_capacity",QCP.large_equals, classSizeInt);
                DynamicObject[] roomCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter05}
                );

                if (roomCheck.length == 0) {
                    //this.getView().showMessage("无该教室"+roomCheck.length);
                    result.put("formInfo", "无该教室");
                    return result;
                }

                // 检查时间+教室组合
                DynamicObject[] timeCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter05.and(qFilter06)}
                );

                if (timeCheck.length == 0) {
                    //this.getView().showMessage("该教室在周一上午不可用"+timeCheck.length);
                    //return;
                    result.put("formInfo", classTime+"不在排课时间范围内");
                    return result;
                }

                // 检查状态
                DynamicObject[] statusCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter05.and(qFilter06).and(qFilter07)}
                );

                if (statusCheck.length == 0) {
                    //this.getView().showMessage("该教室在周一上午已被占用"+statusCheck.length);
                    //return;
                    result.put("formInfo", "在该时间段无空闲教室");
                    return result;
                }

                DynamicObject[] numberCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter05.and(qFilter06).and(qFilter07).and(qFilter08)}
                );

                if (numberCheck.length == 0) {
                    //this.getView().showMessage("该教室在周一上午已被占用"+statusCheck.length);
                    //return;
                    result.put("formInfo", "教室容量不足");
                    return result;
                    // 最后检查容量
                    //this.getView().showMessage("教室容量不足100人");
                }

            }
        }
        return result;

    }

}
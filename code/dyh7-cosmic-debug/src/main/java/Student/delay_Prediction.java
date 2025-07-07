package Student;

import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 单据界面插件
 */
public class delay_Prediction extends AbstractBillPlugIn implements Plugin {

    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("dyh7_prediction")) {

            // 1. 获取当前单据数据包
            DynamicObject billObj = this.getModel().getDataEntity(true);

            // 2. 获取基础资料字段值
//            DynamicObject classroom = (DynamicObject) billObj.get("dyh7_course_scheduling");
//            String course = classroom.getString("dyh7_course");
//            String teacher = classroom.getString("dyh7_teacher");

            // 3.获取信息，并且以JSON字符串的形式展现
            JSONObject jsonResultObject = new JSONObject();
            jsonResultObject.put("stu_no", this.getModel().getValue("dyh7_sno").toString());//学号
            jsonResultObject.put("stu_name", this.getModel().getValue("dyh7_name").toString());//姓名
            jsonResultObject.put("current_year", "2025");//当前年份
            jsonResultObject.put("admission_year", this.getModel().getValue("dyh7_textfield6").toString());//入学年份
            jsonResultObject.put("target_credits", this.getModel().getValue("dyh7_integerfield").toString());//目标学分
            jsonResultObject.put("earned_credits", this.getModel().getValue("dyh7_integerfield1").toString());//已修学分
            jsonResultObject.put("gpa", this.getModel().getValue("dyh7_gpa").toString());//平均绩点
            jsonResultObject.put("major", this.getModel().getValue("dyh7_major").toString());//所属专业

//            this.getView().showMessage(JSONObject.toJSONString(jsonResultObject, true));
            //this.getView().showMessage("调试信息编码: "+course+teacher);
            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("StudentInfo", jsonResultObject.toJSONString());

            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-250705CD53B0C8"),
                    "开始进行延毕风险预测",
                    variableMap
            };
            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
            // 设置值
            this.getModel().setValue("dyh7_txt", jsonObjectData.getString("llmValue"));
            Markdown mk = this.getView().getControl("dyh7_md");
            mk.setText(jsonObjectData.getString("llmValue"));
        }
    }
    // 获取GPT提示的Fid
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }
    @Override
    public void afterBindData(EventObject eventObject) {
        Markdown mk = this.getView().getControl("dyh7_md");
        mk.setText(this.getModel().getValue("dyh7_txt").toString());
    }
}
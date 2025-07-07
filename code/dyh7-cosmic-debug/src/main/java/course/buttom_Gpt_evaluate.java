package course;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.IFrameMessage;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.IFrame;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.dataentity.entity.DynamicObject;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 单据界面插件
 */
public class buttom_Gpt_evaluate extends AbstractBillPlugIn implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("dyh7_course_anal")) {
            // 获取日任务信息，并且以JSON字符串的形式展现
            JSONObject jsonResultObject = new JSONObject();
            jsonResultObject.put("courseName", this.getModel().getValue("name").toString());//课程名称
            jsonResultObject.put("courseBook", this.getModel().getValue("dyh7_book").toString());//教程信息
            jsonResultObject.put("credit", this.getModel().getValue("dyh7_credit").toString());//课程学分
            jsonResultObject.put("classhour", this.getModel().getValue("dyh7_classhour").toString());//课程学时
//            jsonResultObject.put("major", this.getModel().getValue("dyh7_major").toString());//开设专业
//            jsonResultObject.put("department", this.getModel().getValue("dyh7_department").toString());//开设院系
            // 转换专业选项值为标题
            String majorValue = this.getModel().getValue("dyh7_major").toString();
            jsonResultObject.put("major", getOptionTitle("dyh7_major", majorValue));//开设专业

            // 转换院系选项值为标题
            String departmentValue = this.getModel().getValue("dyh7_department").toString();
            jsonResultObject.put("department", getOptionTitle("dyh7_department", departmentValue));//开设院系

            jsonResultObject.put("courseintroduction", this.getModel().getValue("dyh7_courseintroduction").toString());//课程介绍
//            jsonResultObject.put("courseType", this.getModel().getValue("dyh7_type").toString());//课程类型

            // 转换课程类型选项值为标题
            String typeValue = this.getModel().getValue("dyh7_type").toString();
            jsonResultObject.put("courseType", getOptionTitle("dyh7_type", typeValue));//课程类型

            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("courseInfo", jsonResultObject.toJSONString());

            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-25063006D1817F"),
                    "开始分析这门课程",
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
    // 获取选项标题的方法
    private String getOptionTitle(String fieldName, String optionValue) {
        if ("dyh7_major".equals(fieldName)) {
            switch (optionValue) {
                case "0": return "计算机科学与技术";
                case "1": return "网络工程";
                case "2": return "电子信息专业";
                case "3": return "数字媒体专业";
            }
        } else if ("dyh7_department".equals(fieldName)) {
            switch (optionValue) {
                case "0": return "信息技术系";
                case "1": return "外语系";
                case "2": return "经济系";
            }
        } else if ("dyh7_type".equals(fieldName)) {
            switch (optionValue) {
                case "0": return "必修";
                case "1": return "选修";
            }
        }
        return optionValue;
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
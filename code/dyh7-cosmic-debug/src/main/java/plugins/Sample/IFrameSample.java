package plugins.Sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.IFrame;
import kd.bos.form.events.CustomEventArgs;
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
public class IFrameSample extends AbstractBillPlugIn implements Plugin {
    private final static String KEY_IFRAME1 ="dyh7_iframeap";//控件标识

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getView().getControl(KEY_IFRAME1);
        iframe.setSrc("http://127.0.0.1:8080/index111.html");// 设置url

    }

    //在金蝶中接收消息
    @Override
    public void customEvent(CustomEventArgs e) {//用于触发自定义控件的定制事件。
        String key = e.getKey();//自定义控件标识
        String args = e.getEventArgs();//数据
        String ename = e.getEventName();//事件名称:这里默认是invokeCustomEvent
        //调试：c5509f36c7d94882ae3f54cd8e37203e{"content":{"msg":"编译原理","methodName":"other","type":"click"}}invokeCustomEvent
        //this.getView().showMessage("调试："+key+args+ename);

        //提取名称
        Map<String, Object> jsonMap = JSON.parseObject(args);
        Map<String, Object> contentMap = (Map<String, Object>) jsonMap.get("content");
        String message = (String) contentMap.get("msg");
        String type = (String) contentMap.get("type");

        // 调用GPT开发平台微服务
        Map<String, String> variableMap = new HashMap<>();
        variableMap.put("input", message);

        Object[] params = new Object[]{
                //GPT提示编码
                getPromptFid("prompt-2507072DCF7E6D"),
                "开始生成知识图谱三元组",
                variableMap
        };
        Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
        JSONObject jsonObjectResult = new JSONObject(result);
        JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");

        //调试：{"llmValue":"```json\n[\n [\"编译原理\", \"属于\", \"计算机科学\"],\n [\"编译原理\", \"研究内容\", \"编译器设计与实现\"],\n [\"编译原理\", \"包括\", \"词法分析\"],\n [\"编译原理\", \"包括\", \"语法分析\"],\n [\"编译原理\", \"包括\", \"语义分析\"],\n [\"编译原理\", \"包括\", \"中间代码生成\"],\n [\"编译原理\", \"包括\", \"代码优化\"],\n [\"编译原理\", \"包括\", \"目标代码生成\"]\n]\n```","referenceInfos":"[]","citationInfos":"[]","taskId":"2253656460141529088"}
        //this.getView().showMessage("调试："+jsonObjectData);


        //向iframe中发送一个消息
        String data = jsonObjectData.getString("llmValue");
        if (data != null) {
            data = data.replace("```json", "").replace("```", "").trim();
        }
        //调试前：```json [ { "source": "编译原理", "relation": "属于", "target": "计算机科学" }, { "source": "编译原理", "relation": "研究内容", "target": "编译器设计与实现" }, { "source": "编译原理", "relation": "包含阶段", "target": "词法分析" }, { "source": "编译原理", "relation": "包含阶段", "target": "语法分析" }, { "source": "编译原理", "relation": "包含阶段", "target": "语义分析" }, { "source": "编译原理", "relation": "应用领域", "target": "编程语言开发" } ] ```
        //调试后：[ { "source": "编译原理", "relation": "属于", "target": "计算机科学" }, { "source": "编译原理", "relation": "研究内容", "target": "编译器设计与实现" }, { "source": "编译原理", "relation": "包含阶段", "target": "词法分析" }, { "source": "编译原理", "relation": "包含阶段", "target": "语法分析" }, { "source": "编译原理", "relation": "包含阶段", "target": "语义分析" }, { "source": "编译原理", "relation": "应用领域", "target": "编程语言开发" } ]
        //this.getView().showMessage("调试："+data);



        IFrame iframe = this.getControl("dyh7_iframeap");
        IFrameMessage msg = new IFrameMessage();
        msg.setType(type);
        msg.setOrigin("*");
        msg.setContent(data);

        //调试：kd.bos.entity.IFrameMessage@54768eeb
        //this.getView().showMessage("调试："+msg);
        iframe.postMessage(msg);

    }
    // 获取GPT提示的Fid
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }

}